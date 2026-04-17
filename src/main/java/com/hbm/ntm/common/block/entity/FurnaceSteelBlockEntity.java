package com.hbm.ntm.common.block.entity;

import api.hbm.tile.IHeatSource;
import com.hbm.ntm.common.block.FurnaceSteelBlock;
import com.hbm.ntm.common.menu.FurnaceSteelMenu;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class FurnaceSteelBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_INPUT_3 = 2;
    public static final int SLOT_OUTPUT_1 = 3;
    public static final int SLOT_OUTPUT_2 = 4;
    public static final int SLOT_OUTPUT_3 = 5;
    public static final int SLOT_COUNT = 6;

    private static final int[] SLOT_ACCESS = new int[]{0, 1, 2, 3, 4, 5};
    public static final int PROCESS_TIME = 40_000;
    private static final int MAX_HEAT = 100_000;
    private static final double DIFFUSION = 0.05D;

    private final int[] progress = new int[]{0, 0, 0};
    private final int[] bonus = new int[]{0, 0, 0};
    private final ItemStack[] lastInputs = new ItemStack[]{ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    private int heat;
    private boolean wasOn;

    public FurnaceSteelBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.FURNACE_STEEL.get(), pos, state, SLOT_COUNT);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final FurnaceSteelBlockEntity furnace) {
        boolean dirty = false;

        if (furnace.tryPullHeat()) {
            dirty = true;
        }

        boolean active = false;
        final int burn = Math.max(0, (furnace.heat - MAX_HEAT / 3) / 10);

        for (int lane = 0; lane < 3; lane++) {
            if (furnace.isInputChanged(lane)) {
                furnace.progress[lane] = 0;
                furnace.bonus[lane] = 0;
                dirty = true;
            }

            if (furnace.canSmelt(lane) && burn > 0) {
                furnace.progress[lane] += burn;
                furnace.heat = Math.max(0, furnace.heat - burn);
                active = true;
                dirty = true;
                furnace.emitPeriodicPollution(PollutionType.SOOT, MACHINE_SOOT_PER_SECOND * 2);
            }

            if (furnace.progress[lane] >= PROCESS_TIME) {
                furnace.smeltLane(lane);
                furnace.progress[lane] = 0;
                dirty = true;
            }

            furnace.lastInputs[lane] = furnace.getInternalItemHandler().getStackInSlot(lane).copy();
        }

        furnace.updateLitState(active);
        furnace.wasOn = active;

        if (dirty) {
            furnace.markChangedAndSync();
        }
        furnace.tickMachineStateSync();
    }

    private boolean isInputChanged(final int lane) {
        final ItemStack current = this.getInternalItemHandler().getStackInSlot(lane);
        final ItemStack previous = this.lastInputs[lane];

        if (current.isEmpty() && previous.isEmpty()) {
            return false;
        }
        if (current.isEmpty() != previous.isEmpty()) {
            return true;
        }

        return !ItemStack.isSameItemSameTags(current, previous);
    }

    private boolean tryPullHeat() {
        if (this.level == null) {
            return false;
        }

        final int oldHeat = this.heat;
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
                return this.heat != oldHeat;
            }
        }

        this.heat = Math.max(this.heat - Math.max(this.heat / 1000, 1), 0);
        return this.heat != oldHeat;
    }

    private boolean canSmelt(final int lane) {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(lane);
        if (this.heat < MAX_HEAT / 3 || input.isEmpty() || this.level == null) {
            return false;
        }

        final @Nullable SmeltingRecipe recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input.copyWithCount(1)), this.level).orElse(null);
        if (recipe == null) {
            return false;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess());
        if (result.isEmpty()) {
            return false;
        }

        final ItemStack output = this.getInternalItemHandler().getStackInSlot(lane + 3);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(output, result)) {
            return false;
        }

        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void smeltLane(final int lane) {
        if (this.level == null) {
            return;
        }

        final ItemStack input = this.getInternalItemHandler().getStackInSlot(lane);
        final @Nullable SmeltingRecipe recipe = this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input.copyWithCount(1)), this.level).orElse(null);
        if (recipe == null) {
            return;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess());
        if (result.isEmpty()) {
            return;
        }

        final ItemStackHandler handler = this.getInternalItemHandler();
        final int outputSlot = lane + 3;
        final ItemStack existing = handler.getStackInSlot(outputSlot);

        if (existing.isEmpty()) {
            handler.setStackInSlot(outputSlot, result.copy());
        } else {
            final ItemStack merged = existing.copy();
            merged.grow(result.getCount());
            handler.setStackInSlot(outputSlot, merged);
        }

        this.addBonus(input, lane);
        while (this.bonus[lane] >= 100) {
            final ItemStack loopExisting = handler.getStackInSlot(outputSlot);
            if (loopExisting.isEmpty() || !ItemStack.isSameItemSameTags(loopExisting, result) || loopExisting.getCount() + result.getCount() > loopExisting.getMaxStackSize()) {
                break;
            }
            final ItemStack grown = loopExisting.copy();
            grown.grow(result.getCount());
            handler.setStackInSlot(outputSlot, grown);
            this.bonus[lane] -= 100;
        }

        final ItemStack reduced = input.copy();
        reduced.shrink(1);
        handler.setStackInSlot(lane, reduced.isEmpty() ? ItemStack.EMPTY : reduced);
    }

    private void addBonus(final ItemStack input, final int lane) {
        if (input.is(net.minecraftforge.common.Tags.Items.ORES)) {
            this.bonus[lane] += 25;
            return;
        }
        if (input.is(net.minecraft.tags.ItemTags.LOGS)) {
            this.bonus[lane] += 50;
            return;
        }

        final String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(input.getItem()).getPath();
        if (path.contains("tar")) {
            this.bonus[lane] += 50;
        }
    }

    private void updateLitState(final boolean active) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        final BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof FurnaceSteelBlock) || !state.hasProperty(FurnaceSteelBlock.LIT)) {
            return;
        }
        if (state.getValue(FurnaceSteelBlock.LIT) == active) {
            return;
        }

        this.level.setBlock(this.worldPosition, state.setValue(FurnaceSteelBlock.LIT, active), 3);
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (slot < 3) {
            if (this.level == null) {
                return false;
            }
            return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack.copyWithCount(1)), this.level).isPresent();
        }

        return false;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return slot > 2;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return SLOT_ACCESS;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.FURNACE_STEEL.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new FurnaceSteelMenu(containerId, inventory, this);
    }

    public int[] getProgress() {
        return Arrays.copyOf(this.progress, this.progress.length);
    }

    public int[] getBonus() {
        return Arrays.copyOf(this.bonus, this.bonus.length);
    }

    public int getHeat() {
        return this.heat;
    }

    public int getMaxHeat() {
        return MAX_HEAT;
    }

    public boolean wasOn() {
        return this.wasOn;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putIntArray("progress", this.progress);
        tag.putIntArray("bonus", this.bonus);
        tag.putInt("heat", this.heat);
        tag.putBoolean("wasOn", this.wasOn);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        final int[] loadedProgress = tag.getIntArray("progress");
        final int[] loadedBonus = tag.getIntArray("bonus");
        for (int i = 0; i < 3; i++) {
            this.progress[i] = loadedProgress.length > i ? Math.max(0, loadedProgress[i]) : 0;
            this.bonus[i] = loadedBonus.length > i ? Math.max(0, loadedBonus[i]) : 0;
        }
        this.heat = Math.max(0, tag.getInt("heat"));
        this.wasOn = tag.getBoolean("wasOn");
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putIntArray("progress", this.progress);
        tag.putIntArray("bonus", this.bonus);
        tag.putInt("heat", this.heat);
        tag.putInt("maxHeat", MAX_HEAT);
        tag.putBoolean("wasOn", this.wasOn);
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        final int[] loadedProgress = tag.getIntArray("progress");
        final int[] loadedBonus = tag.getIntArray("bonus");
        for (int i = 0; i < 3; i++) {
            this.progress[i] = loadedProgress.length > i ? Math.max(0, loadedProgress[i]) : 0;
            this.bonus[i] = loadedBonus.length > i ? Math.max(0, loadedBonus[i]) : 0;
        }
        this.heat = Math.max(0, tag.getInt("heat"));
        this.wasOn = tag.getBoolean("wasOn");
    }

    @Override
    public Map<com.hbm.ntm.common.item.MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades() {
        return Map.of();
    }
}
