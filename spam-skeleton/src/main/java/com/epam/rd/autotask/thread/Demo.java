package com.epam.rd.autotask.thread;

import java.io.InputStream;

public class Demo {
    public static void main(String[] args) {

        // Set the EnterKeyInputStream stream to read from the keyboard with a delay of 2 seconds
        InputStream originalIn = System.in; // Cache the original input stream
        System.setIn(new EnterKeyInputStream(2000)); // Set the custom input stream
        // Run the send() method of the Spam class in a separate thread
        Thread t = new Thread(Spam::send);
        t.start();
        // Wait for the spam to finish
        try {
            t.join();
        } catch (InterruptedException e) {
            // Handle the exception
            Thread.currentThread().interrupt();
        }
        // Restore the original input stream
        System.setIn(originalIn);
    }
}
