package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.block.FurnaceIronBlock;
import com.hbm.ntm.common.item.BriquetteItemType;
import com.hbm.ntm.common.item.CokeItemType;
import com.hbm.ntm.common.item.MachineUpgradeItem;
import com.hbm.ntm.common.menu.FurnaceIronMenu;
import com.hbm.ntm.common.pollution.PollutionType;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class FurnaceIronBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL_LEFT = 1;
    public static final int SLOT_FUEL_RIGHT = 2;
    public static final int SLOT_OUTPUT = 3;
    public static final int SLOT_UPGRADE = 4;
    public static final int SLOT_COUNT = 5;

    private static final int[] SLOT_ACCESS = new int[]{SLOT_INPUT, SLOT_FUEL_LEFT, SLOT_FUEL_RIGHT, SLOT_OUTPUT};
    private static final int BASE_PROCESSING_TIME = 160;

    private int maxBurnTime;
    private int burnTime;
    private int progress;
    private int processingTime = BASE_PROCESSING_TIME;

    public FurnaceIronBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.FURNACE_IRON.get(), pos, state, SLOT_COUNT);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final FurnaceIronBlockEntity furnace) {
        boolean dirty = false;

        furnace.recomputeProcessingTime();

        if (furnace.burnTime <= 0 && furnace.tryConsumeFuel()) {
            dirty = true;
        }

        final @Nullable SmeltingRecipe recipe = furnace.findRecipe(furnace.getInternalItemHandler().getStackInSlot(SLOT_INPUT));
        final boolean canProcess = recipe != null && furnace.canProcess(recipe);
        final boolean active = canProcess && furnace.burnTime > 0;

        if (active) {
            furnace.progress++;
            furnace.burnTime = Math.max(0, furnace.burnTime - 1);
            furnace.emitPeriodicPollution(PollutionType.SOOT, MACHINE_SOOT_PER_SECOND);
            if (!furnace.isMuffled() && level.getGameTime() % 15L == 0L) {
                level.playSound(null, pos, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F, 0.5F + level.random.nextFloat() * 0.5F);
            }
            dirty = true;

            if (furnace.progress >= furnace.processingTime) {
                furnace.progress = 0;
                furnace.processRecipe(recipe);
            }
        } else if (furnace.progress > 0) {
            furnace.progress = 0;
            dirty = true;
        }

        furnace.updateLitState(active);

        if (dirty) {
            furnace.markChangedAndSync();
        }
        furnace.tickMachineStateSync();
    }

    private void recomputeProcessingTime() {
        final int speedLevel = this.countUpgrades(MachineUpgradeItem.UpgradeType.SPEED);
        this.processingTime = Math.max(1, BASE_PROCESSING_TIME - ((BASE_PROCESSING_TIME / 2) * speedLevel / 3));
    }

    private boolean tryConsumeFuel() {
        return this.tryConsumeFuelFromSlot(SLOT_FUEL_LEFT) || this.tryConsumeFuelFromSlot(SLOT_FUEL_RIGHT);
    }

    private boolean tryConsumeFuelFromSlot(final int slot) {
        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack stack = handler.getStackInSlot(slot);
        final int fuel = this.getFuelValue(stack);
        if (fuel <= 0) {
            return false;
        }

        this.maxBurnTime = fuel;
        this.burnTime = fuel;

        final ItemStack replacement = stack.hasCraftingRemainingItem() && stack.getCount() == 1
            ? stack.getCraftingRemainingItem()
            : ItemStack.EMPTY;

        final ItemStack remaining = stack.copy();
        remaining.shrink(1);

        if (!replacement.isEmpty()) {
            handler.setStackInSlot(slot, replacement);
        } else {
            handler.setStackInSlot(slot, remaining);
        }

        return true;
    }

    private int getFuelValue(final ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        final int base = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
        if (base <= 0) {
            return 0;
        }

        double modifier = 1.0D;
        final String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();

        if (stack.is(HbmItems.getCoke(CokeItemType.COAL).get())
            || stack.is(HbmItems.getCoke(CokeItemType.LIGNITE).get())
            || stack.is(HbmItems.getCoke(CokeItemType.PETROLEUM).get())) {
            modifier = 1.5D;
        } else if (stack.is(ItemTags.COALS)
            || stack.is(HbmItems.getMaterialPart(com.hbm.ntm.common.material.HbmMaterials.COAL, com.hbm.ntm.common.material.HbmMaterialShape.DUST).get())
            || stack.is(HbmItems.getMaterialPart(com.hbm.ntm.common.material.HbmMaterials.LIGNITE, com.hbm.ntm.common.material.HbmMaterialShape.GEM).get())
            || stack.is(HbmItems.getMaterialPart(com.hbm.ntm.common.material.HbmMaterials.LIGNITE, com.hbm.ntm.common.material.HbmMaterialShape.DUST).get())
            || stack.is(HbmItems.getBriquette(BriquetteItemType.COAL).get())
            || stack.is(HbmItems.getBriquette(BriquetteItemType.LIGNITE).get())) {
            modifier = 1.25D;
        }

        if (path.contains("solid_fuel") || path.contains("rocket_fuel") || path.contains("balefire")) {
            modifier = Math.max(modifier, 2.0D);
        }

        return Math.max(1, (int) Math.round(base * modifier));
    }

    private @Nullable SmeltingRecipe findRecipe(final ItemStack input) {
        if (this.level == null || input.isEmpty()) {
            return null;
        }
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input.copyWithCount(1)), this.level).orElse(null);
    }

    private boolean canProcess(final SmeltingRecipe recipe) {
        if (this.level == null || this.getInternalItemHandler().getStackInSlot(SLOT_INPUT).isEmpty()) {
            return false;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess());
        if (result.isEmpty()) {
            return false;
        }

        final ItemStack existing = this.getInternalItemHandler().getStackInSlot(SLOT_OUTPUT);
        if (existing.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(existing, result)) {
            return false;
        }
        return existing.getCount() + result.getCount() <= existing.getMaxStackSize();
    }

    private void processRecipe(final SmeltingRecipe recipe) {
        if (this.level == null) {
            return;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess()).copy();
        if (result.isEmpty()) {
            return;
        }

        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack existing = handler.getStackInSlot(SLOT_OUTPUT);

        if (existing.isEmpty()) {
            handler.setStackInSlot(SLOT_OUTPUT, result);
        } else {
            final ItemStack merged = existing.copy();
            merged.grow(result.getCount());
            handler.setStackInSlot(SLOT_OUTPUT, merged);
        }

        final ItemStack reducedInput = handler.getStackInSlot(SLOT_INPUT).copy();
        reducedInput.shrink(1);
        handler.setStackInSlot(SLOT_INPUT, reducedInput.isEmpty() ? ItemStack.EMPTY : reducedInput);
    }

    private void updateLitState(final boolean active) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        final BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof FurnaceIronBlock) || !state.hasProperty(FurnaceIronBlock.LIT)) {
            return;
        }
        if (state.getValue(FurnaceIronBlock.LIT) == active) {
            return;
        }

        this.level.setBlock(this.worldPosition, state.setValue(FurnaceIronBlock.LIT, active), 3);
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (slot == SLOT_INPUT) {
            return this.findRecipe(stack) != null;
        }
        if (slot == SLOT_FUEL_LEFT || slot == SLOT_FUEL_RIGHT) {
            return this.getFuelValue(stack) > 0;
        }
        if (slot == SLOT_UPGRADE) {
            return stack.getItem() instanceof MachineUpgradeItem upgrade
                && upgrade.type() == MachineUpgradeItem.UpgradeType.SPEED;
        }

        return false;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return slot == SLOT_OUTPUT;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return SLOT_ACCESS;
    }

    @Override
    protected boolean isUpgradeSlot(final int slot) {
        return slot == SLOT_UPGRADE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.FURNACE_IRON.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new FurnaceIronMenu(containerId, inventory, this);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putInt("maxBurnTime", this.maxBurnTime);
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("progress", this.progress);
        tag.putInt("processingTime", this.processingTime);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurnTime"));
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.progress = Math.max(0, tag.getInt("progress"));
        this.processingTime = Math.max(1, tag.getInt("processingTime"));
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putInt("maxBurnTime", this.maxBurnTime);
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("progress", this.progress);
        tag.putInt("processingTime", this.processingTime);
        tag.putBoolean("active", this.burnTime > 0 && this.progress > 0);
        final @Nullable SmeltingRecipe recipe = this.findRecipe(this.getInternalItemHandler().getStackInSlot(SLOT_INPUT));
        tag.putBoolean("canSmelt", recipe != null && this.canProcess(recipe));
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurnTime"));
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.progress = Math.max(0, tag.getInt("progress"));
        this.processingTime = Math.max(1, tag.getInt("processingTime"));
    }

    @Override
    public Map<MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades() {
        return Map.of(MachineUpgradeItem.UpgradeType.SPEED, 3);
    }
}
