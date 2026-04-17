package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.block.BrickFurnaceBlock;
import com.hbm.ntm.common.menu.BrickFurnaceMenu;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BrickFurnaceBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;
    public static final int SLOT_ASH = 3;
    public static final int SLOT_COUNT = 4;

    private static final int[] SLOTS_TOP = new int[]{SLOT_INPUT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_OUTPUT, SLOT_FUEL, SLOT_ASH};
    private static final int[] SLOTS_SIDE = new int[]{SLOT_FUEL};
    private static final int PROCESS_TIME = 200;
    private static final int ASH_THRESHOLD = 2_000;

    private int burnTime;
    private int maxBurnTime;
    private int progress;
    private int ashLevelWood;
    private int ashLevelCoal;
    private int ashLevelMisc;

    public BrickFurnaceBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_FURNACE_BRICK.get(), pos, state, SLOT_COUNT);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final BrickFurnaceBlockEntity furnace) {
        boolean dirty = false;
        final boolean wasBurning = furnace.burnTime > 0;

        if (furnace.burnTime > 0) {
            furnace.burnTime--;
            dirty = true;
        }

        final ItemStack input = furnace.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        final ItemStack fuel = furnace.getInternalItemHandler().getStackInSlot(SLOT_FUEL);
        if (furnace.burnTime != 0 || (!fuel.isEmpty() && !input.isEmpty())) {
            if (furnace.burnTime == 0 && furnace.canSmelt()) {
                final int consumed = furnace.consumeFuel();
                if (consumed > 0) {
                    furnace.maxBurnTime = consumed;
                    furnace.burnTime = consumed;
                    dirty = true;
                }
            }

            if (furnace.burnTime > 0 && furnace.canSmelt()) {
                furnace.progress += furnace.getBurnSpeed();
                dirty = true;

                if (furnace.progress >= PROCESS_TIME) {
                    furnace.progress = 0;
                    furnace.smeltItem();
                }
            } else if (furnace.progress > 0) {
                furnace.progress = 0;
                dirty = true;
            }
        }

        if (wasBurning != furnace.burnTime > 0) {
            furnace.updateLitState(furnace.burnTime > 0);
            dirty = true;
        }

        if (dirty) {
            furnace.markChangedAndSync();
        }
        furnace.tickMachineStateSync();
    }

    private int consumeFuel() {
        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack stack = handler.getStackInSlot(SLOT_FUEL);
        if (stack.isEmpty()) {
            return 0;
        }

        final int burn = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
        if (burn <= 0) {
            return 0;
        }

        this.addAshFromFuel(stack, burn);

        final ItemStack replacement = stack.hasCraftingRemainingItem() && stack.getCount() == 1
            ? stack.getCraftingRemainingItem()
            : ItemStack.EMPTY;

        final ItemStack remaining = stack.copy();
        remaining.shrink(1);

        if (!replacement.isEmpty()) {
            handler.setStackInSlot(SLOT_FUEL, replacement);
        } else {
            handler.setStackInSlot(SLOT_FUEL, remaining);
        }

        return burn;
    }

    private void addAshFromFuel(final ItemStack stack, final int burn) {
        if (this.isWoodFuel(stack)) {
            this.ashLevelWood += burn;
        } else if (this.isCoalFuel(stack)) {
            this.ashLevelCoal += burn;
        } else {
            this.ashLevelMisc += burn;
        }

        while (this.ashLevelWood >= ASH_THRESHOLD && this.processAsh()) {
            this.ashLevelWood -= ASH_THRESHOLD;
        }
        while (this.ashLevelCoal >= ASH_THRESHOLD && this.processAsh()) {
            this.ashLevelCoal -= ASH_THRESHOLD;
        }
        while (this.ashLevelMisc >= ASH_THRESHOLD && this.processAsh()) {
            this.ashLevelMisc -= ASH_THRESHOLD;
        }
    }

    private boolean processAsh() {
        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack ash = handler.getStackInSlot(SLOT_ASH);
        final ItemStack ashItem = new ItemStack(HbmItems.POWDER_ASH.get());

        if (ash.isEmpty()) {
            handler.setStackInSlot(SLOT_ASH, ashItem);
            return true;
        }
        if (!ItemStack.isSameItemSameTags(ash, ashItem) || ash.getCount() >= ash.getMaxStackSize()) {
            return false;
        }

        final ItemStack grown = ash.copy();
        grown.grow(1);
        handler.setStackInSlot(SLOT_ASH, grown);
        return true;
    }

    private boolean isWoodFuel(final ItemStack stack) {
        return stack.is(ItemTags.LOGS)
            || stack.is(Items.CHARCOAL)
            || stack.is(Blocks.OAK_PLANKS.asItem())
            || stack.is(Blocks.BIRCH_PLANKS.asItem())
            || stack.is(Blocks.SPRUCE_PLANKS.asItem())
            || stack.is(Blocks.JUNGLE_PLANKS.asItem())
            || stack.is(Blocks.ACACIA_PLANKS.asItem())
            || stack.is(Blocks.DARK_OAK_PLANKS.asItem())
            || stack.is(Blocks.MANGROVE_PLANKS.asItem())
            || stack.is(Blocks.CHERRY_PLANKS.asItem())
            || stack.is(Blocks.BAMBOO_PLANKS.asItem())
            || stack.is(Blocks.CRIMSON_PLANKS.asItem())
            || stack.is(Blocks.WARPED_PLANKS.asItem());
    }

    private boolean isCoalFuel(final ItemStack stack) {
        return stack.is(ItemTags.COALS);
    }

    private int getBurnSpeed() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return 1;
        }

        if (input.is(Items.CLAY_BALL) || input.is(Blocks.NETHERRACK.asItem())) {
            return 4;
        }
        if (input.is(Blocks.COBBLESTONE.asItem()) || input.is(Blocks.SAND.asItem()) || input.is(ItemTags.LOGS)) {
            return 2;
        }

        return 1;
    }

    private boolean canSmelt() {
        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            return false;
        }

        final @Nullable SmeltingRecipe recipe = this.findRecipe(input);
        if (recipe == null || this.level == null) {
            return false;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess());
        if (result.isEmpty()) {
            return false;
        }

        final ItemStack output = this.getInternalItemHandler().getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(output, result)) {
            return false;
        }

        final int combined = output.getCount() + result.getCount();
        return combined <= this.getInternalItemHandler().getSlotLimit(SLOT_OUTPUT)
            && combined <= output.getMaxStackSize();
    }

    private @Nullable SmeltingRecipe findRecipe(final ItemStack input) {
        if (this.level == null || input.isEmpty()) {
            return null;
        }
        return this.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input.copyWithCount(1)), this.level).orElse(null);
    }

    private void smeltItem() {
        if (this.level == null || !this.canSmelt()) {
            return;
        }

        final ItemStack input = this.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        final @Nullable SmeltingRecipe recipe = this.findRecipe(input);
        if (recipe == null) {
            return;
        }

        final ItemStack result = recipe.getResultItem(this.level.registryAccess()).copy();
        if (result.isEmpty()) {
            return;
        }

        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack output = handler.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            handler.setStackInSlot(SLOT_OUTPUT, result);
        } else if (ItemStack.isSameItemSameTags(output, result)) {
            final ItemStack merged = output.copy();
            merged.grow(result.getCount());
            handler.setStackInSlot(SLOT_OUTPUT, merged);
        }

        final ItemStack reducedInput = input.copy();
        reducedInput.shrink(1);
        handler.setStackInSlot(SLOT_INPUT, reducedInput.isEmpty() ? ItemStack.EMPTY : reducedInput);
    }

    private void updateLitState(final boolean active) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        final BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof BrickFurnaceBlock) || !state.hasProperty(BrickFurnaceBlock.LIT)) {
            return;
        }
        if (state.getValue(BrickFurnaceBlock.LIT) == active) {
            return;
        }

        this.level.setBlock(this.worldPosition, state.setValue(BrickFurnaceBlock.LIT, active), 3);
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (slot >= SLOT_OUTPUT) {
            return false;
        }
        if (slot == SLOT_FUEL) {
            return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
        }
        return true;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return slot == SLOT_ASH;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        if (side == Direction.UP) {
            return SLOTS_TOP;
        }
        if (side == Direction.DOWN) {
            return SLOTS_BOTTOM;
        }
        return SLOTS_SIDE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.MACHINE_FURNACE_BRICK.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new BrickFurnaceMenu(containerId, inventory, this);
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public int getMaxBurnTime() {
        return this.maxBurnTime;
    }

    public int getProgress() {
        return this.progress;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("maxBurn", this.maxBurnTime);
        tag.putInt("progress", this.progress);
        tag.putInt("ashWood", this.ashLevelWood);
        tag.putInt("ashCoal", this.ashLevelCoal);
        tag.putInt("ashMisc", this.ashLevelMisc);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurn"));
        this.progress = Math.max(0, tag.getInt("progress"));
        this.ashLevelWood = Math.max(0, tag.getInt("ashWood"));
        this.ashLevelCoal = Math.max(0, tag.getInt("ashCoal"));
        this.ashLevelMisc = Math.max(0, tag.getInt("ashMisc"));
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("maxBurn", this.maxBurnTime);
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.burnTime = Math.max(0, tag.getInt("burnTime"));
        this.maxBurnTime = Math.max(0, tag.getInt("maxBurn"));
        this.progress = Math.max(0, tag.getInt("progress"));
    }

    @Override
    public Map<com.hbm.ntm.common.item.MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades() {
        return Map.of();
    }
}
