package com.epam.rd.autotask.thread;

public class IncDecThreads {

    static final int COUNT = 5000;
    static long value;

    /**
     * In a loop increments {@code COUNT} times the {@code value}
     * and prints to the console the name of the class, the name of
     * the thread and the value of the field {@code value}.
     */
    static class Increment extends Thread {
        // override the run method
        public void run() {
            // loop for COUNT times
            for (int i = 0; i < COUNT; i++) {
                // increment the value
                value++;
                // print the class name, thread name and value
                System.out.println("Increment : " + getName() + " : " + value);
            }
        }
    }

    /**
     * In a loop decrements {@code COUNT} times the {@code value}
     * and prints to the console the name of the class, the name of
     * the thread and the value of the field {@code value}.
     */
    static class Decrement implements Runnable {
        // implement the run method
        public void run() {
            // loop for COUNT times
            for (int i = 0; i < COUNT; i++) {
                // decrement the value
                value--;
                // print the class name, thread name and value
                System.out.println("Decrement : " + Thread.currentThread().getName() + " : " + value);
            }
        }
    }

    public static void main(String[] args) {
        // create an instance of Increment class
        Increment inc = new Increment();
        // start the thread
        inc.start();
        // create an instance of Decrement class
        Decrement dec = new Decrement();
        // create a thread with Runnable interface
        Thread t = new Thread(dec);
        // start the thread
        t.start();
    }
}
