package com.epam.rd.autotasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

public class QuadraticEquationNoRootsCasesTesting {

    protected QuadraticEquation quadraticEquation = new QuadraticEquation();

    @ParameterizedTest
    @CsvSource({
            "-563, 0, -5",
            "2, 10, 30",
            "-0.5, 1, -50",
            "1, 11, 111",
            "2, 2, 2"
    })
    public void testNoRootsCase(double a, double b, double c) {
        assertEquals("no roots", quadraticEquation.solve(a, b, c));
    }

    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(-563, 0, -5),
                Arguments.of(2, 10, 30),
                Arguments.of(-0.5, 1, -50),
                Arguments.of(1, 11, 111),
                Arguments.of(2, 2, 2));
    }
}