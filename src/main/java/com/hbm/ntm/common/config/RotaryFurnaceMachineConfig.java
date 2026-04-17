package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class RotaryFurnaceMachineConfig implements IConfigurableMachine {
    public static final RotaryFurnaceMachineConfig INSTANCE = new RotaryFurnaceMachineConfig();

    private int inputTankCapacity = 16_000;
    private int steamTankCapacity = 12_000;
    private int spentSteamTankCapacity = 120;
    private int smokeTankCapacity = 4_000;
    private int maxOutputAmount = 10_368;
    private int fluidTransferPerTick = 250;
    private int gasTransferPerTick = 120;

    private RotaryFurnaceMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_rotary_furnace";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.inputTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "inputTankCapacity", this.inputTankCapacity));
        this.steamTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "steamTankCapacity", this.steamTankCapacity));
        this.spentSteamTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "spentSteamTankCapacity", this.spentSteamTankCapacity));
        this.smokeTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "smokeTankCapacity", this.smokeTankCapacity));
        this.maxOutputAmount = Math.max(72, IConfigurableMachine.grab(object, "maxOutputAmount", this.maxOutputAmount));
        this.fluidTransferPerTick = Math.max(1, IConfigurableMachine.grab(object, "fluidTransferPerTick", this.fluidTransferPerTick));
        this.gasTransferPerTick = Math.max(1, IConfigurableMachine.grab(object, "gasTransferPerTick", this.gasTransferPerTick));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("inputTankCapacity").value(this.inputTankCapacity);
        writer.name("steamTankCapacity").value(this.steamTankCapacity);
        writer.name("spentSteamTankCapacity").value(this.spentSteamTankCapacity);
        writer.name("smokeTankCapacity").value(this.smokeTankCapacity);
        writer.name("maxOutputAmount").value(this.maxOutputAmount);
        writer.name("fluidTransferPerTick").value(this.fluidTransferPerTick);
        writer.name("gasTransferPerTick").value(this.gasTransferPerTick);
    }

    public int inputTankCapacity() {
        return this.inputTankCapacity;
    }

    public int steamTankCapacity() {
        return this.steamTankCapacity;
    }

    public int spentSteamTankCapacity() {
        return this.spentSteamTankCapacity;
    }

    public int smokeTankCapacity() {
        return this.smokeTankCapacity;
    }

    public int maxOutputAmount() {
        return this.maxOutputAmount;
    }

    public int fluidTransferPerTick() {
        return this.fluidTransferPerTick;
    }

    public int gasTransferPerTick() {
        return this.gasTransferPerTick;
    }
}
