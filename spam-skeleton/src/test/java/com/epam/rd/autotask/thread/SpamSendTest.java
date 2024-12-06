package com.epam.rd.autotask.thread;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.spy;

@Timeout(60) // each test should finish in 1 minute
class SpamSendTest {
    private static final int FIRST_READ_TIMEOUT = 1000;
    private final InputStream sysIn = System.in;

    @AfterEach
    void tearDown() {
        System.setIn(sysIn);
    }

    @Test
    void testSpamSend() throws IOException {
        EnterKeyInputStream mock = spy(new EnterKeyInputStream(FIRST_READ_TIMEOUT));
        Mockito.doCallRealMethod().when(mock).read();
        System.setIn(mock);
        Thread spam = new Thread(Spam::send);
        spam.start();
        try {
            long startTime = System.currentTimeMillis();
            spam.join();
            long endTime = System.currentTimeMillis();
            assertTrue((endTime - startTime) >= FIRST_READ_TIMEOUT);
            Mockito.verify(mock,
                    Mockito.times(System.lineSeparator().getBytes().length + 1)
                            .description("The current line separator must be used."))
                    .read();
        } catch (InterruptedException e) {
            fail("Unexpected interruption");
        }
    }

}

