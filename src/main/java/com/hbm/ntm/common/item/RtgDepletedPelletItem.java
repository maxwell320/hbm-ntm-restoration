package com.hbm.ntm.common.item;

import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RtgDepletedPelletItem extends Item {
    private final Supplier<Item> remainderSupplier;

    public RtgDepletedPelletItem(final Supplier<Item> remainderSupplier) {
        super(new Item.Properties().stacksTo(1));
        this.remainderSupplier = remainderSupplier;
    }

    @Override
    public boolean hasCraftingRemainingItem(final ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(final ItemStack stack) {
        return new ItemStack(this.remainderSupplier.get());
    }
}

