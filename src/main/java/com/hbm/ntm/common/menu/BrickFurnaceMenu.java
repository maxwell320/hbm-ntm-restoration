package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.block.entity.BrickFurnaceBlockEntity;
import com.hbm.ntm.common.registration.HbmMenuTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemStackHandler;

@SuppressWarnings("null")
public class BrickFurnaceMenu extends MachineMenuBase<BrickFurnaceBlockEntity> {
    private static final int DATA_BURN_TIME = 0;
    private static final int DATA_MAX_BURN = 1;
    private static final int DATA_PROGRESS = 2;
    private static final int DATA_COUNT = 3;

    private final ContainerData data;
    private int clientBurnTime;
    private int clientMaxBurn;
    private int clientProgress;

    public BrickFurnaceMenu(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        this(containerId,
            inventory,
            inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof final BrickFurnaceBlockEntity furnace ? furnace : null);
    }

    public BrickFurnaceMenu(final int containerId, final Inventory inventory, final BrickFurnaceBlockEntity furnace) {
        super(HbmMenuTypes.MACHINE_FURNACE_BRICK.get(), containerId, inventory, furnace, BrickFurnaceBlockEntity.SLOT_COUNT);
        final ItemStackHandler handler = furnace == null ? new ItemStackHandler(BrickFurnaceBlockEntity.SLOT_COUNT) : furnace.getInternalItemHandler();

        this.addSlot(new FilteredSlotItemHandler(handler, BrickFurnaceBlockEntity.SLOT_INPUT, 62, 35,
            (slot, stack) -> this.machine == null || this.machine.isItemValid(slot, stack)));
        this.addSlot(new FilteredSlotItemHandler(handler, BrickFurnaceBlockEntity.SLOT_FUEL, 35, 17,
            (slot, stack) -> ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0));
        this.addSlot(new OutputSlotItemHandler(handler, BrickFurnaceBlockEntity.SLOT_OUTPUT, 116, 35));
        this.addSlot(new OutputSlotItemHandler(handler, BrickFurnaceBlockEntity.SLOT_ASH, 35, 53));

        this.addPlayerInventory(inventory, 8, 84);

        this.data = furnace == null
            ? new SimpleContainerData(DATA_COUNT)
            : new MachineDataSlots(
                java.util.List.of(
                    furnace::getBurnTime,
                    furnace::getMaxBurnTime,
                    furnace::getProgress
                ),
                java.util.List.of(
                    value -> this.clientBurnTime = Math.max(0, value),
                    value -> this.clientMaxBurn = Math.max(1, value),
                    value -> this.clientProgress = Math.max(0, value)
                ));
        this.addMachineDataSlots(this.data);
    }

    @Override
    protected boolean moveToMachineSlots(final ItemStack stack) {
        if (ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0) {
            if (this.moveItemStackTo(stack, BrickFurnaceBlockEntity.SLOT_FUEL, BrickFurnaceBlockEntity.SLOT_FUEL + 1, false)) {
                return true;
            }
            return this.moveItemStackTo(stack, BrickFurnaceBlockEntity.SLOT_INPUT, BrickFurnaceBlockEntity.SLOT_INPUT + 1, false);
        }

        return this.moveItemStackTo(stack, BrickFurnaceBlockEntity.SLOT_INPUT, BrickFurnaceBlockEntity.SLOT_INPUT + 1, false);
    }

    public int burnTime() {
        return this.clientBurnTime > 0 ? this.clientBurnTime : this.data.get(DATA_BURN_TIME);
    }

    public int maxBurnTime() {
        if (this.clientMaxBurn > 0) {
            return this.clientMaxBurn;
        }
        return Math.max(1, this.data.get(DATA_MAX_BURN));
    }

    public int progress() {
        return this.clientProgress > 0 ? this.clientProgress : this.data.get(DATA_PROGRESS);
    }

    @Override
    protected void readMachineStateSync(final CompoundTag data) {
        this.clientBurnTime = Math.max(0, data.getInt("burnTime"));
        this.clientMaxBurn = Math.max(1, data.getInt("maxBurn"));
        this.clientProgress = Math.max(0, data.getInt("progress"));
    }
}
