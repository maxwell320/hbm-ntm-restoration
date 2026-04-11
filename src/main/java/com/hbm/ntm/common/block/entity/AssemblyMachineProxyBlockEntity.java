package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.assembly.AssemblyMachineStructure;
import com.hbm.ntm.common.multiblock.MultiblockProxyBE;
import com.hbm.ntm.common.multiblock.MultiblockStructure;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class AssemblyMachineProxyBlockEntity extends MultiblockProxyBE {
    public AssemblyMachineProxyBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.MACHINE_ASSEMBLY_MACHINE_PROXY.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> capability, final @Nullable Direction side) {
        final BlockPos corePos = this.getCorePos();
        if (this.level != null && corePos != null) {
            final BlockEntity core = this.level.getBlockEntity(corePos);
            if (core != null && core != this) {
                return core.getCapability(capability, side);
            }
        }
        return super.getCapability(capability, side);
    }

    @Override
    public MultiblockStructure getStructure() {
        return AssemblyMachineStructure.INSTANCE;
    }
}
