package edu.epam.fop.jdbc.create;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DbManager {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	private DbManager() {
		throw new UnsupportedOperationException();
	}

	public static List<Group> fetchGroups(Connection connection) throws SQLException {
		List<Group> groups = new ArrayList<>();
		// Step 1: Create a statement object
		Statement statement = connection.createStatement();
		// Step 2: Execute a SQL query
		String sql = "SELECT * FROM groups";
		ResultSet resultSet = statement.executeQuery(sql);
		// Step 3: Process the result set
		while (resultSet.next()) {
			// Get the values from each column
			int id = resultSet.getInt("id");
			String name = resultSet.getString("group_name");
			// Create a group object
			Group group = new Group(id, name);
			// Add the group object to the list
			groups.add(group);
		}
		// Step 4: Close the resources
		resultSet.close();
		statement.close();
		// Return the list of groups
		return groups;
	}

	public static List<Student> fetchStudents(Connection connection) throws SQLException {
		List<Student> students = new ArrayList<>();
		// Step 1: Create a statement object
		Statement statement = connection.createStatement();
		// Step 2: Execute a SQL query
		String sql = "SELECT s.*, g.group_name FROM students s JOIN groups g ON s.group_id = g.id";
		ResultSet resultSet = statement.executeQuery(sql);
		// Step 3: Process the result set
		while (resultSet.next()) {
			// Get the values from each column
			int id = resultSet.getInt("id");
			String firstName = resultSet.getString("first_name");
			String lastName = resultSet.getString("last_name");
			int groupId = resultSet.getInt("group_id");
			String groupName = resultSet.getString("group_name");
			// Create a student object
			Student student = new Student(id, firstName, lastName, new Group(groupId, groupName));
			// Add the student object to the list
			students.add(student);
		}
		// Step 4: Close the resources
		resultSet.close();
		statement.close();
		// Return the list of students
		return students;
	}
}