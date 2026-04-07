package com.hbm.ntm.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class StampBookItem extends Item {
    private static final String STAMP_TAG = "printing_stamp_type";

    public StampBookItem() {
        super(new Properties().stacksTo(1));
    }

    public static ItemStack create(final Item item, final PrintingStampType type) {
        final ItemStack stack = new ItemStack(item);
        setType(stack, type);
        return stack;
    }

    public static void setType(final ItemStack stack, final PrintingStampType type) {
        stack.getOrCreateTag().putString(STAMP_TAG, type.name());
    }

    public static PrintingStampType getType(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(STAMP_TAG)) {
            return PrintingStampType.fromName(tag.getString(STAMP_TAG));
        }
        return PrintingStampType.PRINTING1;
    }

    @Override
    public @NotNull String getDescriptionId(final @NotNull ItemStack stack) {
        return super.getDescriptionId(stack) + "." + getType(stack).translationSuffix();
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return create(this, PrintingStampType.PRINTING1);
    }
}
