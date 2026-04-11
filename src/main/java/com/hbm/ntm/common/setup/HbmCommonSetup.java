package com.hbm.ntm.common.setup;

import api.hbm.block.IToolable;
import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.config.HbmCommonConfig;
import com.hbm.ntm.common.config.MachineDynConfig;
import com.hbm.ntm.common.network.HbmPacketHandler;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class HbmCommonSetup {
    private HbmCommonSetup() {
    }

    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            HbmPacketHandler.register();
            MachineDynConfig.initialize();
            IToolable.ToolType.TORCH.register(new ItemStack(Objects.requireNonNull(HbmItems.BLOWTORCH.get())));
            IToolable.ToolType.TORCH.register(new ItemStack(Objects.requireNonNull(HbmItems.ACETYLENE_TORCH.get())));
            if (HbmCommonConfig.ENABLE_DEBUG_LOGGING.get()) {
                HbmNtmMod.LOGGER.info("Completed common setup bootstrap for {}", HbmNtmMod.MOD_ID);
            }
        });
    }
}
