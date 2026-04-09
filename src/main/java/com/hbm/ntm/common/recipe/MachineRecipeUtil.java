package com.hbm.ntm.common.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("null")
public final class MachineRecipeUtil {
    private MachineRecipeUtil() {
    }

    public static boolean matchesShapelessIngredients(final List<ItemStack> inputs, final List<CountedIngredient> recipe) {
        final List<CountedIngredient> remaining = new ArrayList<>(recipe);
        for (final ItemStack input : inputs) {
            if (input == null || input.isEmpty()) {
                continue;
            }
            boolean hasMatch = false;
            for (int i = 0; i < remaining.size(); i++) {
                if (remaining.get(i).matches(input)) {
                    remaining.remove(i);
                    hasMatch = true;
                    break;
                }
            }
            if (!hasMatch) {
                return false;
            }
        }
        return remaining.isEmpty();
    }

    public static boolean matchesFluidRequirement(final FluidStack input, final FluidStack recipe) {
        if (recipe.isEmpty()) {
            return true;
        }
        return !input.isEmpty() && input.getAmount() >= recipe.getAmount() && input.isFluidEqual(recipe);
    }
}
