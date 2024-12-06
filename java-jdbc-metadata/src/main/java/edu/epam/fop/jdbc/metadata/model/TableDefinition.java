package edu.epam.fop.jdbc.metadata.model;

import java.util.Map;

/**
 * Holds definition of the table.
 *
 * @param name table name
 * @param columns {@linkplain Map} of column names against their definitions
 */
public record TableDefinition(String name,
                              Map<String, ColumnDefinition> columns) {
}
