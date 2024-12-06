package com.epam.rd.autotask.thread;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Timeout(60) // each test should finish in 1 minute
class SpamTest {
    private static final Logger log = LoggerFactory.getLogger(SpamTest.class);
    static final long MAX_TIMEOUT = 10 * 1000L;
    static final Duration MAX_DURATION = Duration.ofMillis(MAX_TIMEOUT);
    static final String[] MESSAGES = new String[]{"@@@", "bbbbbbb"};
    static final long[] INTERVALS = new long[]{30, 20};
    private PrintStream sysOut;
    private ByteArrayOutputStream bytesOut;
    private PrintStream out;
    ExecutorService es = null;

    @BeforeEach
    void setUp() {
        es = Executors.newSingleThreadExecutor();
        sysOut = System.out;
        bytesOut = new ByteArrayOutputStream();
        out = new PrintStream(bytesOut);
        System.setOut(out);
    }

    @AfterEach
    void tearDown() {
        System.setOut(sysOut);
    }

    @ParameterizedTest
    @MethodSource("casesConstructorShouldCreate")
    void testConstructorShouldCreate(String[] messages, long[] intervals) {
        log.trace("start");
        log.debug("Messages: {}\nIntervals: {}", Arrays.toString(messages), Arrays.toString(intervals));
        assertDoesNotThrow(() -> new Spam(messages, intervals));
        log.trace("finish");
    }

    public static Stream<Arguments> casesConstructorShouldCreate() {
        Random random = new Random(25);
        return Stream.of(
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(5).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(5).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(2).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(2).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(7).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(7).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(4).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(4).toArray())
        );
    }

    @ParameterizedTest
    @MethodSource("casesConstructorShouldThrow")
    void testConstructorShouldThrow(String[] messages, long[] intervals) {
        log.trace("start");
        log.debug("Messages: {}\nIntervals: {}", Arrays.toString(messages), Arrays.toString(intervals));
        await("Should throw IllegalArgumentException")
                .atMost(MAX_DURATION)
                .untilAsserted(
                        () -> assertThrows(IllegalArgumentException.class,
                                () -> new Spam(messages, intervals)));
        log.trace("finish");
    }

    public static Stream<Arguments> casesConstructorShouldThrow() {
        Random random = new Random(25);
        return Stream.of(
                Arguments.of(null,
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(5).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(2).toArray(String[]::new),
                        null),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(1).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(1).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(4).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(3).toArray()),
                Arguments.of(Stream.generate(() -> Util.nextString(random)).limit(2).toArray(String[]::new),
                        LongStream.generate(() -> Util.nextInt(random, 50, 200)).limit(3).toArray())
        );
    }

    @Test
    void testSpamDefault() {
        testSpam(MESSAGES, INTERVALS);
    }

    @ParameterizedTest
    @MethodSource("casesConstructorShouldCreate")
    void testSpamThreads(String[] messages, long[] intervals) {
        testSpam(messages, intervals);
    }

    static class Holder {
        public int value;

        public Holder(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    void testSpam(String[] messages, long[] intervals) {
        log.trace("start");
        Holder baseCount = new Holder(Thread.activeCount());
        log.debug("initial baseCount {}", baseCount);
        // wait while JVM handle threads
        await().atMost(2, TimeUnit.SECONDS)
                .with().pollDelay(Duration.ofMillis(1000))
                .until(() -> {
                    int ac = Thread.activeCount();
                    log.debug("initial waiting baseCount: {} activeCount: {}", baseCount, ac);
                    baseCount.value = ac;
                    return true;
                });

        baseCount.value = Thread.activeCount();
        // test constructor
        Future<Spam> submit = es.submit(() -> new Spam(messages, intervals));
        log.debug("creating spam, baseCount: {}", baseCount);
        Spam spam = null;
        try {
            spam = submit.get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
            log.debug("spam created baseCount {}", baseCount);
            int activeCount = Thread.activeCount();
            log.debug("spam created baseCount: {} - activeCount: {}", baseCount, activeCount);
            assertBetween(baseCount.value - 1, activeCount, baseCount.value + 1,
                    "The workers should not start in Spam constructor");
        } catch (TimeoutException e) {
            fail("The workers should not start in Spam constructor");
        } catch (ExecutionException e) {
            fail("Workers should not throw exceptions");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Unexpected interruption");
        }

        // test start should create threads
        assert spam != null;
        try {
            Future<?> startFuture = es.submit(spam::start);
            startFuture.get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
            int activeCount = Thread.activeCount();
            log.debug("workers should start baseCount: {} - activeCount: {}", baseCount, activeCount);
            assertBetween(baseCount.value - 1 + messages.length, activeCount, baseCount.value + 1 + messages.length,
                    "The number of workers should equals to the number of messages");
        } catch (TimeoutException e) {
            fail("The worker should be started simultaneously");
        } catch (ExecutionException e) {
            fail("start() should not throw exceptions");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Unexpected interruption");
        }

        // test stop() should stop threads and wait until they stop
        try {
            Future<?> stopFuture = es.submit(spam::stop);
            stopFuture.get(MAX_TIMEOUT, TimeUnit.MILLISECONDS);
            int activeCount = Thread.activeCount();
            log.debug("workers should stop, baseCount: {} - activeCount: {}", baseCount, activeCount);
            assertBetween(baseCount.value - 1, activeCount, baseCount.value + 1,
                    "stop() should wait until workers stop. Do not use Demons.");
        } catch (TimeoutException e) {
            fail("The workers should be stopped in stop() method");
        } catch (ExecutionException e) {
            fail("Workers should not throw exceptions");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Unexpected interruption");
        }
        log.trace("finish");
    }

    @SuppressWarnings("java:S2925")
    @ParameterizedTest
    @MethodSource("casesConstructorShouldCreate")
    void testOutputNumberOfMessages(String[] messages, long[] intervals) {
        log.debug("bytesOut: {}", bytesOut);
        long timeout = Arrays.stream(intervals).sum() * 2;
        log.debug("timeout: {}}", timeout);
        Thread spamThread = new Thread(() -> {
            Spam spammer = new Spam(messages, intervals);
            spammer.start();
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Unexpected interruption");
            }
            spammer.stop();
        });
        spamThread.start();
        try {
            spamThread.join();
            out.flush();
            String[] strings = bytesOut.toString().split(System.lineSeparator());
            log.debug("output: {}\n{}", Arrays.toString(messages), Arrays.toString(strings));
            assertEquals(messages.length, Arrays.stream(strings).distinct().count());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Unexpected interruption");
        }
    }

    @Test
    void testCompliance() {
        testWorkerCompliance();
        testSpamCompliance();
    }

    void testWorkerCompliance() {
        Class<Spam.Worker> workerClass = Spam.Worker.class;
        Class<?> clazz = workerClass;
        assertTrue(Arrays.stream(workerClass.getInterfaces()).anyMatch(c -> c == Runnable.class),
                "Spam.Worker must be Runnable");
        while (clazz != null) {
            assertNotEquals(Thread.class, clazz,
                    "Spam.Worker must not be Thread it must be Runnable instead");
            clazz = clazz.getSuperclass();
        }
    }

    void testSpamCompliance() {
        Class<Spam> spamClass = Spam.class;
        assertTrue(Arrays.stream(spamClass.getInterfaces()).noneMatch(c -> c == Runnable.class),
                "Spam should not implement Runnable");
    }

    void assertBetween(int min, int actual, int max, String msg) {
        assertTrue((min <= actual) && (actual <= max),
                msg + " : " + min + " <= " + actual
                        + " <= " + max);
    }
}

