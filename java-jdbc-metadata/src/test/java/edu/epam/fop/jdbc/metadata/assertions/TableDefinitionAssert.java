package edu.epam.fop.jdbc.metadata.assertions;

import edu.epam.fop.jdbc.metadata.model.TableDefinition;

import java.util.Map;
import java.util.function.Consumer;

public class TableDefinitionAssert extends AbstractDefinitionAssert<TableDefinitionAssert, TableDefinition> {

    public TableDefinitionAssert(TableDefinition tableDefinition) {
        super(tableDefinition, TableDefinitionAssert.class);
    }

    public TableDefinitionAssert hasName(String name) {
        return hasStringProperty("name", TableDefinition::name, name);
    }

    public TableDefinitionAssert hasOnlyColumns(Map<String, Consumer<ColumnDefinitionAssert>> assertions) {
        return hasMap(TableDefinition::columns, ColumnDefinitionAssert::new, assertions);
    }
}
