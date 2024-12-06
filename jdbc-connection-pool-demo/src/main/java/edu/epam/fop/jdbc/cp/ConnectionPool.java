package edu.epam.fop.jdbc.cp;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public final class ConnectionPool implements AutoCloseable {

    BlockingQueue<Connection> availableConnections;
    BlockingQueue<Connection> usedConnections;

    private ConnectionPool() {
    }

    public static ConnectionPool create(String url, String user, String password, int poolSize)
            throws ConnectionPoolException {
        ConnectionPool connectionPool = new ConnectionPool();
        try {
            connectionPool.usedConnections = new ArrayBlockingQueue<>(poolSize);
            connectionPool.availableConnections = new ArrayBlockingQueue<>(poolSize);

            for (int i = 0; i < poolSize; i++) {
                Connection connection = DriverManager.getConnection(url, user, password);
                PooledConnection pooledConnection = connectionPool.new PooledConnection(connection);
                connectionPool.availableConnections.add(pooledConnection);
            }
        } catch (Exception e) {
            throw new ConnectionPoolException("Pool initialization error", e);
        }
        return connectionPool;
    }

    public Connection takeConnection() throws ConnectionPoolException {
        try {
            // Take a connection from the availableConnections queue using the BlockingQueue.take() method.
            // This method will block until a connection is available or the thread is interrupted.
            Connection connection = availableConnections.take();
            // Add the taken connection to the usedConnections queue.
            usedConnections.add(connection);
            // Return the taken connection.
            return connection;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionPoolException("Error taking connection", e);
        }
    }

    public synchronized void closeConnection(Connection connection) throws ConnectionPoolException {
        try {
            // Return the connection to the pool.
            // Use the PooledConnection.close() method, which will remove the connection from the usedConnections queue
            // and add it to the availableConnections queue.
            connection.close();
        } catch (SQLException e) {
            throw new ConnectionPoolException("Error closing connection", e);
        }
    }

    @Override
    public synchronized void close() throws ConnectionPoolException {
        closeConnections(usedConnections);
        closeConnections(availableConnections);
    }

    private void closeConnections(BlockingQueue<Connection> connections) throws ConnectionPoolException {
        for (Connection connection : connections) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
                ((PooledConnection) connection).getInnerConnection().close();
            } catch (SQLException e) {
                throw new ConnectionPoolException("Error closing connection", e);
            }
        }
        connections.clear();
    }

    final class PooledConnection implements Connection {

        private final Connection innerConnection;

        public PooledConnection(Connection connection) throws SQLException {
            innerConnection = connection;
            innerConnection.setAutoCommit(true);
        }

        public Connection getInnerConnection() {
            return innerConnection;
        }

        @Override
        public synchronized void close() throws SQLException {
            if (innerConnection.isClosed()) {
                throw new SQLException("Attempting to close a closed connection");
            }

            if (innerConnection.isReadOnly()) {
                innerConnection.setReadOnly(false);
            }

            if (!usedConnections.remove(this)) {
                throw new SQLException("Error removing connection from pool");
            }

            if (!availableConnections.offer(this)) {
                throw new SQLException("Error inserting connection into pool");
            }
        }

        @Override
        public void clearWarnings() throws SQLException {
            innerConnection.clearWarnings();
        }

        @Override
        public void commit() throws SQLException {
            innerConnection.commit();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return innerConnection.createArrayOf(typeName, elements);
        }

        @Override
        public Blob createBlob() throws SQLException {
            return innerConnection.createBlob();
        }

        @Override
        public Clob createClob() throws SQLException {
            return innerConnection.createClob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            return innerConnection.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return innerConnection.createSQLXML();
        }

        @Override
        public Statement createStatement() throws SQLException {
            return innerConnection.createStatement();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return innerConnection.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return innerConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return innerConnection.createStruct(typeName, attributes);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return innerConnection.getAutoCommit();
        }

        @Override
        public String getCatalog() throws SQLException {
            return innerConnection.getCatalog();
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return innerConnection.getClientInfo();
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            return innerConnection.getClientInfo(name);
        }

        @Override
        public int getHoldability() throws SQLException {
            return innerConnection.getHoldability();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return innerConnection.getMetaData();
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return innerConnection.getTransactionIsolation();
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return innerConnection.getTypeMap();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return innerConnection.getWarnings();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return innerConnection.isClosed();
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return innerConnection.isReadOnly();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            return innerConnection.isValid(timeout);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            return innerConnection.nativeSQL(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return innerConnection.prepareCall(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return innerConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                             int resultSetHoldability) throws SQLException {
            return innerConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return innerConnection.prepareStatement(sql);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return innerConnection.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return innerConnection.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return innerConnection.prepareStatement(sql, columnNames);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                throws SQLException {
            return innerConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                                  int resultSetHoldability) throws SQLException {
            return innerConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public void rollback() throws SQLException {
            innerConnection.rollback();
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            innerConnection.setAutoCommit(autoCommit);
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            innerConnection.setCatalog(catalog);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            innerConnection.setClientInfo(name, value);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            innerConnection.setHoldability(holdability);
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            innerConnection.setReadOnly(readOnly);
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return innerConnection.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return innerConnection.setSavepoint(name);
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            innerConnection.setTransactionIsolation(level);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return innerConnection.isWrapperFor(iface);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return innerConnection.unwrap(iface);
        }

        @Override
        public void abort(Executor arg0) throws SQLException {
            innerConnection.abort(arg0);

        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return innerConnection.getNetworkTimeout();
        }

        @Override
        public String getSchema() throws SQLException {
            return innerConnection.getSchema();
        }

        @Override
        public void releaseSavepoint(Savepoint arg0) throws SQLException {
            innerConnection.releaseSavepoint(arg0);
        }

        @Override
        public void rollback(Savepoint arg0) throws SQLException {
            innerConnection.rollback(arg0);
        }

        @Override
        public void setClientInfo(Properties arg0) throws SQLClientInfoException {
            innerConnection.setClientInfo(arg0);
        }

        @Override
        public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
            innerConnection.setNetworkTimeout(arg0, arg1);
        }

        @Override
        public void setSchema(String arg0) throws SQLException {
            innerConnection.setSchema(arg0);
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
            innerConnection.setTypeMap(arg0);
        }
    }
}