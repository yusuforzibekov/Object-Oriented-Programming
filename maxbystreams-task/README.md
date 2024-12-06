# Maximum in the matrix (streams)

The purpose of this exercise is to give you some practice using the fork/join framework and the Stream API.

Duration: *45 minutes*.

## Description

In this exercise, you will find the maximum value in a matrix of numbers in two ways:
-	Using a sequential stream and one thread 
-	Using parallel streams and the fork/join framework  to parallelize the search for the maximum value in each row of the matrix and then finding the maximum value among the maximum values found for each row.
You will also need to calculate how much time it will take to search for the maximum value to evaluate the performance of each approach.


Please proceed to `MaxOfMatrix` class and implement the following methods:
- `public static String oneThreadSearch(int[][] matrix, int pause)`   
This method searches for the maximum value using a sequential stream. The result is returned as a `String` in the format "max_element search_time".

- `public static String multipleThreadSearch(int[][] matrix, int pause)`  
This method searches for the maximum value using parallel streams for find the maximum values in each row of the matrix. Then, it finds the maximum value among the maximum values of the rows in the matrix. The result is returned as a `String` in the format "max_element search_time".


### Details

- Use the static `currentTimeMillis()` method of the `System` class to define when the search starts and ends. This will allow you to calculate the difference and get the search time in milliseconds.
- Don't forget to use a pause in the `oneThreadSearch()` and `multipleThreadSearch()` methods before each comparison of the matrix elements in the search process.
- Give the same `pause` value to each method.
- Each of the methods can contain only two statements. The first statement is the declaration of a variable to store the execution start time. The second is the `return` statement, which contains all required calculations.

>_Note_: You can use the Main class from the   
  `src/test/java/com/epam/rd/autocode/threads/maxbystreams/` folder to check your solution.

## Restrictions

Please do not remove the contents of the `MaxOfMatrix` class; only implement the methods specified.
You may not add any content in the `MaxOfMatrix` class except the body of the `oneThreadSearch()` and `multipleThreadSearch()` methods.


## Example
The code in the `main()` method of the `Main` class is the following:
```java
System.out.println(oneThreadSearch(matrix, 1));
System.out.println(multipleThreadSearch(matrix, 1));
```

The following is a sample output:
```
993 357
993 100
```
