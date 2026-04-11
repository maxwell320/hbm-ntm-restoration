package com.hbm.ntm.common.block;

import com.hbm.ntm.common.assembly.AssemblyMachinePart;
import com.hbm.ntm.common.assembly.AssemblyMachineStructure;
import com.hbm.ntm.common.block.entity.AssemblyMachineBlockEntity;
import com.hbm.ntm.common.block.entity.AssemblyMachineProxyBlockEntity;
import com.hbm.ntm.common.multiblock.MultiblockBlock;
import com.hbm.ntm.common.multiblock.MultiblockStructure;
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
public class AssemblyMachineBlock extends MultiblockBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<AssemblyMachinePart> PART = EnumProperty.create("part", AssemblyMachinePart.class);

    public AssemblyMachineBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, AssemblyMachinePart.CORE));
    }

    @Override
    public PushReaction getPistonPushReaction(final BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final Direction facing = AssemblyMachineStructure.normalizeFacing(context.getHorizontalDirection().getOpposite());
        if (!AssemblyMachineStructure.INSTANCE.canPlaceAt(level, pos, facing)) {
            return null;
        }
        return this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(PART, AssemblyMachinePart.PROXY);
    }

    @Override
    public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity placer, final ItemStack stack) {
        if (level.isClientSide()) {
            return;
        }
        final Direction facing = state.getValue(FACING);
        final BlockPos corePos = AssemblyMachineStructure.INSTANCE.getCorePosition(pos, facing);
        level.setBlockAndUpdate(corePos, this.defaultBlockState()
            .setValue(FACING, facing)
            .setValue(PART, AssemblyMachinePart.CORE));
        if (level.getBlockEntity(corePos) instanceof final AssemblyMachineBlockEntity core) {
            core.setDirection(facing);
            core.setCorePos(corePos);
            core.setChanged();
            core.syncToClient();
        }
        AssemblyMachineStructure.INSTANCE.form(level, corePos, facing);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return state.getValue(PART) == AssemblyMachinePart.CORE ? new AssemblyMachineBlockEntity(pos, state) : new AssemblyMachineProxyBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState state, final BlockEntityType<T> type) {
        if (level.isClientSide() || state.getValue(PART) != AssemblyMachinePart.CORE) {
            return null;
        }
        return createTickerHelper(type, HbmBlockEntityTypes.MACHINE_ASSEMBLY_MACHINE.get(), AssemblyMachineBlockEntity::serverTick);
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
        return state.getValue(PART) == AssemblyMachinePart.CORE;
    }

    @Override
    public int getAnalogOutputSignal(final BlockState state, final Level level, final BlockPos pos) {
        final BlockPos corePos = this.findCore(level, pos);
        if (corePos != null && level.getBlockEntity(corePos) instanceof final AssemblyMachineBlockEntity machine) {
            return machine.getComparatorOutput();
        }
        return 0;
    }

    @Override
    protected MultiblockStructure getStructure() {
        return AssemblyMachineStructure.INSTANCE;
    }

    @Override
    protected BlockEntityType<?> getCoreType() {
        return HbmBlockEntityTypes.MACHINE_ASSEMBLY_MACHINE.get();
    }

    @Override
    protected BlockEntityType<?> getProxyType() {
        return HbmBlockEntityTypes.MACHINE_ASSEMBLY_MACHINE_PROXY.get();
    }
}
