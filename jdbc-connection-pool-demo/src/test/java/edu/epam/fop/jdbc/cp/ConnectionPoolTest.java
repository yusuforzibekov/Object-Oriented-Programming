package edu.epam.fop.jdbc.cp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import edu.epam.fop.jdbc.cp.ConnectionPool.PooledConnection;

@DisplayNameGeneration(ReplaceCamelCase.class)
public class ConnectionPoolTest {
	private ConnectionPool connectionPool;
	private String url;
	private String user;
	private String password;
	private int poolSize;

	private Connection createMockConnection() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		AtomicBoolean isClosed = new AtomicBoolean(false);
		when(mockConnection.isClosed()).thenAnswer(invocation -> isClosed.get());
		doAnswer(invocation -> {
			isClosed.set(true);
			return null;
		}).when(mockConnection).close();
		when(mockConnection.getAutoCommit()).thenReturn(false);
		return mockConnection;
	}

	@BeforeEach
	void init() throws ConnectionPoolException {
		url = "jdbc:mockUrl";
		user = "mockUser";
		password = "mockPassword";
		poolSize = 5;

		try (MockedStatic<DriverManager> mockedDriverManager = Mockito.mockStatic(DriverManager.class)) {
			mockedDriverManager.when(() -> DriverManager.getConnection(url, user, password))
					.thenAnswer(invocation -> createMockConnection());
			connectionPool = ConnectionPool.create(url, user, password, poolSize);
		}
	}

	@ParameterizedTest
	@ValueSource(ints = { -2, -1, 0 })
	void testInvalidPoolSize(int poolSize) {
		assertThrows(ConnectionPoolException.class, () -> ConnectionPool.create(url, user, password, poolSize),
				"The pool size should not be less than 1");
	}

	@Test
	void testInitPoolData() {
		Class<?> connectionPoolClass = ConnectionPool.class;

		try {
			Field availableConnectionsField = connectionPoolClass.getDeclaredField("availableConnections");
			assertEquals(BlockingQueue.class, availableConnectionsField.getType(),
					"availableConnections should be of type BlockingQueue<Connection>");
		} catch (NoSuchFieldException e) {
			fail("availableConnections field should be present");
		}

		try {
			Field usedConnectionsField = connectionPoolClass.getDeclaredField("usedConnections");
			assertEquals(BlockingQueue.class, usedConnectionsField.getType(),
					"usedConnections should be of type BlockingQueue<Connection>");
		} catch (NoSuchFieldException e) {
			fail("usedConnections field should be present");
		}

		assertNotNull(connectionPool.availableConnections, "Collection of available connections should not be null");
		assertNotNull(connectionPool.usedConnections, "Collection of used connections should not be null");
		assertEquals(poolSize, connectionPool.availableConnections.size(),
				"All connections should be available in the pool");
		assertEquals(0, connectionPool.usedConnections.size(), "No connections should be in use initially");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTakeConnectionSuccessfully() throws Exception {
		Connection mockedConnection = Mockito.mock(Connection.class);
		BlockingQueue<Connection> mockedAvailableConnections = Mockito.mock(BlockingQueue.class);
		BlockingQueue<Connection> mockedUsedConnections = Mockito.mock(BlockingQueue.class);
		when(mockedAvailableConnections.take()).thenReturn(mockedConnection);
		connectionPool.availableConnections = mockedAvailableConnections;
		connectionPool.usedConnections = mockedUsedConnections;

		Connection returnedConnection = connectionPool.takeConnection();

		assertNotNull(returnedConnection, "Returned connection should not be null");
		verify(mockedAvailableConnections, times(1)).take();
		verify(mockedUsedConnections, times(1)).add(mockedConnection);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTakeConnectionException() throws InterruptedException {
		BlockingQueue<Connection> mockedAvailableConnections = Mockito.mock(BlockingQueue.class);
		BlockingQueue<Connection> mockedUsedConnections = Mockito.mock(BlockingQueue.class);
		doThrow(new InterruptedException("Taking interrupted")).when(mockedAvailableConnections).take();
		connectionPool.availableConnections = mockedAvailableConnections;
		connectionPool.usedConnections = mockedUsedConnections;

		assertThrows(ConnectionPoolException.class, () -> connectionPool.takeConnection(),
				"Expected ConnectionPoolException to be thrown");

		verify(mockedAvailableConnections, times(1)).take();
		verifyNoInteractions(mockedUsedConnections);
	}

	@Test
	@Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
	void testTakeAndCloseAllConnections() throws Exception {
		Set<Connection> connections = new HashSet<>();

		for (int i = 0; i < poolSize; i++) {
			Connection connection = connectionPool.takeConnection();

			assertNotNull(connection, "Connection retrieved from the pool should not be null");
			assertDoesNotThrow(() -> assertFalse(connection.isClosed(), "Connection should not be closed"));
			assertTrue(connections.add(connection), "All connections in the pool should be different");
			assertFalse(connectionPool.availableConnections.contains(connection), "Connection is still available");
			assertTrue(connectionPool.usedConnections.contains(connection), "Connection should be in use");
			assertEquals(poolSize - 1 - i, connectionPool.availableConnections.size(),
					"Available connections should decrease as they are taken");
			assertEquals(1 + i, connectionPool.usedConnections.size(),
					"Used connections should increase as they are taken");
		}

		int i = 1;
		for (Connection connection : connections) {
			assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
				connectionPool.closeConnection(connection);
			}, "The closeConnection() method should not block execution");

			assertDoesNotThrow(() -> assertFalse(connection.isClosed(), "Connection should not be closed"));
			assertTrue(connectionPool.availableConnections.contains(connection), "Connection should be available");
			assertFalse(connectionPool.usedConnections.contains(connection), "Connection is still in use");
			assertEquals(i, connectionPool.availableConnections.size(),
					"Available connections should increase as they are returned to the pool");
			assertEquals(poolSize - i++, connectionPool.usedConnections.size(),
					"Used connections should decrease as they are returned to the pool");
		}

		for (Connection connection : connections) {
			assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
				assertThrows(ConnectionPoolException.class, () -> connectionPool.closeConnection(connection),
						"Closing an already closed connection should raise an exception");
			}, "The closeConnection() method should not block execution");
		}
	}

	@Test
	@Timeout(value = 1500, unit = TimeUnit.MILLISECONDS)
	void testWaitForAvailableConnection() throws ConnectionPoolException, InterruptedException {
		List<Connection> takenConnections = new ArrayList<>();
		for (int i = 0; i < poolSize; i++) {
			takenConnections.add(connectionPool.takeConnection());
		}
		assertEquals(0, connectionPool.availableConnections.size(), "No connections should be available");

		ExecutorService executor = Executors.newFixedThreadPool(2);
		AtomicBoolean waitingStarted = new AtomicBoolean(false);
		AtomicBoolean waitingFinished = new AtomicBoolean(false);

		executor.submit(() -> {
			waitingStarted.set(true);
			Connection connection;
			try {
				connection = connectionPool.takeConnection();
				waitingFinished.set(true);
				connectionPool.closeConnection(connection);
			} catch (ConnectionPoolException e) {
				fail("Taking or closing connection in the waiting thread should not throw an exception");
			}
		});

		while (!waitingStarted.get()) {
			Thread.sleep(100);
		}

		assertFalse(waitingFinished.get(), "Taking connection should still be waiting");

		executor.submit(() -> {
			try {
				connectionPool.closeConnection(takenConnections.remove(0));
			} catch (ConnectionPoolException e) {
				fail("Closing connection in the releasing thread should not throw an exception");
			}
		});

		while (!waitingFinished.get()) {
			Thread.sleep(100);
		}

		for (Connection connection : takenConnections) {
			assertTimeoutPreemptively(Duration.ofMillis(500), () -> {
				connectionPool.closeConnection(connection);
			}, "The closeConnection() method should not block execution");
		}

		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.SECONDS);
	}

	@Test
	void testCommitBeforeCloseForAvailableConnections() throws SQLException, ConnectionPoolException {
		List<Connection> innerConnections = new ArrayList<>(connectionPool.availableConnections.size());

		for (Connection connection : connectionPool.availableConnections) {
			innerConnections.add(((PooledConnection) connection).getInnerConnection());
		}

		connectionPool.close();
		for (Connection connection : innerConnections) {
			InOrder inOrder = Mockito.inOrder(connection);
			inOrder.verify(connection, atLeast(1)).getAutoCommit();
			inOrder.verify(connection).commit();
			inOrder.verify(connection).close();
		}
	}

	@Test
	void testCommitBeforeCloseForUsedConnections() throws SQLException, ConnectionPoolException {
		connectionPool.usedConnections.addAll(connectionPool.availableConnections);
		connectionPool.availableConnections.clear();
		List<Connection> innerConnections = new ArrayList<>(connectionPool.usedConnections.size());

		for (Connection connection : connectionPool.usedConnections) {
			innerConnections.add(((PooledConnection) connection).getInnerConnection());
		}

		connectionPool.close();
		for (Connection connection : innerConnections) {
			InOrder inOrder = Mockito.inOrder(connection);
			inOrder.verify(connection, atLeast(1)).getAutoCommit();
			inOrder.verify(connection, atLeast(1)).commit();
			inOrder.verify(connection, atLeast(1)).close();
		}
	}

	@Test
	public void testCloseConnectionSuccessfully() throws SQLException, ConnectionPoolException {
		Connection mockedConnection = createMockConnection();
		connectionPool.closeConnection(mockedConnection);
		verify(mockedConnection, times(1)).close();
	}

	@Test
	public void testCloseConnectionException() throws SQLException {
		Connection mockedConnection = createMockConnection();
		doThrow(new SQLException("Closing error")).when(mockedConnection).close();
		assertThrows(ConnectionPoolException.class, () -> connectionPool.closeConnection(mockedConnection),
				"Expected ConnectionPoolException to be thrown");
		verify(mockedConnection, times(1)).close();
	}
}