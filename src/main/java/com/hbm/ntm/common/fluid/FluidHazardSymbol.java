package com.hbm.ntm.common.fluid;

public enum FluidHazardSymbol {
    NONE(0, 0),
    RADIATION(195, 2),
    NOWATER(195, 63),
    ACID(195, 124),
    ASPHYXIANT(195, 185),
    CRYOGENIC(134, 185),
    ANTIMATTER(73, 185),
    OXIDIZER(12, 185);

    private final int u;
    private final int v;

    FluidHazardSymbol(final int u, final int v) {
        this.u = u;
        this.v = v;
    }

    public int u() {
        return this.u;
    }

    public int v() {
        return this.v;
    }
}
