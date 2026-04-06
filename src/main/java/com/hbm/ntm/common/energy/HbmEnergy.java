package com.hbm.ntm.common.energy;

public final class HbmEnergy {
    public static final String UNIT = "HE";
    public static final long RF_HE_INPUT_RF = 2L;
    public static final long RF_HE_OUTPUT_HE = 5L;
    public static final long HE_RF_INPUT_HE = 5L;
    public static final long HE_RF_OUTPUT_RF = 1L;

    private HbmEnergy() {
    }

    public static long convertRfToHe(final long rf) {
        if (rf <= 0L) {
            return 0L;
        }
        return rf * RF_HE_OUTPUT_HE / RF_HE_INPUT_RF;
    }

    public static long convertHeToRf(final long he) {
        if (he <= 0L) {
            return 0L;
        }
        return he * HE_RF_OUTPUT_RF / HE_RF_INPUT_HE;
    }

    public static long rfRequiredForHe(final long he) {
        if (he <= 0L) {
            return 0L;
        }
        return ceilDiv(he * RF_HE_INPUT_RF, RF_HE_OUTPUT_HE);
    }

    public static long heRequiredForRf(final long rf) {
        if (rf <= 0L) {
            return 0L;
        }
        return ceilDiv(rf * HE_RF_INPUT_HE, HE_RF_OUTPUT_RF);
    }

    public static long ceilDiv(final long dividend, final long divisor) {
        if (dividend <= 0L) {
            return 0L;
        }
        if (divisor <= 0L) {
            throw new IllegalArgumentException("Divisor must be positive");
        }
        return (dividend + divisor - 1L) / divisor;
    }
}
