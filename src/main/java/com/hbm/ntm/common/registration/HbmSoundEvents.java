package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class HbmSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HbmNtmMod.MOD_ID);

    private HbmSoundEvents() {
    }
}
