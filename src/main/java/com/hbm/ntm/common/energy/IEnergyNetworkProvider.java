package com.hbm.ntm.common.energy;

import net.minecraft.core.Direction;

public interface IEnergyNetworkProvider extends IEnergyConnector {
    long getAvailableNetworkEnergy(@org.jetbrains.annotations.Nullable Direction side);

    void consumeNetworkEnergy(@org.jetbrains.annotations.Nullable Direction side, long amount);

    default long getNetworkProviderSpeed(@org.jetbrains.annotations.Nullable final Direction side) {
        return Long.MAX_VALUE;
    }
}
