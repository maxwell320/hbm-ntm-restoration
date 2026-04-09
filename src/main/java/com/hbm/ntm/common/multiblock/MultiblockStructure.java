package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.List;

public interface MultiblockStructure {
    /** Legacy-style bounds order: up, down, north, south, west, east. */
    int[] getDimensions();

    default int getUp() {
        return getDimensions()[0];
    }

    default int getDown() {
        return getDimensions()[1];
    }

    default int getNorth() {
        return getDimensions()[2];
    }

    default int getSouth() {
        return getDimensions()[3];
    }

    default int getWest() {
        return getDimensions()[4];
    }

    default int getEast() {
        return getDimensions()[5];
    }

    default BlockPos getCorePosition(final BlockPos placedPos, final Direction direction) {
        return placedPos;
    }

    default boolean canPlace(final Level level, final BlockPos placedPos, final Direction direction) {
        final BlockPos corePos = this.getCorePosition(placedPos, direction);
        for (final BlockPos pos : this.getPositions(corePos, direction)) {
            if (pos.equals(placedPos)) {
                continue;
            }
            final var state = level.getBlockState(pos);
            if (!state.isAir() && !state.canBeReplaced()) {
                return false;
            }
        }
        return true;
    }

    boolean isFormed(Level level, BlockPos corePos, Direction direction);

    void form(Level level, BlockPos corePos, Direction direction);

    void breakStructure(Level level, BlockPos corePos, Direction direction);

    List<BlockPos> getPositions(BlockPos corePos, Direction direction);
}
