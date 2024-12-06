package com.epam.autotasks;

import com.epam.autotasks.JsonService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import com.epam.autotasks.Animal;
import com.epam.autotasks.Cat;
import com.epam.autotasks.Dog;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JsonServiceTest {

    public final static String FILE1 = "src/test/resources/animals1.txt";
    public final static String EXPECTED1 = "src/test/resources/expected/animals1.txt";
    public final static String FILE2 = "src/test/resources/animals2.txt";
    public final static String EXPECTED2 = "src/test/resources/expected/animals2.txt";
    public final static String FILE3 = "src/test/resources/animals3.txt";
    public final static String EXPECTED3 = "src/test/resources/expected/animals3.txt";

    public JsonService jsonService = new JsonService();

    @Test
    @SneakyThrows
    void shouldSuccessfullyCreateJsonFileV1() {
        List<Animal> animals = getAnimalsV1();
        jsonService.createAnimalJson(FILE1, animals);

        JSONAssert.assertEquals(Files.readString(Path.of(EXPECTED1)), Files.readString(Path.of(FILE1)), true);
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyCreateJsonFileV2() {
        List<Animal> animals = getAnimalsV2();
        jsonService.createAnimalJson(FILE2, animals);

        JSONAssert.assertEquals(Files.readString(Path.of(EXPECTED2)), Files.readString(Path.of(FILE2)), true);
    }

    @Test
    @SneakyThrows
    void shouldSuccessfullyCreateEmptyJsonFile() {
        jsonService.createAnimalJson(FILE3, new ArrayList<>());

        JSONAssert.assertEquals(Files.readString(Path.of(EXPECTED3)), Files.readString(Path.of(FILE3)), true);
    }

    private List<Animal> getAnimalsV1() {
        Cat vasya = Cat.builder()
                .name("Vasya")
                .age(2)
                .breed(Cat.Breed.MAINE_COON)
                .build();

        Dog kesha = Dog.builder()
                .name("Kesha")
                .age(5)
                .breed(Dog.Breed.BULLDOG)
                .build();

        Cat anatoliy = Cat.builder()
                .name("Anatoliy")
                .age(10)
                .breed(Cat.Breed.BRITISH)
                .build();

        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(vasya);
        animals.add(kesha);
        animals.add(anatoliy);
        return animals;
    }

    private List<Animal> getAnimalsV2() {
        Animal bob = Cat.builder()
                .name("Bob")
                .breed(Cat.Breed.SIBERIAN)
                .build();

        Animal petya = Dog.builder()
                .name("Petya")
                .age(2)
                .build();

        Animal jack = Dog.builder()
                .name("Jack")
                .age(8)
                .breed(Dog.Breed.MALAMUTE)
                .build();

        Animal jackie = Dog.builder()
                .name("Jackie")
                .age(15)
                .breed(Dog.Breed.HUSKY)
                .build();

        ArrayList<Animal> animals = new ArrayList<>();
        animals.add(bob);
        animals.add(petya);
        animals.add(jack);
        animals.add(jackie);
        return animals;
    }
}
