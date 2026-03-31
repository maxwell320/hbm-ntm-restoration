package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class HbmItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, HbmNtmMod.MOD_ID);

    private HbmItems() {
    }
}
