package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.fluid.HbmFluidTank;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class FluidDuctBlockEntity extends BlockEntity {
    public static final int BUFFER_CAPACITY = 1_000;
    public static final int TRANSFER_PER_TICK = 250;
    private static final String CONFIGURED_FLUID_TAG = "configured_fluid";
    private final HbmFluidTank buffer = new HbmFluidTank(BUFFER_CAPACITY, this::isValidFluid, this::onContentsChanged);
    @Nullable
    private ResourceLocation configuredFluidId;

    public FluidDuctBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.FLUID_DUCT.get(), pos, state);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final FluidDuctBlockEntity blockEntity) {
        blockEntity.pushFluid(level, pos);
        blockEntity.pullFluid(level, pos);
    }

    @Override
    protected void saveAdditional(final CompoundTag tag) {
        super.saveAdditional(tag);
        final CompoundTag tankTag = new CompoundTag();
        this.buffer.writeToNBT(tankTag);
        tag.put("buffer", tankTag);
        if (this.configuredFluidId != null) {
            tag.putString(CONFIGURED_FLUID_TAG, this.configuredFluidId.toString());
        }
    }

    @Override
    public void load(final CompoundTag tag) {
        super.load(tag);
        if (tag.contains("buffer", CompoundTag.TAG_COMPOUND)) {
            this.buffer.readFromNBT(tag.getCompound("buffer"));
        }
        this.configuredFluidId = tag.contains(CONFIGURED_FLUID_TAG) ? ResourceLocation.tryParse(tag.getString(CONFIGURED_FLUID_TAG)) : null;
    }

    public @Nullable ResourceLocation getConfiguredFluidId() {
        return this.configuredFluidId;
    }

    public void setConfiguredFluidId(@Nullable final ResourceLocation configuredFluidId) {
        if (Objects.equals(this.configuredFluidId, configuredFluidId)) {
            return;
        }
        this.configuredFluidId = configuredFluidId;
        if (!this.buffer.isEmpty() && !this.isValidFluid(this.buffer.getFluid())) {
            this.buffer.setFluidStack(FluidStack.EMPTY);
        }
        this.onContentsChanged();
    }

    private void pullFluid(final Level level, final BlockPos pos) {
        if (this.buffer.getFluidAmount() >= BUFFER_CAPACITY) {
            return;
        }
        for (final Direction direction : Direction.values()) {
            final BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null || neighbor instanceof FluidDuctBlockEntity) {
                continue;
            }
            final IFluidHandler handler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).orElse(null);
            if (handler == null) {
                continue;
            }
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                final FluidStack simulated = handler.drain(Math.min(TRANSFER_PER_TICK, BUFFER_CAPACITY - this.buffer.getFluidAmount()), IFluidHandler.FluidAction.SIMULATE);
                if (simulated.isEmpty() || !this.isValidFluid(simulated)) {
                    continue;
                }
                final int accepted = this.buffer.fill(simulated, IFluidHandler.FluidAction.EXECUTE);
                if (accepted <= 0) {
                    continue;
                }
                handler.drain(new FluidStack(simulated, accepted), IFluidHandler.FluidAction.EXECUTE);
                return;
            }
        }
    }

    private void pushFluid(final Level level, final BlockPos pos) {
        if (this.buffer.isEmpty()) {
            return;
        }
        final FluidStack stored = this.buffer.getFluid().copy();
        for (final Direction direction : Direction.values()) {
            if (stored.isEmpty()) {
                return;
            }
            final BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            if (neighbor instanceof final FluidDuctBlockEntity duct) {
                if (!Objects.equals(this.configuredFluidId, duct.configuredFluidId) && duct.configuredFluidId != null) {
                    continue;
                }
                final int moved = duct.receiveFromDuct(stored, TRANSFER_PER_TICK);
                if (moved > 0) {
                    this.buffer.drain(moved, IFluidHandler.FluidAction.EXECUTE);
                    stored.shrink(moved);
                }
                continue;
            }
            final IFluidHandler handler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).orElse(null);
            if (handler == null) {
                continue;
            }
            final int moved = handler.fill(new FluidStack(stored, Math.min(TRANSFER_PER_TICK, stored.getAmount())), IFluidHandler.FluidAction.EXECUTE);
            if (moved > 0) {
                this.buffer.drain(moved, IFluidHandler.FluidAction.EXECUTE);
                stored.shrink(moved);
            }
        }
    }

    private int receiveFromDuct(final FluidStack stack, final int maxAmount) {
        if (stack.isEmpty() || !this.isValidFluid(stack)) {
            return 0;
        }
        final int amount = Math.min(maxAmount, stack.getAmount());
        return this.buffer.fill(new FluidStack(stack, amount), IFluidHandler.FluidAction.EXECUTE);
    }

    private boolean isValidFluid(final FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        if (this.configuredFluidId == null) {
            return true;
        }
        if (!ForgeRegistries.FLUIDS.containsKey(this.configuredFluidId)) {
            return false;
        }
        return ForgeRegistries.FLUIDS.getValue(this.configuredFluidId) == stack.getFluid();
    }

    private void onContentsChanged() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}
