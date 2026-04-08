package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.item.FluidIdentifierItem;
import com.hbm.ntm.common.item.IItemFluidIdentifier;
import com.hbm.ntm.common.registration.HbmMenuTypes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class FluidIdentifierMenu extends AbstractContainerMenu {
    private final Inventory inventory;
    private final int itemSlot;

    public FluidIdentifierMenu(final int containerId, final Inventory inventory, final FriendlyByteBuf buffer) {
        this(containerId, inventory, buffer.readInt());
    }

    public FluidIdentifierMenu(final int containerId, final Inventory inventory, final int itemSlot) {
        super(HbmMenuTypes.FLUID_IDENTIFIER.get(), containerId);
        this.inventory = inventory;
        this.itemSlot = itemSlot;
    }

    @Override
    public boolean stillValid(final @NotNull Player player) {
        return this.getIdentifierStack().getItem() instanceof IItemFluidIdentifier;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player player, final int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(final @NotNull Player player, final int buttonId) {
        final List<ResourceLocation> fluids = orderedSelectableFluids();
        final int fluidIndex = buttonId / 2;
        if (fluidIndex < 0 || fluidIndex >= fluids.size()) {
            return false;
        }
        final boolean secondary = (buttonId & 1) == 1;
        final ItemStack stack = this.getIdentifierStack();
        if (!(stack.getItem() instanceof final FluidIdentifierItem item)) {
            return false;
        }
        item.setFluidId(stack, fluids.get(fluidIndex), !secondary);
        this.broadcastChanges();
        return true;
    }

    public ItemStack getIdentifierStack() {
        return this.inventory.getItem(this.itemSlot);
    }

    public int getItemSlot() {
        return this.itemSlot;
    }

    public static List<ResourceLocation> orderedSelectableFluids() {
        final List<ResourceLocation> ids = new ArrayList<>();
        for (final ResourceLocation key : ForgeRegistries.FLUIDS.getKeys()) {
            if ("empty".equals(key.getPath()) || key.getPath().startsWith("flowing_")) {
                continue;
            }
            ids.add(key);
        }
        ids.sort(Comparator.comparing(ResourceLocation::toString));
        return ids;
    }

    public static String displayName(final ResourceLocation fluidId) {
        if (!ForgeRegistries.FLUIDS.containsKey(fluidId)) {
            return fluidId.toString();
        }
        return new FluidStack(ForgeRegistries.FLUIDS.getValue(fluidId), 1).getDisplayName().getString();
    }
}
