package com.hbm.ntm.common.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidNetworkReceiver extends IFluidNetworkParticipant {
    long getNetworkFluidDemand(FluidStack fluid, int pressure, @org.jetbrains.annotations.Nullable Direction side);

    long receiveNetworkFluid(FluidStack fluid, int pressure, long amount, @org.jetbrains.annotations.Nullable Direction side);

    default long getFluidReceiverSpeed(final FluidStack fluid, final int pressure, @org.jetbrains.annotations.Nullable final Direction side) {
        return Long.MAX_VALUE;
    }

    default FluidNetworkPriority getFluidNetworkPriority() {
        return FluidNetworkPriority.NORMAL;
    }
}
