package com.hbm.ntm.common.block;

import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterials;

public enum MaterialBlockType {
    STEEL("block_steel", "Block of Steel", HbmMaterials.STEEL, 5.0F, 50.0F),
    BERYLLIUM("block_beryllium", "Block of Beryllium", HbmMaterials.BERYLLIUM, 5.0F, 20.0F);

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
