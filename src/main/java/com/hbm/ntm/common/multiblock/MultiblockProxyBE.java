package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class MultiblockProxyBE extends MultiblockCoreBE {
    private BlockPos corePos = BlockPos.ZERO;

    protected MultiblockProxyBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state, 0);
    }

    public @Nullable BlockPos getCorePos() {
        if (this.corePos.equals(BlockPos.ZERO)) {
            this.corePos = findCore();
        }
        return this.corePos.equals(BlockPos.ZERO) ? null : this.corePos;
    }

    public void setCorePos(@NotNull BlockPos corePos) {
        this.corePos = corePos.immutable();
        this.setChanged();
        this.syncToClient();
    }

    private @NotNull BlockPos findCore() {
        if (this.level == null) {
            return BlockPos.ZERO;
        }

        for (Direction direction : Direction.values()) {
            BlockPos checkPos = this.worldPosition.relative(direction);
            if (checkPos.equals(this.corePos)) {
                continue;
            }
            if (this.level.getBlockEntity(checkPos) instanceof MultiblockCoreBE core) {
                return core.getCorePos();
            }
        }
        return BlockPos.ZERO;
    }

    @Override
    protected void saveMachineData(@NotNull CompoundTag tag) {
        super.saveMachineData(tag);
        tag.putInt("coreX", this.corePos.getX());
        tag.putInt("coreY", this.corePos.getY());
        tag.putInt("coreZ", this.corePos.getZ());
    }

    @Override
    protected void loadMachineData(@NotNull CompoundTag tag) {
        super.loadMachineData(tag);
        this.corePos = new BlockPos(tag.getInt("coreX"), tag.getInt("coreY"), tag.getInt("coreZ"));
    }

    @Override
    public void validateStructure() {
        BlockPos corePos = this.getCorePos();
        if (corePos != null && this.level != null && !this.level.isClientSide()) {
            if (!this.getStructure().canForm(this.level, corePos, this.getDirection())) {
                this.getStructure().breakStructure(this.level, corePos, this.getDirection());
            }
        }
    }
}
