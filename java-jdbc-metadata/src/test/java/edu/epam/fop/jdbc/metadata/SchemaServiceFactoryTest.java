package edu.epam.fop.jdbc.metadata;

import edu.epam.fop.jdbc.metadata.assertions.SchemaDefinitionAssert;
import edu.epam.fop.jdbc.metadata.model.SchemaDefinition;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Types;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

class SchemaServiceFactoryTest {

    private void runTest(String catalogName, String schemaName, Map<String, Set<String>> dbTables,
                         Consumer<SchemaDefinitionAssert> schemaAssert) throws Exception {
        try (DatabaseConfig db = new DatabaseConfig(catalogName)) {
            //// Arrange
            db.initializeSchema(schemaName, dbTables);
            Connection connection = db.getConnection();

            //// Act
            SchemaService service = new SchemaServiceFactory().getSchemaService(connection);
            SchemaDefinition schema = service.readSchema(schemaName);

            //// Assert
            schemaAssert.accept(new SchemaDefinitionAssert(schema)
                    .isNotNull()
                    .hasCatalog(catalogName)
                    .hasName(schemaName)
            );
        }
    }

    @Test
    void shouldWorkForSimpleCase() throws Exception {
        runTest(
                "users-catalog",
                "users",
                Map.of(
                        "user", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50)",
                                "SURNAME VARCHAR(50)"
                        )
                ),
                schema -> schema.hasOnlyTables(Map.of(
                        "user", table -> table.hasName("user").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .hasSize(64)
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .hasSize(50)
                                        .isNullable()
                                        .doesNotHaveDefaultValue(),
                                "SURNAME", column -> column
                                        .hasName("SURNAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .hasSize(50)
                                        .isNullable()
                                        .doesNotHaveDefaultValue()
                        ))
                ))
        );
    }

    @Test
    void shouldWorkWithMultipleTables() throws Exception {
        runTest(
                "countries",
                "countries",
                Map.of(
                        "country", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50)"
                        ),
                        "city", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50)"
                        ),
                        "street", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50)"
                        )
                ),
                schema -> schema.hasOnlyTables(Map.of(
                        "country", table -> table.hasName("country").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .isNullable()
                                        .doesNotHaveDefaultValue()
                        )),
                        "city", table -> table.hasName("city").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .isNullable()
                                        .doesNotHaveDefaultValue()
                        )),
                        "street", table -> table.hasName("street").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue()
                                        .hasSize(64),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .isNullable()
                                        .doesNotHaveDefaultValue()
                                        .hasSize(50)
                        ))
                ))
        );
    }

    @Test
    void shouldDistinguishNullableProperty() throws Exception {
        runTest(
                "characters",
                "characters",
                Map.of(
                        "character", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(100) NOT NULL",
                                "ALIAS VARCHAR(50)"
                        )
                ),
                schema -> schema.hasOnlyTables(Map.of(
                        "character", table -> table.hasName("character").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .hasSize(64)
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .hasSize(100)
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "ALIAS", column -> column
                                        .hasName("ALIAS")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasSize(50)
                                        .hasTypeName("CHARACTER VARYING")
                                        .isNullable()
                                        .doesNotHaveDefaultValue()
                        ))
                ))
        );
    }

    @Test
    void shouldDistinguishDefaultValues() throws Exception {
        runTest(
                "characters",
                "characters",
                Map.of(
                        "character", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50) NOT NULL",
                                "HEALTH INTEGER NOT NULL DEFAULT 10"
                        )
                ),
                schema -> schema.hasOnlyTables(Map.of(
                        "character", table -> table.hasName("character").hasOnlyColumns(Map.of(
                                "ID", column -> column
                                        .hasName("ID")
                                        .hasJdbcType(Types.BIGINT)
                                        .hasTypeName("BIGINT")
                                        .hasSize(64)
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "NAME", column -> column
                                        .hasName("NAME")
                                        .hasJdbcType(Types.VARCHAR)
                                        .hasTypeName("CHARACTER VARYING")
                                        .hasSize(50)
                                        .isNotNullable()
                                        .doesNotHaveDefaultValue(),
                                "HEALTH", column -> column
                                        .hasName("HEALTH")
                                        .hasJdbcType(Types.INTEGER)
                                        .hasTypeName("INTEGER")
                                        .hasSize(32)
                                        .isNotNullable()
                                        .hasDefaultValue("10")
                        ))
                ))
        );
    }

    @Test
    void shouldSupportVarietyOfColumnTypes() throws Exception {
        runTest(
                "jdbc-metadata",
                "metadata",
                Map.of(
                        "my_data", Set.of(
                                "ID BIGINT PRIMARY KEY",
                                "NAME VARCHAR(50) NOT NULL",
                                "AGE INTEGER NOT NULL",
                                "SOCIAL_RATING LONG NOT NULL DEFAULT 0",
                                "PERSONAL_ID UUID NOT NULL",
                                "REGISTRATION_TIMESTAMP TIMESTAMP NOT NULL DEFAULT NOW()",

                                "BIRTHDATE DATE",
                                "IS_PREMIUM BOOLEAN",
                                "TOTAL_CASH DECFLOAT(5) DEFAULT 0",
                                "BONUS_EXCHANGE_RATE FLOAT(30)"
                        )
                ),
                schema -> schema.hasOnlyTables(Map.of("my_data", table -> table.hasName("my_data").hasOnlyColumns(Map.of(
                        "ID", column -> column
                                .hasName("ID")
                                .hasJdbcType(Types.BIGINT)
                                .hasTypeName("BIGINT")
                                .hasSize(64)
                                .isNotNullable()
                                .doesNotHaveDefaultValue(),
                        "NAME", column -> column
                                .hasName("NAME")
                                .hasJdbcType(Types.VARCHAR)
                                .hasTypeName("CHARACTER VARYING")
                                .hasSize(50)
                                .isNotNullable()
                                .doesNotHaveDefaultValue(),
                        "AGE", column -> column
                                .hasName("AGE")
                                .hasJdbcType(Types.INTEGER)
                                .hasSize(32)
                                .hasTypeName("INTEGER")
                                .isNotNullable()
                                .doesNotHaveDefaultValue(),
                        "SOCIAL_RATING", column -> column
                                .hasName("SOCIAL_RATING")
                                .hasJdbcType(Types.BIGINT)
                                .hasTypeName("BIGINT")
                                .hasSize(64)
                                .isNotNullable()
                                .hasDefaultValue("0"),
                        "PERSONAL_ID", column -> column
                                .hasName("PERSONAL_ID")
                                .hasJdbcType(Types.BINARY)
                                .hasTypeName("UUID")
                                .hasSize(16)
                                .isNotNullable()
                                .doesNotHaveDefaultValue(),
                        "REGISTRATION_TIMESTAMP", column -> column
                                .hasName("REGISTRATION_TIMESTAMP")
                                .hasJdbcType(Types.TIMESTAMP)
                                .hasTypeName("TIMESTAMP")
                                .hasSize(26)
                                .isNotNullable()
                                .hasDefaultValue("LOCALTIMESTAMP"),
                        "BIRTHDATE", column -> column
                                .hasName("BIRTHDATE")
                                .hasJdbcType(Types.DATE)
                                .hasTypeName("DATE")
                                .hasSize(10)
                                .isNullable()
                                .doesNotHaveDefaultValue(),
                        "IS_PREMIUM", column -> column
                                .hasName("IS_PREMIUM")
                                .hasJdbcType(Types.BOOLEAN)
                                .hasTypeName("BOOLEAN")
                                .hasSize(1)
                                .isNullable()
                                .doesNotHaveDefaultValue(),
                        "TOTAL_CASH", column -> column
                                .hasName("TOTAL_CASH")
                                .hasJdbcType(Types.NUMERIC)
                                .hasTypeName("DECFLOAT")
                                .hasSize(5)
                                .isNullable()
                                .hasDefaultValue("0"),
                        "BONUS_EXCHANGE_RATE", column -> column
                                .hasName("BONUS_EXCHANGE_RATE")
                                .hasJdbcType(Types.FLOAT)
                                .hasTypeName("DOUBLE PRECISION")
                                .hasSize(53)
                                .isNullable()
                                .doesNotHaveDefaultValue()
                ))))
        );
    }
}