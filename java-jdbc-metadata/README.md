# Database Schema Service

The purpose of this task is to practice using JDBC metadata.

Duration: _45 minutes_

## Description

You are provided with three model classes, one service interface, and one factory class.

In the `model` package you will find three records:

* `ColumnDefinition` holds the definition of a column.
* `TableDefinition` holds the definition of a table (i.e., it contains information about the columns in a table).
* `SchemaDefinition` holds the definition of a schema (i.e., it contains information about the tables in a schema).

`SchemaService` has the method `readSchema(String)`, which returns an instance of `SchemaDefinition`.
And `SchemaServiceFactory` must return your implementation of `SchemaService`.


## Requirements

1. `SchemaServiceFactory` must not return `null`.
2. `SchemaServiceFactory` must return an instance of `SchemaService`.
3. `SchemaService.readSchema` must not return `null`.
4. `tables` of `SchemaDefinition` must not be `null`.
5. `columns` of `TableDefinition` must not be `null`.
6. `SchemaService.readSchema` must return the full description of the specified schema.

## Examples

If a schema is created in the catalog `jdbc-metadata` using the following script:
```sql
CREATE SCHEMA my_schema;

CREATE TABLE city (
    ID BIGINT PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL
);

CREATE TABLE company (
    ID BIGINT PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    CODE INTEGER,
    CAPITAL BIGINT NOT NULL DEFAULT 10000
);
```

Then your code might look like the following:
```java
SchemaServiceFactory factory = new SchemaServiceFactory();
        SchemaService service = factory.getSchemaService(connection);
        SchemaDefinition schema = service.readSchema("my_schema");
        assert schema != null;
        assert schema.catalog().equals("jdbc-metadata");
        assert schema.name().equals("my_schema");

        Map<String, TableDefinition> tables = schema.tables();
        assert tables.size() == 2;

        TableDefinition city = tables.get("city");
        assert city != null;
        assert city.name().equals("city");

        Map<String, ColumnDefinition> cityColumns = city.columns();
        assert cityColumns != null;
        assert cityColumns.size() == 2;

        ColumnDefinition cityId = cityColumns.get("ID");
        assert cityId != null;
        assert cityId.name().equals("ID")
        assert cityId.jdbcType() == java.sql.Types.BIGINT;
        assert cityId.typeName().equals("BIGINT");
        assert cityId.nullable() == false;
        assert cityId.defaultValue() == null;
        assert cityId.size() == 64;

        ColumnDefinition cityName = cityColumns.get("NAME");
        assert cityName != null;
        assert cityName.name().equals("NAME");
        assert cityName.jdbcType() == java.sql.Types.VARCHAR;
        assert cityName.typeName().equals("CHARACTER VARYING");
        assert cityName.nullable() == false;
        assert cityName.defaultValue() == null;
        assert cityName.size() == 50;

        TableDefinition company = tables.get("company");
        assert company != null;
        assert company.name().equals("company");

        Map<String, ColumnDefinition> companyColumns = company.columns();
        assert companyColumns != null;
        assert companyColumns.size() == 4;

        ColumnDefinition companyId = companyColumns.get("ID");
        assert companyId != null;
        assert companyId.name().equals("ID")
        assert companyId.jdbcType() == java.sql.Types.BIGINT;
        assert companyId.typeName().equals("BIGINT");
        assert companyId.nullable() == false;
        assert companyId.defaultValue() == null;
        assert companyId.size() == 64;

        ColumnDefinition companyName = companyColumns.get("NAME")
        assert companyName != null;
        assert companyName.name().equals("NAME");
        assert companyName.jdbcType() == java.sql.Types.VARCHAR;
        assert companyName.typeName().equals("CHARACTER VARYING");
        assert companyName.nullable() == false;
        assert companyName.defaultValue() == null;
        assert companyName.size() == 100;

        ColumnDefinition companyCode = companyColumns.get("CODE")
        assert companyCode != null;
        assert companyCode.name().equals("CODE");
        assert companyCode.jdbcType() == java.sql.Types.INTEGER;
        assert companyCode.typeName().equals("INTEGER");
        assert companyCode.nullable() == true;
        assert companyCode.defaultValue() == null;
        assert companyCode.size() == 32;

        ColumnDefinition companyCapital = companyColumns.get("CAPITAL")
        assert companyCapital != null;
        assert companyCapital.name().equals("CAPITAL");
        assert companyCapital.jdbcType() == java.sql.Types.BIGINT;
        assert companyCapital.typeName().equals("BIGINT");
        assert companyCapital.nullable() == false;
        assert companyCapital.defaultValue() == "10000";
        assert companyCapital.size() == 64;
```
