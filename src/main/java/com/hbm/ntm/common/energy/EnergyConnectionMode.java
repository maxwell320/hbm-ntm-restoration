package com.hbm.ntm.common.energy;

public enum EnergyConnectionMode {
    NONE(false, false),
    RECEIVE(true, false),
    EXTRACT(false, true),
    BOTH(true, true);

    private final boolean canReceive;
    private final boolean canExtract;

    EnergyConnectionMode(final boolean canReceive, final boolean canExtract) {
        this.canReceive = canReceive;
        this.canExtract = canExtract;
    }

    public boolean canReceive() {
        return this.canReceive;
    }

    public boolean canExtract() {
        return this.canExtract;
    }

    public boolean canConnect() {
        return this.canReceive || this.canExtract;
    }

    public static EnergyConnectionMode of(final boolean canReceive, final boolean canExtract) {
        if (canReceive) {
            return canExtract ? BOTH : RECEIVE;
        }
        return canExtract ? EXTRACT : NONE;
    }
}
