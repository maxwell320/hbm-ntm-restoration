package com.hbm.ntm.common.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IItemFluidIdentifier {
    @Nullable ResourceLocation getFluidId(ItemStack stack);
}
