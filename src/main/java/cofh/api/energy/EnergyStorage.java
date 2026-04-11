package cofh.api.energy;

import com.hbm.ntm.common.energy.HbmEnergyStorage;
import net.minecraft.nbt.CompoundTag;

public class EnergyStorage extends HbmEnergyStorage implements IEnergyStorage {

    public EnergyStorage(final int capacity) {
        this(capacity, capacity, capacity);
    }

    public EnergyStorage(final int capacity, final int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer);
    }

    public EnergyStorage(final int capacity, final int maxReceive, final int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public EnergyStorage readFromNBT(final CompoundTag nbt) {
        this.setEnergy(nbt.getInt("Energy"));
        return this;
    }

    public CompoundTag writeToNBT(final CompoundTag nbt) {
        nbt.putInt("Energy", this.getEnergyStored());
        return nbt;
    }

    public void setCapacity(final int capacity) {
        this.capacity = Math.max(0, capacity);
        if (this.energy > this.capacity) {
            this.setEnergy(this.capacity);
        }
    }

    public void setMaxTransfer(final int maxTransfer) {
        this.setMaxReceive(maxTransfer);
        this.setMaxExtract(maxTransfer);
    }

    public void setMaxReceive(final int maxReceive) {
        this.maxReceive = Math.max(0, maxReceive);
    }

    public void setMaxExtract(final int maxExtract) {
        this.maxExtract = Math.max(0, maxExtract);
    }

    public void setEnergyStored(final int energy) {
        this.setEnergy(energy);
    }

    public void modifyEnergyStored(final int energy) {
        this.setEnergy(this.getEnergyStored() + energy);
    }
}
