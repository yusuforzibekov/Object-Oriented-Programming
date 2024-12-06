package edu.epam.fop.xml.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class BreakfastMenuHandler extends DefaultHandler {
	private final List<Food> foodList = new ArrayList<>();

	// Declare the fields to store the element values
	private String id;
	private String name;
	private String price;
	private String description;
	private String calories;

	// Declare a StringBuilder to store the text content
	private final StringBuilder currentValue = new StringBuilder();

	public List<Food> getFoodList() {
		return foodList;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// Reset the StringBuilder when a new element starts
		currentValue.setLength(0);

		// If the element is food, get the id attribute value
		if (qName.equalsIgnoreCase("food")) {
			id = attributes.getValue("id");
		}

		// If the element is salary, get the currency attribute value
		if (qName.equalsIgnoreCase("salary")) {
			price = attributes.getValue("currency");
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// If the element is name, get the text content and trim the white spaces
		String trimmed = currentValue.toString().replaceAll("\\s+", " ").trim();
		if (qName.equalsIgnoreCase("name")) {
			name = trimmed;
		}

		// If the element is price, append the text content and trim the white spaces
		if (qName.equalsIgnoreCase("price")) {
			price = trimmed;
		}

		// If the element is description, get the text content and trim the white spaces
		if (qName.equalsIgnoreCase("description")) {
			description = trimmed;
		}

		// If the element is calories, get the text content and trim the white spaces
		if (qName.equalsIgnoreCase("calories")) {
			calories = trimmed;
		}

		// If the element is food, create a Food object and add it to the list
		if (qName.equalsIgnoreCase("food")) {
			Food food = new Food(id, name, price, description, calories);
			foodList.add(food);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// Append the character array segments to the StringBuilder
		currentValue.append(ch, start, length);
	}
}
