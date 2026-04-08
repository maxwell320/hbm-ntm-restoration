package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.item.SteelBarrelBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class SteelBarrelBlockEntity extends FluidTankBlockEntity {
    public static final int CAPACITY = 16_000;

    public SteelBarrelBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.BARREL.get(), pos, state, CAPACITY, SteelBarrelBlockEntity::isValidBarrelFluid);
    }

    private static boolean isValidBarrelFluid(final FluidStack stack) {
        if (stack.isEmpty()) {
            return true;
        }
        final FluidType fluidType = stack.getFluid().getFluidType();
        if (fluidType instanceof final com.hbm.ntm.common.fluid.HbmFluidType hbmFluidType) {
            return !hbmFluidType.isAntimatter() && hbmFluidType.getCorrosiveRating() <= 50;
        }
        return true;
    }

    public void loadFromItem(final ItemStack stack) {
        final FluidStack storedFluid = SteelBarrelBlockItem.getStoredFluid(stack, CAPACITY);
        this.tank.setFluidStack(storedFluid);
        this.setChanged();
    }

    public @NotNull FluidStack getStoredFluid() {
        return this.getFluid();
    }
}
