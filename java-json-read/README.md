# Read Warehouse JSON

The goal of this task is practicing with reading JSON files
using `ObjectMapper` of Jackson library. 

Duration: _30 minutes_

## Description

You're provided with 2 domain entity classes and 1 service to be implemented.

The main domain class is `Item` which contains information needed for warehouse products.
It contains fields of different types, but the most interesting is `vendor`.
This field is of type `Vendor` which is other domain class containing info about supplier of a product.

And you're provided with `WarehouseReader` interface which is responsible for reading JSON and
convert it to `Collection` of `Item`.
Your task is to implement method `getInstance` which must return an implementation
of `WarehouseReader`.
`WarehouseReader` has one abstract method `readItems(InputStream data)` which must
read the data in JSON array format from provided `InputStream` and return non-null `Collection`
of `Item`. The returned collection must not contain any null element.

## Requirements

* `WarehouseReader#readItems` must always return non-null collection
* `WarehouseReader#getInstance` must not return null value
* `WarehouseReader#readItems` must return collection without nulls
* `WarehouseReader#readItems` must read all the data from `InputStream` in JSON format

## Examples

Input file is:
```json
[
  {
    "id": 7469,
    "name": "Enormous Plastic Plate",
    "supplyDate": "2020-12-26T23:52:15.252969157",
    "price": 360,
    "totalCount": 17700,
    "barCode": "EAN133607340948954",
    "vendor": {
      "id": 9699,
      "name": "Adidas"
    }
  }
]
```

The example of `WarehouseReader` usage:
```java
var reader = WarehouseReader.getInstance();
var path = Path.of(pathToFile);
var data = Files.newInputStream(path);
Collection<Item> items = reader.readItems(data);
assert items != null;     // true
assert items.size() == 1; // true
Item item = item.iterator().next();
assert item.getId() == 7469; // true
// ... all other information must be from JSON
```
