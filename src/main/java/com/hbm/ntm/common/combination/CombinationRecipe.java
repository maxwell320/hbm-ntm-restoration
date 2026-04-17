package com.hbm.ntm.common.combination;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("null")
public record CombinationRecipe(Ingredient input, ItemStack itemOutput, FluidStack fluidOutput) {
    public boolean matches(final ItemStack stack) {
        return !stack.isEmpty() && this.input.test(stack);
    }

    public CombinationOutput outputCopy() {
        return new CombinationOutput(this.itemOutput.copy(), this.fluidOutput.copy());
    }
}
