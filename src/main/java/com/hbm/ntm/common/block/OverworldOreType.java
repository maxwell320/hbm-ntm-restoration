package com.hbm.ntm.common.block;

public enum OverworldOreType {
    URANIUM("ore_uranium", "Uranium Ore", 3.0F, 5.0F, 2),
    URANIUM_SCORCHED("ore_uranium_scorched", "Scorched Uranium Ore", 3.0F, 5.0F, 2),
    TITANIUM("ore_titanium", "Titanium Ore", 5.0F, 10.0F, 2),
    THORIUM("ore_thorium", "Thorium Ore", 3.0F, 5.0F, 2),
    NITER("ore_niter", "Niter Ore", 2.0F, 3.0F, 1),
    COPPER("ore_copper", "Copper Ore", 3.0F, 5.0F, 1),
    TUNGSTEN("ore_tungsten", "Tungsten Ore", 5.0F, 10.0F, 2),
    ALUMINIUM("ore_aluminium", "Aluminium Ore", 3.0F, 5.0F, 1),
    FLUORITE("ore_fluorite", "Fluorite Ore", 3.0F, 5.0F, 1),
    LEAD("ore_lead", "Lead Ore", 5.0F, 10.0F, 2),
    SCHRABIDIUM("ore_schrabidium", "Schrabidium Ore", 15.0F, 600.0F, 3),
    BERYLLIUM("ore_beryllium", "Beryllium Ore", 3.0F, 5.0F, 2),
    AUSTRALIUM("ore_australium", "Australium Ore", 6.0F, 10.0F, 2),
    RARE("ore_rare", "Rare Earth Ore", 3.0F, 5.0F, 2),
    COBALT("ore_cobalt", "Cobalt Ore", 3.0F, 5.0F, 2),
    CINNEBAR("ore_cinnebar", "Cinnabar Ore", 3.0F, 5.0F, 1),
    COLTAN("ore_coltan", "Coltan Ore", 3.0F, 5.0F, 2),
    ALEXANDRITE("ore_alexandrite", "Alexandrite", 3.0F, 5.0F, 2),
    LIGNITE("ore_lignite", "Lignite", 3.0F, 5.0F, 0),
    ASBESTOS("ore_asbestos", "Asbestos Ore", 3.0F, 5.0F, 1);

    private final String blockId;
    private final String displayName;
    private final float hardness;
    private final float resistance;
    private final int harvestLevel;

    OverworldOreType(final String blockId, final String displayName, final float hardness, final float resistance, final int harvestLevel) {
        this.blockId = blockId;
        this.displayName = displayName;
        this.hardness = hardness;
        this.resistance = resistance;
        this.harvestLevel = harvestLevel;
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

    public int harvestLevel() {
        return this.harvestLevel;
    }
}
