package com.hbm.lib;

public final class HbmCollection {
    public enum EnumGunManufacturer {
        ARMALITE,
        AUTO_ORDINANCE,
        BAE,
        BENELLI,
        BLACK_MESA,
        CERIX,
        COLT,
        COMBINE,
        CUBE,
        DRG,
        ENZINGER,
        EQUESTRIA,
        F_PRICE,
        F_STRONG,
        FLIMFLAM,
        GLORIA,
        H_AND_K,
        H_AND_R,
        HASBRO,
        IF,
        IMI,
        IMI_BIGMT,
        LANGFORD,
        MAGNUM_R_IMI,
        MANN,
        MAXIM,
        METRO,
        MWT,
        NAWS,
        ERFURT,
        NONE,
        OXFORD,
        LUNA,
        RAYTHEON,
        REMINGTON,
        ROCKWELL,
        ROCKWELL_U,
        RYAN,
        SAAB,
        SACO,
        TULSKY,
        UAC,
        UNKNOWN,
        WESTTEK,
        WGW,
        WINCHESTER,
        WINCHESTER_BIGMT;

        public String getKey() {
            return "gun.make." + this;
        }
    }

    public static final String ammo = "desc.item.gun.ammo";
    public static final String ammoMag = "desc.item.gun.ammoMag";
    public static final String ammoBelt = "desc.item.gun.ammoBelt";
    public static final String ammoEnergy = "desc.item.gun.ammoEnergy";
    public static final String altAmmoEnergy = "desc.item.gun.ammoEnergyAlt";
    public static final String ammoType = "desc.item.gun.ammoType";
    public static final String altAmmoType = "desc.item.gun.ammoTypeAlt";
    public static final String gunName = "desc.item.gun.name";
    public static final String gunMaker = "desc.item.gun.manufacturer";
    public static final String gunDamage = "desc.item.gun.damage";
    public static final String gunPellets = "desc.item.gun.pellets";
    public static final String capacity = "desc.block.barrel.capacity";
    public static final String durability = "desc.item.durability";
    public static final String meltPoint = "desc.misc.meltPoint";
    public static final String lctrl = "desc.misc.lctrl";
    public static final String lshift = "desc.misc.lshift";

    private HbmCollection() {
    }
}
