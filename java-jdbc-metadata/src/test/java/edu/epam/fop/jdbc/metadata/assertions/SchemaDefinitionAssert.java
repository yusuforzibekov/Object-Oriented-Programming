package edu.epam.fop.jdbc.metadata.assertions;

import edu.epam.fop.jdbc.metadata.model.SchemaDefinition;

import java.util.Map;
import java.util.function.Consumer;

public class SchemaDefinitionAssert extends AbstractDefinitionAssert<SchemaDefinitionAssert, SchemaDefinition> {

    public SchemaDefinitionAssert(SchemaDefinition schemaDefinition) {
        super(schemaDefinition, SchemaDefinitionAssert.class);
    }

    public SchemaDefinitionAssert hasCatalog(String catalog) {
        return hasStringProperty("catalog", SchemaDefinition::catalog, catalog);
    }

    public SchemaDefinitionAssert hasName(String name) {
        return hasStringProperty("name", SchemaDefinition::name, name);
    }

    public SchemaDefinitionAssert hasOnlyTables(Map<String, Consumer<TableDefinitionAssert>> assertions) {
        return hasMap(SchemaDefinition::tables, TableDefinitionAssert::new, assertions);
    }

    @Override
    public String toString() {
        return actual.toString();
    }
}
