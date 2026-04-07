package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.fluid.HbmFluidTank;
import com.hbm.ntm.common.fluid.SidedFluidHandler;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Reusable fluid tank block entity base (Phase 9 step 094).
 * Provides HbmFluidTank storage, sided IFluidHandler capability exposure,
 * NBT persistence, comparator output, and configurable fill/drain modes per side.
 * Mirrors legacy TileEntityBarrel core responsibilities using modern Forge APIs.
 */
@SuppressWarnings("null")
public abstract class FluidTankBlockEntity extends BlockEntity {
    protected final HbmFluidTank tank;
    private final Map<Direction, LazyOptional<IFluidHandler>> sidedCapabilities = new EnumMap<>(Direction.class);
    private LazyOptional<IFluidHandler> tankCapability = LazyOptional.empty();
    private final int comparatorOutput;

    /**
     * Creates a tank block entity with the specified capacity.
     * @param type the block entity type
     * @param pos block position
     * @param state block state
     * @param capacity tank capacity in mB
     */
    protected FluidTankBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state, final int capacity) {
        this(type, pos, state, capacity, fluidStack -> true);
    }

    /**
     * Creates a tank block entity with capacity and fluid validator.
     * @param type the block entity type
     * @param pos block position
     * @param state block state
     * @param capacity tank capacity in mB
     * @param validator predicate to validate fluids for this tank
     */
    protected FluidTankBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state, final int capacity, final java.util.function.Predicate<FluidStack> validator) {
        super(type, pos, state);
        this.tank = new HbmFluidTank(capacity, validator, this::onTankContentsChanged);
        this.comparatorOutput = 0;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.createCapabilities();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.tankCapability.invalidate();
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
        final CompoundTag tankTag = new CompoundTag();
        this.tank.writeToNBT(tankTag);
        tag.put("tank", tankTag);
        tag.putInt("comparator_output", this.comparatorOutput);
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank", CompoundTag.TAG_COMPOUND)) {
            this.tank.readFromNBT(tag.getCompound("tank"));
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return side == null ? this.tankCapability.cast() : this.sidedCapabilities.getOrDefault(side, LazyOptional.empty()).cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Returns the tank's stored fluid amount.
     */
    public int getFluidAmount() {
        return this.tank.getFluidAmount();
    }

    /**
     * Returns the tank's capacity.
     */
    public int getCapacity() {
        return this.tank.getCapacity();
    }

    /**
     * Returns the stored fluid stack (may be empty).
     */
    public FluidStack getFluid() {
        return this.tank.getFluid().copy();
    }

    /**
     * Returns true if the tank is empty.
     */
    public boolean isEmpty() {
        return this.tank.isEmpty();
    }

    /**
     * Calculates comparator output based on fill level (0-15).
     * Matches legacy TileEntityBarrel#getComparatorPower behavior.
     */
    public int getComparatorOutput() {
        final int fill = this.tank.getFluidAmount();
        if (fill == 0) {
            return 0;
        }
        final int capacity = this.tank.getCapacity();
        if (capacity == 0) {
            return 0;
        }
        return Math.min(15, (int) ((double) fill / (double) capacity * 15.0) + 1);
    }

    /**
     * Server tick hook for subclasses. Call from block's getTicker.
     * Subclasses can override for push/pull fluid logic.
     */
    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final FluidTankBlockEntity blockEntity) {
        blockEntity.tick(level, pos, state);
    }

    /**
     * Override for per-tick logic (fluid pushing, mode updates, etc.).
     */
    protected void tick(final Level level, final BlockPos pos, final BlockState state) {
        // Subclasses override for push/pull behavior
    }

    /**
     * Called when tank contents change. Marks block entity dirty and updates comparators.
     */
    protected void onTankContentsChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    /**
     * Creates sided fluid capabilities. Subclasses can override fill/drain permissions per side.
     * Default: all sides allow fill and drain.
     */
    protected void createCapabilities() {
        this.tankCapability = LazyOptional.of(() -> this.tank);
        this.sidedCapabilities.clear();
        for (final Direction direction : Direction.values()) {
            final boolean canFill = canFillFromSide(direction);
            final boolean canDrain = canDrainFromSide(direction);
            this.sidedCapabilities.put(direction, LazyOptional.of(() -> new SidedFluidHandler(this.tank, canFill, canDrain)));
        }
    }

    /**
     * Override to control whether fluids can be inserted from a specific side.
     * @param side the direction from which fluid would be inserted
     * @return true if fill is allowed (default true)
     */
    protected boolean canFillFromSide(final Direction side) {
        return true;
    }

    /**
     * Override to control whether fluids can be extracted from a specific side.
     * @param side the direction from which fluid would be extracted
     * @return true if drain is allowed (default true)
     */
    protected boolean canDrainFromSide(final Direction side) {
        return true;
    }

    /**
     * Validates if a fluid can be stored in this tank.
     * @param stack fluid stack to validate
     * @return true if valid for this tank
     */
    public boolean isFluidValid(final FluidStack stack) {
        return this.tank.isFluidValid(stack);
    }

    /**
     * Direct fill method for internal use (returns amount filled).
     */
    protected int fillInternal(final FluidStack resource, final IFluidHandler.FluidAction action) {
        return this.tank.fill(resource, action);
    }

    /**
     * Direct drain method for internal use (returns drained fluid).
     */
    protected FluidStack drainInternal(final int maxDrain, final IFluidHandler.FluidAction action) {
        return this.tank.drain(maxDrain, action);
    }
}
