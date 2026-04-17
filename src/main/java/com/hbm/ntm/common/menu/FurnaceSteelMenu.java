package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.block.entity.FurnaceSteelBlockEntity;
import com.hbm.ntm.common.registration.HbmMenuTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

@SuppressWarnings("null")
public class FurnaceSteelMenu extends MachineMenuBase<FurnaceSteelBlockEntity> {
    private static final int DATA_PROGRESS_1 = 0;
    private static final int DATA_PROGRESS_2 = 1;
    private static final int DATA_PROGRESS_3 = 2;
    private static final int DATA_BONUS_1 = 3;
    private static final int DATA_BONUS_2 = 4;
    private static final int DATA_BONUS_3 = 5;
    private static final int DATA_HEAT = 6;
    private static final int DATA_WAS_ON = 7;
    private static final int DATA_COUNT = 8;

    private final ContainerData data;
    private final int[] clientProgress = new int[3];
    private final int[] clientBonus = new int[3];
    private int clientHeat;
    private boolean clientWasOn;

    public FurnaceSteelMenu(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        this(containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof final FurnaceSteelBlockEntity furnace ? furnace : null);
    }

    public FurnaceSteelMenu(final int containerId, final Inventory inventory, final FurnaceSteelBlockEntity furnace) {
        super(HbmMenuTypes.FURNACE_STEEL.get(), containerId, inventory, furnace, FurnaceSteelBlockEntity.SLOT_COUNT);
        final ItemStackHandler handler = furnace == null ? new ItemStackHandler(FurnaceSteelBlockEntity.SLOT_COUNT) : furnace.getInternalItemHandler();

        this.addSlot(new FilteredSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_INPUT_1, 35, 17,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new FilteredSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_INPUT_2, 35, 35,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new FilteredSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_INPUT_3, 35, 53,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new OutputSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_OUTPUT_1, 125, 17));
        this.addSlot(new OutputSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_OUTPUT_2, 125, 35));
        this.addSlot(new OutputSlotItemHandler(handler, FurnaceSteelBlockEntity.SLOT_OUTPUT_3, 125, 53));

        this.addPlayerInventory(inventory, 8, 84);

        this.data = furnace == null
            ? new SimpleContainerData(DATA_COUNT)
            : new MachineDataSlots(
                java.util.List.of(
                    () -> furnace.getProgress()[0],
                    () -> furnace.getProgress()[1],
                    () -> furnace.getProgress()[2],
                    () -> furnace.getBonus()[0],
                    () -> furnace.getBonus()[1],
                    () -> furnace.getBonus()[2],
                    furnace::getHeat,
                    () -> furnace.wasOn() ? 1 : 0
                ),
                java.util.List.of(
                    value -> this.clientProgress[0] = Math.max(0, value),
                    value -> this.clientProgress[1] = Math.max(0, value),
                    value -> this.clientProgress[2] = Math.max(0, value),
                    value -> this.clientBonus[0] = Math.max(0, value),
                    value -> this.clientBonus[1] = Math.max(0, value),
                    value -> this.clientBonus[2] = Math.max(0, value),
                    value -> this.clientHeat = Math.max(0, value),
                    value -> this.clientWasOn = value > 0
                ));
        this.addMachineDataSlots(this.data);
    }

    @Override
    protected boolean moveToMachineSlots(final ItemStack stack) {
        if (this.machine != null && this.machine.isItemValid(FurnaceSteelBlockEntity.SLOT_INPUT_1, stack)) {
            return this.moveItemStackTo(stack, FurnaceSteelBlockEntity.SLOT_INPUT_1, FurnaceSteelBlockEntity.SLOT_INPUT_3 + 1, false);
        }
        return false;
    }

    public int progress(final int lane) {
        if (lane < 0 || lane > 2) {
            return 0;
        }
        return this.clientProgress[lane] > 0 ? this.clientProgress[lane] : this.data.get(DATA_PROGRESS_1 + lane);
    }

    public int bonus(final int lane) {
        if (lane < 0 || lane > 2) {
            return 0;
        }
        return this.clientBonus[lane] > 0 ? this.clientBonus[lane] : this.data.get(DATA_BONUS_1 + lane);
    }

    public int heat() {
        return this.clientHeat > 0 ? this.clientHeat : this.data.get(DATA_HEAT);
    }

    public int maxHeat() {
        return this.machine == null ? 100_000 : this.machine.getMaxHeat();
    }

    public int processTime() {
        return FurnaceSteelBlockEntity.PROCESS_TIME;
    }

    public boolean wasOn() {
        return this.clientWasOn;
    }

    @Override
    protected void readMachineStateSync(final CompoundTag data) {
        final int[] progress = data.getIntArray("progress");
        final int[] bonus = data.getIntArray("bonus");

        for (int i = 0; i < 3; i++) {
            this.clientProgress[i] = progress.length > i ? Math.max(0, progress[i]) : 0;
            this.clientBonus[i] = bonus.length > i ? Math.max(0, bonus[i]) : 0;
        }
        this.clientHeat = Math.max(0, data.getInt("heat"));
        this.clientWasOn = data.getBoolean("wasOn");
    }
}
