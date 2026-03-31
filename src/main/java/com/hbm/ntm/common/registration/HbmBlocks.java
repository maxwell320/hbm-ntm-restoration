package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class HbmBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HbmNtmMod.MOD_ID);

    private HbmBlocks() {
    }
}
