package edu.epam.fop.jdbc.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DbManager {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private DbManager() {
        //throw new UnsupportedOperationException();
    }

    public static boolean setGroupForStudents(Connection connection, Group group, List<Student> students)
            throws SQLException {
        try {
            // Set auto-commit to false only if it's currently true
            if (connection.getAutoCommit()) connection.setAutoCommit(false);

            // Set the group for each student
            String updateStudentGroupSQL = "UPDATE students SET group_id = ? WHERE id = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateStudentGroupSQL)) {
                updateStatement.setInt(1, group.getId());

                for (Student student : students) {
                    updateStatement.setInt(2, student.getId());
                    int rowsAffected = updateStatement.executeUpdate();
                    // Rollback if any update fails
                    if (rowsAffected != 1) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Commit the transaction if all updates are successful
            connection.commit();
            return true;

        } catch (SQLException e) {
            // Rollback if an exception occurs
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static boolean deleteStudents(Connection connection, List<Student> students) throws SQLException {
        try {
            // Set auto-commit to false only if it's currently true
            if (connection.getAutoCommit()) connection.setAutoCommit(false);

            // Delete each student
            String deleteStudentSQL = "DELETE FROM students WHERE id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteStudentSQL)) {
                for (Student student : students) {
                    deleteStatement.setInt(1, student.getId());
                    int rowsAffected = deleteStatement.executeUpdate();
                    // Rollback if any deletion fails
                    if (rowsAffected != 1) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            // Commit the transaction if all deletions are successful
            connection.commit();
            return true;

        } catch (SQLException e) {
            // Rollback if an exception occurs
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}