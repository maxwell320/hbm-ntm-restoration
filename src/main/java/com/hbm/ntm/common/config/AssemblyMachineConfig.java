package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class AssemblyMachineConfig implements IConfigurableMachine {
    public static final AssemblyMachineConfig INSTANCE = new AssemblyMachineConfig();

    private int maxPower = 100_000;
    private int tankCapacity = 4_000;

    private AssemblyMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_assembly_machine";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.maxPower = Math.max(1, IConfigurableMachine.grab(object, "maxPower", this.maxPower));
        this.tankCapacity = Math.max(1, IConfigurableMachine.grab(object, "tankCapacity", this.tankCapacity));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("maxPower").value(this.maxPower);
        writer.name("tankCapacity").value(this.tankCapacity);
    }

    public int maxPower() {
        return this.maxPower;
    }

    public int tankCapacity() {
        return this.tankCapacity;
    }
}

