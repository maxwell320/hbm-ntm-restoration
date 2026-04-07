package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.energy.HbmEnergyStorage;
import com.hbm.ntm.common.energy.IEnergyGenerator;
import com.hbm.ntm.common.energy.IEnergyUser;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BatteryBlockEntity extends BlockEntity implements IEnergyUser, IEnergyGenerator {
    public static final int CAPACITY = 1_000_000;
    public static final int MAX_RECEIVE = CAPACITY / 200;
    public static final int MAX_EXTRACT = CAPACITY / 600;

    private final BatteryEnergyStorage energyStorage = new BatteryEnergyStorage();
    private LazyOptional<IEnergyStorage> energyCapability = LazyOptional.empty();

    public BatteryBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_BATTERY.get(), pos, state);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final BatteryBlockEntity blockEntity) {
        blockEntity.pushEnergy(level, pos);
    }

    public int getStoredEnergy() {
        return this.energyStorage.getEnergyStored();
    }

    public int getComparatorOutput() {
        final int stored = this.energyStorage.getEnergyStored();
        if (stored <= 0) {
            return 0;
        }
        return Math.min(15, Math.max(1, (int) Math.ceil(stored * 15.0D / this.energyStorage.getMaxEnergyStored())));
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", this.energyStorage.serializeNBT());
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        final Tag energyTag = tag.get("energy");
        if (energyTag != null) {
            this.energyStorage.deserializeNBT(energyTag);
        }
    }

    @Override
    public @Nullable IEnergyStorage getEnergyStorage(@Nullable final Direction side) {
        return this.energyStorage;
    }

    @Override
    public com.hbm.ntm.common.energy.EnergyConnectionMode getEnergyConnection(@Nullable final Direction side) {
        return IEnergyUser.super.getEnergyConnection(side);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    private void pushEnergy(final Level level, final BlockPos pos) {
        int remaining = Math.min(this.energyStorage.getEnergyStored(), this.energyStorage.getMaxExtract());
        if (remaining <= 0) {
            return;
        }

        for (final Direction direction : Direction.values()) {
            if (remaining <= 0) {
                return;
            }

            final BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }

            final IEnergyStorage target = neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).orElse(null);
            if (target == null || !target.canReceive()) {
                continue;
            }

            final int accepted = target.receiveEnergy(Math.min(remaining, this.energyStorage.getMaxExtract()), false);
            if (accepted <= 0) {
                continue;
            }

            this.energyStorage.extractEnergy(accepted, false);
            remaining -= accepted;
        }
    }

    private final class BatteryEnergyStorage extends HbmEnergyStorage {
        private BatteryEnergyStorage() {
            super(CAPACITY, MAX_RECEIVE, MAX_EXTRACT);
        }

        @Override
        protected void onEnergyChanged() {
            BatteryBlockEntity.this.setChanged();
            if (BatteryBlockEntity.this.level != null) {
                final BlockState state = BatteryBlockEntity.this.getBlockState();
                BatteryBlockEntity.this.level.updateNeighbourForOutputSignal(BatteryBlockEntity.this.worldPosition, state.getBlock());
            }
        }
    }
}
