package com.epam.rd.autocode.threads.maxbystreams;

import java.util.Random;

public class Util {

    private static final int MIN = 0;

    private static final int MAX = 1000;

    public static int[][] matrixGenerator(Random r, int m, int n) {
        int[][] res = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                res[i][j] = r.nextInt(MAX - MIN) + MIN;
            }
        }
        return res;
    }

}
