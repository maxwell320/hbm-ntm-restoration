package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.energy.EnergyConnectionMode;
import com.hbm.ntm.common.energy.HbmEnergyStorage;
import com.hbm.ntm.common.energy.IEnergyGenerator;
import com.hbm.ntm.common.energy.IEnergyUser;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import java.util.EnumMap;
import java.util.Map;
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
public class CableBlockEntity extends BlockEntity implements IEnergyUser, IEnergyGenerator {
    private final int transferPerTick;
    private final CableEnergyStorage energyStorage;
    private final Map<Direction, LazyOptional<IEnergyStorage>> sidedCapabilities = new EnumMap<>(Direction.class);
    private LazyOptional<IEnergyStorage> energyCapability = LazyOptional.empty();
    @Nullable
    private Direction lastInputSide;
    private long lastInputTick;

    public CableBlockEntity(final BlockPos pos, final BlockState state) {
        this(pos, state, 20_000, 20_000);
    }

    public CableBlockEntity(final BlockPos pos, final BlockState state, final int bufferCapacity, final int transferPerTick) {
        super(HbmBlockEntityTypes.RED_CABLE.get(), pos, state);
        this.transferPerTick = Math.max(1, transferPerTick);
        this.energyStorage = new CableEnergyStorage(Math.max(1, bufferCapacity), this.transferPerTick);
        this.lastInputTick = Long.MIN_VALUE;
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final CableBlockEntity blockEntity) {
        blockEntity.pushEnergy(level, pos);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.createCapabilities();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyCapability.invalidate();
        this.sidedCapabilities.values().forEach(LazyOptional::invalidate);
        this.sidedCapabilities.clear();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        this.createCapabilities();
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", this.energyStorage.serializeNBT());
        if (this.lastInputSide != null) {
            tag.putInt("last_input_side", this.lastInputSide.get3DDataValue());
        }
        tag.putLong("last_input_tick", this.lastInputTick);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        final Tag energyTag = tag.get("energy");
        if (energyTag != null) {
            this.energyStorage.deserializeNBT(energyTag);
        }
        this.lastInputSide = tag.contains("last_input_side") ? Direction.from3DDataValue(tag.getInt("last_input_side")) : null;
        this.lastInputTick = tag.getLong("last_input_tick");
    }

    @Override
    public EnergyConnectionMode getEnergyConnection(@Nullable final Direction side) {
        return IEnergyUser.super.getEnergyConnection(side);
    }

    @Override
    public @Nullable IEnergyStorage getEnergyStorage(@Nullable final Direction side) {
        if (side == null) {
            return this.energyStorage;
        }
        final LazyOptional<IEnergyStorage> capability = this.sidedCapabilities.get(side);
        return capability == null ? null : capability.orElse(null);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return side == null ? this.energyCapability.cast() : this.sidedCapabilities.getOrDefault(side, LazyOptional.empty()).cast();
        }
        return super.getCapability(cap, side);
    }

    private void createCapabilities() {
        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
        this.sidedCapabilities.clear();
        for (final Direction direction : Direction.values()) {
            this.sidedCapabilities.put(direction, LazyOptional.of(() -> new CableSideEnergyStorage(this, direction)));
        }
    }

    private void pushEnergy(final Level level, final BlockPos pos) {
        int remaining = Math.min(this.transferPerTick, this.energyStorage.getEnergyStored());
        if (remaining <= 0) {
            return;
        }

        for (final Direction direction : Direction.values()) {
            if (remaining <= 0) {
                return;
            }
            if (direction == this.lastInputSide) {
                continue;
            }

            final BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }

            final IEnergyStorage target = neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).orElse(null);
            if (target == null || !target.canReceive()) {
                continue;
            }

            final int accepted = target.receiveEnergy(remaining, false);
            if (accepted <= 0) {
                continue;
            }

            this.energyStorage.extractEnergy(accepted, false);
            remaining -= accepted;
        }
    }

    private void markInput(final Direction side) {
        this.lastInputSide = side;
        if (this.level != null) {
            this.lastInputTick = this.level.getGameTime();
        }
        this.setChanged();
    }

    private static final class CableSideEnergyStorage implements IEnergyStorage {
        private final CableBlockEntity blockEntity;
        private final Direction side;

        private CableSideEnergyStorage(final CableBlockEntity blockEntity, final Direction side) {
            this.blockEntity = blockEntity;
            this.side = side;
        }

        @Override
        public int receiveEnergy(final int maxReceive, final boolean simulate) {
            final int received = this.blockEntity.energyStorage.receiveEnergy(Math.min(maxReceive, this.blockEntity.transferPerTick), simulate);
            if (received > 0 && !simulate) {
                this.blockEntity.markInput(this.side);
            }
            return received;
        }

        @Override
        public int extractEnergy(final int maxExtract, final boolean simulate) {
            return this.blockEntity.energyStorage.extractEnergy(Math.min(maxExtract, this.blockEntity.transferPerTick), simulate);
        }

        @Override
        public int getEnergyStored() {
            return this.blockEntity.energyStorage.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return this.blockEntity.energyStorage.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }

    private final class CableEnergyStorage extends HbmEnergyStorage {
        private CableEnergyStorage(final int capacity, final int maxTransfer) {
            super(capacity, maxTransfer, maxTransfer);
        }

        @Override
        protected void onEnergyChanged() {
            CableBlockEntity.this.setChanged();
        }
    }
}
