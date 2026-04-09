package com.hbm.ntm.common.recipe;

import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@SuppressWarnings("null")
public record KeyedItemRecipe<K>(Ingredient input, K key, ItemStack output) {
    public KeyedItemRecipe {
        input = Objects.requireNonNull(input, "input");
        key = Objects.requireNonNull(key, "key");
        output = output.copy();
    }

    public boolean matches(final ItemStack inputStack, final K activeKey) {
        return Objects.equals(this.key, activeKey) && this.input.test(inputStack);
    }

    public ItemStack resultCopy() {
        return this.output.copy();
    }
}
