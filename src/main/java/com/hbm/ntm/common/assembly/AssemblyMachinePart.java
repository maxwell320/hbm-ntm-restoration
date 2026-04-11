package com.hbm.ntm.common.assembly;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public enum AssemblyMachinePart implements StringRepresentable {
    CORE("core"),
    PROXY("proxy");

    private final String serializedName;

    AssemblyMachinePart(final String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.serializedName;
    }
}
