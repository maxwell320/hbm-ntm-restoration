package com.hbm.ntm.common.soldering;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SolderingStationPart implements StringRepresentable {
    CORE("core"),
    PROXY("proxy");

    private final String name;

    SolderingStationPart(final String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}

