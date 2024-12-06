package com.epam.autotasks.validator;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class MenuXmlValidatorTest extends AbstractXmlValidatorTest {

    public static String XSD_PATH = "xsd/breakfastmenu.xsd";
    public static String VALID_XML_PATH = "xml/breakfastmenu.xml";
    public static String INVALID_XML_PATH_V1 = "xml/invalid/breakfastmenu-v1.xml";
    public static String INVALID_XML_PATH_V2 = "xml/invalid/breakfastmenu-v2.xml";
    public static String INVALID_XML_PATH_V3 = "xml/invalid/breakfastmenu-v3.xml";
    public static String INVALID_XML_PATH_V4 = "xml/invalid/breakfastmenu-v4.xml";
    public static String INVALID_XML_PATH_V5 = "xml/invalid/breakfastmenu-v5.xml";
    public static String INVALID_XML_PATH_V6 = "xml/invalid/breakfastmenu-v6.xml";

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
