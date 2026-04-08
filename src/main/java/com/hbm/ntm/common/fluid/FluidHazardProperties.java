package com.hbm.ntm.common.fluid;

public record FluidHazardProperties(boolean hot, int corrosiveRating, boolean antimatter) {
    public static final FluidHazardProperties NONE = new FluidHazardProperties(false, 0, false);

    public boolean isCorrosive() {
        return corrosiveRating > 0;
    }

    public boolean isHighlyCorrosive() {
        return corrosiveRating > 50;
    }
}
