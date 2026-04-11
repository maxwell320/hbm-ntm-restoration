package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public abstract class MachineMenuBase<T extends MachineBlockEntity> extends AbstractContainerMenu {
    protected final T machine;
    protected final Inventory playerInventory;
    protected final int machineSlotCount;
    private boolean maintenanceBlocked;
    private int maintenanceLevel;
    private List<ItemStack> repairMaterials = List.of();

    protected MachineMenuBase(final MenuType<?> menuType, final int containerId, final Inventory playerInventory, final T machine, final int machineSlotCount) {
        super(menuType, containerId);
        this.machine = machine;
        this.playerInventory = playerInventory;
        this.machineSlotCount = machineSlotCount;
    }

    protected final void addPlayerInventory(final Inventory inventory, final int left, final int top) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, left + column * 18, top + row * 18));
            }
        }
        for (int hotbar = 0; hotbar < 9; hotbar++) {
            this.addSlot(new Slot(inventory, hotbar, left + hotbar * 18, top + 58));
        }
    }

    protected final void addMachineDataSlots(final ContainerData dataSlots) {
        this.addDataSlots(dataSlots);
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return this.machine != null && this.machine.canPlayerControl(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        final Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        final ItemStack original = slot.getItem();
        final ItemStack copy = original.copy();
        if (index < this.machineSlotCount) {
            if (!this.moveItemStackTo(original, this.machineSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveToMachineSlots(original)) {
            return ItemStack.EMPTY;
        }
        if (original.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    protected boolean moveToMachineSlots(final ItemStack stack) {
        return this.moveItemStackTo(stack, 0, this.machineSlotCount, false);
    }

    public T machine() {
        return this.machine;
    }

    public final boolean applyMachineStateSync(final BlockPos pos, final CompoundTag data) {
        if (this.machine == null || !this.machine.getBlockPos().equals(pos)) {
            return false;
        }
        this.readSharedMachineStateSync(data);
        this.readMachineStateSync(data);
        return true;
    }

    private void readSharedMachineStateSync(final CompoundTag data) {
        this.maintenanceBlocked = data.getBoolean("maintenanceBlocked");
        if (data.contains("maintenanceLevel", Tag.TAG_INT)) {
            this.maintenanceLevel = data.getInt("maintenanceLevel");
        }

        if (!data.contains("repairMaterials", Tag.TAG_LIST)) {
            this.repairMaterials = List.of();
            return;
        }

        final ListTag listTag = data.getList("repairMaterials", Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) {
            this.repairMaterials = List.of();
            return;
        }

        final List<ItemStack> synced = new ArrayList<>(listTag.size());
        for (int i = 0; i < listTag.size(); i++) {
            final ItemStack stack = ItemStack.of(listTag.getCompound(i));
            if (!stack.isEmpty()) {
                synced.add(stack);
            }
        }
        this.repairMaterials = List.copyOf(synced);
    }

    public boolean isMaintenanceBlocked() {
        return this.maintenanceBlocked;
    }

    public int maintenanceLevel() {
        return this.maintenanceLevel;
    }

    public List<ItemStack> repairMaterials() {
        return this.repairMaterials;
    }

    protected void readMachineStateSync(final CompoundTag data) {
    }
}
