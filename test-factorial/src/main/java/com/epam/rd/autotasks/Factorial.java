package com.epam.rd.autotasks;

import java.math.BigDecimal;

public class Factorial {
    public String factorial(String n) {
        if (n == null) {
            throw new IllegalArgumentException();
        }
        if (n.matches("^[0-9]+\\.[0-9]+$")){
            throw new IllegalArgumentException();
        }
        if (n.equals("")){
            throw new IllegalArgumentException();
        }
        if (n.matches("[^0-9]")){
            throw new IllegalArgumentException();
        }
        if (Integer.parseInt(n) < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal inputNum = new BigDecimal(n);
        BigDecimal factorialResult = new BigDecimal(String.valueOf(1));
        for (int i = 1; i <= inputNum.longValue(); i++) {
            factorialResult = factorialResult.multiply(new BigDecimal(String.valueOf(i)));
        }
        return String.valueOf(factorialResult);

    }
}
