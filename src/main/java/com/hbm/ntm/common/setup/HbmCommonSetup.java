package com.hbm.ntm.common.setup;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.config.HbmCommonConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class HbmCommonSetup {
    private HbmCommonSetup() {
    }

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (HbmCommonConfig.ENABLE_DEBUG_LOGGING.get()) {
                HbmNtmMod.LOGGER.info("Completed common setup bootstrap for {}", HbmNtmMod.MOD_ID);
            }
        });
    }
}
