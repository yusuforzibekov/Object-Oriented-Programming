package edu.epam.fop.xml.jaxb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import edu.epam.fop.xml.jaxb.model.Message;
import edu.epam.fop.xml.jaxb.model.Message.Mentions;
import edu.epam.fop.xml.jaxb.model.Message.Recipients;
import edu.epam.fop.xml.jaxb.model.Person;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MessageAuditServiceTest {
  
  private static final Path resources = Path.of("src", "test", "resources");

  private MessageAuditService service;

  @BeforeEach
  void setUp() {
    service = new MessageAuditService();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource
  void test(String name, List<Message> expected) {
    var messageStream = service.readMessages(resources.resolve(name).resolve("message.xml"));
    assertThat(messageStream).isNotNull();
    var actual = messageStream.toList();
    assertThat(actual)
        .usingElementComparator(new MessageComparator())
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  static Stream<Arguments> test() throws Exception {
    return Files.list(resources)
        .map(dir -> arguments(
            dir.getFileName().toFile().getName(),
            readMessages(dir.resolve("expected.obj"))
        ));
  }

  @SuppressWarnings("unchecked")
  private static <T> T readMessages(Path file) {
    try (var in = new ObjectInputStream(Files.newInputStream(file))) {
      return ((T) in.readObject());
    } catch (IOException | ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  private static final class MessageComparator implements Comparator<Message> {

    @Override
    public int compare(Message o1, Message o2) {
      return Comparator.comparing(Message::getId)
          .thenComparing(Message::getHeading)
          .thenComparing(Message::getBody)
          .thenComparing(Message::getFrom)
          .thenComparing(Message::getType)
          .thenComparing(Message::getRecipients, personListComparator(Recipients::getRecipient))
          .thenComparing(Message::getMentions, personListComparator(Mentions::getMention))
          .compare(o1, o2);
    }

    static <T> Comparator<T> personListComparator(Function<T, List<Person>> mapper) {
      return (o1, o2) -> {
        if (o1 == null && o2 == null) {
          return 0;
        }
        if (o1 != null && o2 == null) return 1;
        if (o1 == null) return -1;
        List<Person> l1 = mapper.apply(o1), l2 = mapper.apply(o2);
        var personComparator = Comparator.comparing(Person::getName);
        l1 = l1.stream().sorted(personComparator).toList();
        l2 = l2.stream().sorted(personComparator).toList();
        return listComparator(personComparator).compare(l1, l2);
      };
    }

    static <T> Comparator<List<T>> listComparator(Comparator<T> cmp) {
      return (List<T> l1, List<T> l2) -> {
        if (l1 == null && l2 == null) {
          return 0;
        }
        if (l1 != null && l2 == null) {
          return 1;
        }
        if (l1 == null) {
          return -1;
        }
        if (l1.size() < l2.size()) return -1;
        if (l1.size() > l2.size()) return 1;
        var size = l1.size();
        for (int i = 0; i < size; ++i) {
          var res = cmp.compare(l1.get(i), l2.get(i));
          if (res != 0) return res;
        }
        return 0;
      };
    }
  }

}