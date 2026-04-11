package com.hbm.ntm.client.network;

import com.hbm.ntm.common.machine.IMachineStateSyncReceiver;
import com.hbm.ntm.common.menu.MachineMenuBase;
import com.hbm.ntm.common.network.MachineStateSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MachineClientPacketHandlers {
    private MachineClientPacketHandlers() {
    }

    public static void handleMachineStateSync(final MachineStateSyncPacket packet) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        final BlockEntity blockEntity = minecraft.level.getBlockEntity(packet.pos());
        if (blockEntity instanceof IMachineStateSyncReceiver receiver) {
            receiver.applyMachineStateSync(packet.data().copy());
        }

        if (minecraft.screen instanceof final AbstractContainerScreen<?> containerScreen) {
            final AbstractContainerMenu menu = containerScreen.getMenu();
            if (menu instanceof final MachineMenuBase<?> machineMenu) {
                machineMenu.applyMachineStateSync(packet.pos(), packet.data().copy());
            }
        }
    }
}


