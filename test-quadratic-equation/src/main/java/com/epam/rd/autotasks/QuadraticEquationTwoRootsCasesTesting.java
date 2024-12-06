package com.epam.rd.autotasks;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

public class QuadraticEquationTwoRootsCasesTesting {
    protected QuadraticEquation quadraticEquation = new QuadraticEquation();

    @ParameterizedTest
    @CsvSource({
            "2, 5, -3, '-3.0 0.5'",
            "1, -3, 1, '0.3819660112501051 2.618033988749895'",
            "2, -38, 156, '6.0 13.0'",
            "-0.5, 34, 1046.5, '-23.0 91.0'"
    })
    public void testCase(double a, double b, double c, String expected) {
        String result = quadraticEquation.solve(a, b, c);
        String[] roots = result.split(" ");
        String[] expectedRoots = expected.split(" ");
        assertTrue((roots[0].equals(expectedRoots[0]) && roots[1].equals(expectedRoots[1])) ||
                (roots[0].equals(expectedRoots[1]) && roots[1].equals(expectedRoots[0])));
    }

    public static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(2, 5, -3, "-3.0 0.5"),
                Arguments.of(1, -3, 1, "0.3819660112501051 2.618033988749895"),
                Arguments.of(2, -38, 156, "6.0 13.0"),
                Arguments.of(-0.5, 34, 1046.5, "-23.0 91.0"));
    }
}