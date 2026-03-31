package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;

public final class HbmCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HbmNtmMod.MOD_ID);

    private HbmCreativeTabs() {
    }
}
