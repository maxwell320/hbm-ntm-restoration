package com.hbm.ntm.common.transfer;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface TransferNodeProvider {
    TransferNetworkKind getTransferNetworkKind();

    Set<Direction> getConnectionDirections();

    default boolean canConnectTo(final Level level, final BlockPos pos, final Direction side, final BlockEntity neighbor) {
        return true;
    }
}
