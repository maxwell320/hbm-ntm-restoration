package com.hbm.ntm.common.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

@SuppressWarnings("null")
public class MachineFluidHandler implements IFluidHandler {
    private final HbmFluidTank[] tanks;

    public MachineFluidHandler(final HbmFluidTank... tanks) {
        this.tanks = tanks == null ? new HbmFluidTank[0] : tanks.clone();
    }

    @Override
    public int getTanks() {
        return this.tanks.length;
    }

    @Override
    public FluidStack getFluidInTank(final int tank) {
        return this.isValidTank(tank) ? this.tanks[tank].getFluid().copy() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(final int tank) {
        return this.isValidTank(tank) ? this.tanks[tank].getCapacity() : 0;
    }

    @Override
    public boolean isFluidValid(final int tank, final FluidStack stack) {
        return this.isValidTank(tank) && this.tanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        int remaining = resource.getAmount();
        int filled = 0;

        for (final HbmFluidTank tank : this.tanks) {
            if (remaining <= 0) {
                break;
            }
            if (!tank.isFluidValid(resource)) {
                continue;
            }
            final FluidStack tankFluid = tank.getFluid();
            if (!tankFluid.isEmpty() && !tankFluid.isFluidEqual(resource)) {
                continue;
            }
            final FluidStack copy = resource.copy();
            copy.setAmount(remaining);
            final int accepted = tank.fill(copy, action);
            if (accepted > 0) {
                remaining -= accepted;
                filled += accepted;
            }
        }

        return filled;
    }

    @Override
    public FluidStack drain(final FluidStack resource, final FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int remaining = resource.getAmount();
        FluidStack drained = FluidStack.EMPTY;

        for (final HbmFluidTank tank : this.tanks) {
            if (remaining <= 0) {
                break;
            }
            final FluidStack tankFluid = tank.getFluid();
            if (tankFluid.isEmpty() || !tankFluid.isFluidEqual(resource)) {
                continue;
            }
            final FluidStack part = tank.drain(remaining, action);
            if (part.isEmpty()) {
                continue;
            }
            if (drained.isEmpty()) {
                drained = part.copy();
            } else {
                drained.grow(part.getAmount());
            }
            remaining -= part.getAmount();
        }

        return drained;
    }

    @Override
    public FluidStack drain(final int maxDrain, final FluidAction action) {
        if (maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        int remaining = maxDrain;
        FluidStack drained = FluidStack.EMPTY;

        for (final HbmFluidTank tank : this.tanks) {
            if (remaining <= 0) {
                break;
            }
            final FluidStack part = tank.drain(remaining, action);
            if (part.isEmpty()) {
                continue;
            }
            if (drained.isEmpty()) {
                drained = part.copy();
            } else if (drained.isFluidEqual(part)) {
                drained.grow(part.getAmount());
            } else {
                break;
            }
            remaining -= part.getAmount();
        }

        return drained;
    }

    private boolean isValidTank(final int tank) {
        return tank >= 0 && tank < this.tanks.length;
    }
}
