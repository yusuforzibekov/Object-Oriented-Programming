package edu.epam.fop.jdbc.metadata.model;

/**
 * Holds definition of the table column.
 *
 * @param name name of the column
 * @param jdbcType JDBC type ordinal referencing {@linkplain java.sql.Types}
 * @param typeName DB dependent name of the type
 * @param size size of the type
 * @param nullable flag defines is column nullable or not
 * @param defaultValue default value of the column in {@code String} format
 */
public record ColumnDefinition(String name,
                               int jdbcType,
                               String typeName,
                               int size,
                               boolean nullable,
                               String defaultValue) {
}
