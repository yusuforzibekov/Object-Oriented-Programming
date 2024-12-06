package com.epam.rd.autotasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

public class QuadraticEquationSingleRootCasesTesting {
    protected QuadraticEquation quadraticEquation = new QuadraticEquation();

    @ParameterizedTest
    @CsvSource({
            "1, -2, 1, 1.0",
            "1, 0, 0, -0.0",
            "8, 0, 0, -0.0",
            "1, -6, 9, 3.0",
            "1, 12, 36, -6.0"
    })
    public void testCase(double a, double b, double c, String expected) {
        assertEquals(expected, quadraticEquation.solve(a, b, c));
    }

    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(1, -2, 1, "1.0"),
                Arguments.of(1, 0, 0, "-0.0"),
                Arguments.of(8, 0, 0, "-0.0"),
                Arguments.of(1, -6, 9, "3.0"),
                Arguments.of(1, 12, 36, "-6.0"));
    }
}