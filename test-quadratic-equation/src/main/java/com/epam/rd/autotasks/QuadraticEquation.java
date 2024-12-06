package com.epam.rd.autotasks;

public class QuadraticEquation {

    public String solve(double a, double b, double c) {
        if (a == 0) {
            throw new IllegalArgumentException("Coefficient a cannot be zero");
        }

        double discriminant = b * b - 4 * a * c;

        if (discriminant > 0) {
            double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
            double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
            return root1 + " " + root2;
        } else if (discriminant == 0) {
            double root = -b / (2 * a);
            return String.valueOf(root);
        } else {
            return "no roots";
        }
    }
}