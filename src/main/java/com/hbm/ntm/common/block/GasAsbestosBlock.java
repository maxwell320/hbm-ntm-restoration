package com.hbm.ntm.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"null", "deprecation"})
public class GasAsbestosBlock extends Block {
    public GasAsbestosBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void onPlace(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState oldState,
                        final boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void neighborChanged(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Block block,
                                final @NotNull BlockPos fromPos, final boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide()) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void tick(final @NotNull BlockState state, final @NotNull ServerLevel level, final @NotNull BlockPos pos, final @NotNull RandomSource random) {
        if (random.nextInt(50) == 0) {
            level.removeBlock(pos, false);
            return;
        }

        if (!tryMove(level, pos, firstDirection(random)) && !tryMove(level, pos, secondDirection(random))) {
            level.scheduleTick(pos, this, 2);
        }
    }

    @Override
    public boolean propagatesSkylightDown(final @NotNull BlockState state, final @NotNull BlockGetter reader, final @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(final @NotNull BlockState state, final @NotNull BlockGetter reader, final @NotNull BlockPos pos) {
        return 1.0F;
    }

    private boolean tryMove(final ServerLevel level, final BlockPos pos, final Direction direction) {
        final BlockPos targetPos = pos.relative(direction);
        if (!level.getBlockState(targetPos).isAir()) {
            return false;
        }

        level.setBlock(targetPos, defaultBlockState(), Block.UPDATE_ALL);
        level.removeBlock(pos, false);
        level.scheduleTick(targetPos, this, 2);
        return true;
    }

    private Direction firstDirection(final RandomSource random) {
        if (random.nextInt(5) == 0) {
            return Direction.DOWN;
        }
        return Direction.from3DDataValue(random.nextInt(6));
    }

    private Direction secondDirection(final RandomSource random) {
        return Direction.from2DDataValue(random.nextInt(4));
    }
}
