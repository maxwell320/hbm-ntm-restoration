package com.hbm.ntm.common.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public final class EnergyConnectionHelper {
    private EnergyConnectionHelper() {
    }

    public static boolean canConnectEnergy(final BlockGetter level, final BlockPos pos, @Nullable final Direction side) {
        final BlockState state = level.getBlockState(pos);
        final Direction targetSide = side == null ? null : side.getOpposite();

        if (state.getBlock() instanceof IEnergyConnectorBlock connectorBlock && connectorBlock.canConnectEnergy(level, pos, targetSide)) {
            return true;
        }

        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        if (blockEntity instanceof IEnergyConnector connector && connector.canConnectEnergy(targetSide)) {
            return true;
        }

        if (targetSide != null && blockEntity.getCapability(ForgeCapabilities.ENERGY, targetSide).isPresent()) {
            return true;
        }

        return blockEntity.getCapability(ForgeCapabilities.ENERGY, null).isPresent();
    }
}
