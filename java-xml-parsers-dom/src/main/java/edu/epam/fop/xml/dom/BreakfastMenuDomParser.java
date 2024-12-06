package edu.epam.fop.xml.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BreakfastMenuDomParser {

	public static List<Food> parseBreakfastMenu(String pathToXml) throws Exception {
		// Create a list to store the food objects
		List<Food> foodList = new ArrayList<>();

		// Create a document builder factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// Create a document builder
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Parse the XML file and get the document object
		Document document = builder.parse(new File(pathToXml));

		// Normalize the document
		document.getDocumentElement().normalize();

		// Get the root element
		Element root = document.getDocumentElement();

		// Get the list of food elements
		NodeList foodNodes = root.getElementsByTagName("food");

		// Loop through the food elements
		for (int i = 0; i < foodNodes.getLength(); i++) {
			// Get the current food node
			Node foodNode = foodNodes.item(i);

			// Check if the node is an element node
			if (foodNode.getNodeType() == Node.ELEMENT_NODE) {
				// Cast the node to an element
				Element foodElement = (Element) foodNode;

				// Get the id attribute of the food element
				String id = foodElement.getAttribute("id");

				// Get the name element of the food element
				Element nameElement = (Element) foodElement.getElementsByTagName("name").item(0);

				// Get the text content of the name element and trim the white spaces
				String name = nameElement.getTextContent().replaceAll("\\s+", " ").trim();

				// Get the price element of the food element
				Element priceElement = (Element) foodElement.getElementsByTagName("price").item(0);

				// Get the text content of the price element and trim the white spaces
				String price = priceElement.getTextContent().replaceAll("\\s+", " ").trim();

				// Get the description element of the food element
				Element descriptionElement = (Element) foodElement.getElementsByTagName("description").item(0);

				// Get the text content of the description element and trim the white spaces
				String description = descriptionElement.getTextContent().replaceAll("\\s+", " ").trim();

				// Get the calories element of the food element
				Element caloriesElement = (Element) foodElement.getElementsByTagName("calories").item(0);

				// Get the text content of the calories element and trim the white spaces
				String calories = caloriesElement.getTextContent().replaceAll("\\s+", " ").trim();

				// Create a food object with the extracted values
				Food food = new Food(id, name, price, description, calories);

				// Add the food object to the list
				foodList.add(food);
			}
		}

		// Return the list of food objects
		return foodList;
	}

}