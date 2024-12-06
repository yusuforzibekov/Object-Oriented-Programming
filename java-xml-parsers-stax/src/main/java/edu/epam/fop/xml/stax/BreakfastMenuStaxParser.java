package edu.epam.fop.xml.stax;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class BreakfastMenuStaxParser {

	public static List<Food> parseBreakfastMenu(String pathToXml) throws XMLStreamException, IOException {
		// Create a list to store the food objects
		List<Food> foodList = new ArrayList<>();

		// Create an XML input factory
		XMLInputFactory factory = XMLInputFactory.newInstance();

		// Create an XML stream reader
		XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(pathToXml));

		// Declare the fields to store the element values
		String id = null;
		String name = null;
		String price = null;
		String description = null;
		String calories = null;

		// Declare a String variable to store the local name of the previous start
		// element
		String previousElement = null;

		// Loop through the stream of events
		while (reader.hasNext()) {
			// Get the next event and its type
			int eventType = reader.next();

			// Switch on the event type
			switch (eventType) {
				// If the event is a start element
				case XMLStreamConstants.START_ELEMENT:
					// Get the local name of the element and assign it to the previousElement
					// variable
					previousElement = reader.getLocalName();

					// If the element is food, get the id attribute value
					if (previousElement.equalsIgnoreCase("food")) {
						id = reader.getAttributeValue(null, "id");
					}
					break;

				// If the event is a character
				case XMLStreamConstants.CHARACTERS:
					// Get the text content and trim the white spaces
					String text = reader.getText().replaceAll("\\s+", " ").trim();

					// If the text is not empty
					if (!text.isEmpty()) {
						// Use the previousElement variable to compare the element names

						// If the previous element is name, assign the text to name
						if (previousElement.equalsIgnoreCase("name")) {
							name = text;
						}

						// If the previous element is price, assign the text to price
						if (previousElement.equalsIgnoreCase("price")) {
							price = text;
						}

						// If the previous element is description, assign the text to description
						if (previousElement.equalsIgnoreCase("description")) {
							description = text;
						}

						// If the previous element is calories, assign the text to calories
						if (previousElement.equalsIgnoreCase("calories")) {
							calories = text;
						}
					}
					break;

				// If the event is an end element
				case XMLStreamConstants.END_ELEMENT:
					// Get the local name of the element and assign it to the previousElement
					// variable
					previousElement = reader.getLocalName();

					// If the element is food, create a food object and add it to the list
					if (previousElement.equalsIgnoreCase("food")) {
						Food food = new Food(id, name, price, description, calories);
						foodList.add(food);
					}
					break;
			}
		}

		// Return the list of food objects
		return foodList;
	}
}