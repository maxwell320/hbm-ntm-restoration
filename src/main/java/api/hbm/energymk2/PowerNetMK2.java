package api.hbm.energymk2;

import api.hbm.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import com.hbm.util.Tuple.Pair;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class PowerNetMK2 {
    private final List<IEnergyProviderMK2> providers = new ArrayList<>();
    private final EnumMap<ConnectionPriority, List<IEnergyReceiverMK2>> receivers = new EnumMap<>(ConnectionPriority.class);
    public long energyTracker = 0L;

    public PowerNetMK2() {
        for (final ConnectionPriority priority : ConnectionPriority.values()) {
            this.receivers.put(priority, new ArrayList<>());
        }
    }

    public void addProvider(final IEnergyProviderMK2 provider) {
        if (!this.providers.contains(provider)) {
            this.providers.add(provider);
        }
    }

    public void removeProvider(final IEnergyProviderMK2 provider) {
        this.providers.remove(provider);
    }

    public void addReceiver(final IEnergyReceiverMK2 receiver) {
        final List<IEnergyReceiverMK2> list = this.receivers.get(receiver.getPriority());
        if (!list.contains(receiver)) {
            list.add(receiver);
        }
    }

    public void removeReceiver(final IEnergyReceiverMK2 receiver) {
        this.receivers.values().forEach(list -> list.remove(receiver));
    }

    public void resetTrackers() {
        this.energyTracker = 0L;
    }

    public void update() {
        long totalAvailable = 0L;
        final List<Pair<IEnergyProviderMK2, Long>> activeProviders = new ArrayList<>();
        for (final IEnergyProviderMK2 provider : this.providers) {
            final long available = Math.min(provider.getPower(), provider.getProviderSpeed());
            if (available > 0L) {
                totalAvailable += available;
                activeProviders.add(new Pair<>(provider, available));
            }
        }

        long totalUsed = 0L;
        for (int index = ConnectionPriority.values().length - 1; index >= 0 && totalAvailable > 0L; index--) {
            final ConnectionPriority priority = ConnectionPriority.values()[index];
            final List<IEnergyReceiverMK2> receiversAtPriority = this.receivers.get(priority);
            long priorityDemand = 0L;
            final List<Pair<IEnergyReceiverMK2, Long>> demandEntries = new ArrayList<>();
            for (final IEnergyReceiverMK2 receiver : receiversAtPriority) {
                final long demand = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
                if (demand > 0L) {
                    priorityDemand += demand;
                    demandEntries.add(new Pair<>(receiver, demand));
                }
            }
            if (priorityDemand <= 0L) {
                continue;
            }
            final long transferBudget = Math.min(totalAvailable, priorityDemand);
            long usedHere = 0L;
            for (final Pair<IEnergyReceiverMK2, Long> entry : demandEntries) {
                final long toSend = Math.min(Math.round((double) transferBudget * entry.getValue() / (double) priorityDemand), entry.getValue());
                usedHere += toSend - entry.getKey().transferPower(toSend);
            }
            totalAvailable -= usedHere;
            totalUsed += usedHere;
        }

        long remainingToChargeProviders = totalUsed;
        for (final Pair<IEnergyProviderMK2, Long> entry : activeProviders) {
            final long toUse = Math.min(Math.round((double) totalUsed * entry.getValue() / Math.max(1.0D, activeProviders.stream().mapToLong(Pair::getValue).sum())), entry.getKey().getPower());
            entry.getKey().usePower(toUse);
            remainingToChargeProviders -= toUse;
        }
        if (remainingToChargeProviders > 0L) {
            for (final IEnergyProviderMK2 provider : this.providers) {
                if (remainingToChargeProviders <= 0L) {
                    break;
                }
                final long extra = Math.min(remainingToChargeProviders, provider.getPower());
                provider.usePower(extra);
                remainingToChargeProviders -= extra;
            }
        }
        this.energyTracker += totalUsed;
    }

    public long sendPowerDiode(final long power) {
        long remaining = power;
        for (int index = ConnectionPriority.values().length - 1; index >= 0 && remaining > 0L; index--) {
            final ConnectionPriority priority = ConnectionPriority.values()[index];
            final List<IEnergyReceiverMK2> receiversAtPriority = this.receivers.get(priority);
            long priorityDemand = 0L;
            final List<Pair<IEnergyReceiverMK2, Long>> demandEntries = new ArrayList<>();
            for (final IEnergyReceiverMK2 receiver : receiversAtPriority) {
                final long demand = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
                if (demand > 0L) {
                    priorityDemand += demand;
                    demandEntries.add(new Pair<>(receiver, demand));
                }
            }
            if (priorityDemand <= 0L) {
                continue;
            }
            final long transferBudget = Math.min(remaining, priorityDemand);
            long usedHere = 0L;
            for (final Pair<IEnergyReceiverMK2, Long> entry : demandEntries) {
                final long toSend = Math.min(Math.round((double) transferBudget * entry.getValue() / (double) priorityDemand), entry.getValue());
                usedHere += toSend - entry.getKey().transferPower(toSend);
            }
            remaining -= usedHere;
            this.energyTracker += usedHere;
        }
        return remaining;
    }
}
