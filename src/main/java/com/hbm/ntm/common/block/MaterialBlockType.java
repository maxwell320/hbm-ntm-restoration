package com.hbm.ntm.common.block;

import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterials;

public enum MaterialBlockType {
    STEEL("block_steel", "Block of Steel", HbmMaterials.STEEL, 5.0F, 50.0F),
    BERYLLIUM("block_beryllium", "Block of Beryllium", HbmMaterials.BERYLLIUM, 5.0F, 20.0F),
    LEAD("block_lead", "Block of Lead", HbmMaterials.LEAD, 5.0F, 50.0F),
    URANIUM("block_uranium", "Block of Uranium", HbmMaterials.URANIUM, 5.0F, 20.0F),
    TITANIUM("block_titanium", "Block of Titanium", HbmMaterials.TITANIUM, 5.0F, 50.0F),
    COPPER("block_copper", "Block of Copper", HbmMaterials.COPPER, 5.0F, 20.0F),
    TUNGSTEN("block_tungsten", "Block of Tungsten", HbmMaterials.TUNGSTEN, 5.0F, 50.0F),
    ALUMINIUM("block_aluminium", "Block of Aluminium", HbmMaterials.ALUMINIUM, 5.0F, 20.0F),
    RED_COPPER("block_red_copper", "Block of Red Copper", HbmMaterials.RED_COPPER, 5.0F, 20.0F),
    ADVANCED_ALLOY("block_advanced_alloy", "Block of Advanced Alloy", HbmMaterials.ADVANCED_ALLOY, 5.0F, 50.0F),
    THORIUM("block_thorium", "Block of Thorium", HbmMaterials.TH232, 5.0F, 20.0F),
    PLUTONIUM("block_plutonium", "Block of Plutonium", HbmMaterials.PLUTONIUM, 5.0F, 20.0F),
    NEPTUNIUM("block_neptunium", "Block of Neptunium", HbmMaterials.NEPTUNIUM, 5.0F, 20.0F),
    POLONIUM("block_polonium", "Block of Polonium", HbmMaterials.POLONIUM, 5.0F, 20.0F),
    TCALLOY("block_tcalloy", "Block of Technetium Steel", HbmMaterials.TCALLOY, 5.0F, 50.0F),
    CDALLOY("block_cdalloy", "Block of Cadmium Steel", HbmMaterials.CDALLOY, 5.0F, 50.0F),
    AUSTRALIUM("block_australium", "Block of Australium", HbmMaterials.AUSTRALIUM, 5.0F, 20.0F),
    SCHRABIDIUM("block_schrabidium", "Block of Schrabidium", HbmMaterials.SCHRABIDIUM, 15.0F, 600.0F),
    SCHRARANIUM("block_schraranium", "Block of Schraranium", HbmMaterials.SCHRARANIUM, 10.0F, 400.0F),
    SCHRABIDATE("block_schrabidate", "Block of Ferric Schrabidate", HbmMaterials.SCHRABIDATE, 15.0F, 600.0F),
    SOLINIUM("block_solinium", "Block of Solinium", HbmMaterials.SOLINIUM, 15.0F, 600.0F),
    MAGNETIZED_TUNGSTEN("block_magnetized_tungsten", "Block of Magnetized Tungsten", HbmMaterials.MAGNETIZED_TUNGSTEN, 5.0F, 50.0F),
    COMBINE_STEEL("block_combine_steel", "Block of CMB Steel", HbmMaterials.COMBINE_STEEL, 5.0F, 50.0F),
    DESH("block_desh", "Block of Desh", HbmMaterials.DESH, 5.0F, 50.0F),
    DURA_STEEL("block_dura_steel", "Block of High-Speed Steel", HbmMaterials.DURA_STEEL, 5.0F, 50.0F),
    STARMETAL("block_starmetal", "Block of Starmetal", HbmMaterials.STARMETAL, 5.0F, 50.0F),
    EUPHEMIUM("block_euphemium", "Block of Euphemium", HbmMaterials.EUPHEMIUM, 50.0F, 6000.0F),
    DINEUTRONIUM("block_dineutronium", "Block of Dineutronium", HbmMaterials.DINEUTRONIUM, 50.0F, 6000.0F),
    YELLOWCAKE("block_yellowcake", "Block of Yellowcake", HbmMaterials.URANIUM, 3.0F, 10.0F),
    COBALT("block_cobalt", "Block of Cobalt", HbmMaterials.COBALT, 5.0F, 20.0F),
    LITHIUM("block_lithium", "Block of Lithium", HbmMaterials.LITHIUM, 5.0F, 20.0F),
    BISMUTH("block_bismuth", "Block of Bismuth", HbmMaterials.BISMUTH, 5.0F, 20.0F),
    ZIRCONIUM("block_zirconium", "Block of Zirconium", HbmMaterials.ZIRCONIUM, 5.0F, 20.0F),
    POLYMER("block_polymer", "Block of Polymer", HbmMaterials.POLYMER, 2.0F, 10.0F),
    BAKELITE("block_bakelite", "Block of Bakelite", HbmMaterials.BAKELITE, 2.0F, 10.0F),
    RUBBER("block_rubber", "Block of Rubber", HbmMaterials.RUBBER, 2.0F, 10.0F),
    ASBESTOS("block_asbestos", "Block of Asbestos", HbmMaterials.ASBESTOS, 3.0F, 10.0F),
    FIBERGLASS("block_fiberglass", "Block of Fiberglass", HbmMaterials.FIBERGLASS, 3.0F, 10.0F);

    private final String blockId;
    private final String displayName;
    private final HbmMaterialDefinition material;
    private final float hardness;
    private final float resistance;

    MaterialBlockType(final String blockId, final String displayName, final HbmMaterialDefinition material, final float hardness, final float resistance) {
        this.blockId = blockId;
        this.displayName = displayName;
        this.material = material;
        this.hardness = hardness;
        this.resistance = resistance;
    }

    public String blockId() {
        return this.blockId;
    }

    public String displayName() {
        return this.displayName;
    }

    public HbmMaterialDefinition material() {
        return this.material;
    }

    public float hardness() {
        return this.hardness;
    }

    public float resistance() {
        return this.resistance;
    }
}
