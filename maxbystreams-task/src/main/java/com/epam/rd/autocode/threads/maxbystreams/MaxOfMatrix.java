package com.epam.rd.autocode.threads.maxbystreams;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class MaxOfMatrix {

    /**
     * Pauses current thread or{@code pause} millis
     *
     * @param pause time in millis
     */
    private static void pause(int pause) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String oneThreadSearch(int[][] matrix, int pause) {
        long before = System.currentTimeMillis();
        // Use a sequential stream to flatten the matrix and find the max element
        return Arrays.stream(matrix) // create a sequential stream of rows
                .flatMapToInt(IntStream::of) // flatten each row into an IntStream of elements
                .reduce(Integer.MIN_VALUE, (a, b) -> { // reduce the stream to find the maximum element
                    pause(pause); // pause before each comparison
                    return Math.max(a, b); // compare and return the larger value
                }) + " " + (System.currentTimeMillis() - before); // append the search time in milliseconds
    }

    public static String multipleThreadSearch(int[][] matrix, int pause) {
        long before = System.currentTimeMillis();
        // Use a parallel stream to find the max element in each row
        return ForkJoinPool.commonPool().submit(() -> // submit a task to the common pool
                Arrays.stream(matrix) // create a stream of int[] from the matrix
                        .parallel() // make the stream parallel
                        .mapToInt(row -> Arrays.stream(row) // create a stream of int from each row
                                .peek(e -> pause(pause)) // pause before each comparison
                                .max() // find the maximum value in each row
                                .orElse(Integer.MIN_VALUE)) // return the maximum value or a default value if the row is empty
                        .max() // find the maximum value among the maximum values of the rows
                        .stream() // convert the OptionalInt to a stream of one or zero elements
                        .mapToObj(max -> max + " " + (System.currentTimeMillis() - before)) // map the maximum value to a string with the search time
                        .findFirst() // collect the result as an Optional
                        .orElse("No max found") // return the string or a default value if the stream is empty
        ).join(); // wait for the task to complete and return the result
    }
}
