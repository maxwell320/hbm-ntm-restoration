package com.hbm.ntm.common.multiblock;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class MultiblockBlock extends BaseEntityBlock {
    protected MultiblockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos corePos = findCore(level, pos);
        if (corePos == null) {
            return InteractionResult.PASS;
        }

        BlockEntity coreBE = level.getBlockEntity(corePos);
        if (!(coreBE instanceof MenuProvider menuProvider) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        NetworkHooks.openScreen(serverPlayer, menuProvider, corePos);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockPos corePos = findCore(level, pos);
            if (corePos != null) {
                BlockEntity coreBE = level.getBlockEntity(corePos);
                if (coreBE instanceof MachineBlockEntity machine) {
                    machine.dropContents();
                }
                getStructure().breakStructure(level, corePos, getDirection(level, corePos));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                               @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
        if (!level.isClientSide()) {
            validateStructure(level, pos);
        }
    }

    protected void validateStructure(@NotNull Level level, @NotNull BlockPos pos) {
        BlockPos corePos = findCore(level, pos);
        if (corePos == null) {
            level.removeBlock(pos, false);
            return;
        }

        Direction direction = getDirection(level, corePos);
        if (!getStructure().canForm(level, corePos, direction)) {
            getStructure().breakStructure(level, corePos, direction);
        }
    }

    protected @Nullable BlockPos findCore(@NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MultiblockCoreBE core) {
            return core.getCorePos();
        }
        if (be instanceof MultiblockProxyBE proxy) {
            return proxy.getCorePos();
        }
        return null;
    }

    protected @NotNull Direction getDirection(@NotNull Level level, @NotNull BlockPos corePos) {
        BlockEntity be = level.getBlockEntity(corePos);
        if (be instanceof MultiblockCoreBE core) {
            return core.getDirection();
        }
        return Direction.NORTH;
    }

    protected abstract MultiblockStructure getStructure();

    protected abstract BlockEntityType<?> getCoreType();

    protected abstract BlockEntityType<?> getProxyType();
}
