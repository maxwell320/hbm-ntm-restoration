package com.hbm.ntm.common.machine;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IConditionalInventoryAccess {
    boolean canInsertItem(int slot, ItemStack stack, @Nullable Direction side);

    boolean canExtractItem(int slot, ItemStack stack, @Nullable Direction side);

    int[] getAccessibleSlots(@Nullable Direction side);
}
