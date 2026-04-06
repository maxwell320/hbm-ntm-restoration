package com.hbm.ntm.common.energy;

import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public interface IEnergyUser extends IEnergyConnector {
    @Nullable
    IEnergyStorage getEnergyStorage(@Nullable Direction side);

    @Override
    default EnergyConnectionMode getEnergyConnection(@Nullable final Direction side) {
        final IEnergyStorage storage = this.getEnergyStorage(side);
        return storage == null ? EnergyConnectionMode.NONE : EnergyConnectionMode.of(storage.canReceive(), storage.canExtract());
    }
}
