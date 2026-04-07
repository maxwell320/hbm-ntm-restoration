package com.hbm.ntm.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class TemplateFolderItem extends Item {
    public TemplateFolderItem() {
        super(new Properties());
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(Component.literal("Machine Templates: Paper + Dye").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Press Stamps: Flat Stamp").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Siren Tracks: Insulator + Steel Plate").withStyle(ChatFormatting.GRAY));
    }
}
