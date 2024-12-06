package com.epam.rd.autocode.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedWorker implements Runnable {

	private final int numberOfIterations;
	private final int pause;
	private final Counter counter;
	private final Lock lock; // use Lock as a reference type for the lock object

	public SynchronizedWorker(int numberOfIterations, int pause, Counter counter) {
		// initialize the fields with the parameters
		this.numberOfIterations = numberOfIterations;
		this.pause = pause;
		this.counter = counter;
		// create a new ReentrantLock instance and assign it to the lock object
		this.lock = new ReentrantLock();
	}

	@Override
	public void run() {
		// loop for the number of iterations
		for (int i = 0; i < numberOfIterations; i++) {
			// acquire the lock before accessing the counter
			lock.lock();
			try {
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
			} finally {
				// release the lock after accessing the counter
				lock.unlock();
			}
		}
	}

}
