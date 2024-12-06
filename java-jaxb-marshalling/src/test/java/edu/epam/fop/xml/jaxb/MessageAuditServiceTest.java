package edu.epam.fop.xml.jaxb;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import edu.epam.fop.xml.jaxb.model.Message;
import edu.epam.fop.xml.jaxb.model.Message.Mentions;
import edu.epam.fop.xml.jaxb.model.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xmlunit.assertj.XmlAssert;
import org.xmlunit.builder.Input;

class MessageAuditServiceTest {

  @TempDir
  Path baseDir;

  private MessageAuditService service;

  @BeforeEach
  void setUp() {
    service = new MessageAuditService();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource
  void test(String name, List<Message> messages, MessageFilter filter, String expectedXML) throws Exception {
    var path = baseDir.resolve(name + ".xml");
    assertDoesNotThrow(() -> service.writeFilteredMessages(path, messages.stream(), filter));
    var resultXML = Files.readString(path, StandardCharsets.UTF_8);
    var expected = Input.fromString(expectedXML).build();
    var actual = Input.fromPath(path).build();
    XmlAssert.assertThat(expected).and(actual).areIdentical();
  }

  static Stream<Arguments> test() throws Exception {
    var testDir = Path.of("src", "test", "resources");
    return Files.list(testDir)
        .map(dir -> {
          try {
            return arguments(
                dir.getFileName().toFile().getName(),
                readObject(List.class, dir.resolve("messages.obj")),
                readObject(MessageFilter.class, dir.resolve("filter.obj")),
                Files.readString(dir.resolve("message.xml"), StandardCharsets.UTF_8)
            );
          } catch (IOException e) {
            throw new IllegalStateException(e);
          }
        });
  }

  @SuppressWarnings("unchecked")
  private static <T> T readObject(Class<T> type, Path file) {
    try (var in = new ObjectInputStream(Files.newInputStream(file))) {
      return ((T) in.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

}

enum MessageFilter implements Predicate<Message> {
  ANY_MESSAGE {
    @Override
    public boolean test(Message message) {
      return true;
    }
  },
  NONE_MESSAGE {
    @Override
    public boolean test(Message message) {
      return false;
    }
  },
  HAS_MENTIONS {
    @Override
    public boolean test(Message message) {
      return Optional.of(message)
          .map(Message::getMentions)
          .map(Mentions::getMention)
          .map(List::size)
          .map(size -> size > 0)
          .orElse(false);
    }
  },
  OF_SMTP_TYPE {
    @Override
    public boolean test(Message message) {
      return message.getType() == MessageType.SMTP;
    }
  },
  OF_SMS_TYPE {
    @Override
    public boolean test(Message message) {
      return message.getType() == MessageType.SMS;
    }
  },
  OF_MMS_TYPE {
    @Override
    public boolean test(Message message) {
      return message.getType() == MessageType.MMS;
    }
  },
  BROADCAST_MESSAGE {
    @Override
    public boolean test(Message message) {
      return message.getRecipients().getRecipient().size() > 1;
    }
  }
}