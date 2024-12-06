package com.epam.rd.autotask.threads.batchprocessor;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.UnaryOperator;

/**
 *
 */
public class MessagePreprocessor {

    private final List<BlockingQueue<String>> queues;
    private final List<Transformer> transformers;
    private volatile boolean completed;

    public MessagePreprocessor(Collection<String> initial, List<UnaryOperator<String>> functions) {
        if (initial == null || functions == null) {
            throw new NullPointerException("Parameters cannot be null");
        }

        if (initial.isEmpty() || functions.isEmpty()) {
            throw new IllegalArgumentException("Parameters cannot be empty");
        }

        int numQueues = functions.size() + 1;
        int queueCapacity = initial.size() / 2;

        this.queues = new ArrayList<>(numQueues);
        this.transformers = new ArrayList<>(numQueues - 1);

        for (int i = 0; i < numQueues; i++) {
            this.queues.add(new ArrayBlockingQueue<>(i == 0 || i == numQueues - 1 ? initial.size() : queueCapacity));
        }

        Iterator<String> initialIterator = initial.iterator();
        this.queues.get(0).addAll(initial);

        for (int i = 0; i < functions.size(); i++) {
            UnaryOperator<String> function = functions.get(i);
            BlockingQueue<String> sourceQueue = this.queues.get(i);
            BlockingQueue<String> destinationQueue = this.queues.get(i + 1);
            Transformer transformer = new Transformer(function, sourceQueue, destinationQueue);
            this.transformers.add(transformer);
        }

        this.completed = false;
    }

    List<BlockingQueue<String>> getState() {
        return Collections.unmodifiableList(queues);
    }

    /**
     * Starts all operations in separate threads.
     */
    public void start() {
        transformers.forEach(Thread::start);
    }

    /**
     * @return Optional.empty() if not all jobs have been processed
     *         and processor isn't forcibly stopped, otherwise it stops all
     *         running operations and returns a collection of processed jobs
     * @see MessagePreprocessor#stop()
     */
    public Optional<Collection<? extends String>> getResult() {
        if (!completed) {
            return Optional.empty();
        }

        Set<String> result = new HashSet<>();
        queues.get(queues.size() - 1).drainTo(result);
        return Optional.of(Collections.unmodifiableCollection(result));
    }

    /**
     * Stops all threads. The threads must interrupt immediately
     * after finishing current processing object.<br>
     * It must ensure that all threads are stopped.
     */
    public void stop() {
        transformers.forEach(Thread::interrupt);
        transformers.forEach(transformer -> {
            try {
                transformer.join();
            } catch (InterruptedException e) {
                // Handle interruption if needed
            }
        });
        completed = true;
    }
}
