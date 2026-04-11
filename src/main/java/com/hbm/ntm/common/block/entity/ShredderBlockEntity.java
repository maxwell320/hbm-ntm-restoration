package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.config.ShredderMachineConfig;
import com.hbm.ntm.common.energy.EnergyConnectionMode;
import com.hbm.ntm.common.energy.HbmEnergyStorage;
import com.hbm.ntm.common.item.BatteryItem;
import com.hbm.ntm.common.item.ShredderBladesItem;
import com.hbm.ntm.common.menu.ShredderMenu;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.shredder.HbmShredderRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class ShredderBlockEntity extends MachineBlockEntity {

    public static final int SLOT_INPUT_START = 0;
    public static final int SLOT_INPUT_END = 9;
    public static final int INPUT_COUNT = 9;
    public static final int SLOT_OUTPUT_START = 9;
    public static final int SLOT_OUTPUT_END = 27;
    public static final int OUTPUT_COUNT = 18;
    public static final int SLOT_BLADE_LEFT = 27;
    public static final int SLOT_BLADE_RIGHT = 28;
    public static final int SLOT_BATTERY = 29;
    public static final int SLOT_COUNT = 30;

    public static final int MAX_POWER = 10_000;
    public static final int PROCESSING_SPEED = 60;
    public static final int POWER_PER_TICK = 5;

    private int progress;
    private int soundTimer;

    public ShredderBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_SHREDDER.get(), pos, state, SLOT_COUNT);
    }

    @Override
    protected @Nullable HbmEnergyStorage createEnergyStorage() {
        final int capacity = this.maxPower();
        return this.createSimpleEnergyStorage(capacity, capacity, 0);
    }

    @Override
    protected EnergyConnectionMode getEnergyConnectionMode(final @Nullable Direction side) {
        return EnergyConnectionMode.RECEIVE;
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final ShredderBlockEntity shredder) {
        HbmShredderRecipes.ensureInitialized();
        boolean dirty = false;

        if (shredder.tryChargeFromBattery()) {
            dirty = true;
        }

        final int powerPerTick = shredder.powerPerTick();
        final int processingSpeed = shredder.processingSpeed();
        final boolean hasPower = shredder.getStoredEnergy() >= powerPerTick;
        final boolean canProcess = hasPower && shredder.canProcess();

        if (shredder.progress == 0) {
            shredder.soundTimer = 0;
        }

        if (canProcess) {
            shredder.progress++;
            shredder.consumeEnergy(powerPerTick);
            dirty = true;

            if (shredder.progress >= processingSpeed) {
                shredder.damageBlades();
                shredder.processAllInputSlots();
                shredder.progress = 0;
            }

            if (shredder.soundTimer == 0) {
                level.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS,
                    shredder.getVolume(1.0F), 0.75F);
            }
            shredder.soundTimer++;
            if (shredder.soundTimer >= 50) {
                shredder.soundTimer = 0;
            }
        } else {
            if (shredder.progress > 0) {
                shredder.progress = 0;
                dirty = true;
            }
            shredder.soundTimer = 0;
        }

        if (dirty) {
            shredder.markChangedAndSync();
        }
        shredder.tickMachineStateSync();
    }

    private boolean canProcess() {
        final int gearLeft = getGearState(SLOT_BLADE_LEFT);
        final int gearRight = getGearState(SLOT_BLADE_RIGHT);

        if (gearLeft == 0 || gearLeft == 3) return false;
        if (gearRight == 0 || gearRight == 3) return false;

        for (int i = SLOT_INPUT_START; i < SLOT_INPUT_END; i++) {
            final ItemStack input = this.getInternalItemHandler().getStackInSlot(i);
            if (input.isEmpty()) continue;
            final ItemStack result = HbmShredderRecipes.getResult(input);
            if (result != null && canFitOutput(result)) {
                return true;
            }
        }
        return false;
    }

    private void processAllInputSlots() {
        final ItemStackHandler handler = this.getInternalItemHandler();
        for (int i = SLOT_INPUT_START; i < SLOT_INPUT_END; i++) {
            final ItemStack input = handler.getStackInSlot(i);
            if (input.isEmpty()) continue;

            final ItemStack result = HbmShredderRecipes.getResult(input);
            if (result == null || !canFitOutput(result)) continue;

            final ItemStack shrunk = input.copy();
            shrunk.shrink(1);
            handler.setStackInSlot(i, shrunk.isEmpty() ? ItemStack.EMPTY : shrunk);

            mergeIntoOutputSlots(result);
        }
    }

    private boolean canFitOutput(final ItemStack result) {
        final ItemStackHandler handler = this.getInternalItemHandler();
        for (int i = SLOT_OUTPUT_START; i < SLOT_OUTPUT_END; i++) {
            final ItemStack existing = handler.getStackInSlot(i);
            if (existing.isEmpty()) return true;
            if (ItemStack.isSameItemSameTags(existing, result)
                && existing.getCount() + result.getCount() <= existing.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private void mergeIntoOutputSlots(final ItemStack result) {
        final ItemStackHandler handler = this.getInternalItemHandler();
        int remaining = result.getCount();

        for (int i = SLOT_OUTPUT_START; i < SLOT_OUTPUT_END && remaining > 0; i++) {
            final ItemStack existing = handler.getStackInSlot(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, result)) {
                final int toAdd = Math.min(remaining, existing.getMaxStackSize() - existing.getCount());
                if (toAdd > 0) {
                    final ItemStack grown = existing.copy();
                    grown.grow(toAdd);
                    handler.setStackInSlot(i, grown);
                    remaining -= toAdd;
                }
            }
        }

        for (int i = SLOT_OUTPUT_START; i < SLOT_OUTPUT_END && remaining > 0; i++) {
            final ItemStack existing = handler.getStackInSlot(i);
            if (existing.isEmpty()) {
                final ItemStack placed = result.copy();
                placed.setCount(remaining);
                handler.setStackInSlot(i, placed);
                remaining = 0;
            }
        }
    }

    private void damageBlades() {
        damageBlade(SLOT_BLADE_LEFT);
        damageBlade(SLOT_BLADE_RIGHT);
    }

    private void damageBlade(final int slot) {
        final ItemStack blade = this.getInternalItemHandler().getStackInSlot(slot);
        if (blade.isEmpty() || !blade.isDamageableItem()) {
            return; // maxDamage == 0 = infinite (desh blades)
        }
        final ItemStack damaged = blade.copy();
        damaged.setDamageValue(damaged.getDamageValue() + 1);
        this.getInternalItemHandler().setStackInSlot(slot, damaged);
    }

    // 0=empty, 1=good, 2=worn, 3=broken
    public int getGearState(final int slot) {
        final ItemStack blade = this.getInternalItemHandler().getStackInSlot(slot);
        if (blade.isEmpty() || !(blade.getItem() instanceof ShredderBladesItem)) {
            return 0;
        }
        if (!blade.isDamageableItem()) {
            return 1;
        }
        if (blade.getDamageValue() >= blade.getMaxDamage()) return 3;
        if (blade.getDamageValue() >= blade.getMaxDamage() / 2) return 2;
        return 1;
    }

    public int getGearLeft() {
        return getGearState(SLOT_BLADE_LEFT);
    }

    public int getGearRight() {
        return getGearState(SLOT_BLADE_RIGHT);
    }

    private boolean tryChargeFromBattery() {
        final ItemStack battery = this.getInternalItemHandler().getStackInSlot(SLOT_BATTERY);
        if (battery.isEmpty()) return false;

        if (battery.getItem() instanceof final BatteryItem batteryItem) {
            final int stored = this.getStoredEnergy();
            final int space = this.getMaxStoredEnergy() - stored;
            if (space <= 0) return false;
            final int rate = batteryItem.getDischargeRate();
            final int charge = batteryItem.getStoredEnergy(battery);
            final int toDischarge = Math.min(Math.min(space, rate), charge);
            if (toDischarge <= 0) return false;
            batteryItem.withStoredEnergy(battery, charge - toDischarge);
            this.receiveEnergy(toDischarge);
            return true;
        }
        return false;
    }

    private void consumeEnergy(final int amount) {
        final var storage = this.getEnergyStorage(null);
        if (storage != null) {
            storage.extractEnergy(amount, false);
        }
    }

    private void receiveEnergy(final int amount) {
        final var storage = this.getEnergyStorage(null);
        if (storage != null) {
            storage.receiveEnergy(amount, false);
        }
    }

    private boolean isValidInputItem(final @NotNull ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() instanceof ShredderBladesItem) {
            return false;
        }
        HbmShredderRecipes.ensureInitialized();
        return HbmShredderRecipes.getResult(stack) != null;
    }

    private boolean canInsertInputSlot(final int slot, final @NotNull ItemStack stack) {
        if (!this.isValidInputItem(stack)) {
            return false;
        }

        final ItemStackHandler handler = this.getInternalItemHandler();
        final ItemStack target = handler.getStackInSlot(slot);
        if (target.isEmpty()) {
            return true;
        }

        final int size = target.getCount();
        for (int inputSlot = SLOT_INPUT_START; inputSlot < SLOT_INPUT_END; inputSlot++) {
            final ItemStack existing = handler.getStackInSlot(inputSlot);
            if (existing.isEmpty()) {
                return false;
            }
            if (ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < size) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (slot >= SLOT_INPUT_START && slot < SLOT_INPUT_END) return this.isValidInputItem(stack);
        if (slot == SLOT_BLADE_LEFT || slot == SLOT_BLADE_RIGHT) return stack.getItem() instanceof ShredderBladesItem;
        if (slot == SLOT_BATTERY) return stack.getItem() instanceof BatteryItem;
        return false;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        if (slot >= SLOT_INPUT_START && slot < SLOT_INPUT_END) {
            return this.canInsertInputSlot(slot, stack);
        }
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        if (slot >= SLOT_OUTPUT_START && slot < SLOT_OUTPUT_END) return true;
        if (slot == SLOT_BLADE_LEFT || slot == SLOT_BLADE_RIGHT) return getGearState(slot) == 3;
        return false;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setClientProgress(final int progress) {
        this.progress = progress;
    }

    public int configuredProcessingSpeed() {
        return this.processingSpeed();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.MACHINE_SHREDDER.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new ShredderMenu(containerId, inventory, this);
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putInt("progress", this.progress);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.progress = tag.getInt("progress");
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putInt("progress", this.progress);
        tag.putInt("energy", this.getStoredEnergy());
        tag.putInt("maxEnergy", this.getMaxStoredEnergy());
        tag.putInt("gearLeft", this.getGearLeft());
        tag.putInt("gearRight", this.getGearRight());
        tag.putInt("processingSpeed", this.processingSpeed());
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.progress = Math.max(0, tag.getInt("progress"));
    }

    private int maxPower() {
        return Math.max(1, ShredderMachineConfig.INSTANCE.maxPower());
    }

    private int processingSpeed() {
        return Math.max(1, ShredderMachineConfig.INSTANCE.processingSpeed());
    }

    private int powerPerTick() {
        return Math.max(1, ShredderMachineConfig.INSTANCE.powerPerTick());
    }
}
