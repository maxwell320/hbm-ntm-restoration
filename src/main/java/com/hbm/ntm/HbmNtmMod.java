package com.hbm.ntm;

import com.hbm.ntm.common.config.HbmCommonConfig;
import com.hbm.ntm.common.registration.HbmRegistries;
import com.hbm.ntm.common.setup.HbmCommonSetup;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(HbmNtmMod.MOD_ID)
public class HbmNtmMod {
    public static final String MOD_ID = "hbmntm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HbmNtmMod(final FMLJavaModLoadingContext context) {
        final IEventBus modEventBus = context.getModEventBus();

        HbmCommonConfig.register(context);
        HbmRegistries.register(modEventBus);
        modEventBus.addListener(HbmCommonSetup::onCommonSetup);

        LOGGER.info("Initializing {}", MOD_ID);
    }
}
