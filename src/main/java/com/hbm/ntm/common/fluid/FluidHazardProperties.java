package com.hbm.ntm.common.fluid;

public record FluidHazardProperties(boolean hot, int corrosiveRating, boolean antimatter, int poison, int flammability, int reactivity, FluidHazardSymbol symbol) {
    public static final FluidHazardProperties NONE = new FluidHazardProperties(false, 0, false, 0, 0, 0, FluidHazardSymbol.NONE);

    public FluidHazardProperties(final boolean hot, final int corrosiveRating, final boolean antimatter) {
        this(hot, corrosiveRating, antimatter, 0, 0, 0, FluidHazardSymbol.NONE);
    }

    public boolean isCorrosive() {
        return corrosiveRating > 0;
    }

    public boolean isHighlyCorrosive() {
        return corrosiveRating > 50;
    }
}
