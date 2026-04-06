package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.energy.IEnergyGenerator;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
public class CreativeEnergySourceBlockEntity extends BlockEntity implements IEnergyGenerator {
    private static final int TRANSFER_PER_TICK = 100_000;
    private static final IEnergyStorage ENERGY_STORAGE = new CreativeEnergyStorage();
    private LazyOptional<IEnergyStorage> energyCapability = LazyOptional.empty();

    public CreativeEnergySourceBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.CREATIVE_ENERGY_SOURCE.get(), pos, state);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final CreativeEnergySourceBlockEntity blockEntity) {
        for (final Direction direction : Direction.values()) {
            final BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(storage -> {
                if (storage.canReceive()) {
                    storage.receiveEnergy(TRANSFER_PER_TICK, false);
                }
            });
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.energyCapability = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.energyCapability = LazyOptional.of(() -> ENERGY_STORAGE);
    }

    @Override
    public @Nullable IEnergyStorage getEnergyStorage(@Nullable final Direction side) {
        return ENERGY_STORAGE;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    private static final class CreativeEnergyStorage implements IEnergyStorage {
        @Override
        public int receiveEnergy(final int maxReceive, final boolean simulate) {
            return 0;
        }

        @Override
        public int extractEnergy(final int maxExtract, final boolean simulate) {
            return Math.max(0, maxExtract);
        }

        @Override
        public int getEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxEnergyStored() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    }
}
