package com.hbm.ntm.common.material;

public enum HbmMaterialShape {
    FRAGMENT("bedrockorefragment", "Fragment", "fragments", "item/prismarine_shard", "item/bedrock_ore_fragment", "%s Bedrock Ore Fragment"),
    INGOT("ingot", "Ingot", "ingots", "item/iron_ingot", "item/ingot_raw"),
    NUGGET("nugget", "Nugget", "nuggets", "item/gold_nugget"),
    GEM("gem", "Gem", "gems", "item/emerald"),
    CRYSTAL("crystal", "Crystal", "crystals", "item/amethyst_shard", null, "%s Crystals"),
    DUST_TINY("dust_tiny", "Tiny Dust", "tiny_dusts", "item/sugar", null, "Tiny Pile of %s Powder"),
    ORE_DUST("dust_ore", "Ore Dust", "ore_dusts", "item/gunpowder", null, "Crushed %s"),
    DUST("dust", "Dust", "dusts", "item/gunpowder", "item/dust", "%s Powder"),
    WIRE("wire", "Fine Wire", "wires", "item/string", "item/wire_fine", "%s Wire"),
    DENSE_WIRE("wire_dense", "Dense Wire", "dense_wires", "item/redstone", "item/wire_dense", "Dense %s Wire"),
    BOLT("bolt", "Bolt", "bolts", "item/iron_nugget", "item/bolt"),
    PLATE("plate", "Plate", "plates", "item/paper"),
    CAST_PLATE("plate_cast", "Cast Plate", "cast_plates", "item/map", "item/plate_cast", "Cast %s Plate"),
    WELDED_PLATE("plate_welded", "Welded Plate", "welded_plates", "item/book", "item/plate_welded", "Welded %s Plate"),
    SHELL("shellntm", "Shell", "shells", "item/rabbit_hide", "item/shell"),
    PIPE("pipentm", "Pipe", "pipes", "item/copper_ingot", "item/pipe"),
    BILLET("billet", "Billet", "billets", "item/brick"),
    LIGHT_BARREL("part_barrel_light", "Light Barrel", "light_barrels", "item/blaze_rod", "item/part_barrel_light", "Light %s Barrel"),
    HEAVY_BARREL("part_barrel_heavy", "Heavy Barrel", "heavy_barrels", "item/blaze_powder", "item/part_barrel_heavy", "Heavy %s Barrel"),
    LIGHT_RECEIVER("part_receiver_light", "Light Receiver", "light_receivers", "item/flint", "item/part_receiver_light", "Light %s Receiver"),
    HEAVY_RECEIVER("part_receiver_heavy", "Heavy Receiver", "heavy_receivers", "item/netherite_scrap", "item/part_receiver_heavy", "Heavy %s Receiver"),
    MECHANISM("part_mechanism", "Mechanism", "mechanisms", "item/iron_nugget", "item/part_mechanism"),
    STOCK("part_stock", "Stock", "stocks", "item/stick", "item/part_stock"),
    GRIP("part_grip", "Grip", "grips", "item/leather", "item/part_grip");

    private final String registryPrefix;
    private final String displaySuffix;
    private final String tagFolder;
    private final String defaultTexturePath;
    private final String legacySharedTexturePath;
    private final String translationFormat;

    HbmMaterialShape(final String registryPrefix, final String displaySuffix, final String tagFolder, final String defaultTexturePath) {
        this(registryPrefix, displaySuffix, tagFolder, defaultTexturePath, null, "%s " + displaySuffix);
    }

    HbmMaterialShape(final String registryPrefix, final String displaySuffix, final String tagFolder, final String defaultTexturePath,
                     final String legacySharedTexturePath) {
        this(registryPrefix, displaySuffix, tagFolder, defaultTexturePath, legacySharedTexturePath, "%s " + displaySuffix);
    }

    HbmMaterialShape(final String registryPrefix, final String displaySuffix, final String tagFolder, final String defaultTexturePath,
                     final String legacySharedTexturePath, final String translationFormat) {
        this.registryPrefix = registryPrefix;
        this.displaySuffix = displaySuffix;
        this.tagFolder = tagFolder;
        this.defaultTexturePath = defaultTexturePath;
        this.legacySharedTexturePath = legacySharedTexturePath;
        this.translationFormat = translationFormat;
    }

    public String registryPrefix() {
        return this.registryPrefix;
    }

    public String displaySuffix() {
        return this.displaySuffix;
    }

    public String formatDisplayName(final String materialDisplayName) {
        return this.translationFormat.formatted(materialDisplayName);
    }

    public String tagFolder() {
        return this.tagFolder;
    }

    public String defaultTexturePath() {
        return this.defaultTexturePath;
    }

    public String legacySharedTexturePath() {
        return this.legacySharedTexturePath;
    }

    public boolean usesLegacySharedTexture() {
        return this.legacySharedTexturePath != null;
    }
}
