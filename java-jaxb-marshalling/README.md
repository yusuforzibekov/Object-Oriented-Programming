# Message Audit Service (Marshalling)

In this task, you will practice marshalling objects into XML.

Duration: _30 minutes_

## Description

You're provided with the XSD - `messages.xsd`, which is located in the `src/main/resources` directory.
Use it to generate JAXB classes into the package `edu.epam.fop.xml.jaxb.model`.

Also, you have `MessageAuditService` with one method `writeFilteredMessages`, which you must implement.
This method takes `Stream<Message>` (the class which must be generated from XSD),
selecting only the messages needed using `Predicate<Filter>`
and writing them as XML into the file specified by `output`.
The resulting XML must be formatted using the JAXB marshaller configuration.

## Requirements

* JAXB classes are generated using the `messages.xsd` schema.
* `MessageAuditService#writeFilteredMessages` is implemented, so it writes filtered `messages` using `filter` parameter into the specified `output`
  (all of them are parameters).
* All tests are passed

## Examples

```java
var service = new MessageAuditService();
var messages = Stream.of(
    createMessage() // Creates a message from Alice to Bob of the type SMTP with the heading Greetings and the body "Hello, Bob!".
);
var output = Path.of("output.xml");

service.writeFilteredMessages(output, messages, msg -> true);

System.out.println(Files.readString(output, StandardCharsets.UTF_8));
```

Result:
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
