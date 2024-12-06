package edu.epam.fop.jdbc.callable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class DbManager {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	private DbManager() {
		throw new UnsupportedOperationException();
	}

	public static int callCountGroups(Connection connection) throws SQLException {
		try (CallableStatement statement = connection.prepareCall("{call COUNT_GROUPS(?)}")) {
			statement.registerOutParameter(1, java.sql.Types.INTEGER);
			statement.execute();
			return statement.getInt(1);
		}
	}

	public static int callCountStudents(Connection connection) throws SQLException {
		try (CallableStatement statement = connection.prepareCall("{call COUNT_STUDENTS(?)}")) {
			statement.registerOutParameter(1, java.sql.Types.INTEGER);
			statement.execute();
			return statement.getInt(1);
		}
	}

	public static int callCountStudentsByGroupId(Connection connection, int groupId) throws SQLException {
		try (CallableStatement statement = connection.prepareCall("{call COUNT_STUDENTS_BY_GROUP_ID(?, ?)}")) {
			statement.setInt(1, groupId);
			statement.registerOutParameter(2, java.sql.Types.INTEGER);
			statement.execute();
			return statement.getInt(2);
		}
	}
}