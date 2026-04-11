package com.hbm.ntm.common.network;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

@SuppressWarnings("null")
public record MachineStateSyncPacket(BlockPos pos, CompoundTag data) {
    public static void encode(final MachineStateSyncPacket packet, final FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
        buffer.writeNbt(packet.data);
    }

    public static MachineStateSyncPacket decode(final FriendlyByteBuf buffer) {
        return new MachineStateSyncPacket(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(final MachineStateSyncPacket packet, final Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> HbmPacketHandler.dispatchMachineStateOnClient(packet));
        context.setPacketHandled(true);
    }
}


