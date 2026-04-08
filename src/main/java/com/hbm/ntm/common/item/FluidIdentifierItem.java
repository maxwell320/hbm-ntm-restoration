package com.hbm.ntm.common.item;

import com.hbm.ntm.common.menu.FluidIdentifierMenu;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class FluidIdentifierItem extends Item implements IItemFluidIdentifier {
    private static final String PRIMARY_FLUID_TAG = "primary_fluid";
    private static final String SECONDARY_FLUID_TAG = "secondary_fluid";

    public FluidIdentifierItem(final Item.Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ResourceLocation getFluidId(final ItemStack stack) {
        return this.getPrimaryFluidId(stack);
    }

    public @Nullable ResourceLocation getPrimaryFluidId(final ItemStack stack) {
        return stack.hasTag() ? ResourceLocation.tryParse(stack.getOrCreateTag().getString(PRIMARY_FLUID_TAG)) : null;
    }

    public @Nullable ResourceLocation getSecondaryFluidId(final ItemStack stack) {
        return stack.hasTag() ? ResourceLocation.tryParse(stack.getOrCreateTag().getString(SECONDARY_FLUID_TAG)) : null;
    }

    public void setFluidId(final ItemStack stack, @Nullable final ResourceLocation fluidId, final boolean primary) {
        final String key = primary ? PRIMARY_FLUID_TAG : SECONDARY_FLUID_TAG;
        if (fluidId == null) {
            stack.removeTagKey(key);
            return;
        }
        stack.getOrCreateTag().putString(key, fluidId.toString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(final Level level, final Player player, final InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(stack);
        }
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide() && player instanceof final ServerPlayer serverPlayer) {
                final int slot = player.getInventory().selected;
                NetworkHooks.openScreen(serverPlayer,
                    new SimpleMenuProvider((containerId, inventory, ignored) -> new FluidIdentifierMenu(containerId, inventory, slot), Component.translatable(this.getDescriptionId())),
                    buffer -> buffer.writeInt(slot));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        if (!level.isClientSide()) {
            final ResourceLocation primary = this.getPrimaryFluidId(stack);
            final ResourceLocation secondary = this.getSecondaryFluidId(stack);
            this.setFluidId(stack, secondary, true);
            this.setFluidId(stack, primary, false);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        final ResourceLocation primary = this.getPrimaryFluidId(stack);
        final ResourceLocation secondary = this.getSecondaryFluidId(stack);
        tooltip.add(Component.literal("Primary: " + (primary == null ? "None" : FluidIdentifierMenu.displayName(primary))));
        tooltip.add(Component.literal("Secondary: " + (secondary == null ? "None" : FluidIdentifierMenu.displayName(secondary))));
        tooltip.add(Component.translatable("item.hbmntm.fluid_identifier.usage0"));
        tooltip.add(Component.translatable("item.hbmntm.fluid_identifier.usage1"));
    }
}
