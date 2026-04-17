package com.hbm.ntm.common.rotary;

import com.hbm.ntm.common.block.RotaryFurnaceBlock;
import com.hbm.ntm.common.block.entity.RotaryFurnaceProxyBlockEntity;
import com.hbm.ntm.common.multiblock.SimpleMultiblockStructure;
import com.hbm.ntm.common.registration.HbmBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public final class RotaryFurnaceStructure extends SimpleMultiblockStructure {
    private static final int[] DIMENSIONS = new int[]{4, 0, 1, 1, 2, 2};
    public static final RotaryFurnaceStructure INSTANCE = new RotaryFurnaceStructure();

    private RotaryFurnaceStructure() {
        super(DIMENSIONS, Blocks.AIR);
    }

    public static Direction normalizeFacing(final Direction direction) {
        return direction.getAxis().isHorizontal() ? direction : Direction.NORTH;
    }

    public boolean canPlaceAt(final Level level, final BlockPos corePos, final Direction facing) {
        final int y = corePos.getY();
        if (y < level.getMinBuildHeight() || y + DIMENSIONS[0] >= level.getMaxBuildHeight()) {
            return false;
        }
        return this.canPlace(level, corePos, normalizeFacing(facing));
    }

    @Override
    protected BlockState stateForPosition(final BlockPos corePos, final BlockPos pos, final Direction direction) {
        return HbmBlocks.MACHINE_ROTARY_FURNACE.get().defaultBlockState()
            .setValue(RotaryFurnaceBlock.FACING, normalizeFacing(direction))
            .setValue(RotaryFurnaceBlock.PART, pos.equals(corePos) ? RotaryFurnacePart.CORE : RotaryFurnacePart.PROXY);
    }

    @Override
    protected boolean isExpectedState(final Level level, final BlockPos corePos, final BlockPos pos, final Direction direction) {
        final BlockState actual = level.getBlockState(pos);
        if (!actual.is(HbmBlocks.MACHINE_ROTARY_FURNACE.get())) {
            return false;
        }
        return actual.getValue(RotaryFurnaceBlock.FACING) == normalizeFacing(direction)
            && actual.getValue(RotaryFurnaceBlock.PART) == (pos.equals(corePos) ? RotaryFurnacePart.CORE : RotaryFurnacePart.PROXY);
    }

    @Override
    protected void afterPlace(final Level level, final BlockPos corePos, final BlockPos pos, final Direction direction) {
        if (pos.equals(corePos)) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof final RotaryFurnaceProxyBlockEntity proxy) {
            proxy.setCorePos(corePos);
            proxy.setDirection(normalizeFacing(direction));
        }
    }
}
