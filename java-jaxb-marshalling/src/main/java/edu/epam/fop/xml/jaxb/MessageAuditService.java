package edu.epam.fop.xml.jaxb;

import edu.epam.fop.xml.jaxb.model.Message;
import edu.epam.fop.xml.jaxb.model.Messages;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MessageAuditService {

  public void writeFilteredMessages(Path output, Stream<Message> messages, Predicate<Message> filter) {
    try {
      // Create a JAXBContext for the generated package
      JAXBContext context = JAXBContext.newInstance("edu.epam.fop.xml.jaxb.model");

      // Create a Marshaller from the context
      Marshaller marshaller = context.createMarshaller();

      // Set some properties to format the XML output
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

      // Create a Messages object to hold the filtered messages
      Messages messagesObject = new Messages();

      // Get the list of messages from the messagesObject
      List<Message> messageList = messagesObject.getMessage();

      // Add the filtered messages to the list
      messages.filter(filter).forEach(messageList::add);

      // Write the object to the output file
      Files.createDirectories(output.getParent());
      marshaller.marshal(messagesObject, output.toFile());
    } catch (JAXBException | IOException e) {
      // Handle the exceptions
      e.printStackTrace();
    }
  }
}
