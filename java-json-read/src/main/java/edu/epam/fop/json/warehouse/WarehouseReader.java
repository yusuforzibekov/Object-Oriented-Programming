package edu.epam.fop.json.warehouse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.epam.fop.json.warehouse.item.Item;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface WarehouseReader {

  Collection<Item> readItems(InputStream data);

  static WarehouseReader getInstance() {
    // return an anonymous class that implements the WarehouseReader interface
    return new WarehouseReader() {
      @Override
      public Collection<Item> readItems(InputStream data) {
        // create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // create an empty list to store the items
        List<Item> items = new ArrayList<>();
        try {
          // read the JSON array from the input stream and convert it to a list of Item
          // objects
          items = objectMapper.readValue(data,
              objectMapper.getTypeFactory().constructCollectionType(List.class, Item.class));
        } catch (JsonParseException e) {
          // handle JSON parsing exception
          e.printStackTrace();
        } catch (JsonMappingException e) {
          // handle JSON mapping exception
          e.printStackTrace();
        } catch (IOException e) {
          // handle IO exception
          e.printStackTrace();
        }
        // return the list of items as a collection
        return items;
      }
    };
  }
}
