package com.hbm.ntm;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(HbmNtmMod.MOD_ID)
public class HbmNtmMod {
    public static final String MOD_ID = "hbmntm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HbmNtmMod() {
        LOGGER.info("Initializing {}", MOD_ID);
    }
}
