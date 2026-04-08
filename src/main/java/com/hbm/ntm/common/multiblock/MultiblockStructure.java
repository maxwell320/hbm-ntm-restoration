package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.List;

public interface MultiblockStructure {
    int[] getDimensions();

    default int getWidth() {
        return getDimensions()[0];
    }

    default int getHeight() {
        return getDimensions()[1];
    }

    default int getDepth() {
        return getDimensions()[2];
    }

    default int getOffset() {
        return getDimensions()[3];
    }

    boolean canForm(Level level, BlockPos corePos, Direction direction);

    void form(Level level, BlockPos corePos, Direction direction);

    void breakStructure(Level level, BlockPos corePos, Direction direction);

    List<BlockPos> getPositions(BlockPos corePos, Direction direction);

    default BlockPos getCorePosition(BlockPos placedPos, Direction direction) {
        int offset = getOffset();
        return placedPos.relative(direction.getOpposite(), offset);
    }
}
