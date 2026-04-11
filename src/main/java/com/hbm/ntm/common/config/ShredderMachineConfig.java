package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class ShredderMachineConfig implements IConfigurableMachine {
    public static final ShredderMachineConfig INSTANCE = new ShredderMachineConfig();

    private int maxPower = 10_000;
    private int processingSpeed = 60;
    private int powerPerTick = 5;

    private ShredderMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_shredder";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.maxPower = Math.max(1, IConfigurableMachine.grab(object, "maxPower", this.maxPower));
        this.processingSpeed = Math.max(1, IConfigurableMachine.grab(object, "processingSpeed", this.processingSpeed));
        this.powerPerTick = Math.max(1, IConfigurableMachine.grab(object, "powerPerTick", this.powerPerTick));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("maxPower").value(this.maxPower);
        writer.name("processingSpeed").value(this.processingSpeed);
        writer.name("powerPerTick").value(this.powerPerTick);
    }

    public int maxPower() {
        return this.maxPower;
    }

    public int processingSpeed() {
        return this.processingSpeed;
    }

    public int powerPerTick() {
        return this.powerPerTick;
    }
}

