package com.epam.rd.autotask.threads.batchprocessor;

import java.util.concurrent.BlockingQueue;
import java.util.function.UnaryOperator;

public class Transformer extends Thread {
    private final UnaryOperator<String> operation;
    private final BlockingQueue<String> sourceQueue;
    private final BlockingQueue<String> destinationQueue;

    public Transformer(UnaryOperator<String> op,
            BlockingQueue<String> sourceQueue,
            BlockingQueue<String> destinationQueue) {
        if (op == null || sourceQueue == null || destinationQueue == null) {
            throw new NullPointerException("Parameters cannot be null");
        }

        this.operation = op;
        this.sourceQueue = sourceQueue;
        this.destinationQueue = destinationQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = sourceQueue.take();

                if (message.equals("empty")) {
                    destinationQueue.put("empty");
                    break;
                }

                String processedMessage = operation.apply(message);
                destinationQueue.put(processedMessage);
            }
        } catch (InterruptedException e) {
            // Handle interruption if needed
        }
    }
}
