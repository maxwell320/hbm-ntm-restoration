package com.hbm.ntm.client;

import com.hbm.ntm.HbmNtmMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HbmNtmMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class HbmClientSetup {
    private HbmClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        HbmNtmMod.LOGGER.debug("Client setup event received for {}", HbmNtmMod.MOD_ID);
    }
}
