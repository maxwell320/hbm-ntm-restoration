package com.hbm.ntm.common.item;

import com.hbm.ntm.common.assembly.HbmAssemblyRecipes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BlueprintItem extends Item {
    private static final String POOL_TAG = "pool";

    public BlueprintItem() {
        super(new Properties().stacksTo(1));
    }

    public static ItemStack create(final Item item, final String pool) {
        final ItemStack stack = new ItemStack(item);
        setPool(stack, pool);
        return stack;
    }

    public static void setPool(final ItemStack stack, final String pool) {
        stack.getOrCreateTag().putString(POOL_TAG, pool);
    }

    public static @Nullable String getPool(final ItemStack stack) {
        final CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(POOL_TAG)) {
            return null;
        }
        final String pool = tag.getString(POOL_TAG);
        return pool.isBlank() ? null : pool;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level level, final @NotNull Player player,
                                                            final @NotNull InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        final String pool = getPool(stack);
        if (pool == null || pool.startsWith(HbmAssemblyRecipes.POOL_PREFIX_SECRET)) {
            return InteractionResultHolder.pass(stack);
        }

        if (!player.getAbilities().instabuild) {
            final int paperSlot = player.getInventory().findSlotMatchingItem(new ItemStack(Items.PAPER));
            if (paperSlot < 0) {
                return InteractionResultHolder.pass(stack);
            }
            player.getInventory().removeItem(paperSlot, 1);
        }

        if (!level.isClientSide()) {
            final ItemStack copy = stack.copy();
            copy.setCount(1);
            if (!player.getAbilities().instabuild && stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(1);
            } else if (!player.getInventory().add(copy)) {
                player.drop(copy, false);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack, final @Nullable Level level,
                                final @NotNull List<Component> tooltip, final @NotNull TooltipFlag flag) {
        final String pool = getPool(stack);
        if (pool != null) {
            tooltip.add(Component.literal(pool).withStyle(ChatFormatting.GRAY));
            if (pool.startsWith(HbmAssemblyRecipes.POOL_PREFIX_SECRET)) {
                tooltip.add(Component.literal("Cannot be copied!").withStyle(ChatFormatting.RED));
            } else {
                tooltip.add(Component.literal("Right-click to copy (requires paper)").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
