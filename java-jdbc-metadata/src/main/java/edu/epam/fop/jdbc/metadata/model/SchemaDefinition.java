package edu.epam.fop.jdbc.metadata.model;

import java.util.Map;

/**
 * Holds definition of the schema.
 *
 * @param catalog catalogues of the schema
 * @param name schema name
 * @param tables {@linkplain Map} of table names against their definitions
 */
public record SchemaDefinition(String catalog, String name, Map<String, TableDefinition> tables) {
}
