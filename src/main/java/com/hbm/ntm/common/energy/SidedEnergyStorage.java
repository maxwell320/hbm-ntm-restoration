package com.hbm.ntm.common.energy;

import java.util.Objects;
import java.util.function.Supplier;
import net.minecraftforge.energy.IEnergyStorage;

public final class SidedEnergyStorage implements IEnergyStorage {
    private final IEnergyStorage delegate;
    private final Supplier<EnergyConnectionMode> connectionMode;

    public SidedEnergyStorage(final IEnergyStorage delegate, final EnergyConnectionMode connectionMode) {
        this(delegate, () -> connectionMode);
    }

    public SidedEnergyStorage(final IEnergyStorage delegate, final Supplier<EnergyConnectionMode> connectionMode) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.connectionMode = Objects.requireNonNull(connectionMode, "connectionMode");
    }

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        return this.mode().canReceive() ? this.delegate.receiveEnergy(maxReceive, simulate) : 0;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        return this.mode().canExtract() ? this.delegate.extractEnergy(maxExtract, simulate) : 0;
    }

    @Override
    public int getEnergyStored() {
        return this.delegate.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.delegate.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return this.mode().canExtract() && this.delegate.canExtract();
    }

    @Override
    public boolean canReceive() {
        return this.mode().canReceive() && this.delegate.canReceive();
    }

    private EnergyConnectionMode mode() {
        final EnergyConnectionMode mode = this.connectionMode.get();
        return mode == null ? EnergyConnectionMode.NONE : mode;
    }
}
