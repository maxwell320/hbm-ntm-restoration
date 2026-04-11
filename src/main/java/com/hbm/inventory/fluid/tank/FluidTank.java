package com.hbm.inventory.fluid.tank;

import com.hbm.inventory.fluid.FluidType;

public class FluidTank {
    private FluidType tankType;
    private int pressure;
    private int fill;
    private int maxFill;

    public FluidTank(final FluidType tankType, final int pressure, final int maxFill) {
        this.tankType = tankType;
        this.pressure = pressure;
        this.maxFill = maxFill;
    }

    public FluidType getTankType() {
        return this.tankType;
    }

    public void setTankType(final FluidType tankType) {
        this.tankType = tankType;
    }

    public int getPressure() {
        return this.pressure;
    }

    public void setPressure(final int pressure) {
        this.pressure = pressure;
    }

    public int getFill() {
        return this.fill;
    }

    public void setFill(final int fill) {
        this.fill = Math.max(0, Math.min(fill, this.maxFill));
    }

    public int getMaxFill() {
        return this.maxFill;
    }

    public void setMaxFill(final int maxFill) {
        this.maxFill = Math.max(0, maxFill);
        if (this.fill > this.maxFill) {
            this.fill = this.maxFill;
        }
    }
}
