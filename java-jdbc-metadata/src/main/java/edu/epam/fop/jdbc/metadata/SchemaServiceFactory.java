package edu.epam.fop.jdbc.metadata;

import java.sql.Connection;

public class SchemaServiceFactory {

    public SchemaService getSchemaService(Connection connection) {
        return new SchemaServiceImpl(connection);
    }
}
