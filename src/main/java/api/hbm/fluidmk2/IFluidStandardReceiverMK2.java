package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTank;

public interface IFluidStandardReceiverMK2 extends IFluidReceiverMK2 {

    FluidTank[] getReceivingTanks();

    @Override
    default long getDemand(final FluidType type, final int pressure) {
        long amount = 0L;
        for (final FluidTank tank : this.getReceivingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                amount += tank.getMaxFill() - tank.getFill();
            }
        }
        return amount;
    }

    @Override
    default long transferFluid(final FluidType type, final int pressure, long amount) {
        for (final FluidTank tank : this.getReceivingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure && amount > 0L) {
                final int toAdd = (int) Math.min(amount, tank.getMaxFill() - tank.getFill());
                tank.setFill(tank.getFill() + toAdd);
                amount -= toAdd;
            }
        }
        return amount;
    }

    @Override
    default int[] getReceivingPressureRange(final FluidType type) {
        int lowest = HIGHEST_VALID_PRESSURE;
        int highest = 0;
        for (final FluidTank tank : this.getReceivingTanks()) {
            if (tank.getTankType() == type) {
                lowest = Math.min(lowest, tank.getPressure());
                highest = Math.max(highest, tank.getPressure());
            }
        }
        return lowest <= highest ? new int[] {lowest, highest} : DEFAULT_PRESSURE_RANGE;
    }
}
