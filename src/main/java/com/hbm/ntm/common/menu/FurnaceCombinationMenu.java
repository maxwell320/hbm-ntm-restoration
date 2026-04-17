package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.block.entity.FurnaceCombinationBlockEntity;
import com.hbm.ntm.common.registration.HbmMenuTypes;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

@SuppressWarnings("null")
public class FurnaceCombinationMenu extends MachineMenuBase<FurnaceCombinationBlockEntity> {
    private static final int DATA_PROGRESS = 0;
    private static final int DATA_HEAT = 1;
    private static final int DATA_MAX_HEAT = 2;
    private static final int DATA_TANK_AMOUNT = 3;
    private static final int DATA_TANK_CAPACITY = 4;
    private static final int DATA_COUNT = 5;

    private final ContainerData data;
    private int clientProgress;
    private int clientHeat;
    private int clientMaxHeat = 1;
    private int clientTankAmount;
    private int clientTankCapacity;
    private String clientTankName = "Empty";

    public FurnaceCombinationMenu(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        this(containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof final FurnaceCombinationBlockEntity furnace ? furnace : null);
    }

    public FurnaceCombinationMenu(final int containerId, final Inventory inventory, final FurnaceCombinationBlockEntity furnace) {
        super(HbmMenuTypes.FURNACE_COMBINATION.get(), containerId, inventory, furnace, FurnaceCombinationBlockEntity.SLOT_COUNT);
        final ItemStackHandler handler = furnace == null ? new ItemStackHandler(FurnaceCombinationBlockEntity.SLOT_COUNT) : furnace.getInternalItemHandler();

        this.addSlot(new FilteredSlotItemHandler(handler, FurnaceCombinationBlockEntity.SLOT_INPUT, 26, 36,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new OutputSlotItemHandler(handler, FurnaceCombinationBlockEntity.SLOT_OUTPUT, 89, 36));
        this.addSlot(new FilteredSlotItemHandler(handler, FurnaceCombinationBlockEntity.SLOT_CONTAINER_IN, 136, 18,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new OutputSlotItemHandler(handler, FurnaceCombinationBlockEntity.SLOT_CONTAINER_OUT, 136, 54));

        this.addPlayerInventory(inventory, 8, 104);

        this.data = furnace == null
            ? new SimpleContainerData(DATA_COUNT)
            : new MachineDataSlots(
                List.of(
                    furnace::getProgress,
                    furnace::getHeat,
                    furnace::getMaxHeat,
                    () -> {
                        final var tank = furnace.getFluidTank(FurnaceCombinationBlockEntity.TANK_OUTPUT);
                        return tank == null ? 0 : tank.getFluidAmount();
                    },
                    () -> {
                        final var tank = furnace.getFluidTank(FurnaceCombinationBlockEntity.TANK_OUTPUT);
                        return tank == null ? FurnaceCombinationBlockEntity.TANK_CAPACITY : tank.getCapacity();
                    }
                ),
                List.of(
                    value -> this.clientProgress = Math.max(0, value),
                    value -> this.clientHeat = Math.max(0, value),
                    value -> this.clientMaxHeat = Math.max(1, value),
                    value -> this.clientTankAmount = Math.max(0, value),
                    value -> this.clientTankCapacity = Math.max(0, value)
                ));
        this.addMachineDataSlots(this.data);
    }

    @Override
    protected boolean moveToMachineSlots(final ItemStack stack) {
        if (this.machine != null && this.machine.isItemValid(FurnaceCombinationBlockEntity.SLOT_CONTAINER_IN, stack)) {
            if (this.moveItemStackTo(stack, FurnaceCombinationBlockEntity.SLOT_CONTAINER_IN, FurnaceCombinationBlockEntity.SLOT_CONTAINER_IN + 1, false)) {
                return true;
            }
        }

        if (this.machine != null && this.machine.isItemValid(FurnaceCombinationBlockEntity.SLOT_INPUT, stack)) {
            return this.moveItemStackTo(stack, FurnaceCombinationBlockEntity.SLOT_INPUT, FurnaceCombinationBlockEntity.SLOT_INPUT + 1, false);
        }

        return false;
    }

    public int progress() {
        return this.clientProgress > 0 ? this.clientProgress : this.data.get(DATA_PROGRESS);
    }

    public int processTime() {
        return 20_000;
    }

    public int heat() {
        return this.clientHeat > 0 ? this.clientHeat : this.data.get(DATA_HEAT);
    }

    public int maxHeat() {
        return this.clientMaxHeat > 0 ? this.clientMaxHeat : Math.max(1, this.data.get(DATA_MAX_HEAT));
    }

    public int tankAmount() {
        return this.clientTankAmount > 0 ? this.clientTankAmount : this.data.get(DATA_TANK_AMOUNT);
    }

    public int tankCapacity() {
        return this.clientTankCapacity > 0 ? this.clientTankCapacity : this.data.get(DATA_TANK_CAPACITY);
    }

    public String tankName() {
        return this.clientTankName;
    }

    @Override
    protected void readMachineStateSync(final CompoundTag data) {
        this.clientProgress = Math.max(0, data.getInt("progress"));
        this.clientHeat = Math.max(0, data.getInt("heat"));
        this.clientMaxHeat = Math.max(1, data.getInt("maxHeat"));
        this.clientTankAmount = Math.max(0, data.getInt("tankAmount"));
        this.clientTankCapacity = Math.max(0, data.getInt("tankCapacity"));
        this.clientTankName = data.getString("tankName");
    }
}
