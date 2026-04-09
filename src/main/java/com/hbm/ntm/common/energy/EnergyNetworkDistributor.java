package com.hbm.ntm.common.energy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

@SuppressWarnings("null")
public final class EnergyNetworkDistributor {
    private EnergyNetworkDistributor() {
    }

    public static int distribute(final Level level, final BlockPos origin, final int available, final int perConnectionLimit, final Direction excludedSide) {
        if (available <= 0) {
            return 0;
        }

        final List<ReceiverEntry> receivers = new ArrayList<>();
        int totalDemand = 0;

        for (final Direction direction : Direction.values()) {
            if (direction == excludedSide) {
                continue;
            }
            final BlockEntity neighbor = level.getBlockEntity(origin.relative(direction));
            if (neighbor == null) {
                continue;
            }
            if (neighbor instanceof final IEnergyNetworkReceiver receiver && receiver.canReceiveEnergy(direction.getOpposite())) {
                final int demand = (int) Math.min(Integer.MAX_VALUE,
                    Math.min(receiver.getNetworkEnergyDemand(direction.getOpposite()), receiver.getNetworkReceiverSpeed(direction.getOpposite())));
                if (demand > 0) {
                    receivers.add(new ReceiverEntry(receiver, direction.getOpposite(), demand, receiver.getNetworkPriority()));
                    totalDemand += demand;
                }
                continue;
            }
            final IEnergyStorage storage = neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).orElse(null);
            if (storage == null || !storage.canReceive()) {
                continue;
            }
            final int demand = storage.receiveEnergy(Math.max(1, perConnectionLimit), true);
            if (demand <= 0) {
                continue;
            }
            receivers.add(new ReceiverEntry(storage, demand));
            totalDemand += demand;
        }

        if (totalDemand <= 0) {
            return 0;
        }

        receivers.sort(Comparator.comparingInt((ReceiverEntry entry) -> entry.priority.ordinal()).reversed());
        int remaining = Math.min(available, totalDemand);
        int transferred = 0;
        for (int i = 0; i < receivers.size() && remaining > 0; i++) {
            final int demand = receivers.get(i).demand;
            final int weighted = Math.min(demand, Math.max(1, (int) Math.floor((double) Math.min(available, totalDemand) * demand / (double) totalDemand)));
            final int accepted = receivers.get(i).receive(Math.min(remaining, weighted));
            if (accepted > 0) {
                transferred += accepted;
                remaining -= accepted;
            }
        }

        for (int i = 0; i < receivers.size() && remaining > 0; i++) {
            final int accepted = receivers.get(i).receive(remaining);
            if (accepted > 0) {
                transferred += accepted;
                remaining -= accepted;
            }
        }

        return transferred;
    }

    private record ReceiverEntry(IEnergyStorage storage, int demand, EnergyNetworkPriority priority, IEnergyNetworkReceiver receiver, Direction side) {
        private ReceiverEntry(final IEnergyStorage storage, final int demand) {
            this(storage, demand, EnergyNetworkPriority.NORMAL, null, null);
        }

        private ReceiverEntry(final IEnergyNetworkReceiver receiver, final Direction side, final int demand, final EnergyNetworkPriority priority) {
            this(null, demand, priority, receiver, side);
        }

        private int receive(final int amount) {
            if (this.receiver != null) {
                return amount - (int) Math.min(Integer.MAX_VALUE, this.receiver.receiveNetworkEnergy(this.side, amount));
            }
            return this.storage == null ? 0 : this.storage.receiveEnergy(amount, false);
        }
    }
}
