package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.multiblock.MultiblockProxyBE;
import com.hbm.ntm.common.multiblock.MultiblockStructure;
import com.hbm.ntm.common.press.PressStructure;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public class PressProxyBlockEntity extends MultiblockProxyBE {
    public PressProxyBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_PRESS_PROXY.get(), pos, state);
    }

    @Override
    public MultiblockStructure getStructure() {
        return PressStructure.INSTANCE;
    }
}
