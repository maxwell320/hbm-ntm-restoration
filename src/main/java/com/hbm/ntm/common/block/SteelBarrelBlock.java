package com.hbm.ntm.common.block;

import com.hbm.ntm.common.block.entity.SteelBarrelBlockEntity;
import com.hbm.ntm.common.item.SteelBarrelBlockItem;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class SteelBarrelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public SteelBarrelBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos,
                                                 final @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(final @NotNull BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return new SteelBarrelBlockEntity(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos,
                                          final @NotNull Player player, final @NotNull InteractionHand hand, final @NotNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        return FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection()) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    public boolean hasAnalogOutputSignal(final @NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof final SteelBarrelBlockEntity barrelBlockEntity) {
            return barrelBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof final SteelBarrelBlockEntity barrelBlockEntity) {
            return Collections.singletonList(SteelBarrelBlockItem.withStoredFluid(Objects.requireNonNull(HbmItems.BARREL_STEEL.get()), barrelBlockEntity.getFluid(), barrelBlockEntity.getCapacity()));
        }
        return Collections.singletonList(new ItemStack(Objects.requireNonNull(HbmItems.BARREL_STEEL.get())));
    }

    @Override
    public void setPlacedBy(final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState state,
                            @Nullable final LivingEntity placer, final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof final SteelBarrelBlockEntity barrelBlockEntity) {
            barrelBlockEntity.loadFromItem(stack);
        }
    }

    @Override
    public @NotNull FluidState getFluidState(final @NotNull BlockState state) {
        return super.getFluidState(state);
    }
}
