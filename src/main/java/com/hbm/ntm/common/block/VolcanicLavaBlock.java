package com.hbm.ntm.common.block;

import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmFluids;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VolcanicLavaBlock extends LiquidBlock {
    public VolcanicLavaBlock(final Supplier<? extends FlowingFluid> fluid, final BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void onPlace(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState oldState, final boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            reactToNeighbors(level, pos);
        }
    }

    @Override
    public void neighborChanged(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Block block,
                                final @NotNull BlockPos fromPos, final boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide()) {
            reactToNeighbors(level, pos);
        }
    }

    @Override
    public void randomTick(final @NotNull BlockState state, final @NotNull ServerLevel level, final @NotNull BlockPos pos, final @NotNull RandomSource random) {
        int lavaCount = 0;
        int basaltCount = 0;

        for (final Direction direction : Direction.values()) {
            final BlockPos checkPos = pos.relative(direction);
            final BlockState checkState = level.getBlockState(checkPos);
            if (isTrackedFluid(checkState.getFluidState())) {
                lavaCount++;
            }
            if (checkState.is(getSolidificationBaseBlock())) {
                basaltCount++;
            }
        }

        if (((!level.getFluidState(pos).isSource() && lavaCount < 2) || (random.nextInt(5) == 0 && lavaCount < 5))
            && !isTrackedFluid(level.getFluidState(pos.below()))) {
            onSolidify(level, pos, lavaCount, basaltCount, random);
        }
    }

    @Override
    public void animateTick(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull RandomSource random) {
        if (level.getBlockState(pos.above()).isAir() && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
            if (random.nextInt(100) == 0) {
                final double x = pos.getX() + random.nextDouble();
                final double y = pos.getY() + 1.0D;
                final double z = pos.getZ() + random.nextDouble();
                level.addParticle(ParticleTypes.LAVA, x, y, z, 0.0D, 0.0D, 0.0D);
                level.playLocalSound(x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }

            if (random.nextInt(200) == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F,
                    0.9F + random.nextFloat() * 0.15F, false);
            }
        }

        if (random.nextInt(10) == 0 && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)
            && !level.getBlockState(pos.below(2)).blocksMotion()) {
            level.addParticle(ParticleTypes.DRIPPING_LAVA, pos.getX() + random.nextDouble(), pos.getY() - 1.05D, pos.getZ() + random.nextDouble(), 0.0D, 0.0D, 0.0D);
        }
    }

    protected void reactToNeighbors(final Level level, final BlockPos pos) {
        for (final Direction direction : Direction.values()) {
            final BlockPos reactionPos = pos.relative(direction);
            final BlockState reactionState = getReaction(level, reactionPos);
            if (reactionState != null) {
                level.setBlock(reactionPos, reactionState, Block.UPDATE_ALL);
            }
        }
    }

    protected @Nullable BlockState getReaction(final Level level, final BlockPos pos) {
        final BlockState state = level.getBlockState(pos);
        if (state.getFluidState().is(FluidTags.WATER)) {
            return Blocks.STONE.defaultBlockState();
        }
        if (state.is(BlockTags.LOGS)) {
            return Objects.requireNonNull(HbmBlocks.WASTE_LOG.get().defaultBlockState());
        }
        if (state.is(BlockTags.PLANKS)) {
            return Objects.requireNonNull(HbmBlocks.WASTE_PLANKS.get().defaultBlockState());
        }
        if (state.is(BlockTags.LEAVES)) {
            return BaseFireBlock.getState(level, pos);
        }
        if (state.is(Blocks.DIAMOND_ORE)) {
            return Objects.requireNonNull(HbmBlocks.ORE_BASALT_GEM.get().defaultBlockState());
        }
        return null;
    }

    protected void onSolidify(final ServerLevel level, final BlockPos pos, final int lavaCount, final int basaltCount, final RandomSource random) {
        final int result = random.nextInt(200);
        final BlockState aboveState = level.getBlockState(pos.above(10));
        final boolean canMakeGem = lavaCount + basaltCount == 6 && lavaCount < 3
            && (aboveState.is(getSolidificationBaseBlock()) || isTrackedFluid(aboveState.getFluidState()));

        if (result < 2) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.ORE_BASALT_SULFUR.get().defaultBlockState()), Block.UPDATE_ALL);
        } else if (result == 2) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.ORE_BASALT_FLUORITE.get().defaultBlockState()), Block.UPDATE_ALL);
        } else if (result == 3) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.ORE_BASALT_ASBESTOS.get().defaultBlockState()), Block.UPDATE_ALL);
        } else if (result == 4) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.ORE_BASALT_MOLYSITE.get().defaultBlockState()), Block.UPDATE_ALL);
        } else if (result < 15 && canMakeGem) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.ORE_BASALT_GEM.get().defaultBlockState()), Block.UPDATE_ALL);
        } else {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.BASALT.get().defaultBlockState()), Block.UPDATE_ALL);
        }
    }

    protected @NotNull Block getSolidificationBaseBlock() {
        return Objects.requireNonNull(HbmBlocks.BASALT.get());
    }

    protected boolean isTrackedFluid(final FluidState state) {
        return state.getType().isSame(HbmFluids.VOLCANIC_LAVA.getStillFluid())
            || state.getType().isSame(HbmFluids.VOLCANIC_LAVA.getFlowingFluid());
    }
}
