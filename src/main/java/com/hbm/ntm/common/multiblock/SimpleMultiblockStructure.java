package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SimpleMultiblockStructure implements MultiblockStructure {
    private final int[] dimensions;
    private final Block block;

    public SimpleMultiblockStructure(int[] dimensions, Block block) {
        this.dimensions = dimensions.clone();
        this.block = block;
    }

    @Override
    public int[] getDimensions() {
        return this.dimensions.clone();
    }

    @Override
    public boolean canForm(Level level, BlockPos corePos, Direction direction) {
        int[] rotated = rotateDimensions(direction);
        BlockPos minPos = corePos.offset(-rotated[4], -rotated[1], -rotated[2]);
        BlockPos maxPos = corePos.offset(rotated[5], rotated[0], rotated[3]);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (!pos.equals(corePos)) {
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && !state.canBeReplaced()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void form(Level level, BlockPos corePos, Direction direction) {
        int[] rotated = rotateDimensions(direction);
        BlockPos minPos = corePos.offset(-rotated[4], -rotated[1], -rotated[2]);
        BlockPos maxPos = corePos.offset(rotated[5], rotated[0], rotated[3]);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (!pos.equals(corePos)) {
                level.setBlockAndUpdate(pos, this.block.defaultBlockState());
            }
        }
    }

    @Override
    public void breakStructure(Level level, BlockPos corePos, Direction direction) {
        int[] rotated = rotateDimensions(direction);
        BlockPos minPos = corePos.offset(-rotated[4], -rotated[1], -rotated[2]);
        BlockPos maxPos = corePos.offset(rotated[5], rotated[0], rotated[3]);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (level.getBlockEntity(pos) instanceof MultiblockProxyBE) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    public List<BlockPos> getPositions(BlockPos corePos, Direction direction) {
        int[] rotated = rotateDimensions(direction);
        BlockPos minPos = corePos.offset(-rotated[4], -rotated[1], -rotated[2]);
        BlockPos maxPos = corePos.offset(rotated[5], rotated[0], rotated[3]);
        List<BlockPos> positions = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            positions.add(pos.immutable());
        }
        return positions;
    }

    private int[] rotateDimensions(Direction direction) {
        return switch (direction) {
            case NORTH -> this.dimensions;
            case SOUTH -> new int[]{this.dimensions[0], this.dimensions[1], this.dimensions[2], this.dimensions[3], this.dimensions[5], this.dimensions[4]};
            case EAST -> new int[]{this.dimensions[0], this.dimensions[1], this.dimensions[2], this.dimensions[3], this.dimensions[2], this.dimensions[3]};
            case WEST -> new int[]{this.dimensions[0], this.dimensions[1], this.dimensions[2], this.dimensions[3], this.dimensions[3], this.dimensions[2]};
            default -> this.dimensions;
        };
    }
}
