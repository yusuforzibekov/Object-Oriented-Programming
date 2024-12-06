package edu.epam.fop.jdbc.metadata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DatabaseConfig implements AutoCloseable {

    private static final String DB_USER = "sa";
    private static final String DB_PWD = "";

    private final String catalogName;
    private final Connection connection;

    public DatabaseConfig(String catalogName) {
        verifyDriver();
        this.catalogName = catalogName;
        this.connection = createConnection();
    }

    private void verifyDriver() {
        try {
            Class.forName("org.h2.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("H2 Driver is not found", e);
        }
    }

    private Connection createConnection() {
        String url = "jdbc:h2:mem:" + catalogName;
        try {
            return DriverManager.getConnection(url, DB_USER, DB_PWD);
        }
        catch (SQLException e) {
            throw new IllegalStateException("Could not create connection to %s DB using credentials %s:%s"
                    .formatted(url, DB_USER, DB_PWD), e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void initializeSchema(String schema, Map<String, Set<String>> tableDefinitions) {
        createSchema(schema);
        tableDefinitions.forEach((tableName, tableDefinition) -> createTable(schema, tableName, tableDefinition));
    }

    private void createSchema(String schema) {
        if (isBlank(schema)) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }
        try (var stat = connection.createStatement()){
            stat.executeUpdate("CREATE SCHEMA \"%s\"".formatted(schema));
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("Failed to create schema [%s]".formatted(schema), e);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    private void createTable(String schema, String tableName, Set<String> tableDefinition) {
        if (isBlank(tableName)) {
            throw new IllegalArgumentException("Table name must not be blank");
        }
        if (tableDefinition.isEmpty()) {
            throw new IllegalArgumentException("Table definition must not be empty");
        }
        try (var stat = connection.createStatement()){
            stat.executeUpdate("CREATE TABLE \"%s\".\"%s\" (%s)".formatted(schema, tableName, String.join(", ", tableDefinition)));
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("Failed to create table [%s].[%s] using definition (%s)"
                    .formatted(schema, tableName, tableDefinition), e);
        }
    }

    private static boolean isBlank(String str) {
        return Objects.isNull(str) || str.isBlank();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
