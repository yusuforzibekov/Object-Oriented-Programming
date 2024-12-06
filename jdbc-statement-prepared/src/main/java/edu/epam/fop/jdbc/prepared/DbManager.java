package edu.epam.fop.jdbc.prepared;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	private DbManager() {
		throw new UnsupportedOperationException();
	}

	public static boolean insertGroup(Connection connection, Group group) throws SQLException {
		String sql = "INSERT INTO groups (group_name) VALUES (?)";
		try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, group.getName());
			int affectedRows = statement.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						group.setId(generatedKeys.getInt(1));
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean insertStudent(Connection connection, Student student) throws SQLException {
		String sql = "INSERT INTO students (first_name, last_name, group_id) VALUES (?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, student.getFirstName());
			statement.setString(2, student.getLastName());
			statement.setInt(3, student.getGroup().getId());
			int affectedRows = statement.executeUpdate();
			if (affectedRows > 0) {
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						student.setId(generatedKeys.getInt(1));
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Group findFirstGroupByName(Connection connection, String name) throws SQLException {
		String sql = "SELECT * FROM groups WHERE group_name = ? LIMIT 1";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Group(resultSet.getInt("id"), resultSet.getString("group_name"));
				}
			}
		}
		return null;
	}

	public static Student findFirstStudentByName(Connection connection, String firstName, String lastName)
			throws SQLException {
		String sql = "SELECT * FROM students WHERE first_name = ? AND last_name = ? LIMIT 1";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, firstName);
			statement.setString(2, lastName);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					int groupId = resultSet.getInt("group_id");
					Group group = findGroupById(connection, groupId);
					return new Student(resultSet.getInt("id"), resultSet.getString("first_name"),
							resultSet.getString("last_name"), group);
				}
			}
		}
		return null;
	}

	public static List<Student> findStudentsByGroup(Connection connection, Group group) throws SQLException {
		String sql = "SELECT * FROM students WHERE group_id = ?";
		List<Student> students = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, group.getId());
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					students.add(new Student(resultSet.getInt("id"), resultSet.getString("first_name"),
							resultSet.getString("last_name"), group));
				}
			}
		}
		return students;
	}

	public static boolean updateGroupById(Connection connection, Group group) throws SQLException {
		String sql = "UPDATE groups SET group_name = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, group.getName());
			statement.setInt(2, group.getId());
			return statement.executeUpdate() > 0;
		}
	}

	public static boolean updateStudentById(Connection connection, Student student) throws SQLException {
		String sql = "UPDATE students SET first_name = ?, last_name = ?, group_id = ? WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, student.getFirstName());
			statement.setString(2, student.getLastName());
			statement.setInt(3, student.getGroup().getId());
			statement.setInt(4, student.getId());
			return statement.executeUpdate() > 0;
		}
	}

	private static Group findGroupById(Connection connection, int groupId) throws SQLException {
		String sql = "SELECT * FROM groups WHERE id = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, groupId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					return new Group(resultSet.getInt("id"), resultSet.getString("group_name"));
				}
			}
		}
		return null;
	}
}