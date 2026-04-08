package com.hbm.ntm.common.saveddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

@SuppressWarnings("null")
public final class TomImpactSavedData extends SavedData {
    private static final String DATA_NAME = "impactData";
    private static final TomImpactSavedData EMPTY = new TomImpactSavedData();
    private float dust;
    private float fire;
    private boolean impact;

    public static TomImpactSavedData get(final Level level) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return EMPTY;
        }
        return serverLevel.getDataStorage().computeIfAbsent(TomImpactSavedData::load, TomImpactSavedData::new, DATA_NAME);
    }

    public static TomImpactSavedData load(final CompoundTag tag) {
        final TomImpactSavedData data = new TomImpactSavedData();
        data.dust = tag.getFloat("dust");
        data.fire = tag.getFloat("fire");
        data.impact = tag.getBoolean("impact");
        return data;
    }

    @Override
    public CompoundTag save(final CompoundTag tag) {
        tag.putFloat("dust", this.dust);
        tag.putFloat("fire", this.fire);
        tag.putBoolean("impact", this.impact);
        return tag;
    }

    public float dust() {
        return this.dust;
    }

    public float fire() {
        return this.fire;
    }

    public boolean impact() {
        return this.impact;
    }

    public void setDust(final float dust) {
        this.dust = dust;
        this.setDirty();
    }

    public void setFire(final float fire) {
        this.fire = fire;
        this.setDirty();
    }

    public void setImpact(final boolean impact) {
        this.impact = impact;
        this.setDirty();
    }
}
