package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.block.entity.ShredderBlockEntity;
import com.hbm.ntm.common.item.BatteryItem;
import com.hbm.ntm.common.item.ShredderBladesItem;
import com.hbm.ntm.common.registration.HbmMenuTypes;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

@SuppressWarnings("null")
public class ShredderMenu extends MachineMenuBase<ShredderBlockEntity> {

    private static final int DATA_PROGRESS = 0;
    private static final int DATA_ENERGY = 1;
    private static final int DATA_MAX_ENERGY = 2;
    private static final int DATA_GEAR_LEFT = 3;
    private static final int DATA_GEAR_RIGHT = 4;
    private static final int DATA_COUNT = 5;

    private final ContainerData data;

    public ShredderMenu(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        this(containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof final ShredderBlockEntity shredder ? shredder : null);
    }

    public ShredderMenu(final int containerId, final Inventory inventory, final ShredderBlockEntity shredder) {
        super(HbmMenuTypes.MACHINE_SHREDDER.get(), containerId, inventory, shredder, ShredderBlockEntity.SLOT_COUNT);
        final ItemStackHandler handler = shredder == null ? new ItemStackHandler(ShredderBlockEntity.SLOT_COUNT) : shredder.getInternalItemHandler();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int slot = row * 3 + col;
                this.addSlot(new FilteredSlotItemHandler(handler, slot, 44 + col * 18, 18 + row * 18,
                    (index, stack) -> this.machine != null && this.machine.isItemValid(index, stack)));
            }
        }

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                this.addSlot(new OutputSlotItemHandler(handler, 9 + row * 3 + col, 116 + col * 18, 18 + row * 18));
            }
        }

        this.addSlot(new FilteredSlotItemHandler(handler, ShredderBlockEntity.SLOT_BLADE_LEFT, 44, 108,
            (slot, stack) -> stack.getItem() instanceof ShredderBladesItem));
        this.addSlot(new FilteredSlotItemHandler(handler, ShredderBlockEntity.SLOT_BLADE_RIGHT, 80, 108,
            (slot, stack) -> stack.getItem() instanceof ShredderBladesItem));
        this.addSlot(new FilteredSlotItemHandler(handler, ShredderBlockEntity.SLOT_BATTERY, 8, 108,
            (slot, stack) -> stack.getItem() instanceof BatteryItem));

        this.addPlayerInventory(inventory, 8, 151);

        this.data = shredder == null
            ? new SimpleContainerData(DATA_COUNT)
            : new MachineDataSlots(
                List.of(
                    shredder::getProgress,
                    shredder::getStoredEnergy,
                    shredder::getMaxStoredEnergy,
                    shredder::getGearLeft,
                    shredder::getGearRight
                ),
                List.of(
                    shredder::setClientProgress,
                    v -> {}, // energy is display-only on client
                    v -> {}, // max energy is display-only on client
                    v -> {}, // gear left is display-only on client
                    v -> {}  // gear right is display-only on client
                ));
        this.addMachineDataSlots(this.data);
    }

    @Override
    protected boolean moveToMachineSlots(final ItemStack stack) {
        if (stack.getItem() instanceof BatteryItem) {
            return this.moveItemStackTo(stack, ShredderBlockEntity.SLOT_BATTERY, ShredderBlockEntity.SLOT_BATTERY + 1, false);
        }
        if (stack.getItem() instanceof ShredderBladesItem) {
            return this.moveItemStackTo(stack, ShredderBlockEntity.SLOT_BLADE_LEFT, ShredderBlockEntity.SLOT_BLADE_RIGHT + 1, false);
        }
        if (this.machine == null || !this.machine.isItemValid(ShredderBlockEntity.SLOT_INPUT_START, stack)) {
            return false;
        }
        return this.moveItemStackTo(stack, ShredderBlockEntity.SLOT_INPUT_START, ShredderBlockEntity.SLOT_INPUT_END, false);
    }

    public int progress() {
        return this.data.get(DATA_PROGRESS);
    }

    public int energy() {
        return this.data.get(DATA_ENERGY);
    }

    public int maxEnergy() {
        return this.data.get(DATA_MAX_ENERGY);
    }

    public int gearLeft() {
        return this.data.get(DATA_GEAR_LEFT);
    }

    public int gearRight() {
        return this.data.get(DATA_GEAR_RIGHT);
    }
}
