package cofh.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class TileEnergyHandler extends BlockEntity implements IEnergyHandler {
    protected final EnergyStorage storage = new EnergyStorage(32000);
    private final LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyCapability = LazyOptional.of(() -> this.storage);

    protected TileEnergyHandler(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        this.storage.writeToNBT(tag);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        this.storage.readFromNBT(tag);
    }

    @Override
    public boolean canConnectEnergy(final Direction from) {
        return from != null;
    }

    @Override
    public int receiveEnergy(final Direction from, final int maxReceive, final boolean simulate) {
        return this.storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(final Direction from, final int maxExtract, final boolean simulate) {
        return this.storage.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(final Direction from) {
        return this.storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(final Direction from) {
        return this.storage.getMaxEnergyStored();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.energyCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }
}
