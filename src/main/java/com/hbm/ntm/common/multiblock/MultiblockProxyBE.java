package com.hbm.ntm.common.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class MultiblockProxyBE extends BlockEntity {
    private BlockPos corePos = BlockPos.ZERO;
    private Direction direction = Direction.NORTH;

    protected MultiblockProxyBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
    }

    public @NotNull Direction getDirection() {
        return this.direction;
    }

    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
        this.setChanged();
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
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("coreX", this.corePos.getX());
        tag.putInt("coreY", this.corePos.getY());
        tag.putInt("coreZ", this.corePos.getZ());
        tag.putInt("direction", this.direction.ordinal());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.corePos = new BlockPos(tag.getInt("coreX"), tag.getInt("coreY"), tag.getInt("coreZ"));
        this.direction = Direction.from3DDataValue(tag.getInt("direction"));
    }

    public void validateStructure() {
        BlockPos corePos = this.getCorePos();
        if (corePos != null && this.level != null && !this.level.isClientSide()) {
            if (!this.getStructure().isFormed(this.level, corePos, this.getDirection())) {
                this.getStructure().breakStructure(this.level, corePos, this.getDirection());
            }
        }
    }

    public abstract MultiblockStructure getStructure();
}
