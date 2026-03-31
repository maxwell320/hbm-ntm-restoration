package com.hbm.ntm.common.material;

public enum HbmMaterialShape {
    FRAGMENT("bedrockorefragment", "Fragment", "fragments", "item/prismarine_shard"),
    INGOT("ingot", "Ingot", "ingots", "item/iron_ingot"),
    NUGGET("nugget", "Nugget", "nuggets", "item/gold_nugget"),
    DUST("dust", "Dust", "dusts", "item/gunpowder"),
    WIRE("wire_fine", "Fine Wire", "wires", "item/string"),
    DENSE_WIRE("wire_dense", "Dense Wire", "dense_wires", "item/redstone"),
    BOLT("bolt", "Bolt", "bolts", "item/iron_nugget"),
    PLATE("plate", "Plate", "plates", "item/paper"),
    CAST_PLATE("plate_triple", "Cast Plate", "cast_plates", "item/map"),
    WELDED_PLATE("plate_sextuple", "Welded Plate", "welded_plates", "item/book"),
    SHELL("shell", "Shell", "shells", "item/rabbit_hide"),
    PIPE("ntmpipe", "Pipe", "pipes", "item/copper_ingot"),
    BILLET("billet", "Billet", "billets", "item/brick"),
    LIGHT_BARREL("barrel_light", "Light Barrel", "light_barrels", "item/blaze_rod"),
    HEAVY_BARREL("barrel_heavy", "Heavy Barrel", "heavy_barrels", "item/blaze_powder"),
    LIGHT_RECEIVER("receiver_light", "Light Receiver", "light_receivers", "item/flint"),
    HEAVY_RECEIVER("receiver_heavy", "Heavy Receiver", "heavy_receivers", "item/netherite_scrap"),
    MECHANISM("gun_mechanism", "Gun Mechanism", "mechanisms", "item/iron_nugget"),
    STOCK("stock", "Stock", "stocks", "item/stick"),
    GRIP("grip", "Grip", "grips", "item/leather");

    private final String registryPrefix;
    private final String displaySuffix;
    private final String tagFolder;
    private final String defaultTexturePath;

    HbmMaterialShape(final String registryPrefix, final String displaySuffix, final String tagFolder, final String defaultTexturePath) {
        this.registryPrefix = registryPrefix;
        this.displaySuffix = displaySuffix;
        this.tagFolder = tagFolder;
        this.defaultTexturePath = defaultTexturePath;
    }

    public String registryPrefix() {
        return this.registryPrefix;
    }

    public String displaySuffix() {
        return this.displaySuffix;
    }

    public String tagFolder() {
        return this.tagFolder;
    }

    public String defaultTexturePath() {
        return this.defaultTexturePath;
    }
}
