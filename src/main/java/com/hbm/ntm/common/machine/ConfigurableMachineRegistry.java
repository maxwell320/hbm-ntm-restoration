package com.hbm.ntm.common.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ConfigurableMachineRegistry {
    private static final List<Supplier<? extends IConfigurableMachine>> ENTRIES = new ArrayList<>();

    private ConfigurableMachineRegistry() {
    }

    public static void register(final Supplier<? extends IConfigurableMachine> supplier) {
        ENTRIES.add(supplier);
    }

    public static List<IConfigurableMachine> createInstances() {
        final List<IConfigurableMachine> machines = new ArrayList<>(ENTRIES.size());
        for (final Supplier<? extends IConfigurableMachine> supplier : ENTRIES) {
            try {
                final IConfigurableMachine machine = supplier.get();
                if (machine != null) {
                    machines.add(machine);
                }
            } catch (Exception ignored) {
            }
        }
        return machines;
    }
}

