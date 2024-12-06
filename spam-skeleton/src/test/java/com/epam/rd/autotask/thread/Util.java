package com.epam.rd.autotask.thread;

import java.util.Random;

public class Util {
    private Util() {}

    public static String nextString(Random r) {
        return "" + (char) nextInt(r, 'a', 'z') + (char) nextInt(r, 'a', 'z');
    }

    public static int nextInt(Random r, int origin, int bound) {
        return r.nextInt(bound - origin) + origin;
    }
}
