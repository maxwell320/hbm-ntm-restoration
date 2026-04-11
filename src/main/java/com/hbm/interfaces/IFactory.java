package com.hbm.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IFactory {

    boolean isStructureValid(Level level);

    long getPowerScaled(long i);

    int getProgressScaled(int i);

    boolean isProcessable(ItemStack item);
}
