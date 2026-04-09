package com.hbm.ntm.common.energy;

import net.minecraft.core.Direction;

public interface IEnergyNetworkReceiver extends IEnergyConnector {
    long getNetworkEnergyDemand(@org.jetbrains.annotations.Nullable Direction side);

    long receiveNetworkEnergy(@org.jetbrains.annotations.Nullable Direction side, long amount);

    default long getNetworkReceiverSpeed(@org.jetbrains.annotations.Nullable final Direction side) {
        return Long.MAX_VALUE;
    }

    default EnergyNetworkPriority getNetworkPriority() {
        return EnergyNetworkPriority.NORMAL;
    }

    default boolean allowDirectEnergyProvision() {
        return true;
    }
}
