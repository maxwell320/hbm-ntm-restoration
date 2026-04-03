package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.SellafieldBlock;
import com.hbm.ntm.common.item.SellafieldBlockItem;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import java.util.Objects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class HbmCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HbmNtmMod.MOD_ID);
    @SuppressWarnings("null")
    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
        .title(Objects.requireNonNull(Component.translatable("itemGroup." + HbmNtmMod.MOD_ID + ".main")))
        .icon(() -> new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.URANIUM, HbmMaterialShape.INGOT).get())))
        .displayItems((parameters, output) -> HbmItems.creativeTabEntries().forEach(item -> {
            if (item == HbmItems.SELLAFIELD) {
                for (int level = 0; level <= SellafieldBlock.MAX_LEVEL; level++) {
                    output.accept(SellafieldBlockItem.withLevel(Objects.requireNonNull(HbmItems.SELLAFIELD.get()), level));
                }
            } else {
                output.accept(Objects.requireNonNull(item.get()));
            }
        }))
        .build());

    private HbmCreativeTabs() {
    }
}
