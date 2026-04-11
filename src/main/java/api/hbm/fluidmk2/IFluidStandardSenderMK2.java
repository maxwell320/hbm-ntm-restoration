package api.hbm.fluidmk2;

import api.hbm.tile.ILoadedTile.TileAccessCache;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.tank.FluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFluidStandardSenderMK2 extends IFluidProviderMK2 {

    default void tryProvide(final FluidTank tank, final Level level, final BlockPos pos, final Direction dir) {
        this.tryProvide(tank.getTankType(), tank.getPressure(), level, pos, dir);
    }

    default void tryProvide(final FluidType type, final int pressure, final Level level, final BlockPos pos, final Direction dir) {
        final BlockEntity blockEntity = TileAccessCache.getTileOrCache(level, pos);
        if (blockEntity instanceof IFluidConnectorMK2 connector && connector.canConnect(type, dir.getOpposite())) {
            final FluidNode node = FluidNetMK2.getNode(level, pos, type);
            if (node != null && node.net() != null) {
                node.net().addProvider(this);
            }
        }
        if (blockEntity instanceof IFluidReceiverMK2 receiver && blockEntity != this && receiver.canConnect(type, dir.getOpposite())) {
            final long provided = Math.min(this.getFluidAvailable(type, pressure), this.getProviderSpeed(type, pressure));
            final long requested = Math.min(receiver.getDemand(type, pressure), receiver.getReceiverSpeed(type, pressure));
            long toTransfer = Math.min(provided, requested);
            toTransfer -= receiver.transferFluid(type, pressure, toTransfer);
            this.useUpFluid(type, pressure, toTransfer);
        }
    }

    FluidTank[] getSendingTanks();

    @Override
    default long getFluidAvailable(final FluidType type, final int pressure) {
        long amount = 0L;
        for (final FluidTank tank : this.getSendingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure) {
                amount += tank.getFill();
            }
        }
        return amount;
    }

    @Override
    default void useUpFluid(final FluidType type, final int pressure, long amount) {
        for (final FluidTank tank : this.getSendingTanks()) {
            if (tank.getTankType() == type && tank.getPressure() == pressure && amount > 0L) {
                final int toRemove = (int) Math.min(amount, tank.getFill());
                tank.setFill(tank.getFill() - toRemove);
                amount -= toRemove;
            }
        }
    }

    @Override
    default int[] getProvidingPressureRange(final FluidType type) {
        int lowest = HIGHEST_VALID_PRESSURE;
        int highest = 0;
        for (final FluidTank tank : this.getSendingTanks()) {
            if (tank.getTankType() == type) {
                lowest = Math.min(lowest, tank.getPressure());
                highest = Math.max(highest, tank.getPressure());
            }
        }
        return lowest <= highest ? new int[] {lowest, highest} : DEFAULT_PRESSURE_RANGE;
    }
}
