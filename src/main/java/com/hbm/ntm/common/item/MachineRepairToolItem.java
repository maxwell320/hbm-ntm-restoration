package com.hbm.ntm.common.item;

import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface MachineRepairToolItem {
    default boolean canRepairMachine(final ItemStack stack, final Player player, final MachineBlockEntity machine) {
        return true;
    }

    default void onMachineRepairUsed(final ItemStack stack, final Player player, final MachineBlockEntity machine) {
    }
}

