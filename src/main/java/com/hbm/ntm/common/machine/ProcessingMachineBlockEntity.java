package com.hbm.ntm.common.machine;

import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import com.hbm.ntm.common.menu.MachineDataSlots;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public abstract class ProcessingMachineBlockEntity<R> extends MachineBlockEntity {
    private int processProgress;
    private int processDuration;
    private boolean processingActive;

    protected ProcessingMachineBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state, final int slotCount) {
        super(type, pos, state, slotCount);
    }

    protected final boolean tickProcessing() {
        final R recipe = this.findActiveRecipe();
        final boolean canProcess = recipe != null && this.canProcessRecipe(recipe);
        final boolean wasActive = this.processingActive;

        if (!canProcess) {
            this.processingActive = false;
            if (this.resetProgressWhenBlocked()) {
                this.processProgress = 0;
                this.processDuration = 0;
            }
            if (wasActive != this.processingActive) {
                this.markChangedAndSync();
                return true;
            }
            return false;
        }

        this.processingActive = true;
        this.processDuration = Math.max(1, this.getProcessDuration(recipe));
        this.onProcessTick(recipe);
        this.processProgress = Math.min(this.processDuration, this.processProgress + Math.max(1, this.getProcessStep(recipe)));

        if (this.processProgress >= this.processDuration) {
            this.finishRecipe(recipe);
            this.processProgress = 0;
            this.processDuration = 0;
            this.onProcessFinished(recipe);
        }

        if (wasActive != this.processingActive) {
            this.onProcessingStateChanged(wasActive, this.processingActive);
        }
        this.markChangedAndSync();
        return true;
    }

    public int getProcessProgress() {
        return this.processProgress;
    }

    public int getProcessDurationValue() {
        return this.processDuration;
    }

    public boolean isProcessingActive() {
        return this.processingActive;
    }

    public void setClientProcessProgress(final int processProgress) {
        this.processProgress = Math.max(0, processProgress);
    }

    public void setClientProcessDuration(final int processDuration) {
        this.processDuration = Math.max(0, processDuration);
    }

    public void setClientProcessingActive(final int active) {
        this.processingActive = active != 0;
    }

    protected MachineDataSlots createProcessingDataSlots() {
        return new MachineDataSlots(
            List.of(this::getProcessProgress, this::getProcessDurationValue, () -> this.isProcessingActive() ? 1 : 0),
            List.of(this::setClientProcessProgress, this::setClientProcessDuration, this::setClientProcessingActive)
        );
    }

    @Override
    protected void saveMachineData(final CompoundTag tag) {
        super.saveMachineData(tag);
        tag.putInt("processProgress", this.processProgress);
        tag.putInt("processDuration", this.processDuration);
        tag.putBoolean("processingActive", this.processingActive);
    }

    @Override
    protected void loadMachineData(final CompoundTag tag) {
        super.loadMachineData(tag);
        this.processProgress = tag.getInt("processProgress");
        this.processDuration = tag.getInt("processDuration");
        this.processingActive = tag.getBoolean("processingActive");
    }

    protected int getProcessStep(final R recipe) {
        return 1;
    }

    protected boolean resetProgressWhenBlocked() {
        return true;
    }

    protected void onProcessTick(final R recipe) {
    }

    protected void onProcessFinished(final R recipe) {
    }

    protected void onProcessingStateChanged(final boolean oldState, final boolean newState) {
    }

    protected abstract @Nullable R findActiveRecipe();

    protected abstract boolean canProcessRecipe(R recipe);

    protected abstract int getProcessDuration(R recipe);

    protected abstract void finishRecipe(R recipe);
}
