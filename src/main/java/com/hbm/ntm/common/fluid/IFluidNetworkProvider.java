package com.hbm.ntm.common.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidNetworkProvider extends IFluidNetworkParticipant {
    long getAvailableNetworkFluid(FluidStack fluid, int pressure, @org.jetbrains.annotations.Nullable Direction side);

    void consumeNetworkFluid(FluidStack fluid, int pressure, long amount, @org.jetbrains.annotations.Nullable Direction side);

    default long getFluidProviderSpeed(final FluidStack fluid, final int pressure, @org.jetbrains.annotations.Nullable final Direction side) {
        return Long.MAX_VALUE;
    }
}
