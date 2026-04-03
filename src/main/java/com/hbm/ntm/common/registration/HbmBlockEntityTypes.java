package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.entity.GeigerCounterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class HbmBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, HbmNtmMod.MOD_ID);
    public static final RegistryObject<BlockEntityType<GeigerCounterBlockEntity>> GEIGER = BLOCK_ENTITY_TYPES.register("geiger",
        () -> BlockEntityType.Builder.of(GeigerCounterBlockEntity::new, HbmBlocks.GEIGER.get()).build(null));

    private HbmBlockEntityTypes() {
    }
}
