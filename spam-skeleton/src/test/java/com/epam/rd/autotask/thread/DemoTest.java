package com.epam.rd.autotask.thread;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test output // what should be tested? how?
 */
@Timeout(60) // each test should finish in 1 minute
class DemoTest {
    private PrintStream sysOut;
    private ByteArrayOutputStream bytesOut;
    private PrintStream out;

    @BeforeEach
    void setUp() {
        sysOut = System.out;
        bytesOut = new ByteArrayOutputStream();
        out = new PrintStream(bytesOut);
        System.setOut(out);
    }

    @AfterEach
    void tearDown() {
        System.setOut(sysOut);
        out.close();
    }

    @Test
    void testDemoMain() {
        Class<?> originalInClass = System.in.getClass();
        Class<?> expectedClass = System.in.getClass();
        Thread demo = new Thread(() -> Demo.main(null));
        demo.start();
        try {
            while (originalInClass == expectedClass && demo.isAlive()) {
                expectedClass = System.in.getClass();
                Thread.sleep(1);
            }
            demo.join();
            assertEquals(EnterKeyInputStream.class, expectedClass,
                    "Demo main must use FirstTimeWaitInputStream");
        } catch (InterruptedException e) {
            fail("Unexpected interruption");
        }
        out.flush();
        String output = bytesOut.toString();
        String[] strings = output.split(System.lineSeparator());
        assertTrue(Arrays.stream(strings).limit(4).distinct().count() >= 2,
                "The workers must work concurrently");
        Arrays.stream(strings)
                .collect(Collectors.groupingBy(Function.identity()))
                .values().stream()
                .map(List::size)
                .forEach(v -> assertTrue(v >= 4,
                        "The output should contains at least 4 messages for each thread."));
    }
}