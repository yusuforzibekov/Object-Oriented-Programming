package edu.epam.fop.jdbc.metadata;

import edu.epam.fop.jdbc.metadata.model.SchemaDefinition;

public interface SchemaService {

    SchemaDefinition readSchema(String schema);
}
