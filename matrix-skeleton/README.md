# Matrix

The purpose of this exercise is to practice using a thread pool to parallelize the execution of tasks.  

Duration: _45 minutes_

## Description 

 
In this exercise, you will find the maximum value in a matrix of numbers in two ways:
* By sequential iteration of all elements of the matrix in the main thread
* 	By using a thread pool to parallelize the search for the maximum value in each row of the matrix and then finding the maximum value among the maximum values found for each row  

You will also need to calculate how much time it will take to search for the maximum value to evaluate the performance of each approach.    

The `Matrix` class contains two static final fields: `MIN_VALUE` and `MAX_VALUE`. These fields are used in the static `matrixGenerator()` method to generate elements of the matrix.


Now, please proceed to  the `Matrix` class and implement the following methods:  
* `public static String oneThreadSearch(int[][] matrix, int pause)`  
 Performs a search for the maximum value by iterating through all elements of the matrix in the current thread (i.e. does not create a separate thread). The result is returned as a `String` in the format "max_element search_time".  

* `public static String multipleThreadSearch(int[][] matrix, int pause) `  
 Creates threads to find the maximum values in each row of the matrix, and the threads are executed using a thread pool. Then, in the current thread, the method finds the maximum value among the maximum values of the matrix rows. The result is returned as a `String` in the format "max_element search_time".


### Details

* Use the static `currentTimeMillis()` method of the `System` class to define when the search starts and when it ends. This will allow you to calculate the difference and get the search time in milliseconds.
* Don't forget to use a pause in the `oneThreadSearch()` and `multipleThreadSearch()` methods before each comparison of the matrix elements in the search process. 
* Give the same `pause` value to each method.
* Describe each thread in the `multipleThreadSearch()` method as a `Callable<Integer>` object.
* Create a thread pool of the `ExecutorService` type with the same number of worker threads as the number of rows in the matrix.

### Restrictions
> Please do not remove the contents of the `Matrix` class; only implement the specified methods.

## Example 
The code in the `main()` method of the `Matrix` class is the following:
```java
System.out.println(oneThreadSearch(matrix, 1));
System.out.println(multipleThreadSearch(matrix, 1));
```
The following is a sample output:
```
982 350
982 95
```