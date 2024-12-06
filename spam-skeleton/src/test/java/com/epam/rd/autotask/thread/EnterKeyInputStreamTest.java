package com.epam.rd.autotask.thread;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class EnterKeyInputStreamTest {

    @ParameterizedTest
    @CsvSource({"500","750","1000"})
    void testRead(long interval) {
        try (EnterKeyInputStream in = new EnterKeyInputStream(interval)) {
            byte[] bytes = System.lineSeparator().getBytes();
            long start = System.currentTimeMillis();
            int b = in.read();
            long end = System.currentTimeMillis();
            assertTrue(end - start >= interval,
                    "Should wait given interval for first read: current=" +
                            (end - start) + " should be >= than expected=" + interval);
            assertEquals(bytes[0], b, "System line separator must be used");

            for (int i = 1; i < bytes.length; i++) {
                start = System.currentTimeMillis();
                int actual = in.read();
                end = System.currentTimeMillis();
                assertEquals(bytes[i], actual, "System line separator must be used");
                assertTrue(end - start < interval,
                        "Should not wait next readings: " +
                                (end - start) + " should be < than expected=" + interval);
            }
            assertEquals(-1, in.read(), "System line separator must be used");
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
