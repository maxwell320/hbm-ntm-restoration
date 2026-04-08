package com.hbm.ntm.common.block;

import java.util.List;

public enum BarrelType {
    PLASTIC("barrel_plastic", 12_000,
        List.of(
            "Capacity: 12,000mB",
            "Cannot store hot fluids",
            "Cannot store corrosive fluids",
            "Cannot store antimatter")),
    IRON("barrel_iron", 8_000,
        List.of(
            "Capacity: 8,000mB",
            "Can store hot fluids",
            "Cannot store corrosive fluids properly",
            "Cannot store antimatter")),
    STEEL("barrel_steel", 16_000,
        List.of(
            "Capacity: 16,000mB",
            "Can store hot fluids",
            "Can store corrosive fluids",
            "Cannot store highly corrosive fluids properly",
            "Cannot store antimatter")),
    TCALLOY("barrel_tcalloy", 24_000,
        List.of(
            "Capacity: 24,000mB",
            "Can store hot fluids",
            "Can store corrosive fluids",
            "Cannot store antimatter")),
    ANTIMATTER("barrel_antimatter", 16_000,
        List.of(
            "Capacity: 16,000mB",
            "Can store hot fluids",
            "Can store corrosive fluids",
            "Can store antimatter")),
    CORRODED("barrel_corroded", 6_000,
        List.of(
            "Capacity: 6,000mB",
            "Leaky and unstable",
            "Can store hot fluids",
            "Can store corrosive fluids",
            "Cannot store antimatter"));

    private final String blockId;
    private final int capacity;
    private final List<String> tooltipLines;

    BarrelType(final String blockId, final int capacity, final List<String> tooltipLines) {
        this.blockId = blockId;
        this.capacity = capacity;
        this.tooltipLines = tooltipLines;
    }

    public String blockId() {
        return this.blockId;
    }

    public int capacity() {
        return this.capacity;
    }

    public List<String> tooltipLines() {
        return this.tooltipLines;
    }

    public boolean isPlastic() {
        return this == PLASTIC;
    }

    public boolean isIron() {
        return this == IRON;
    }

    public boolean isSteel() {
        return this == STEEL;
    }

    public boolean isCorroded() {
        return this == CORRODED;
    }

    public boolean isAntimatter() {
        return this == ANTIMATTER;
    }
}
