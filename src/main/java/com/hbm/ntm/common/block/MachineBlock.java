package com.hbm.ntm.common.block;

import api.hbm.block.IToolable;
import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public abstract class MachineBlock extends BaseEntityBlock implements IToolable {
    protected MachineBlock(final Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(final @NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos,
                                          final @NotNull Player player, final @NotNull InteractionHand hand, final @NotNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof final MachineBlockEntity machine && machine.tryRepairInteraction(player, player.getItemInHand(hand))) {
            return InteractionResult.CONSUME;
        }
        if (!(blockEntity instanceof final MenuProvider menuProvider) || !(player instanceof final ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }
        NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onScrew(final Level level, final Player player, final BlockPos pos, final Direction side,
                           final BlockHitResult hitResult, final ToolType tool) {
        if (tool != ToolType.TORCH) {
            return false;
        }
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof final MachineBlockEntity machine)) {
            return false;
        }
        return machine.tryRepairFromPlayer(player);
    }

    @Override
    public void onRemove(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos,
                         final @NotNull BlockState newState, final boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            final BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof final MachineBlockEntity machine) {
                machine.dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public boolean hasAnalogOutputSignal(final @NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos) {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof final MachineBlockEntity machine ? machine.getComparatorOutput() : 0;
    }
}
