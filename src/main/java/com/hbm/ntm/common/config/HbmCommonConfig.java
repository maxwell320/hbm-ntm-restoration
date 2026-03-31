package com.hbm.ntm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class HbmCommonConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING = BUILDER.define("debug.enableBootstrapLogging", false);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private HbmCommonConfig() {
    }

    public static void register(final FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, SPEC);
    }
}
