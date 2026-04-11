package cofh.api.energy;

import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemEnergyContainer extends Item implements IEnergyContainerItem {
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public ItemEnergyContainer(final Properties properties) {
        this(0, 0, 0, properties);
    }

    public ItemEnergyContainer(final int capacity, final Properties properties) {
        this(capacity, capacity, capacity, properties);
    }

    public ItemEnergyContainer(final int capacity, final int maxTransfer, final Properties properties) {
        this(capacity, maxTransfer, maxTransfer, properties);
    }

    public ItemEnergyContainer(final int capacity, final int maxReceive, final int maxExtract, final Properties properties) {
        super(properties);
        this.capacity = Math.max(0, capacity);
        this.maxReceive = Math.max(0, maxReceive);
        this.maxExtract = Math.max(0, maxExtract);
    }

    public ItemEnergyContainer setCapacity(final int capacity) {
        this.capacity = Math.max(0, capacity);
        return this;
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

    @Override
    public int receiveEnergy(final ItemStack container, final int maxReceive, final boolean simulate) {
        final int energy = this.getEnergyStored(container);
        final int energyReceived = Math.min(this.capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate && energyReceived > 0) {
            container.getOrCreateTag().putInt("Energy", energy + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(final ItemStack container, final int maxExtract, final boolean simulate) {
        final int energy = this.getEnergyStored(container);
        final int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate && energyExtracted > 0) {
            container.getOrCreateTag().putInt("Energy", energy - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(final ItemStack container) {
        if (!container.hasTag()) {
            return 0;
        }
        return Mth.clamp(container.getOrCreateTag().getInt("Energy"), 0, this.capacity);
    }

    @Override
    public int getMaxEnergyStored(final ItemStack container) {
        return this.capacity;
    }
}
