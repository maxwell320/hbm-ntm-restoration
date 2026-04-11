package com.hbm.util;

public final class BobMathUtil {
    private BobMathUtil() {
    }

    public static double squirt(final double x) {
        return Math.sqrt(x + 1D / ((x + 2D) * (x + 2D))) - 1D / (x + 2D);
    }
}
