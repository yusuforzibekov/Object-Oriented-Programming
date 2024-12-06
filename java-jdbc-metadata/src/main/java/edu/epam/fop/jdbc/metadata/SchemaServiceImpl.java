package edu.epam.fop.jdbc.metadata;

import edu.epam.fop.jdbc.metadata.model.ColumnDefinition;
import edu.epam.fop.jdbc.metadata.model.SchemaDefinition;
import edu.epam.fop.jdbc.metadata.model.TableDefinition;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SchemaServiceImpl implements SchemaService {

    private final Connection connection;

    public SchemaServiceImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SchemaDefinition readSchema(String schema) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            // Get the catalog and schema name
            ResultSet schemaRs = metaData.getSchemas(null, schema);

            if (schemaRs.next()) {

                String catalog = schemaRs.getString("TABLE_CATALOG");
                String schemaName = schemaRs.getString("TABLE_SCHEM");

                // Get the table names and definitions
                Map<String, TableDefinition> tables = new HashMap<>();
                ResultSet tableRs = metaData.getTables(catalog, schemaName, null, null);

                while (tableRs.next()) {

                    String tableName = tableRs.getString("TABLE_NAME");

                    // Get the column names and definitions
                    Map<String, ColumnDefinition> columns = new HashMap<>();
                    ResultSet columnRs = metaData.getColumns(catalog, schemaName, tableName, null);

                    while (columnRs.next()) {
                        String columnName = columnRs.getString("COLUMN_NAME");
                        int jdbcType = columnRs.getInt("DATA_TYPE");
                        String typeName = columnRs.getString("TYPE_NAME");
                        int size = columnRs.getInt("COLUMN_SIZE");
                        boolean nullable = columnRs.getBoolean("IS_NULLABLE");
                        String defaultValue = columnRs.getString("COLUMN_DEF");
                        ColumnDefinition column = new ColumnDefinition(columnName, jdbcType, typeName, size, nullable,
                                defaultValue);
                        columns.put(columnName, column);
                    }

                    columnRs.close();
                    TableDefinition table = new TableDefinition(tableName, columns);
                    tables.put(tableName, table);
                }

                tableRs.close();

                // Create the schema definition object
                SchemaDefinition schemaDefinition = new SchemaDefinition(catalog, schemaName, tables);
                schemaRs.close();
                return schemaDefinition;

            } else {
                schemaRs.close();
                throw new SQLException("Schema not found: " + schema);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
