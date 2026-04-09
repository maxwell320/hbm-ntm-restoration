package com.hbm.ntm.common.recipe;

import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("null")
public record CountedIngredient(Ingredient ingredient, int count) {
    public CountedIngredient {
        ingredient = Objects.requireNonNull(ingredient, "ingredient");
        count = Math.max(1, count);
    }

    public boolean matches(final ItemStack stack) {
        return !stack.isEmpty() && stack.getCount() >= this.count && this.ingredient.test(stack);
    }
}
