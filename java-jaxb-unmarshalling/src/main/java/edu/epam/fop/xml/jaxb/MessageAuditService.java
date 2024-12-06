package edu.epam.fop.xml.jaxb;

import edu.epam.fop.xml.jaxb.model.Message;
import edu.epam.fop.xml.jaxb.model.Messages;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class MessageAuditService {

  public Stream<Message> readMessages(Path input) {
    try {
      // Create a JAXB context for the Messages class
      JAXBContext context = JAXBContext.newInstance(Messages.class);
      // Create an unmarshaller to read XML
      Unmarshaller unmarshaller = context.createUnmarshaller();
      // Unmarshal the XML file into a Messages object
      Messages messages = (Messages) unmarshaller.unmarshal(input.toFile());
      // Convert the list of messages into a stream and return it
      return messages.getMessage().stream();
    } catch (JAXBException e) {
      // Handle the exception
      e.printStackTrace();
      return Stream.empty();
    }
  }
}
