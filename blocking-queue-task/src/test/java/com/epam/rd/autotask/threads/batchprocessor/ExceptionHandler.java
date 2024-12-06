package com.epam.rd.autotask.threads.batchprocessor;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

import static org.junit.jupiter.api.Assertions.fail;

public class ExceptionHandler implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext ctx, Throwable throwable) {
        String message = throwable.getMessage();
        ctx.publishReportEntry(message);
        fail(message, throwable);
    }
}