package com.hbm.ntm.common.registration;

import net.minecraftforge.eventbus.api.IEventBus;

public final class HbmRegistries {
    private HbmRegistries() {
    }

    public static void register(final IEventBus modEventBus) {
        HbmBlocks.BLOCKS.register(modEventBus);
        HbmBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        HbmCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        HbmFluids.FLUID_TYPES.register(modEventBus);
        HbmFluids.FLUIDS.register(modEventBus);
        HbmFluids.BLOCKS.register(modEventBus);
        HbmMobEffects.MOB_EFFECTS.register(modEventBus);
        HbmItems.ITEMS.register(modEventBus);
        HbmMenuTypes.MENU_TYPES.register(modEventBus);
        HbmSoundEvents.SOUND_EVENTS.register(modEventBus);
    }
}
