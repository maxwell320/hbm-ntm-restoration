package api.hbm.fluidmk2;

import api.hbm.energymk2.IEnergyReceiverMK2.ConnectionPriority;
import api.hbm.tile.ILoadedTile.TileAccessCache;
import com.hbm.inventory.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFluidReceiverMK2 extends IFluidUserMK2 {

    long transferFluid(FluidType type, int pressure, long amount);

    default long getReceiverSpeed(final FluidType type, final int pressure) {
        return 1_000_000_000L;
    }

    long getDemand(FluidType type, int pressure);

    default int[] getReceivingPressureRange(final FluidType type) {
        return DEFAULT_PRESSURE_RANGE;
    }

    default void trySubscribe(final FluidType type, final Level level, final BlockPos pos, final Direction dir) {
        final BlockEntity blockEntity = TileAccessCache.getTileOrCache(level, pos);
        if (blockEntity instanceof IFluidConnectorMK2 connector && connector.canConnect(type, dir.getOpposite())) {
            final FluidNode node = FluidNetMK2.getNode(level, pos, type);
            if (node != null && node.net() != null) {
                node.net().addReceiver(this);
            }
        }
    }

    default ConnectionPriority getFluidPriority() {
        return ConnectionPriority.NORMAL;
    }
}
