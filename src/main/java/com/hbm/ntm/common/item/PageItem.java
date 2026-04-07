package com.hbm.ntm.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class PageItem extends Item {
    private static final String PAGE_TAG = "page_type";

    public PageItem() {
        super(new Properties().stacksTo(1));
    }

    public static ItemStack create(final Item item, final PageItemType type) {
        final ItemStack stack = new ItemStack(item);
        setType(stack, type);
        return stack;
    }

    public static void setType(final ItemStack stack, final PageItemType type) {
        stack.getOrCreateTag().putString(PAGE_TAG, type.name());
    }

    public static PageItemType getType(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(PAGE_TAG)) {
            return PageItemType.fromName(tag.getString(PAGE_TAG));
        }
        return PageItemType.PAGE1;
    }

    @Override
    public @NotNull String getDescriptionId(final @NotNull ItemStack stack) {
        return super.getDescriptionId(stack) + "." + getType(stack).translationSuffix();
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return create(this, PageItemType.PAGE1);
    }
}
