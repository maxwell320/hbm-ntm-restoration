package com.hbm.ntm.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

public class SellafieldSlakedBlock extends Block {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 3);
    public static final IntegerProperty SHADE = IntegerProperty.create("shade", 0, 15);

    public SellafieldSlakedBlock(final BlockBehaviour.Properties properties) {
        super(properties.sound(SoundType.STONE));
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0).setValue(SHADE, 0));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, SHADE);
    }

    @Override
    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return stateFor(context.getClickedPos(), 0);
    }

    public BlockState stateFor(final BlockPos pos, final int shade) {
        return defaultBlockState().setValue(VARIANT, variantFor(pos)).setValue(SHADE, Mth.clamp(shade, 0, 15));
    }

    public static int variantFor(final BlockPos pos) {
        long hash = (long) pos.getX() * 3129871L ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
        hash = hash * hash * 42317861L + hash * 11L;
        return (int) (Math.abs((hash >> 16) & 3L) % 4L);
    }

    public static int colorFromState(final @NotNull BlockState state) {
        final int shade = state.getValue(SHADE);
        return Mth.hsvToRgb(0.0F, 0.0F, 1.0F - shade / 15.0F);
    }
}
