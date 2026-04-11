package com.hbm.interfaces;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiEvent;

public interface IItemHUD {

    void renderHUD(RenderGuiEvent.Pre event, Player player, ItemStack stack);
}
