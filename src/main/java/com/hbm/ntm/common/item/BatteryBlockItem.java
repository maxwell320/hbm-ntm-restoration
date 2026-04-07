package com.hbm.ntm.common.item;

import com.hbm.ntm.common.block.entity.BatteryBlockEntity;
import com.hbm.ntm.common.energy.HbmEnergy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

@SuppressWarnings("null")
public class BatteryBlockItem extends BlockItem {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

    public BatteryBlockItem(final Block block, final Item.Properties properties) {
        super(block, properties);
    }

    public static ItemStack withStoredEnergy(final Item item, final int energy) {
        final ItemStack stack = new ItemStack(item);
        stack.getOrCreateTagElement(BLOCK_ENTITY_TAG).putInt("energy", Math.max(0, Math.min(BatteryBlockEntity.CAPACITY, energy)));
        return stack;
    }

    public static int getStoredEnergy(final ItemStack stack) {
        final CompoundTag tag = stack.getTagElement(BLOCK_ENTITY_TAG);
        return tag == null ? 0 : Math.max(0, Math.min(BatteryBlockEntity.CAPACITY, tag.getInt("energy")));
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(Component.literal("Stores up to " + BatteryBlockEntity.CAPACITY + HbmEnergy.UNIT));
        tooltip.add(Component.literal("Charge rate: " + BatteryBlockEntity.MAX_RECEIVE + HbmEnergy.UNIT + "/t"));
        tooltip.add(Component.literal("Discharge rate: " + BatteryBlockEntity.MAX_EXTRACT + HbmEnergy.UNIT + "/t"));
        tooltip.add(Component.literal(getStoredEnergy(stack) + "/" + BatteryBlockEntity.CAPACITY + HbmEnergy.UNIT));
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        return super.getName(stack);
    }
}
