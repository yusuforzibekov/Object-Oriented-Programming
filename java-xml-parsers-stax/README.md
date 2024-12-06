# Handling Food Items in XML for a Breakfast Menu Using StAX

Streaming API for XML (StAX) is one of several XML libraries in Java. The goal of this task is to give you some practice using StAX to parse XML documents.

Duration: 60 minutes

## Description

Implement a Java program that utilizes StAX to read an XML file containing information about various food items and their properties (e.g., id, name, price, description, and calories). The XML file describes a breakfast menu.

## Requirements

- Read an XML file containing food items with different properties. The structure of this XML file must follow the breakfast menu schema.

- Use the `Food` class that consists of the appropriate properties (`id`, `name`, `price`, `description`, `calories`).

- Utilize the StAX parser to process the breakfast menu XML file and create a list of `Food` objects. Use the `BreakfastMenuStaxParser` to implement its static method `parseBreakfastMenu` for creating `Food` objects from the parsed XML data.

- Remove extra white spaces (space, tab, new line, carriage return, etc.) between words when reading food items from the XML file. For example, for `breakfastmenu.xml`, which is shown below, `Food.description` should be `"description1_line1 description1_line2 description1_line3"` and `"description2_line1 description2_line2 description2_line3"`, respectively.

## Examples

The `breakfastmenu.xml` file contains information about various food items and their properties:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<breakfast-menu
    xmlns="http://www.example.org/breakfastmenu"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.example.org/breakfastmenu breakfastmenu.xsd">

    <food id="a1">
        <name>name1</name>
        <price>$5.95</price>
        <description>
            description1_line1
            description1_line2
            description1_line3
        </description>
        <calories>650</calories>
    </food>

    <food id="a2">
        <name>name2</name>
        <price>$7.95</price>
        <description>
            description2_line1
            description2_line2
            description2_line3
        </description>
        <calories>900</calories>
    </food>

</breakfast-menu>
```

The breakfast menu schema:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.w3.org/2001/XMLSchema"
    targetNamespace="http://www.example.org/breakfastmenu"
    xmlns:tns="http://www.example.org/breakfastmenu"
    elementFormDefault="qualified">

    <element name="breakfast-menu">
        <complexType>
            <sequence>
                <element name="food" minOccurs="1" maxOccurs="unbounded">
                    <complexType>
                        <sequence>
                            <element name="name" type="string" />
                            <element name="price" type="string" />
                            <element name="description" type="string" />
                            <element name="calories" type="string" />
                        </sequence>
                        <attribute name="id" type="ID" use="required" />
                    </complexType>
                </element>
            </sequence>
        </complexType>
    </element>

</schema>
```

An example of using `BreakfastMenuStaxParser`:

```java
String pathToXml = "breakfastmenu.xml";
List<Food> foodList = BreakfastMenuStaxParser.parseBreakfastMenu(pathToXml);
for (Food food : foodList) {
    LOGGER.log(System.Logger.Level.INFO, food);
}
```
