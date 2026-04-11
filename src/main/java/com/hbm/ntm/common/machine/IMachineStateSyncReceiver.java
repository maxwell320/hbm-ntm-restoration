package com.hbm.ntm.common.machine;

import net.minecraft.nbt.CompoundTag;

public interface IMachineStateSyncReceiver {
    void applyMachineStateSync(CompoundTag data);
}

