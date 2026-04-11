package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;

public interface IFluidProviderMK2 extends IFluidUserMK2 {

    void useUpFluid(FluidType type, int pressure, long amount);

    default long getProviderSpeed(final FluidType type, final int pressure) {
        return 1_000_000_000L;
    }

    long getFluidAvailable(FluidType type, int pressure);

    default int[] getProvidingPressureRange(final FluidType type) {
        return DEFAULT_PRESSURE_RANGE;
    }
}
