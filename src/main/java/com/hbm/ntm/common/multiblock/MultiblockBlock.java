package com.hbm.ntm.common.multiblock;

import api.hbm.block.IToolable;
import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
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
public abstract class MultiblockBlock extends BaseEntityBlock implements IToolable {
    private static boolean removingStructure;

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
        if (coreBE instanceof final MachineBlockEntity machine && machine.tryRepairInteraction(player, player.getItemInHand(hand))) {
            return InteractionResult.CONSUME;
        }
        if (!(coreBE instanceof MenuProvider menuProvider) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        NetworkHooks.openScreen(serverPlayer, menuProvider, corePos);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onScrew(final Level level, final Player player, final BlockPos pos, final Direction side,
                           final BlockHitResult hitResult, final ToolType tool) {
        if (tool != ToolType.TORCH) {
            return false;
        }
        final BlockPos corePos = this.findCore(level, pos);
        if (corePos == null) {
            return false;
        }
        final BlockEntity coreBE = level.getBlockEntity(corePos);
        if (!(coreBE instanceof final MachineBlockEntity machine)) {
            return false;
        }
        return machine.tryRepairFromPlayer(player);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !removingStructure) {
            BlockPos corePos = findCore(level, pos);
            if (corePos != null) {
                BlockEntity coreBE = level.getBlockEntity(corePos);
                if (coreBE instanceof MachineBlockEntity machine) {
                    machine.dropContents();
                }
                final Direction direction = getDirection(level, corePos);
                removingStructure = true;
                for (final BlockPos structurePos : getStructure().getPositions(corePos, direction)) {
                    if (structurePos.equals(pos)) {
                        continue;
                    }
                    if (level.getBlockState(structurePos).is(state.getBlock())) {
                        level.removeBlock(structurePos, false);
                    }
                }
                removingStructure = false;
                if (!corePos.equals(pos)) {
                    popResource(level, corePos, new ItemStack(state.getBlock()));
                }
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
        if (!getStructure().isFormed(level, corePos, direction)) {
            if (level.getBlockState(corePos).is(this)) {
                level.destroyBlock(corePos, true);
            } else {
                level.removeBlock(pos, false);
            }
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
