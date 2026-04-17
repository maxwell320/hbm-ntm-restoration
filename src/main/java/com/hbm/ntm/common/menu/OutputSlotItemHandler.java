package com.hbm.ntm.common.menu;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

@SuppressWarnings("null")
public class OutputSlotItemHandler extends SlotItemHandler {
    private final int handlerSlots;

    public OutputSlotItemHandler(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.handlerSlots = itemHandler.getSlots();
        if (index < 0 || index >= this.handlerSlots) {
            throw new IllegalArgumentException("Output slot index out of bounds: " + index + " / " + this.handlerSlots);
        }
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
        return false;
    }
}
