package com.epam.rd.autotasks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class FactorialBadInputTesting {

    Factorial factorial = new Factorial();
    private final double EPS = 1e-9;

    @Test
    void testNullInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factorial.factorial("null"));

    }

    @Test
    void testNegativeInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factorial.factorial("-8"));
    }

    @Test
    void testFractionalInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factorial.factorial("2.5"));

    }

    @Test
    void testNonDigitalInput() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> factorial.factorial("zz"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> factorial.factorial(""));
    }

}
