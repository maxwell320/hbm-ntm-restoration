package com.hbm.ntm.common.energy;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IEnergyConnector {
    EnergyConnectionMode getEnergyConnection(@Nullable Direction side);

    default boolean canConnectEnergy(@Nullable final Direction side) {
        return this.getEnergyConnection(side).canConnect();
    }

    default boolean canReceiveEnergy(@Nullable final Direction side) {
        return this.getEnergyConnection(side).canReceive();
    }

    default boolean canExtractEnergy(@Nullable final Direction side) {
        return this.getEnergyConnection(side).canExtract();
    }
}
