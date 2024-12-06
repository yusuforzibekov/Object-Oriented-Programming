package com.epam.autotasks;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

public class JsonService {

    @SneakyThrows
    public void createAnimalJson(String filePath, List<Animal> animals) {
        // Create an ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();
        // Enable pretty printing
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // Write the list of animals to the file as JSON
        mapper.writeValue(new File(filePath), animals);
    }
}
