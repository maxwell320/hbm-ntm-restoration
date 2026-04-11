package com.hbm.inventory;

import net.minecraft.world.item.ItemStack;

public final class RecipesCommon {

    private RecipesCommon() {
    }

    public static final class ComparableStack {
        private final ItemStack stack;

        public ComparableStack(final ItemStack stack) {
            this.stack = stack.copy();
        }

        public ItemStack toStack() {
            return this.stack.copy();
        }

        @Override
        public int hashCode() {
            int result = this.stack.getItem().hashCode();
            result = 31 * result + this.stack.getDamageValue();
            result = 31 * result + (this.stack.getTag() == null ? 0 : this.stack.getTag().hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final ComparableStack other)) {
                return false;
            }
            return ItemStack.isSameItemSameTags(this.stack, other.stack);
        }
    }
}
