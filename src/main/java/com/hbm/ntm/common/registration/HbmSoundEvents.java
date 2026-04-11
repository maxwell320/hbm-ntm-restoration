package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class HbmSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, HbmNtmMod.MOD_ID);
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_1 = register("item.geiger1");
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_2 = register("item.geiger2");
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_3 = register("item.geiger3");
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_4 = register("item.geiger4");
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_5 = register("item.geiger5");
    public static final RegistryObject<SoundEvent> ITEM_GEIGER_6 = register("item.geiger6");
    public static final RegistryObject<SoundEvent> ITEM_RADAWAY = register("item.radaway");
    public static final RegistryObject<SoundEvent> ITEM_TECH_BOOP = register("item.tech_boop");
    public static final RegistryObject<SoundEvent> BLOCK_PRESS_OPERATE = register("block.press_operate");
    public static final RegistryObject<SoundEvent> BLOCK_CENTRIFUGE_OPERATE = register("block.centrifuge_operate");

    private HbmSoundEvents() {
    }

    private static RegistryObject<SoundEvent> register(final String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, id)));
    }
}
