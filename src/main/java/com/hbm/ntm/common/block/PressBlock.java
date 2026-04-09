package com.hbm.ntm.common.block;

import com.hbm.ntm.common.block.entity.PressBlockEntity;
import com.hbm.ntm.common.block.entity.PressProxyBlockEntity;
import com.hbm.ntm.common.multiblock.MultiblockBlock;
import com.hbm.ntm.common.multiblock.MultiblockStructure;
import com.hbm.ntm.common.press.PressPart;
import com.hbm.ntm.common.press.PressStructure;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class PressBlock extends MultiblockBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<PressPart> PART = EnumProperty.create("part", PressPart.class);

    public PressBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, PressPart.CORE));
    }

    @Override
    public PushReaction getPistonPushReaction(final BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final Direction facing = PressStructure.normalizeFacing(context.getHorizontalDirection().getOpposite());
        if (!PressStructure.INSTANCE.canPlaceAt(level, pos, facing)) {
            return null;
        }
        return this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(PART, PressPart.CORE);
    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity placer, final ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        final Direction facing = state.getValue(FACING);
        if (level.getBlockEntity(pos) instanceof final PressBlockEntity core) {
            core.setDirection(facing);
            core.setCorePos(pos);
            core.setChanged();
            core.syncToClient();
        }
        PressStructure.INSTANCE.form(level, pos, facing);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return state.getValue(PART) == PressPart.CORE ? new PressBlockEntity(pos, state) : new PressProxyBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        if (level.isClientSide() || state.getValue(PART) != PressPart.CORE) {
            return null;
        }
        return createTickerHelper(type, HbmBlockEntityTypes.MACHINE_PRESS.get(), PressBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    @Override
    public @NotNull BlockState rotate(final BlockState state, final Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(final BlockState state, final Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean hasAnalogOutputSignal(final BlockState state) {
        return state.getValue(PART) == PressPart.CORE;
    }

    @Override
    public int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos) {
        final BlockPos corePos = this.findCore(level, pos);
        if (corePos != null && level.getBlockEntity(corePos) instanceof final PressBlockEntity press) {
            return press.getComparatorOutput();
        }
        return 0;
    }

    @Override
    protected MultiblockStructure getStructure() {
        return PressStructure.INSTANCE;
    }

    @Override
    protected BlockEntityType<?> getCoreType() {
        return HbmBlockEntityTypes.MACHINE_PRESS.get();
    }

    @Override
    protected BlockEntityType<?> getProxyType() {
        return HbmBlockEntityTypes.MACHINE_PRESS_PROXY.get();
    }
}
