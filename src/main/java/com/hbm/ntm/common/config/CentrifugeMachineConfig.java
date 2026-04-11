package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class CentrifugeMachineConfig implements IConfigurableMachine {
    public static final CentrifugeMachineConfig INSTANCE = new CentrifugeMachineConfig();

    private int maxPower = 100_000;
    private int processingSpeed = 100;
    private int basePowerPerTick = 200;

    private CentrifugeMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_centrifuge";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.maxPower = Math.max(1, IConfigurableMachine.grab(object, "maxPower", this.maxPower));
        this.processingSpeed = Math.max(1, IConfigurableMachine.grab(object, "processingSpeed", this.processingSpeed));
        this.basePowerPerTick = Math.max(1, IConfigurableMachine.grab(object, "basePowerPerTick", this.basePowerPerTick));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("maxPower").value(this.maxPower);
        writer.name("processingSpeed").value(this.processingSpeed);
        writer.name("basePowerPerTick").value(this.basePowerPerTick);
    }

    public int maxPower() {
        return this.maxPower;
    }

    public int processingSpeed() {
        return this.processingSpeed;
    }

    public int basePowerPerTick() {
        return this.basePowerPerTick;
    }
}
