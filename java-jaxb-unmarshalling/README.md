# Message Audit Service (Unmarshalling)

In this task, you will practice unmarshalling objects from XML.

Duration: _30 minutes_

## Description

You're provided with the XSD - `messages.xsd`, which is located in the `src/main/resources` directory.
Use it to generate JAXB classes into the package `edu.epam.fop.xml.jaxb.model`.

Also, you have `MessageAuditService` with one method `readMessages`, which you must implement.
This method takes the `Path input` parameter, which targets the XML file from which you must read your objects.
This method must return objects as `Stream<Message>`.


## Requirements

* JAXB classes are generated using the `messages.xsd` schema.
* `MessageAuditService#readMessages` is implemented, so it reads all the messages from `input`.
* All tests are passed.

## Examples

Contents of `messages.xml`:
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns2:messages xmlns:ns2="http://www.fop.epam.edu/messages">
    <message id="42" from="Alice" type="SMTP">
        <heading>Greeting</heading>
        <body>Hello, Bob!</body>
        <recipients>
            <recipient name="Bob"/>
        </recipients>
    </message>
</ns2:messages>
```

```java
var service = new MessageAuditService();
var input = Path.of("messages.xml");

service.readMessages().forEach(msg -> prettyPrintMessage(msg)); // prettyPrintMessage is not needed to be implemented
/*
    Output:
    Message[
      id: 42
      from: Alice
      type: SMTP
      heading: Greeting
      body: Hello, Bob!
      to:
        - Bob
    ]
 */
```
