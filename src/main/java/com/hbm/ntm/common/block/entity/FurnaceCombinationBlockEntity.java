package com.hbm.ntm.common.block.entity;

import api.hbm.tile.IHeatSource;
import com.hbm.ntm.common.combination.CombinationOutput;
import com.hbm.ntm.common.combination.HbmCombinationRecipes;
import com.hbm.ntm.common.menu.FurnaceCombinationMenu;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class FurnaceCombinationBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_CONTAINER_IN = 2;
    public static final int SLOT_CONTAINER_OUT = 3;
    public static final int SLOT_COUNT = 4;

    public static final int TANK_OUTPUT = 0;
    public static final int TANK_CAPACITY = 24_000;

    private static final int[] SLOT_ACCESS = new int[]{0, 1, 2, 3};
    private static final int PROCESS_TIME = 20_000;
    private static final int MAX_HEAT = 100_000;
    private static final double DIFFUSION = 0.25D;

    private int progress;
    private int heat;

    public FurnaceCombinationBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.FURNACE_COMBINATION.get(), pos, state, SLOT_COUNT);
    }

    @Override
    protected com.hbm.ntm.common.fluid.HbmFluidTank[] createFluidTanks() {
        return new com.hbm.ntm.common.fluid.HbmFluidTank[]{
            this.createFluidTank(TANK_CAPACITY, stack -> true)
        };
    }

    @Override
    protected boolean canFillFromSide(final Direction side) {
        return false;
    }

    @Override
    protected boolean canDrainFromSide(final Direction side) {
        return true;
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final FurnaceCombinationBlockEntity furnace) {
        boolean changed = furnace.tryPullHeat();

        if (level.getGameTime() % 20L == 0L) {
            changed |= furnace.exportTankToNeighbors(TANK_OUTPUT, 250);
        }

        furnace.handleFluidContainerSlots();

        final boolean canSmelt = furnace.canProcess();
        boolean active = false;

        if (canSmelt) {
            final int burn = furnace.heat / 100;
            if (burn > 0) {
                furnace.progress += burn;
                furnace.heat = Math.max(0, furnace.heat - burn);
                furnace.emitPeriodicPollution(PollutionType.SOOT, MACHINE_SOOT_PER_SECOND * 3);
                active = true;
                changed = true;

                if (furnace.progress >= PROCESS_TIME) {
                    furnace.progress -= PROCESS_TIME;
                    furnace.processRecipe();
                    changed = true;
                }
            }
        } else if (furnace.progress > 0) {
            furnace.progress = 0;
            changed = true;
        }

        if (changed) {
            furnace.markChangedAndSync();
        }
        furnace.tickMachineStateSync();
    }

    private void handleFluidContainerSlots() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_CONTAINER_IN);
        if (input.isEmpty()) {
            return;
        }

        final com.hbm.ntm.common.fluid.HbmFluidTank tank = this.getFluidTank(TANK_OUTPUT);
        if (tank == null || tank.isEmpty()) {
            return;
        }

        final FluidActionResult simulated = FluidUtil.tryFillContainer(input.copyWithCount(1), tank, Integer.MAX_VALUE, null, false);
        if (!simulated.isSuccess() || !this.canMoveToOutput(SLOT_CONTAINER_OUT, simulated.getResult())) {
            return;
        }

        final FluidActionResult executed = FluidUtil.tryFillContainer(input.copyWithCount(1), tank, Integer.MAX_VALUE, null, true);
        if (!executed.isSuccess()) {
            return;
        }

        this.getInternalItemHandler().extractItem(SLOT_CONTAINER_IN, 1, false);
        this.insertOutput(SLOT_CONTAINER_OUT, executed.getResult());
    }

    private boolean canMoveToOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.getInternalItemHandler().getStackInSlot(outputSlot);
        return existing.isEmpty()
            || ItemStack.isSameItemSameTags(existing, result) && existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private void insertOutput(final int outputSlot, final ItemStack result) {
        final ItemStack existing = this.getInternalItemHandler().getStackInSlot(outputSlot);
        if (existing.isEmpty()) {
            this.getInternalItemHandler().setStackInSlot(outputSlot, result.copy());
            return;
        }
        final ItemStack grown = existing.copy();
        grown.grow(result.getCount());
        this.getInternalItemHandler().setStackInSlot(outputSlot, grown);
    }

    private boolean tryPullHeat() {
        if (this.level == null) {
            return false;
        }

        final int old = this.heat;
        if (this.heat >= MAX_HEAT) {
            return false;
        }

        final BlockEntity below = this.level.getBlockEntity(this.worldPosition.below());
        if (below instanceof IHeatSource source) {
            int diff = source.getHeatStored() - this.heat;
            if (diff > 0) {
                diff = (int) Math.ceil(diff * DIFFUSION);
                source.useUpHeat(diff);
                this.heat = Math.min(MAX_HEAT, this.heat + diff);
                return this.heat != old;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
        return this.heat != old;
    }

    private boolean canProcess() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return false;
        }

        final CombinationOutput output = HbmCombinationRecipes.getOutput(input);
        if (output.isEmpty()) {
            return false;
        }

        final ItemStack itemOutput = output.itemOutput();
        if (!itemOutput.isEmpty()) {
            final ItemStack currentOutput = this.getInternalItemHandler().getStackInSlot(SLOT_OUTPUT);
            if (!currentOutput.isEmpty() && (!ItemStack.isSameItemSameTags(currentOutput, itemOutput)
                || currentOutput.getCount() + itemOutput.getCount() > currentOutput.getMaxStackSize())) {
                return false;
            }
        }

        final FluidStack fluidOutput = output.fluidOutput();
        if (!fluidOutput.isEmpty()) {
            final com.hbm.ntm.common.fluid.HbmFluidTank tank = this.getFluidTank(TANK_OUTPUT);
            if (tank == null || tank.fill(fluidOutput.copy(), IFluidHandler.FluidAction.SIMULATE) < fluidOutput.getAmount()) {
                return false;
            }
        }

        return true;
    }

    private void processRecipe() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return;
        }

        final CombinationOutput output = HbmCombinationRecipes.getOutput(input);
        if (output.isEmpty()) {
            return;
        }

        if (!output.itemOutput().isEmpty()) {
            this.insertOutput(SLOT_OUTPUT, output.itemOutput());
        }

        if (!output.fluidOutput().isEmpty()) {
            final com.hbm.ntm.common.fluid.HbmFluidTank tank = this.getFluidTank(TANK_OUTPUT);
            if (tank != null) {
                tank.fill(output.fluidOutput().copy(), IFluidHandler.FluidAction.EXECUTE);
            }
        }

        final ItemStack reduced = input.copy();
        reduced.shrink(1);
        this.getInternalItemHandler().setStackInSlot(SLOT_INPUT, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
    }

    private boolean canFillContainerFromTank(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        final com.hbm.ntm.common.fluid.HbmFluidTank tank = this.getFluidTank(TANK_OUTPUT);
        if (tank == null || tank.isEmpty()) {
            return false;
        }

        final LazyOptional<IFluidHandlerItem> optional = FluidUtil.getFluidHandler(stack.copyWithCount(1));
        if (!optional.isPresent()) {
            return false;
        }

        final IFluidHandlerItem itemHandler = optional.resolve().orElse(null);
        if (itemHandler == null) {
            return false;
        }

        final FluidStack reference = tank.getFluid().copy();
        if (reference.isEmpty()) {
            return false;
        }

        return itemHandler.fill(reference, IFluidHandler.FluidAction.SIMULATE) > 0;
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (slot) {
            case SLOT_INPUT -> !HbmCombinationRecipes.getOutput(stack).isEmpty();
            case SLOT_CONTAINER_IN -> this.canFillContainerFromTank(stack);
            default -> false;
        };
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return slot == SLOT_OUTPUT || slot == SLOT_CONTAINER_OUT;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return SLOT_ACCESS;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.FURNACE_COMBINATION.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new FurnaceCombinationMenu(containerId, inventory, this);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getHeat() {
        return this.heat;
    }

    public int getMaxHeat() {
        return MAX_HEAT;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putInt("progress", this.progress);
        tag.putInt("heat", this.heat);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.progress = Math.max(0, tag.getInt("progress"));
        this.heat = Math.max(0, tag.getInt("heat"));
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        final com.hbm.ntm.common.fluid.HbmFluidTank tank = this.getFluidTank(TANK_OUTPUT);
        tag.putInt("progress", this.progress);
        tag.putInt("heat", this.heat);
        tag.putInt("maxHeat", MAX_HEAT);
        tag.putInt("tankAmount", tank == null ? 0 : tank.getFluidAmount());
        tag.putInt("tankCapacity", tank == null ? TANK_CAPACITY : tank.getCapacity());
        tag.putString("tankName", tank == null || tank.isEmpty() ? "Empty" : tank.getFluid().getDisplayName().getString());
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.progress = Math.max(0, tag.getInt("progress"));
        this.heat = Math.max(0, tag.getInt("heat"));
    }

    @Override
    public Map<com.hbm.ntm.common.item.MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades() {
        return Map.of();
    }
}
