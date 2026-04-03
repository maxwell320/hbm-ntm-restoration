package com.hbm.ntm.common.block;

public enum SellafieldOreType {
    DIAMOND("ore_sellafield_diamond", "Sellafite Diamond Ore"),
    EMERALD("ore_sellafield_emerald", "Sellafite Emerald Ore"),
    URANIUM_SCORCHED("ore_sellafield_uranium_scorched", "Scorched Sellafite Uranium Ore"),
    SCHRABIDIUM("ore_sellafield_schrabidium", "Sellafite Schrabidium Ore"),
    RADGEM("ore_sellafield_radgem", "Sellafite Radioactive Gem Ore");

    private final String blockId;
    private final String displayName;

    SellafieldOreType(final String blockId, final String displayName) {
        this.blockId = blockId;
        this.displayName = displayName;
    }

    public String blockId() {
        return blockId;
    }

    public String displayName() {
        return displayName;
    }
}
