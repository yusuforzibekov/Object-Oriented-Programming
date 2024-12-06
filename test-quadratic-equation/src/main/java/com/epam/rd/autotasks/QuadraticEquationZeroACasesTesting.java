package com.epam.rd.autotasks;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuadraticEquationZeroACasesTesting {
    protected QuadraticEquation quadraticEquation = new QuadraticEquation();

    @ParameterizedTest
    @CsvSource({
            "0, 5, -30",
            "0, -3, 10",
            "0, -38, 1560",
            "0, 34, 1046.5"
    })
    public void testCase(double a, double b, double c) {
        assertThrows(IllegalArgumentException.class, () -> {
            quadraticEquation.solve(a, b, c);
        });
    }

    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(0, 5, -30),
                Arguments.of(0, -3, 10),
                Arguments.of(0, -38, 1560),
                Arguments.of(0, 34, 1046.5));
    }
}