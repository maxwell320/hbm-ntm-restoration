package com.hbm.ntm.common.block;

import com.hbm.ntm.common.block.entity.BarrelBlockEntity;
import com.hbm.ntm.common.item.BarrelBlockItem;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BarrelBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private final BarrelType barrelType;

    public BarrelBlock(final BarrelType barrelType, final Properties properties) {
        super(properties);
        this.barrelType = barrelType;
    }

    public BarrelType barrelType() {
        return this.barrelType;
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos, final @NotNull CollisionContext context) {
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
        return new BarrelBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level, final @NotNull BlockState state, final @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, com.hbm.ntm.common.registration.HbmBlockEntityTypes.BARREL.get(), BarrelBlockEntity::serverTick);
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos,
                                          final @NotNull Player player, final @NotNull InteractionHand hand, final @NotNull BlockHitResult hit) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof final BarrelBlockEntity barrel)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown() && player.getItemInHand(hand).getItem() instanceof final com.hbm.ntm.common.item.IItemFluidIdentifier identifier) {
            final net.minecraft.resources.ResourceLocation fluidId = identifier.getFluidId(player.getItemInHand(hand));
            if (!level.isClientSide()) {
                barrel.setConfiguredFluidId(fluidId, true);
                player.displayClientMessage(Component.literal("Changed type to " + barrel.getConfiguredFluidDisplayName() + "!"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!level.isClientSide() && player instanceof final ServerPlayer serverPlayer) {
            final MenuProvider menuProvider = barrel;
            NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public boolean hasAnalogOutputSignal(final @NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos) {
        return level.getBlockEntity(pos) instanceof final BarrelBlockEntity barrel ? barrel.getComparatorOutput() : 0;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof final BarrelBlockEntity barrel) {
            final ItemStack drop = BarrelBlockItem.withStoredState(this.asItem(), barrel.getFluid(), barrel.capacity(), barrel.getConfiguredFluidId());
            return Collections.singletonList(drop);
        }
        return Collections.singletonList(new ItemStack(this.asItem()));
    }

    @Override
    public void setPlacedBy(final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState state,
                            @Nullable final LivingEntity placer, final @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof final BarrelBlockEntity barrel) {
            barrel.loadFromItem(stack);
        }
    }

    @Override
    public void onRemove(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState newState, final boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof final BarrelBlockEntity barrel) {
            barrel.dropContents();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
