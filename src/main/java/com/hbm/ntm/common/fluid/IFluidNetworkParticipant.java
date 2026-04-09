package com.hbm.ntm.common.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

public interface IFluidNetworkParticipant {
    int HIGHEST_VALID_PRESSURE = 5;
    int[] DEFAULT_PRESSURE_RANGE = new int[] {0, 0};

    default int[] getProvidingPressureRange(final FluidStack fluid, @org.jetbrains.annotations.Nullable final Direction side) {
        return DEFAULT_PRESSURE_RANGE;
    }

    default int[] getReceivingPressureRange(final FluidStack fluid, @org.jetbrains.annotations.Nullable final Direction side) {
        return DEFAULT_PRESSURE_RANGE;
    }
}
