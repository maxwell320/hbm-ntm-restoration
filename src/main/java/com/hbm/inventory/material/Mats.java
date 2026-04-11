package com.hbm.inventory.material;

import com.hbm.ntm.common.material.HbmMaterialDefinition;

public final class Mats {

    private Mats() {
    }

    public static final class MaterialStack {
        public final HbmMaterialDefinition material;
        public int amount;

        public MaterialStack(final HbmMaterialDefinition material, final int amount) {
            this.material = material;
            this.amount = amount;
        }

        public MaterialStack copy() {
            return new MaterialStack(this.material, this.amount);
        }
    }
}
