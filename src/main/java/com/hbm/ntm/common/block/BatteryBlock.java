package com.hbm.ntm.common.block;

import com.hbm.ntm.common.block.entity.BatteryBlockEntity;
import com.hbm.ntm.common.energy.IEnergyConnectorBlock;
import com.hbm.ntm.common.item.BatteryBlockItem;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BatteryBlock extends BaseEntityBlock implements IEnergyConnectorBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BatteryBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(final @NotNull BlockState state, final Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(final @NotNull BlockState state, final Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return new BatteryBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level, final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, HbmBlockEntityTypes.MACHINE_BATTERY.get(), BatteryBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(final @NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof final BatteryBlockEntity batteryBlockEntity) {
            return batteryBlockEntity.getComparatorOutput();
        }
        return 0;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof final BatteryBlockEntity batteryBlockEntity) {
            return java.util.Collections.singletonList(BatteryBlockItem.withStoredEnergy(Objects.requireNonNull(HbmItems.MACHINE_BATTERY.get()), batteryBlockEntity.getStoredEnergy()));
        }
        return java.util.Collections.singletonList(new ItemStack(Objects.requireNonNull(HbmItems.MACHINE_BATTERY.get())));
    }

    @Override
    public boolean canConnectEnergy(final BlockGetter level, final BlockPos pos, @Nullable final Direction side) {
        return true;
    }
}
