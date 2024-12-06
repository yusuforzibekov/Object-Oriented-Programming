package com.epam.rd.autotasks;

@FunctionalInterface
public interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;

    static <E extends Throwable> Runnable silentRunnable(ThrowingRunnable<E> throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
    
}
