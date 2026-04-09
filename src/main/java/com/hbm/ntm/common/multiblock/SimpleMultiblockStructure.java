package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@SuppressWarnings("null")
public class SimpleMultiblockStructure implements MultiblockStructure {
    private final int[] dimensions;
    private final Block block;

    public SimpleMultiblockStructure(final int[] dimensions, final Block block) {
        LegacyMultiblockGeometry.validate(dimensions);
        this.dimensions = dimensions.clone();
        this.block = block;
    }

    @Override
    public int[] getDimensions() {
        return this.dimensions.clone();
    }

    @Override
    public boolean isFormed(final Level level, final BlockPos corePos, final Direction direction) {
        for (final BlockPos pos : this.getPositions(corePos, direction)) {
            if (!this.isExpectedState(level, corePos, pos, direction)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void form(final Level level, final BlockPos corePos, final Direction direction) {
        for (final BlockPos pos : this.getPositions(corePos, direction)) {
            if (this.shouldSkipPlacement(corePos, pos, direction)) {
                continue;
            }
            level.setBlockAndUpdate(pos, this.stateForPosition(corePos, pos, direction));
            this.afterPlace(level, corePos, pos, direction);
        }
    }

    @Override
    public void breakStructure(final Level level, final BlockPos corePos, final Direction direction) {
        for (final BlockPos pos : this.getPositions(corePos, direction)) {
            if (this.shouldSkipPlacement(corePos, pos, direction)) {
                continue;
            }
            if (this.isExpectedState(level, corePos, pos, direction)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    public List<BlockPos> getPositions(final BlockPos corePos, final Direction direction) {
        return LegacyMultiblockGeometry.positions(corePos, this.dimensions, direction);
    }

    protected BlockState stateForPosition(final BlockPos corePos, final BlockPos pos, final Direction direction) {
        return this.block.defaultBlockState();
    }

    protected boolean isExpectedState(final Level level, final BlockPos corePos, final BlockPos pos, final Direction direction) {
        return level.getBlockState(pos).is(this.block);
    }

    protected boolean shouldSkipPlacement(final BlockPos corePos, final BlockPos pos, final Direction direction) {
        return pos.equals(corePos);
    }

    protected void afterPlace(final Level level, final BlockPos corePos, final BlockPos pos, final Direction direction) {
    }
}
