package com.hbm.ntm.common.item;

import com.hbm.ntm.common.block.BarrelType;
import com.hbm.ntm.common.fluid.HbmFluidTank;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BarrelBlockItem extends BlockItem {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final String TANK_TAG = "tank";
    private static final String CONFIGURED_FLUID_TAG = "configured_fluid";
    private final BarrelType barrelType;

    public BarrelBlockItem(final Block block, final BarrelType barrelType, final Item.Properties properties) {
        super(block, properties);
        this.barrelType = barrelType;
    }

    public static ItemStack withStoredState(final Item item, final FluidStack fluidStack, final int capacity, @Nullable final ResourceLocation configuredFluidId) {
        final ItemStack stack = new ItemStack(item);
        final CompoundTag blockEntityTag = stack.getOrCreateTagElement(BLOCK_ENTITY_TAG);
        if (!fluidStack.isEmpty()) {
            final HbmFluidTank tank = new HbmFluidTank(capacity);
            tank.setFluidStack(fluidStack);
            final CompoundTag tankTag = new CompoundTag();
            tank.writeToNBT(tankTag);
            blockEntityTag.put(TANK_TAG, tankTag);
        }
        if (configuredFluidId != null) {
            blockEntityTag.putString(CONFIGURED_FLUID_TAG, configuredFluidId.toString());
        }
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

    public static @Nullable ResourceLocation getConfiguredFluidId(final ItemStack stack) {
        final CompoundTag blockEntityTag = stack.getTagElement(BLOCK_ENTITY_TAG);
        if (blockEntityTag == null || !blockEntityTag.contains(CONFIGURED_FLUID_TAG, Tag.TAG_STRING)) {
            return null;
        }
        return ResourceLocation.tryParse(blockEntityTag.getString(CONFIGURED_FLUID_TAG));
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        final FluidStack fluidStack = getStoredFluid(stack, this.barrelType.capacity());
        for (final String line : this.barrelType.tooltipLines()) {
            tooltip.add(Component.literal(line));
        }
        if (!fluidStack.isEmpty()) {
            tooltip.add(Component.literal(fluidStack.getAmount() + "/" + this.barrelType.capacity() + "mB " + fluidStack.getDisplayName().getString()));
            return;
        }
        final ResourceLocation configuredFluidId = getConfiguredFluidId(stack);
        if (configuredFluidId != null && ForgeRegistries.FLUIDS.containsKey(configuredFluidId)) {
            final FluidStack configuredStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(configuredFluidId), 1);
            tooltip.add(Component.literal("0/" + this.barrelType.capacity() + "mB " + configuredStack.getDisplayName().getString()));
            return;
        }
        tooltip.add(Component.literal("0/" + this.barrelType.capacity() + "mB Empty"));
    }
}
