package com.epam.rd.autotask.thread;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Runs threads, each of which prints its own message 
 * with a given time interval while the thread is not interrupted
 */
public class Spam {
    // fields for storing an array of messages and intervals for sending them, as well as an array of threads
    private String[] messages;
    private long[] intervals;
    private Thread[] threads;

    public Spam(String[] messages, long[] intervals) {
        // check if messages or time intervals are nulls, or their length is not less than or equal to 2
        if (messages == null || intervals == null || messages.length < 2 || intervals.length < 2) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        // check if messages and time intervals have the same length
        if (messages.length != intervals.length) {
            throw new IllegalArgumentException("Messages and intervals must have the same length");
        }
        // check if time intervals are non-negative and messages are not empty
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i] < 0) {
                throw new IllegalArgumentException("Intervals must be non-negative");
            }
            if (messages[i] == null || messages[i].isEmpty()) {
                throw new IllegalArgumentException("Messages must not be empty");
            }
        }
        // initialize the fields with the arguments
        this.messages = messages;
        this.intervals = intervals;
        this.threads = new Thread[messages.length];
    }

    /**
     *  Runs all threads in the pool
     */
    public void start() {
        // loop through the messages array and create a Worker object for each message and interval
        for (int i = 0; i < messages.length; i++) {
            Worker worker = new Worker(messages[i], intervals[i]);
            // create a thread for each worker and store it in the threads array
            threads[i] = new Thread(worker);
            // start the thread
            threads[i].start();
        }
    }

    /** 
     * Interrupts all threads in the pool
     */
    public void stop() {
        // loop through the threads array and interrupt each thread
        for (Thread thread : threads) {
            thread.interrupt();
        }
        // loop through the threads array and join each thread
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // handle the exception
                e.printStackTrace();
            }
        }
    }

    /**
     * This class is used to spam a message with a given time interval.
     * The message must be printed to the console until the thread is interrupted.
     */
    static class Worker implements Runnable {
        // fields for storing a message and an interval for sending it
        private String message;
        private long interval;


        public Worker(String message, long interval) {
            // Assign the message and the interval to the fields
            this.message = message;
            this.interval = interval;
        }

        @Override
        public void run() {
            // loop until the thread is interrupted
            while (!Thread.currentThread().isInterrupted()) {
                // print the message to the console
                System.out.println(message);
                // pause the thread for the specified interval
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    // handle the exception and break the loop
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    /**
     * 1) Creates a Spam object.<br/>
     * 2) Starts the threads.<br/>
     * 3) Reads Enter from System.in<br/>
     * 4) Interrupts the threads.<br/>
     */
    public static void send() {
        // create arrays of messages and intervals for sending them
        String[] messages = {"Hello", "World", "Java", "Spam"};
        long[] intervals = {1000, 500, 2000, 1500};
        // create a Spam object with the arrays
        Spam spam = new Spam(messages, intervals);
        // start spamming
        spam.start();
        // create a Scanner object to read the console input
        Scanner scanner = new Scanner(System.in);
        // read the Enter key from the console
        scanner.nextLine();
        // stop spamming
        spam.stop();
        // close the scanner
        scanner.close();
    }
}
