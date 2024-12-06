package edu.epam.fop.json.warehouse;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.epam.fop.json.warehouse.item.Item;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WarehouseReaderTest {
  
  @Test
  void shouldReturnNonNullImplementation() {
    assertNotNull(WarehouseReader.getInstance());
  }
  
  @ParameterizedTest(name = "[{index}] Should read for {0} elements in list")
  @ValueSource(ints = {
      0, 1, 5, 100
  })
  @SuppressWarnings("unchecked")
  void shouldReadData(int amount) {
    var reader = WarehouseReader.getInstance();
    var loader = getClass().getClassLoader();
    var filename = "data-" + amount;
    var json = loader.getResourceAsStream("json/" + filename + ".json");
    Collection<Item> items = reader.readItems(json);
    var expectedIn = loader.getResourceAsStream("expected/"+ filename + ".data");
    try (var in = new ObjectInputStream(expectedIn)) {
      List<Item> expected = (List<Item>) in.readObject();
      Assertions.assertThat(items).containsExactlyInAnyOrderElementsOf(expected);
    }
    catch (IOException | ClassNotFoundException e) {
      Assertions.fail("Can't read expected data", e);
    }
  }
}
