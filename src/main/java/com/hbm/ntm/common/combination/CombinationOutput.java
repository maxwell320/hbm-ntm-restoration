package com.hbm.ntm.common.combination;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("null")
public record CombinationOutput(ItemStack itemOutput, FluidStack fluidOutput) {
    public static final CombinationOutput EMPTY = new CombinationOutput(ItemStack.EMPTY, FluidStack.EMPTY);

    public CombinationOutput copy() {
        return new CombinationOutput(this.itemOutput.copy(), this.fluidOutput.copy());
    }

    public boolean isEmpty() {
        return this.itemOutput.isEmpty() && this.fluidOutput.isEmpty();
    }
}
