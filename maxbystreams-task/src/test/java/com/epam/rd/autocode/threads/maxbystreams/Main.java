package com.epam.rd.autocode.threads.maxbystreams;

import java.util.Random;

import static com.epam.rd.autocode.threads.maxbystreams.MaxOfMatrix.*;
import static com.epam.rd.autocode.threads.maxbystreams.Util.matrixGenerator;

public class Main {
    public static void main(String[] args) {
        int[][] ar = matrixGenerator(new Random(), 4, 50);

        System.out.println(oneThreadSearch(ar, 1));
        System.out.println(multipleThreadSearch(ar, 1));
    }
}
