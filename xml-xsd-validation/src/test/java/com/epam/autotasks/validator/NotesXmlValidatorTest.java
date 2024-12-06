package com.epam.autotasks.validator;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class NotesXmlValidatorTest extends AbstractXmlValidatorTest {

    public static String XSD_PATH = "xsd/notes.xsd";
    public static String VALID_XML_PATH = "xml/notes.xml";
    public static String INVALID_XML_PATH_V1 = "xml/invalid/notes-v1.xml";
    public static String INVALID_XML_PATH_V2 = "xml/invalid/notes-v2.xml";
    public static String INVALID_XML_PATH_V3 = "xml/invalid/notes-v3.xml";
    public static String INVALID_XML_PATH_V4 = "xml/invalid/notes-v4.xml";
    public static String INVALID_XML_PATH_V5 = "xml/invalid/notes-v5.xml";
    public static String INVALID_XML_PATH_V6 = "xml/invalid/notes-v6.xml";

    @Override
    protected String getXsdPath() {
        return XSD_PATH;
    }

    @Override
    protected String getValidXmlPath() {
        return VALID_XML_PATH;
    }

    private static Stream<Arguments> provideFilePath() {
        return Stream.of(Arguments.of(INVALID_XML_PATH_V1),
                Arguments.of(INVALID_XML_PATH_V2),
                Arguments.of(INVALID_XML_PATH_V3),
                Arguments.of(INVALID_XML_PATH_V4),
                Arguments.of(INVALID_XML_PATH_V5),
                Arguments.of(INVALID_XML_PATH_V6));
    }
}
