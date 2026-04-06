package com.hbm.ntm.common.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraftforge.energy.EnergyStorage;

@SuppressWarnings("null")
public class HbmEnergyStorage extends EnergyStorage {
    public HbmEnergyStorage(final int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public HbmEnergyStorage(final int capacity, final int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public HbmEnergyStorage(final int capacity, final int maxReceive, final int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public HbmEnergyStorage(final int capacity, final int maxReceive, final int maxExtract, final int energy) {
        super(capacity, maxReceive, maxExtract, Mth.clamp(energy, 0, capacity));
    }

    @Override
    public int receiveEnergy(final int maxReceive, final boolean simulate) {
        final int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            this.onEnergyChanged();
        }
        return received;
    }

    @Override
    public int extractEnergy(final int maxExtract, final boolean simulate) {
        final int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) {
            this.onEnergyChanged();
        }
        return extracted;
    }

    public int setEnergy(final int energy) {
        final int clamped = Mth.clamp(energy, 0, this.capacity);
        final int delta = clamped - this.energy;
        if (delta != 0) {
            this.energy = clamped;
            this.onEnergyChanged();
        }
        return delta;
    }

    public int addEnergy(final int energy, final boolean simulate) {
        return this.receiveEnergy(energy, simulate);
    }

    public int consumeEnergy(final int energy, final boolean simulate) {
        return this.extractEnergy(energy, simulate);
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    @Override
    public Tag serializeNBT() {
        return IntTag.valueOf(this.energy);
    }

    @Override
    public void deserializeNBT(final Tag tag) {
        if (tag instanceof final IntTag intTag) {
            this.setEnergy(intTag.getAsInt());
        } else if (tag instanceof final CompoundTag compoundTag) {
            this.setEnergy(compoundTag.getInt("energy"));
        }
    }

    protected void onEnergyChanged() {
    }
}
