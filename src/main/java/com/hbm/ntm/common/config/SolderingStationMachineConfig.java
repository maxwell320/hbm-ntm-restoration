package com.hbm.ntm.common.config;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.ntm.common.machine.IConfigurableMachine;
import java.io.IOException;

public final class SolderingStationMachineConfig implements IConfigurableMachine {
    public static final SolderingStationMachineConfig INSTANCE = new SolderingStationMachineConfig();

    private int baseMaxPower = 2_000;
    private int internalEnergyCapacity = 2_000_000;
    private int fluidTankCapacity = 8_000;

    private SolderingStationMachineConfig() {
    }

    @Override
    public String getConfigName() {
        return "machine_soldering_station";
    }

    @Override
    public void readIfPresent(final JsonObject object) {
        this.baseMaxPower = Math.max(1, IConfigurableMachine.grab(object, "baseMaxPower", this.baseMaxPower));
        this.internalEnergyCapacity = Math.max(1, IConfigurableMachine.grab(object, "internalEnergyCapacity", this.internalEnergyCapacity));
        this.fluidTankCapacity = Math.max(1, IConfigurableMachine.grab(object, "fluidTankCapacity", this.fluidTankCapacity));
    }

    @Override
    public void writeConfig(final JsonWriter writer) throws IOException {
        writer.name("baseMaxPower").value(this.baseMaxPower);
        writer.name("internalEnergyCapacity").value(this.internalEnergyCapacity);
        writer.name("fluidTankCapacity").value(this.fluidTankCapacity);
    }

    public int baseMaxPower() {
        return this.baseMaxPower;
    }

    public int internalEnergyCapacity() {
        return this.internalEnergyCapacity;
    }

    public int fluidTankCapacity() {
        return this.fluidTankCapacity;
    }
}

