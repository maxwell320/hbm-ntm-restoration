package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.centrifuge.HbmCentrifugeRecipes;
import com.hbm.ntm.common.config.CentrifugeMachineConfig;
import com.hbm.ntm.common.energy.EnergyConnectionMode;
import com.hbm.ntm.common.energy.HbmEnergyStorage;
import com.hbm.ntm.common.item.BatteryItem;
import com.hbm.ntm.common.item.MachineUpgradeItem;
import com.hbm.ntm.common.menu.CentrifugeMenu;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmSoundEvents;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class CentrifugeBlockEntity extends MachineBlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_BATTERY = 1;
    public static final int SLOT_OUTPUT_1 = 2;
    public static final int SLOT_OUTPUT_2 = 3;
    public static final int SLOT_OUTPUT_3 = 4;
    public static final int SLOT_OUTPUT_4 = 5;
    public static final int SLOT_UPGRADE_1 = 6;
    public static final int SLOT_UPGRADE_2 = 7;
    public static final int SLOT_COUNT = 8;

    private static final int[] SLOT_ACCESS = new int[]{SLOT_INPUT, SLOT_OUTPUT_1, SLOT_OUTPUT_2, SLOT_OUTPUT_3, SLOT_OUTPUT_4};
    private static final int OPERATE_SOUND_INTERVAL = 40;

    private int progress;
    private int lastConsumption;
    private int operateSoundTimer;

    public CentrifugeBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_CENTRIFUGE.get(), pos, state, SLOT_COUNT);
    }

    @Override
    protected @Nullable HbmEnergyStorage createEnergyStorage() {
        final int capacity = Math.max(1, CentrifugeMachineConfig.INSTANCE.maxPower());
        return this.createSimpleEnergyStorage(capacity, capacity, 0);
    }

    @Override
    protected EnergyConnectionMode getEnergyConnectionMode(final @Nullable Direction side) {
        return EnergyConnectionMode.RECEIVE;
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final CentrifugeBlockEntity centrifuge) {
        boolean dirty = false;

        if (centrifuge.tryChargeFromBattery()) {
            dirty = true;
        }

        final ItemStack input = centrifuge.getInternalItemHandler().getStackInSlot(SLOT_INPUT);
        final @Nullable HbmCentrifugeRecipes.CentrifugeRecipe recipe = HbmCentrifugeRecipes.findRecipe(input).orElse(null);

        final int speedLevel = centrifuge.countUpgrades(MachineUpgradeItem.UpgradeType.SPEED);
        final int powerLevel = centrifuge.countUpgrades(MachineUpgradeItem.UpgradeType.POWER);
        final int overdriveLevel = centrifuge.countUpgrades(MachineUpgradeItem.UpgradeType.OVERDRIVE);

        int speed = 1 + speedLevel;
        speed *= 1 + overdriveLevel * 5;

        int consumption = Math.max(1, CentrifugeMachineConfig.INSTANCE.basePowerPerTick());
        consumption += speedLevel * CentrifugeMachineConfig.INSTANCE.basePowerPerTick();
        consumption += overdriveLevel * CentrifugeMachineConfig.INSTANCE.basePowerPerTick() * 50;
        consumption /= 1 + Math.max(0, powerLevel);
        centrifuge.lastConsumption = Math.max(1, consumption);

        final boolean hasPower = centrifuge.getStoredEnergy() > 0;
        final boolean canProcess = recipe != null && centrifuge.canProcess(recipe);

        if (hasPower && canProcess) {
            centrifuge.progress += Math.max(1, speed);
            centrifuge.consumeEnergy(centrifuge.lastConsumption);
            centrifuge.tickOperateSound(level, pos);
            dirty = true;

            if (centrifuge.progress >= centrifuge.processingSpeed()) {
                centrifuge.progress = 0;
                centrifuge.processRecipe(recipe);
            }
        } else if (centrifuge.progress > 0) {
            centrifuge.progress = 0;
            centrifuge.operateSoundTimer = 0;
            dirty = true;
        } else {
            centrifuge.operateSoundTimer = 0;
        }

        if (dirty) {
            centrifuge.markChangedAndSync();
        }
        centrifuge.tickMachineStateSync();
    }

    private boolean canProcess(final HbmCentrifugeRecipes.CentrifugeRecipe recipe) {
        final ItemStackHandler handler = this.getInternalItemHandler();
        final List<ItemStack> outputs = recipe.outputs();
        if (outputs.isEmpty()) {
            return false;
        }

        for (int i = 0; i < Math.min(4, outputs.size()); i++) {
            final ItemStack out = outputs.get(i);
            final int targetSlot = SLOT_OUTPUT_1 + i;
            final ItemStack existing = handler.getStackInSlot(targetSlot);
            if (existing.isEmpty()) {
                continue;
            }
            if (!ItemStack.isSameItemSameTags(existing, out)) {
                return false;
            }
            if (existing.getCount() + out.getCount() > existing.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    private void processRecipe(final HbmCentrifugeRecipes.CentrifugeRecipe recipe) {
        final ItemStackHandler handler = this.getInternalItemHandler();
        final List<ItemStack> outputs = recipe.outputs();

        for (int i = 0; i < Math.min(4, outputs.size()); i++) {
            final int targetSlot = SLOT_OUTPUT_1 + i;
            final ItemStack out = outputs.get(i);
            final ItemStack existing = handler.getStackInSlot(targetSlot);
            if (existing.isEmpty()) {
                handler.setStackInSlot(targetSlot, out.copy());
            } else {
                final ItemStack merged = existing.copy();
                merged.grow(out.getCount());
                handler.setStackInSlot(targetSlot, merged);
            }
        }

        final ItemStack reducedInput = handler.getStackInSlot(SLOT_INPUT).copy();
        reducedInput.shrink(1);
        handler.setStackInSlot(SLOT_INPUT, reducedInput.isEmpty() ? ItemStack.EMPTY : reducedInput);
    }

    private boolean tryChargeFromBattery() {
        final ItemStack battery = this.getInternalItemHandler().getStackInSlot(SLOT_BATTERY);
        if (battery.isEmpty() || !(battery.getItem() instanceof final BatteryItem batteryItem)) {
            return false;
        }

        final int stored = this.getStoredEnergy();
        final int space = this.getMaxStoredEnergy() - stored;
        if (space <= 0) {
            return false;
        }

        final int rate = batteryItem.getDischargeRate();
        final int charge = batteryItem.getStoredEnergy(battery);
        final int toDischarge = Math.min(Math.min(space, rate), charge);
        if (toDischarge <= 0) {
            return false;
        }

        batteryItem.withStoredEnergy(battery, charge - toDischarge);
        final IEnergyStorage storage = this.getEnergyStorage(null);
        if (storage == null) {
            return false;
        }
        storage.receiveEnergy(toDischarge, false);
        return true;
    }

    private void consumeEnergy(final int amount) {
        final IEnergyStorage storage = this.getEnergyStorage(null);
        if (storage == null || amount <= 0) {
            return;
        }
        storage.extractEnergy(Math.min(amount, storage.getEnergyStored()), false);
    }

    @Override
    public boolean isItemValid(final int slot, final @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (slot == SLOT_INPUT) {
            return HbmCentrifugeRecipes.findRecipe(stack).isPresent();
        }
        if (slot == SLOT_BATTERY) {
            return stack.getItem() instanceof BatteryItem;
        }
        if (slot == SLOT_UPGRADE_1 || slot == SLOT_UPGRADE_2) {
            if (!(stack.getItem() instanceof MachineUpgradeItem upgrade)) {
                return false;
            }
            return this.getValidUpgrades().containsKey(upgrade.type());
        }
        return false;
    }

    @Override
    public boolean canInsertIntoSlot(final int slot, final @NotNull ItemStack stack, final @Nullable Direction side) {
        return this.isItemValid(slot, stack);
    }

    @Override
    public boolean canExtractFromSlot(final int slot, final @Nullable Direction side) {
        return slot > SLOT_BATTERY;
    }

    @Override
    public int[] getAccessibleSlots(final @Nullable Direction side) {
        return SLOT_ACCESS;
    }

    @Override
    protected boolean isUpgradeSlot(final int slot) {
        return slot == SLOT_UPGRADE_1 || slot == SLOT_UPGRADE_2;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(HbmBlocks.MACHINE_CENTRIFUGE.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(final int containerId, final @NotNull Inventory inventory, final @NotNull Player player) {
        return new CentrifugeMenu(containerId, inventory, this);
    }

    public int getProgress() {
        return this.progress;
    }

    public int getLastConsumption() {
        return this.lastConsumption;
    }

    @Override
    protected void saveMachineData(final @NotNull CompoundTag tag) {
        tag.putInt("progress", this.progress);
        tag.putInt("consumption", this.lastConsumption);
    }

    @Override
    protected void loadMachineData(final @NotNull CompoundTag tag) {
        this.progress = Math.max(0, tag.getInt("progress"));
        this.lastConsumption = Math.max(0, tag.getInt("consumption"));
    }

    @Override
    protected void writeAdditionalMachineStateSync(final CompoundTag tag) {
        tag.putInt("progress", this.progress);
        tag.putInt("energy", this.getStoredEnergy());
        tag.putInt("maxEnergy", this.getMaxStoredEnergy());
        tag.putInt("processingSpeed", this.processingSpeed());
        tag.putInt("consumption", Math.max(0, this.lastConsumption));
    }

    @Override
    protected void readMachineStateSync(final CompoundTag tag) {
        this.progress = Math.max(0, tag.getInt("progress"));
        this.lastConsumption = Math.max(0, tag.getInt("consumption"));
    }

    @Override
    public Map<MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades() {
        return Map.of(
            MachineUpgradeItem.UpgradeType.SPEED, 3,
            MachineUpgradeItem.UpgradeType.POWER, 3,
            MachineUpgradeItem.UpgradeType.OVERDRIVE, 3
        );
    }

    private int processingSpeed() {
        return Math.max(1, CentrifugeMachineConfig.INSTANCE.processingSpeed());
    }

    private void tickOperateSound(final Level level, final BlockPos pos) {
        if (this.operateSoundTimer <= 0) {
            level.playSound(null, pos, HbmSoundEvents.BLOCK_CENTRIFUGE_OPERATE.get(), SoundSource.BLOCKS, this.getVolume(1.0F), 1.0F);
            this.operateSoundTimer = OPERATE_SOUND_INTERVAL;
        } else {
            this.operateSoundTimer--;
        }
    }
}
