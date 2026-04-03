package com.hbm.ntm.common.item;

import com.hbm.ntm.common.block.SellafieldBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class SellafieldBlockItem extends BlockItem {
    private static final String BLOCK_STATE_TAG = "BlockStateTag";

    public SellafieldBlockItem(final Block block, final Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        return Component.translatable("block.hbmntm.sellafield." + getLevel(stack));
    }

    public static ItemStack withLevel(final Item item, final int level) {
        final ItemStack stack = new ItemStack(item);
        setLevel(stack, level);
        return stack;
    }

    public static int getLevel(final ItemStack stack) {
        final CompoundTag tag = stack.getTagElement(BLOCK_STATE_TAG);
        if (tag == null) {
            return 0;
        }
        if (tag.contains(SellafieldBlock.LEVEL.getName(), Tag.TAG_STRING)) {
            try {
                return Mth.clamp(Integer.parseInt(tag.getString(SellafieldBlock.LEVEL.getName())), 0, SellafieldBlock.MAX_LEVEL);
            } catch (final NumberFormatException ignored) {
                return 0;
            }
        }
        if (tag.contains(SellafieldBlock.LEVEL.getName(), Tag.TAG_INT)) {
            return Mth.clamp(tag.getInt(SellafieldBlock.LEVEL.getName()), 0, SellafieldBlock.MAX_LEVEL);
        }
        return 0;
    }

    public static void setLevel(final ItemStack stack, final int level) {
        stack.getOrCreateTagElement(BLOCK_STATE_TAG).putString(SellafieldBlock.LEVEL.getName(), Integer.toString(Mth.clamp(level, 0, SellafieldBlock.MAX_LEVEL)));
    }
}
