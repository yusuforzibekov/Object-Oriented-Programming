package edu.epam.fop.jdbc.metadata.assertions;

import edu.epam.fop.jdbc.metadata.model.ColumnDefinition;

public class ColumnDefinitionAssert extends AbstractDefinitionAssert<ColumnDefinitionAssert, ColumnDefinition> {

    public ColumnDefinitionAssert(ColumnDefinition columnDefinition) {
        super(columnDefinition, ColumnDefinitionAssert.class);
    }

    public ColumnDefinitionAssert hasName(String name) {
        return hasStringProperty("name", ColumnDefinition::name, name);
    }

    public ColumnDefinitionAssert hasJdbcType(int jdbcType) {
        return hasIntProperty("jdbc-type", ColumnDefinition::jdbcType, jdbcType);
    }

    public ColumnDefinitionAssert hasTypeName(String typeName) {
        return hasStringProperty("type-name", ColumnDefinition::typeName, typeName);
    }

    public ColumnDefinitionAssert hasSize(int size) {
        return hasIntProperty("size", ColumnDefinition::size, size);
    }

    public ColumnDefinitionAssert isNullable() {
        return hasBooleanProperty("nullable", ColumnDefinition::nullable, true);
    }

    public ColumnDefinitionAssert isNotNullable() {
        return hasBooleanProperty("nullable", ColumnDefinition::nullable, false);
    }

    public ColumnDefinitionAssert hasDefaultValue(String defaultValue) {
        return hasStringProperty("default-value", ColumnDefinition::defaultValue, defaultValue);
    }

    public ColumnDefinitionAssert doesNotHaveDefaultValue() {
        return doesNotHaveProperty("default-value", ColumnDefinition::defaultValue);
    }
}
