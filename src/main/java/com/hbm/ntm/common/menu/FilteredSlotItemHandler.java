package com.hbm.ntm.common.menu;

import java.util.function.BiPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

@SuppressWarnings("null")
public class FilteredSlotItemHandler extends SlotItemHandler {
    private final BiPredicate<Integer, ItemStack> validator;
    private final int handlerSlots;

    public FilteredSlotItemHandler(final IItemHandler itemHandler, final int index, final int xPosition, final int yPosition,
                                   final BiPredicate<Integer, ItemStack> validator) {
        super(itemHandler, index, xPosition, yPosition);
        this.validator = validator == null ? (slot, stack) -> true : validator;
        this.handlerSlots = itemHandler.getSlots();
        if (index < 0 || index >= this.handlerSlots) {
            throw new IllegalArgumentException("Filtered slot index out of bounds: " + index + " / " + this.handlerSlots);
        }
    }

    private boolean isValidIndex() {
        final int slot = this.getSlotIndex();
        return slot >= 0 && slot < this.handlerSlots;
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
        if (stack == null || stack.isEmpty() || !this.isValidIndex()) {
            return false;
        }
        return super.mayPlace(stack) && this.validator.test(this.getSlotIndex(), stack);
    }
}
