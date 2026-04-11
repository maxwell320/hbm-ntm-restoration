package com.hbm.inventory.fluid;

public class FluidType {
    private final String name;
    private final int color;

    public FluidType(final String name, final int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }
}
