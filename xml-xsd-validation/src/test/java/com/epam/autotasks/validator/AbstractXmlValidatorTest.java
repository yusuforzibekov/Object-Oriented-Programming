package com.epam.autotasks.validator;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public abstract class AbstractXmlValidatorTest {

    public Validator validator;

    protected abstract String getXsdPath();

    protected abstract String getValidXmlPath();

    @BeforeEach
    void initValidator() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source schemaFile = new StreamSource(getFile(getXsdPath()));
        Schema schema = factory.newSchema(schemaFile);
        validator = schema.newValidator();
    }

    @SneakyThrows
    @Test
    void shouldSuccessfullyValidateXml() {
        Assertions.assertDoesNotThrow(() -> validator.validate(new StreamSource(getFile(getValidXmlPath()))));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource(value = "provideFilePath")
    void shouldNotValidateXml(String path) {
        Assertions.assertThrows(SAXException.class, () -> validator.validate(new StreamSource(getFile(path))));
    }

    @SneakyThrows
    private File getFile(String location) {
        return new File(Thread.currentThread().getContextClassLoader().getResource(location).toURI());
    }
}
