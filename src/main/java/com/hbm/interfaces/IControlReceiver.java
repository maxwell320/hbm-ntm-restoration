package com.hbm.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public interface IControlReceiver {

    boolean hasPermission(Player player);

    void receiveControl(CompoundTag data);

    default void receiveControl(final Player player, final CompoundTag data) {
    }
}
