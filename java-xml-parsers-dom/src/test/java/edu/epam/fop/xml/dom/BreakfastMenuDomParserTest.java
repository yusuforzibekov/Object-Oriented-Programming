package edu.epam.fop.xml.dom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

@DisplayNameGeneration(ReplaceCamelCase.class)
class BreakfastMenuDomParserTest {

	@ParameterizedTest
	@CsvFileSource(resources = "/foods-test-data.csv", numLinesToSkip = 1)
	void testDomParser(int index, String expectedId, String expectedName, String expectedPrice,
			String expectedDescription, String expectedCalories) throws Exception {
		java.net.URL xmlUrl = getClass().getClassLoader().getResource("breakfastmenu.xml");
		assertNotNull(xmlUrl, "Test XML resource not found");

		List<Food> foodList = BreakfastMenuDomParser.parseBreakfastMenu(xmlUrl.getFile());
		assertNotNull(foodList, "List object is null");

		Food food = foodList.get(index);
		assertNotNull(food, "Food object is null");
		assertEquals(expectedId, food.getId());
		assertEquals(expectedName, food.getName());
		assertEquals(expectedPrice, food.getPrice());
		assertEquals(expectedDescription.strip(), food.getDescription().strip());
		assertEquals(expectedCalories, food.getCalories());
	}
}