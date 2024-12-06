# Creating a JSON File

The purpose of this exercise is to train you to create JSON files.

Duration: _10 minutes_

## Description

You are given a hierarchy of animal classes that should be written to JSON.
Please implement a method in the `JsonService` class.
- `public void createAnimalJson(String filePath, List<Animal> animals)` - 
creates a new JSON file under the given `filePath` that contains a list of `animals`.

## Requirements

- `null` values are not printed to JSON files.
- JSON must be pretty printed.

## Examples

```java
List<Cat> animals = new ArrayList<>();
animals.add(Cat.builder().name("Alex").age(1).breed(Cat.Breed.MAINE_COON).build());
animals.add(Cat.builder().name("Fluff").age(4).breed(Cat.Breed.PERSIAN).build());

createAnimalJson("animals.txt", animals)
//[ {
//  "name" : "Alex",
//  "age" : 1,
//  "breed" : "MAINE_COON"
//  }, {
//  "name" : "Fluff",
//  "age" : 4,
//  "breed" : "PERSIAN"
//  } ]
```

```java
List<Cat> animals = new ArrayList<>();
animals.add(Cat.builder().name("Alex").breed(Cat.Breed.MAINE_COON).build());
animals.add(Cat.builder().name("Fluff").age(4).build());

createAnimalJson("animals.txt", animals)
//[ {
//  "name" : "Alex",
//  "breed" : "MAINE_COON"
//  }, {
//  "name" : "Fluff",
//  "age" : 4
//  } ]
```
