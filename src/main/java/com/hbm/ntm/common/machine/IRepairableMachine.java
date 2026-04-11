package com.hbm.ntm.common.machine;

import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IRepairableMachine {
    boolean isDamaged();

    List<ItemStack> getRepairMaterials();

    void repairMachine();

    default boolean canRepairWith(final ItemStack stack) {
        return false;
    }

    default void onRepairToolUsed(final Player player, final ItemStack stack) {
    }

    default void tryExtinguish(final Level level, final net.minecraft.core.BlockPos pos, final ExtinguishType type) {
    }

    enum ExtinguishType {
        WATER,
        FOAM,
        SAND,
        CO2
    }
}
