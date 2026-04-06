package com.hbm.ntm.common.block;

import com.hbm.ntm.common.block.entity.CableBlockEntity;
import com.hbm.ntm.common.energy.EnergyConnectionHelper;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class CableBlock extends BaseEntityBlock {
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    private static final VoxelShape CORE = box(5.5D, 5.5D, 5.5D, 10.5D, 10.5D, 10.5D);
    private static final VoxelShape NORTH_SHAPE = box(5.5D, 5.5D, 0.0D, 10.5D, 10.5D, 5.5D);
    private static final VoxelShape SOUTH_SHAPE = box(5.5D, 5.5D, 10.5D, 10.5D, 10.5D, 16.0D);
    private static final VoxelShape EAST_SHAPE = box(10.5D, 5.5D, 5.5D, 16.0D, 10.5D, 10.5D);
    private static final VoxelShape WEST_SHAPE = box(0.0D, 5.5D, 5.5D, 5.5D, 10.5D, 10.5D);
    private static final VoxelShape UP_SHAPE = box(5.5D, 10.5D, 5.5D, 10.5D, 16.0D, 10.5D);
    private static final VoxelShape DOWN_SHAPE = box(5.5D, 0.0D, 5.5D, 10.5D, 5.5D, 10.5D);

    private final int bufferCapacity;
    private final int transferPerTick;

    public CableBlock(final int bufferCapacity, final int transferPerTick, final Properties properties) {
        super(properties);
        this.bufferCapacity = bufferCapacity;
        this.transferPerTick = transferPerTick;
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(EAST, false)
            .setValue(WEST, false)
            .setValue(UP, false)
            .setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final @NotNull BlockPos pos, final @NotNull BlockState state) {
        return new CableBlockEntity(pos, state, this.bufferCapacity, this.transferPerTick);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final @NotNull Level level, final @NotNull BlockState state,
                                                                  final @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, HbmBlockEntityTypes.RED_CABLE.get(), CableBlockEntity::serverTick);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.updateConnections(this.defaultBlockState(), context.getLevel(), context.getClickedPos());
    }

    @Override
    public @NotNull BlockState updateShape(final @NotNull BlockState state, final @NotNull Direction direction, final @NotNull BlockState neighborState,
                                           final @NotNull LevelAccessor level, final @NotNull BlockPos pos, final @NotNull BlockPos neighborPos) {
        return state.setValue(property(direction), canConnect(level, pos, direction));
    }

    @Override
    public @NotNull VoxelShape getShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos,
                                        final @NotNull CollisionContext context) {
        return this.shapeForState(state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(final @NotNull BlockState state, final @NotNull BlockGetter level, final @NotNull BlockPos pos,
                                                 final @NotNull CollisionContext context) {
        return this.shapeForState(state);
    }

    private BlockState updateConnections(final BlockState state, final BlockGetter level, final BlockPos pos) {
        return state
            .setValue(NORTH, canConnect(level, pos, Direction.NORTH))
            .setValue(SOUTH, canConnect(level, pos, Direction.SOUTH))
            .setValue(EAST, canConnect(level, pos, Direction.EAST))
            .setValue(WEST, canConnect(level, pos, Direction.WEST))
            .setValue(UP, canConnect(level, pos, Direction.UP))
            .setValue(DOWN, canConnect(level, pos, Direction.DOWN));
    }

    private static boolean canConnect(final BlockGetter level, final BlockPos pos, final Direction direction) {
        return EnergyConnectionHelper.canConnectEnergy(level, pos.relative(direction), direction);
    }

    private static BooleanProperty property(final Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private VoxelShape shapeForState(final BlockState state) {
        VoxelShape shape = CORE;
        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, NORTH_SHAPE);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SOUTH_SHAPE);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, EAST_SHAPE);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, WEST_SHAPE);
        }
        if (state.getValue(UP)) {
            shape = Shapes.or(shape, UP_SHAPE);
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, DOWN_SHAPE);
        }
        return shape;
    }
}
