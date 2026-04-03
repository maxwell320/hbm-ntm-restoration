package com.hbm.ntm.common.block;

import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmFluids;
import com.hbm.ntm.common.radiation.RadiationUtil;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RadLavaBlock extends VolcanicLavaBlock {
    public RadLavaBlock(final Supplier<? extends FlowingFluid> fluid, final BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (!level.isClientSide() && entity instanceof final LivingEntity livingEntity) {
            RadiationUtil.contaminate(livingEntity, RadiationUtil.HazardType.RADIATION, RadiationUtil.ContaminationType.CREATIVE, 5.0F);
        }
    }

    @Override
    protected @NotNull Block getSolidificationBaseBlock() {
        return Objects.requireNonNull(HbmBlocks.SELLAFIELD_SLAKED.get());
    }

    @Override
    protected boolean isTrackedFluid(final FluidState state) {
        return state.getType().isSame(HbmFluids.RAD_LAVA.getStillFluid())
            || state.getType().isSame(HbmFluids.RAD_LAVA.getFlowingFluid());
    }

    @Override
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
            return sellafieldState(Objects.requireNonNull(HbmBlocks.ORE_SELLAFIELD_RADGEM.get()), pos, 0);
        }
        return null;
    }

    @Override
    protected void onSolidify(final ServerLevel level, final BlockPos pos, final int lavaCount, final int basaltCount, final RandomSource random) {
        final int result = random.nextInt(400);
        final BlockState aboveState = level.getBlockState(pos.above(10));
        final boolean canMakeGem = lavaCount + basaltCount == 6 && lavaCount < 3
            && (aboveState.is(getSolidificationBaseBlock()) || isTrackedFluid(aboveState.getFluidState()));
        final int shade = 5 + random.nextInt(3);

        if (result < 2) {
            level.setBlock(pos, sellafieldState(Objects.requireNonNull(HbmBlocks.ORE_SELLAFIELD_DIAMOND.get()), pos, shade), Block.UPDATE_ALL);
        } else if (result == 2) {
            level.setBlock(pos, sellafieldState(Objects.requireNonNull(HbmBlocks.ORE_SELLAFIELD_EMERALD.get()), pos, shade), Block.UPDATE_ALL);
        } else if (result < 20 && canMakeGem) {
            level.setBlock(pos, sellafieldState(Objects.requireNonNull(HbmBlocks.ORE_SELLAFIELD_RADGEM.get()), pos, shade), Block.UPDATE_ALL);
        } else {
            level.setBlock(pos, sellafieldState(Objects.requireNonNull(HbmBlocks.SELLAFIELD_SLAKED.get()), pos, shade), Block.UPDATE_ALL);
        }
    }

    private BlockState sellafieldState(final Block block, final BlockPos pos, final int shade) {
        return ((SellafieldSlakedBlock) block).stateFor(pos, shade);
    }
}
