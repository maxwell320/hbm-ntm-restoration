package com.hbm.ntm.common.item;

import com.hbm.ntm.common.block.entity.SteelBarrelBlockEntity;
import com.hbm.ntm.common.fluid.HbmFluidTank;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class SteelBarrelBlockItem extends BlockItem {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String TANK_TAG = "tank";

    public SteelBarrelBlockItem(final Block block, final Item.Properties properties) {
        super(block, properties);
    }

    public static ItemStack withStoredFluid(final Item item, final FluidStack fluidStack, final int capacity) {
        final ItemStack stack = new ItemStack(item);
        if (fluidStack.isEmpty()) {
            return stack;
        }
        final HbmFluidTank tank = new HbmFluidTank(capacity);
        tank.setFluidStack(fluidStack);
        final CompoundTag tankTag = new CompoundTag();
        tank.writeToNBT(tankTag);
        stack.getOrCreateTagElement(BLOCK_ENTITY_TAG).put(TANK_TAG, tankTag);
        return stack;
    }

    public static FluidStack getStoredFluid(final ItemStack stack, final int capacity) {
        final CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if (blockEntityTag == null || !blockEntityTag.contains(TANK_TAG, Tag.TAG_COMPOUND)) {
            return FluidStack.EMPTY;
        }
        final HbmFluidTank tank = new HbmFluidTank(capacity);
        tank.readFromNBT(blockEntityTag.getCompound(TANK_TAG));
        return tank.getFluid().copy();
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        final FluidStack fluidStack = getStoredFluid(stack, SteelBarrelBlockEntity.CAPACITY);
        tooltip.add(Component.literal("Capacity: 16,000mB"));
        tooltip.add(Component.literal("Can store hot fluids"));
        tooltip.add(Component.literal("Can store corrosive fluids"));
        tooltip.add(Component.literal("Cannot store highly corrosive fluids properly"));
        tooltip.add(Component.literal("Cannot store antimatter"));
        if (fluidStack.isEmpty()) {
            tooltip.add(Component.literal("0/" + SteelBarrelBlockEntity.CAPACITY + "mB Empty"));
            return;
        }
        tooltip.add(Component.literal(fluidStack.getAmount() + "/" + SteelBarrelBlockEntity.CAPACITY + "mB " + fluidStack.getDisplayName().getString()));
    }
}
