package com.hbm.ntm.common.fluid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

@SuppressWarnings("null")
public final class FluidNetworkDistributor {
    private FluidNetworkDistributor() {
    }

    public static int distribute(final Level level, final BlockPos origin, final FluidStack available, final int perConnectionLimit) {
        return distribute(level, origin, available, perConnectionLimit, null);
    }

    public static int distribute(final Level level, final BlockPos origin, final FluidStack available, final int perConnectionLimit, final Direction excludedSide) {
        if (available.isEmpty() || perConnectionLimit <= 0) {
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
            if (neighbor instanceof final IFluidNetworkReceiver receiver) {
                final long demandValue = Math.min(receiver.getNetworkFluidDemand(available, 0, direction.getOpposite()),
                    receiver.getFluidReceiverSpeed(available, 0, direction.getOpposite()));
                final int demand = (int) Math.min(Integer.MAX_VALUE, demandValue);
                if (demand > 0) {
                    receivers.add(new ReceiverEntry(receiver, direction.getOpposite(), demand, receiver.getFluidNetworkPriority()));
                    totalDemand += demand;
                }
                continue;
            }
            final IFluidHandler handler = neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).orElse(null);
            if (handler == null) {
                continue;
            }
            final int demand = handler.fill(new FluidStack(available, Math.min(available.getAmount(), perConnectionLimit)), IFluidHandler.FluidAction.SIMULATE);
            if (demand <= 0) {
                continue;
            }
            receivers.add(new ReceiverEntry(handler, demand));
            totalDemand += demand;
        }

        if (totalDemand <= 0) {
            return 0;
        }

        receivers.sort(Comparator.comparingInt((ReceiverEntry entry) -> entry.priority.ordinal()).reversed());
        int remaining = Math.min(available.getAmount(), totalDemand);
        int transferred = 0;
        for (int i = 0; i < receivers.size() && remaining > 0; i++) {
            final int demand = receivers.get(i).demand;
            final int weighted = Math.min(demand, Math.max(1, (int) Math.floor((double) Math.min(available.getAmount(), totalDemand) * demand / (double) totalDemand)));
            final int accepted = receivers.get(i).receive(new FluidStack(available, Math.min(remaining, weighted)));
            if (accepted > 0) {
                transferred += accepted;
                remaining -= accepted;
            }
        }

        for (int i = 0; i < receivers.size() && remaining > 0; i++) {
            final int accepted = receivers.get(i).receive(new FluidStack(available, remaining));
            if (accepted > 0) {
                transferred += accepted;
                remaining -= accepted;
            }
        }

        return transferred;
    }

    private record ReceiverEntry(IFluidHandler handler, int demand, FluidNetworkPriority priority, IFluidNetworkReceiver receiver, Direction side) {
        private ReceiverEntry(final IFluidHandler handler, final int demand) {
            this(handler, demand, FluidNetworkPriority.NORMAL, null, null);
        }

        private ReceiverEntry(final IFluidNetworkReceiver receiver, final Direction side, final int demand, final FluidNetworkPriority priority) {
            this(null, demand, priority, receiver, side);
        }

        private int receive(final FluidStack stack) {
            if (this.receiver != null) {
                return stack.getAmount() - (int) Math.min(Integer.MAX_VALUE, this.receiver.receiveNetworkFluid(stack, 0, stack.getAmount(), this.side));
            }
            return this.handler == null ? 0 : this.handler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
