package com.hbm.ntm.common.rotary;

import net.minecraft.util.StringRepresentable;

public enum RotaryFurnacePart implements StringRepresentable {
    CORE("core"),
    PROXY("proxy");

    private final String serializedName;

    RotaryFurnacePart(final String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }
}
