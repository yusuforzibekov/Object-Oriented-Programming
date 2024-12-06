package com.epam.rd.autocode.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Matrix {

	private static final int MIN_VALUE = 0;

	private static final int MAX_VALUE = 1000;

	public static int[][] matrixGenerator(int m, int n) {
		int[][] res = new int[m][n];
		Random r = new Random();
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				res[i][j] = r.nextInt(MAX_VALUE - MIN_VALUE) + MIN_VALUE;
			}
		}
		return res;
	}

	public static String oneThreadSearch(int[][] ar, int pause) throws InterruptedException {
		// initialize the maximum element and the search time
		int max = ar[0][0];
		long time = System.currentTimeMillis();

		// iterate through all elements of the matrix
		for (int[] ints : ar) {
			for (int anInt : ints) {
				// pause before each comparison
				Thread.sleep(pause);
				// update the maximum element if needed
				if (anInt > max) {
					max = anInt;
				}
			}
		}

		// calculate the search time in milliseconds
		time = System.currentTimeMillis() - time;

		// return the result as a string
		return max + " " + time;
	}

	public static String multipleThreadSearch(int[][] ar, int pause) throws InterruptedException, ExecutionException {
		// initialize the maximum element and the search time
		int max = ar[0][0];
		long time = System.currentTimeMillis();

		// create a list of callable tasks to find the maximum value in each row
		List<Callable<Integer>> tasks = getCallables(ar, pause);

		// create a thread pool with the same number of threads as the number of rows
		ExecutorService executor = Executors.newFixedThreadPool(ar.length);

		// execute the tasks and get the results
		List<Future<Integer>> results = executor.invokeAll(tasks);

		// iterate through the results and find the maximum value among them
		for (Future<Integer> result : results) {
			// get the maximum value for the row
			int rowMax = result.get();
			// update the maximum element if needed
			if (rowMax > max) {
				max = rowMax;
			}
		}

		// shutdown the executor
		executor.shutdown();

		// calculate the search time in milliseconds
		time = System.currentTimeMillis() - time;

		// return the result as a string
		return max + " " + time;
	}

	private static List<Callable<Integer>> getCallables(int[][] ar, int pause) {
		List<Callable<Integer>> tasks = new ArrayList<>();
		for (int i = 0; i < ar.length; i++) {
			// create a new task for each row
			int finalI = i;
			Callable<Integer> task = new Callable<>() {
				// store the reference to the row
				private final int[] row = ar[finalI];

				@Override
				public Integer call() throws Exception {
					// initialize the maximum value for the row
					int max = row[0];
					// iterate through the row elements
					for (int k : row) {
						// pause before each comparison
						Thread.sleep(pause);
						// update the maximum value if needed
						if (k > max) {
							max = k;
						}
					}
					// return the maximum value for the row
					return max;
				}
			};
			// add the task to the list
			tasks.add(task);
		}
		return tasks;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int[][] ar = matrixGenerator(4, 50);

		System.out.println(oneThreadSearch(ar, 1));
		System.out.println(multipleThreadSearch(ar, 1));
	}

}
