package com.hbm.ntm.common.network;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class HbmPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
        .named(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "main"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();
    private static int nextId;
    private static Consumer<MachineStateSyncPacket> machineStateClientDispatcher = packet -> {
    };

    private HbmPacketHandler() {
    }

    public static void register() {
        nextId = 0;
        CHANNEL.registerMessage(nextId++, MachineControlPacket.class, MachineControlPacket::encode, MachineControlPacket::decode, MachineControlPacket::handle);
        CHANNEL.registerMessage(nextId++, MachineStateSyncPacket.class, MachineStateSyncPacket::encode, MachineStateSyncPacket::decode, MachineStateSyncPacket::handle);
        CHANNEL.registerMessage(nextId++, MachineStateRequestPacket.class, MachineStateRequestPacket::encode, MachineStateRequestPacket::decode,
            MachineStateRequestPacket::handle);
    }

    public static void syncMachineState(final MachineBlockEntity machine, final net.minecraft.nbt.CompoundTag data) {
        final Level level = machine.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        final LevelChunk chunk = level.getChunkAt(machine.getBlockPos());
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new MachineStateSyncPacket(machine.getBlockPos(), data.copy()));
    }

    public static void syncMachineStateToPlayer(final MachineBlockEntity machine, final net.minecraft.nbt.CompoundTag data, final ServerPlayer player) {
        final Level level = machine.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MachineStateSyncPacket(machine.getBlockPos(), data.copy()));
    }

    public static void setMachineStateClientDispatcher(final Consumer<MachineStateSyncPacket> dispatcher) {
        machineStateClientDispatcher = dispatcher == null ? packet -> {
        } : dispatcher;
    }

    public static void dispatchMachineStateOnClient(final MachineStateSyncPacket packet) {
        machineStateClientDispatcher.accept(packet);
    }
}
