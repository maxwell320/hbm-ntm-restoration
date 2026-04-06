package com.hbm.ntm.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

public interface IEnergyConnectorBlock {
    boolean canConnectEnergy(BlockGetter level, BlockPos pos, @Nullable Direction side);
}
