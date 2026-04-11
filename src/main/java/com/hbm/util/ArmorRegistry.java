package com.hbm.util;

public final class ArmorRegistry {

    private ArmorRegistry() {
    }

    public enum HazardClass {
        GAS_LUNG("hazard.gasChlorine"),
        GAS_MONOXIDE("hazard.gasMonoxide"),
        GAS_INERT("hazard.gasInert"),
        PARTICLE_COARSE("hazard.particleCoarse"),
        PARTICLE_FINE("hazard.particleFine"),
        BACTERIA("hazard.bacteria"),
        GAS_BLISTERING("hazard.corrosive"),
        SAND("hazard.sand"),
        LIGHT("hazard.light");

        public final String lang;

        HazardClass(final String lang) {
            this.lang = lang;
        }
    }
}
