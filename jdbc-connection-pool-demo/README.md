# Connection Pool Demo

The goal of this task is to give you some practice working with a database connection pool.

Duration: _120 minutes_

## Description

Connection pooling is a well-known data access pattern. In this task, you will implement your own connection pool from scratch. 

## Requirements

1) Use the provided classes: `ConnectionPoolException`, `Group` and `Student`.

2) If necessary, you can use your own relational database. Your database should contain two tables to represent the `Group` and `Student` objects: `groups (id, group_name)` and `students (id, first_name, last_name, group_id)`.

3) Provide an implementation of the following `ConnectionPool` methods:
   - `create` initializes the connection pool.
   - `takeConnection` provides a ready-to-use database connection.
   - `closeConnection` returns the connection to the pool.
   - `close` clears the connection pool and closes all connections.

## Examples

An example of calling methods of the `ConnectionPool` class:

```java
String url = JDBC_URL;
String user = "";
String password = "";
int poolSize = 5;

try (ConnectionPool connectionPool = ConnectionPool.create(url, user, password, poolSize)) {
	...
	try (Connection connection = connectionPool.takeConnection()) {
		List<Group> groups = fetchGroups(connection);
		LOGGER.log(System.Logger.Level.INFO, groups);
	} catch (SQLException e) {
		// TODO: Error handling.
		throw e;
	}
	...
	try (Connection connection = connectionPool.takeConnection()) {
		List<Student> students = fetchStudents(connection);
		LOGGER.log(System.Logger.Level.INFO, students);
	} catch (SQLException e) {
		// TODO: Error handling.
		throw e;
	}
	...
} catch (ConnectionPoolException e) {
	// TODO: Error handling.
	throw e;
}
```
