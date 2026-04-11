package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class GasCentrifugeMachineConfig implements IConfigurableMachine {
    public static final GasCentrifugeMachineConfig INSTANCE = new GasCentrifugeMachineConfig();

    private int maxPower = 100_000;
    private int processingSpeed = 150;
    private int overclockProcessingSpeed = 80;
    private int powerPerTick = 200;
    private int overclockPowerPerTick = 300;
    private int fluidTankCapacity = 2_000;
    private int pseudoTankCapacity = 8_000;
    private int transferIntervalTicks = 10;

    private GasCentrifugeMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_gas_centrifuge";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.maxPower = Math.max(1, IConfigurableMachine.grab(object, "maxPower", this.maxPower));
        this.processingSpeed = Math.max(1, IConfigurableMachine.grab(object, "processingSpeed", this.processingSpeed));
        this.overclockProcessingSpeed = Math.max(1, IConfigurableMachine.grab(object, "overclockProcessingSpeed", this.overclockProcessingSpeed));
        this.powerPerTick = Math.max(1, IConfigurableMachine.grab(object, "powerPerTick", this.powerPerTick));
        this.overclockPowerPerTick = Math.max(1, IConfigurableMachine.grab(object, "overclockPowerPerTick", this.overclockPowerPerTick));
        this.fluidTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "fluidTankCapacity", this.fluidTankCapacity));
        this.pseudoTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "pseudoTankCapacity", this.pseudoTankCapacity));
        this.transferIntervalTicks = Math.max(1, IConfigurableMachine.grab(object, "transferIntervalTicks", this.transferIntervalTicks));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("maxPower").value(this.maxPower);
        writer.name("processingSpeed").value(this.processingSpeed);
        writer.name("overclockProcessingSpeed").value(this.overclockProcessingSpeed);
        writer.name("powerPerTick").value(this.powerPerTick);
        writer.name("overclockPowerPerTick").value(this.overclockPowerPerTick);
        writer.name("fluidTankCapacity").value(this.fluidTankCapacity);
        writer.name("pseudoTankCapacity").value(this.pseudoTankCapacity);
        writer.name("transferIntervalTicks").value(this.transferIntervalTicks);
    }

    public int maxPower() {
        return this.maxPower;
    }

    public int processingSpeed() {
        return this.processingSpeed;
    }

    public int overclockProcessingSpeed() {
        return this.overclockProcessingSpeed;
    }

    public int powerPerTick() {
        return this.powerPerTick;
    }

    public int overclockPowerPerTick() {
        return this.overclockPowerPerTick;
    }

    public int fluidTankCapacity() {
        return this.fluidTankCapacity;
    }

    public int pseudoTankCapacity() {
        return this.pseudoTankCapacity;
    }

    public int transferIntervalTicks() {
        return this.transferIntervalTicks;
    }
}
