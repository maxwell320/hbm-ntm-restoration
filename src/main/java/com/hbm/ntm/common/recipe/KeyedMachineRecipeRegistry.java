package com.hbm.ntm.common.recipe;

import java.util.Optional;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public abstract class KeyedMachineRecipeRegistry<K> extends MachineRecipeRegistry<KeyedItemRecipe<K>> {
    public final Optional<KeyedItemRecipe<K>> findRecipe(final ItemStack inputStack, final K key) {
        if (inputStack.isEmpty() || key == null) {
            return Optional.empty();
        }
        return this.findFirst(recipe -> recipe.matches(inputStack, key));
    }

    public final ItemStack getOutput(final ItemStack inputStack, final K key) {
        return this.findRecipe(inputStack, key).map(KeyedItemRecipe::resultCopy).orElse(ItemStack.EMPTY);
    }
}
