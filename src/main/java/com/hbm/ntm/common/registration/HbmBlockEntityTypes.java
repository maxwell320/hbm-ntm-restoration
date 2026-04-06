package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.entity.CableBlockEntity;
import com.hbm.ntm.common.block.entity.CreativeEnergySourceBlockEntity;
import com.hbm.ntm.common.block.entity.GeigerCounterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class HbmBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HbmNtmMod.MOD_ID);
    public static final RegistryObject<BlockEntityType<CableBlockEntity>> RED_CABLE = BLOCK_ENTITY_TYPES.register("red_cable",
        () -> BlockEntityType.Builder.of(CableBlockEntity::new, HbmBlocks.RED_CABLE.get(), HbmBlocks.RED_CABLE_CLASSIC.get()).build(null));
    public static final RegistryObject<BlockEntityType<CreativeEnergySourceBlockEntity>> CREATIVE_ENERGY_SOURCE = BLOCK_ENTITY_TYPES.register("creative_energy_source",
        () -> BlockEntityType.Builder.of(CreativeEnergySourceBlockEntity::new, HbmBlocks.CREATIVE_ENERGY_SOURCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<GeigerCounterBlockEntity>> GEIGER = BLOCK_ENTITY_TYPES.register("geiger",
        () -> BlockEntityType.Builder.of(GeigerCounterBlockEntity::new, HbmBlocks.GEIGER.get()).build(null));

    private HbmBlockEntityTypes() {
    }
}
