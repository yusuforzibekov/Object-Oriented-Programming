package com.epam.rd.autocode.threads.maxbystreams;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.epam.rd.autocode.threads.maxbystreams.Util.matrixGenerator;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Y. Mishcheriakov
 * @author D. Kolesnikov
 */
public class MaxOfMatrixTest {

    @ParameterizedTest
    @CsvSource({"4,10,1", "5,4,1", "3,7,1"})
    void testShouldBeMaxValuesAreTheSame(int m, int n, int pause) {
        int[][] ar = matrixGenerator(new Random(25), m, n);

        String res1 = MaxOfMatrix.oneThreadSearch(ar, pause);
        String res2 = MaxOfMatrix.multipleThreadSearch(ar, pause);
        assertNotNull(res1);
        assertNotNull(res2);

        int max1 = Integer.parseInt(res1.split(" ")[0]);
        int max2 = Integer.parseInt(res2.split(" ")[0]);
        Assertions.assertEquals(max1, max2);
    }

    @ParameterizedTest
    @CsvSource({"4,15,5", "4,6,5", "3,4,5"})
    void testShouldBeTimeValuesAreDifferMoreThenAtXTimes(int m, int n, int pause) {
        int[][] ar = matrixGenerator(new Random(25), m, n);

        String res1 = MaxOfMatrix.oneThreadSearch(ar, pause);
        String res2 = MaxOfMatrix.multipleThreadSearch(ar, pause);
        assertNotNull(res1);
        assertNotNull(res2);

        int time1 = Integer.parseInt(res1.split(" ")[1]);
        int time2 = Integer.parseInt(res2.split(" ")[1]);

        double x = m * 0.5;
        Assertions.assertTrue(time1 > x * time2);
    }

    @ParameterizedTest
    @CsvSource({"2,10,5", "15,4,10", "13,7,7"})
    void testShouldBeAtLeastMThreadsWhenMultipleThreadSearchIsExecuted(int m, int n, int pause) throws Exception {
        Set<String> threadNames = new TreeSet<>();
        int poolSize = ForkJoinPool.getCommonPoolParallelism();
        m = Math.max(m, poolSize);
        int[][] ar = matrixGenerator(new Random(25), m, n);
        Thread t = startThreadsDetector(threadNames, "ForkJoinPool.commonPool-worker-", pause);

        MaxOfMatrix.multipleThreadSearch(ar, pause);
        t.join();
        long threadsCount = threadNames.size();

        Assertions.assertEquals(poolSize, threadsCount,
                "'multipleThreadSearch' must use " + poolSize + " threads, " +
                        "detected threads are: " + threadNames);
    }

    @ParameterizedTest
    @CsvSource({"4,10,10", "5,4,5", "3,7,7"})
    void testShouldBeNoMoreThanOneThreadWhenSingleThreadSearchIsExecuted(int m, int n, int pause) throws InterruptedException {
        assertTrue(ComplianceTest.isImplemented("oneThreadSearch"),
                "Must be implemented");
        Set<String> threadNames = new TreeSet<>();
        final String threadsNamePattern = "test-thread-";
        int[][] ar = matrixGenerator(new Random(25), m, n);
        Thread t = startThreadsDetector(threadNames, threadsNamePattern, pause);
        AtomicInteger counter = new AtomicInteger();

        ExecutorService pool = Executors.newFixedThreadPool(3, r -> {
            Thread thread = new Thread(r);
            thread.setName(threadsNamePattern + counter.incrementAndGet());
            return thread;
        });
        try {
            pool.submit(() -> MaxOfMatrix.oneThreadSearch(ar, pause)).get();
            pool.shutdown();
        } catch (ExecutionException e) {
            fail("Fail to execute the task.");
        }

        t.join();
        long threadsCount = threadNames.size();
        Assertions.assertTrue(threadsCount <= 1,
                "'oneThreadSearch' must use no more than one thread with name " +
                        "'" + threadsNamePattern + "', " +
                        "detected threads are: " + threadNames);
    }

    @ParameterizedTest
    @CsvSource({"4,10,10", "5,4,5", "3,7,7"})
    void testMultiThreadSearchShouldUsePause(int m, int n, int pause) {
        int[][] ar = matrixGenerator(new Random(25), m, n);

        String result = MaxOfMatrix.multipleThreadSearch(ar, pause);
        assertNotNull(result);

        int actual = Integer.parseInt(result.split(" ")[1]);
        int parallelism = ForkJoinPool.getCommonPoolParallelism();
        int expected = m * n * pause / parallelism;

        Assertions.assertTrue(actual >= expected,
                "'multipleThreadSearch' must use pause before each comparison. " +
                        "expected time (at least) " + expected +
                        ", but was: " + actual);
    }

    @ParameterizedTest
    @CsvSource({"4,10,10", "5,4,5", "3,7,7"})
    void testOneThreadSearchShouldUsePause(int m, int n, int pause) {
        int[][] ar = matrixGenerator(new Random(25), m, n);

        String result = MaxOfMatrix.oneThreadSearch(ar, pause);
        assertNotNull(result);

        int actual = Integer.parseInt(result.split(" ")[1]);
        int expected = m * n * pause;
        Assertions.assertTrue(actual >= expected,
                "'oneThreadSearch' must use pause before each comparison. " +
                        "expected time (at least) " + expected +
                        ", but was: " + actual);
    }


    private Thread startThreadsDetector(Set<String> threadNames, String namePattern, int delay) {
        Thread t = new Thread() {
            public void run() {
                setName("threads-detector");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Thread.getAllStackTraces().keySet().stream()
                        .map(Thread::getName)
                        .filter(name -> name.startsWith(namePattern))
                        .forEach(threadNames::add);
            }
        };
        t.start();
        return t;
    }

}
