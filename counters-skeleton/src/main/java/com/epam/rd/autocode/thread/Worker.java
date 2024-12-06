package com.epam.rd.autocode.thread;

public class Worker implements Runnable {

	private final int numberOfIterations;
	private final int pause;
	private final Counter counter;

	public Worker(int numberOfIterations, int pause, Counter counter) {
		// initialize the fields with the parameters
		this.numberOfIterations = numberOfIterations;
		this.pause = pause;
		this.counter = counter;
	}

	@Override
	public void run() {
		// loop for the number of iterations
		for (int i = 0; i < numberOfIterations; i++) {
			// compare the values of the counter and print the result
			boolean equal = counter.getValue1() == counter.getValue2();
			System.out.println(equal + " " + counter.getValue1() + " " + counter.getValue2());
			// increment the value1 of the counter
			counter.incrementValue1();
			// pause the thread for the specified time
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// increment the value2 of the counter
			counter.incrementValue2();
		}
	}

}
