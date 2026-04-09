package com.hbm.ntm.common.block;

public enum NetherOreType {
    COAL("ore_nether_coal", "Nether Coal Ore", 3.0F, 5.0F),
    SMOLDERING("ore_nether_smoldering", "Smoldering Ore", 3.0F, 5.0F),
    URANIUM("ore_nether_uranium", "Nether Uranium Ore", 3.0F, 5.0F),
    URANIUM_SCORCHED("ore_nether_uranium_scorched", "Scorched Nether Uranium Ore", 3.0F, 5.0F),
    PLUTONIUM("ore_nether_plutonium", "Nether Plutonium Ore", 3.0F, 5.0F),
    TUNGSTEN("ore_nether_tungsten", "Nether Tungsten Ore", 3.0F, 5.0F),
    SULFUR("ore_nether_sulfur", "Nether Sulfur Ore", 3.0F, 5.0F),
    FIRE("ore_nether_fire", "Nether Phosphorus Ore", 3.0F, 5.0F),
    COBALT("ore_nether_cobalt", "Nether Cobalt Ore", 3.0F, 5.0F),
    SCHRABIDIUM("ore_nether_schrabidium", "Nether Schrabidium Ore", 15.0F, 600.0F);

    private final String blockId;
    private final String displayName;
    private final float hardness;
    private final float resistance;

    NetherOreType(final String blockId, final String displayName, final float hardness, final float resistance) {
        this.blockId = blockId;
        this.displayName = displayName;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public String blockId() {
        return this.blockId;
    }

    public String displayName() {
        return this.displayName;
    }

    public float hardness() {
        return this.hardness;
    }

    public float resistance() {
        return this.resistance;
    }
}
