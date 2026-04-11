package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class PressMachineConfig implements IConfigurableMachine {
    public static final PressMachineConfig INSTANCE = new PressMachineConfig();

    private int maxSpeed = 400;
    private int progressAtMax = 25;
    private int maxPress = 200;
    private int burnPerOperation = 200;

    private PressMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_press";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.maxSpeed = Math.max(1, IConfigurableMachine.grab(object, "maxSpeed", this.maxSpeed));
        this.progressAtMax = Math.max(1, IConfigurableMachine.grab(object, "progressAtMax", this.progressAtMax));
        this.maxPress = Math.max(1, IConfigurableMachine.grab(object, "maxPress", this.maxPress));
        this.burnPerOperation = Math.max(1, IConfigurableMachine.grab(object, "burnPerOperation", this.burnPerOperation));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("maxSpeed").value(this.maxSpeed);
        writer.name("progressAtMax").value(this.progressAtMax);
        writer.name("maxPress").value(this.maxPress);
        writer.name("burnPerOperation").value(this.burnPerOperation);
    }

    public int maxSpeed() {
        return this.maxSpeed;
    }

    public int progressAtMax() {
        return this.progressAtMax;
    }

    public int maxPress() {
        return this.maxPress;
    }

    public int burnPerOperation() {
        return this.burnPerOperation;
    }
}

