package com.epam.rd.autotask.thread;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("java:S4929")
public class EnterKeyInputStream extends InputStream {
    // field for storing a time interval
    private long interval;
    // additional fields for reading the Enter keystroke
    private byte[] lineSeparator;
    private int index;

    public EnterKeyInputStream(long interval) {
        // initialize the field with the argument
        this.interval = interval;
        // get the bytes of the line separator
        lineSeparator = System.lineSeparator().getBytes();
        // initialize the index to zero
        index = 0;
    }

    @Override
    public int read() throws IOException {
        // check the number of calls to the method
        if (index == 0) {
            // if the method is called for the first time, pause the work for the specified interval
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                // handle the exception
                e.printStackTrace();
            }
        }
        // read and return the next byte of the line separator
        if (index < lineSeparator.length) {
            return lineSeparator[index++] & 0xFF;
        } else {
            // return -1 at the end
            return -1;
        }
    }
}
