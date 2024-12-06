package edu.epam.fop.xml.sax;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayNameGeneration(ReplaceCamelCase.class)
class BreakfastMenuHandlerTest {

	@ParameterizedTest
	@CsvFileSource(resources = "/foods-test-data.csv", numLinesToSkip = 1)
	void testSaxHandler(int index, String expectedId, String expectedName, String expectedPrice,
			String expectedDescription, String expectedCalories) throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("/breakfastmenu.xml");
		assertNotNull(inputStream, "Test XML resource not found");

		BreakfastMenuHandler handler = new BreakfastMenuHandler();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		XMLReader xmlReader = spf.newSAXParser().getXMLReader();
		xmlReader.setContentHandler(handler);
		xmlReader.parse(new InputSource(inputStream));

		List<Food> foodList = handler.getFoodList();

		Food food = foodList.get(index);
		assertNotNull(food, "Food object is null");
		assertEquals(expectedId, food.getId());
		assertEquals(expectedName, food.getName());
		assertEquals(expectedPrice, food.getPrice());
		assertEquals(expectedDescription.strip(), food.getDescription().strip());
		assertEquals(expectedCalories, food.getCalories());
	}
}