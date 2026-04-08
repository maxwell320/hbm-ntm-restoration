package com.hbm.ntm.common.multiblock;

import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class MultiblockCoreBE extends MachineBlockEntity {
    private Direction direction = Direction.NORTH;
    private BlockPos corePos;

    protected MultiblockCoreBE(BlockEntityType<?> type, BlockPos pos, BlockState state, int slotCount) {
        super(type, pos, state, slotCount);
        this.corePos = pos;
    }

    public @NotNull Direction getDirection() {
        return this.direction;
    }

    public void setDirection(@NotNull Direction direction) {
        this.direction = direction;
        this.setChanged();
        this.syncToClient();
    }

    public @NotNull BlockPos getCorePos() {
        return this.corePos;
    }

    public void setCorePos(@NotNull BlockPos corePos) {
        this.corePos = corePos.immutable();
        this.setChanged();
        this.syncToClient();
    }

    @Override
    protected void saveMachineData(@NotNull CompoundTag tag) {
        super.saveMachineData(tag);
        tag.putInt("direction", this.direction.ordinal());
        tag.putInt("coreX", this.corePos.getX());
        tag.putInt("coreY", this.corePos.getY());
        tag.putInt("coreZ", this.corePos.getZ());
    }

    @Override
    protected void loadMachineData(@NotNull CompoundTag tag) {
        super.loadMachineData(tag);
        this.direction = Direction.from3DDataValue(tag.getInt("direction"));
        this.corePos = new BlockPos(tag.getInt("coreX"), tag.getInt("coreY"), tag.getInt("coreZ"));
    }

    public abstract MultiblockStructure getStructure();

    public void validateStructure() {
        if (this.level != null && !this.level.isClientSide()) {
            if (!this.getStructure().canForm(this.level, this.corePos, this.direction)) {
                this.getStructure().breakStructure(this.level, this.corePos, this.direction);
            }
        }
    }
}
