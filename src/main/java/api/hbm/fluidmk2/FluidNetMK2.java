package api.hbm.fluidmk2;

import api.hbm.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.util.Tuple.Pair;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FluidNetMK2 {
    private static final Map<Key, FluidNode> NODES = new HashMap<>();

    private final FluidType type;
    private final List<IFluidProviderMK2>[] providers = new List[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
    private final EnumMap<ConnectionPriority, List<IFluidReceiverMK2>>[] receivers = new EnumMap[IFluidUserMK2.HIGHEST_VALID_PRESSURE + 1];
    public long fluidTracker = 0L;

    public FluidNetMK2(final FluidType type) {
        this.type = type;
        for (int pressure = 0; pressure <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; pressure++) {
            this.providers[pressure] = new ArrayList<>();
            this.receivers[pressure] = new EnumMap<>(ConnectionPriority.class);
            for (final ConnectionPriority priority : ConnectionPriority.values()) {
                this.receivers[pressure].put(priority, new ArrayList<>());
            }
        }
    }

    public static FluidNode getNode(final Level level, final BlockPos pos, final FluidType type) {
        return NODES.get(new Key(level.dimension().location(), pos, type.getName()));
    }

    public static void createNode(final Level level, final FluidNode node) {
        NODES.put(new Key(level.dimension().location(), node.pos(), node.type().getName()), node);
    }

    public static void destroyNode(final Level level, final BlockPos pos, final FluidType type) {
        NODES.remove(new Key(level.dimension().location(), pos, type.getName()));
    }

    public void addProvider(final IFluidProviderMK2 provider) {
        for (int pressure = 0; pressure <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; pressure++) {
            if (!this.providers[pressure].contains(provider)) {
                this.providers[pressure].add(provider);
            }
        }
    }

    public void addReceiver(final IFluidReceiverMK2 receiver) {
        for (int pressure = 0; pressure <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; pressure++) {
            final List<IFluidReceiverMK2> list = this.receivers[pressure].get(receiver.getFluidPriority());
            if (!list.contains(receiver)) {
                list.add(receiver);
            }
        }
    }

    public void removeReceiver(final IFluidReceiverMK2 receiver) {
        for (int pressure = 0; pressure <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; pressure++) {
            this.receivers[pressure].values().forEach(list -> list.remove(receiver));
        }
    }

    public void resetTrackers() {
        this.fluidTracker = 0L;
    }

    public void update() {
        for (int pressure = 0; pressure <= IFluidUserMK2.HIGHEST_VALID_PRESSURE; pressure++) {
            long totalAvailable = 0L;
            final List<Pair<IFluidProviderMK2, Long>> activeProviders = new ArrayList<>();
            for (final IFluidProviderMK2 provider : this.providers[pressure]) {
                final int[] range = provider.getProvidingPressureRange(this.type);
                if (pressure < range[0] || pressure > range[1]) {
                    continue;
                }
                final long available = Math.min(provider.getFluidAvailable(this.type, pressure), provider.getProviderSpeed(this.type, pressure));
                if (available > 0L) {
                    totalAvailable += available;
                    activeProviders.add(new Pair<>(provider, available));
                }
            }

            long usedTotal = 0L;
            for (int index = ConnectionPriority.values().length - 1; index >= 0 && totalAvailable > 0L; index--) {
                final ConnectionPriority priority = ConnectionPriority.values()[index];
                final List<IFluidReceiverMK2> receiversAtPriority = this.receivers[pressure].get(priority);
                long demand = 0L;
                final List<Pair<IFluidReceiverMK2, Long>> demandEntries = new ArrayList<>();
                for (final IFluidReceiverMK2 receiver : receiversAtPriority) {
                    final int[] range = receiver.getReceivingPressureRange(this.type);
                    if (pressure < range[0] || pressure > range[1]) {
                        continue;
                    }
                    final long required = Math.min(receiver.getDemand(this.type, pressure), receiver.getReceiverSpeed(this.type, pressure));
                    if (required > 0L) {
                        demand += required;
                        demandEntries.add(new Pair<>(receiver, required));
                    }
                }
                if (demand <= 0L) {
                    continue;
                }
                final long transferBudget = Math.min(totalAvailable, demand);
                long usedHere = 0L;
                for (final Pair<IFluidReceiverMK2, Long> entry : demandEntries) {
                    final long toSend = Math.min(Math.round((double) transferBudget * entry.getValue() / (double) demand), entry.getValue());
                    usedHere += toSend - entry.getKey().transferFluid(this.type, pressure, toSend);
                }
                totalAvailable -= usedHere;
                usedTotal += usedHere;
            }

            long leftover = usedTotal;
            for (final Pair<IFluidProviderMK2, Long> entry : activeProviders) {
                final long toUse = Math.min(Math.round((double) usedTotal * entry.getValue() / Math.max(1.0D, activeProviders.stream().mapToLong(Pair::getValue).sum())),
                    entry.getKey().getFluidAvailable(this.type, pressure));
                entry.getKey().useUpFluid(this.type, pressure, toUse);
                leftover -= toUse;
            }
            if (leftover > 0L) {
                for (final Pair<IFluidProviderMK2, Long> entry : activeProviders) {
                    if (leftover <= 0L) {
                        break;
                    }
                    final long extra = Math.min(leftover, entry.getKey().getFluidAvailable(this.type, pressure));
                    entry.getKey().useUpFluid(this.type, pressure, extra);
                    leftover -= extra;
                }
            }
            this.fluidTracker += usedTotal;
        }
    }

    private record Key(ResourceLocation dimension, BlockPos pos, String fluidName) {
    }
}
