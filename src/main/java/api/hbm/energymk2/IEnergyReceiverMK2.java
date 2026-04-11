package api.hbm.energymk2;

import api.hbm.tile.ILoadedTile.TileAccessCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyReceiverMK2 extends IEnergyHandlerMK2 {

    default long transferPower(final long power) {
        if (power + this.getPower() <= this.getMaxPower()) {
            this.setPower(power + this.getPower());
            return 0L;
        }
        final long capacity = this.getMaxPower() - this.getPower();
        final long overshoot = power - capacity;
        this.setPower(this.getMaxPower());
        return overshoot;
    }

    default long getReceiverSpeed() {
        return this.getMaxPower();
    }

    default boolean allowDirectProvision() {
        return true;
    }

    default void trySubscribe(final Level level, final BlockPos pos, final Direction dir) {
        final BlockEntity blockEntity = TileAccessCache.getTileOrCache(level, pos);
        if (blockEntity instanceof IEnergyConductorMK2 conductor && conductor.canConnect(dir.getOpposite())) {
            final Nodespace.PowerNode node = Nodespace.getNode(level, pos);
            if (node != null && node.net() != null) {
                node.net().addReceiver(this);
            }
        }
    }

    default void tryUnsubscribe(final Level level, final BlockPos pos) {
        final Nodespace.PowerNode node = Nodespace.getNode(level, pos);
        if (node != null && node.net() != null) {
            node.net().removeReceiver(this);
        }
    }

    enum ConnectionPriority {
        LOWEST,
        LOW,
        NORMAL,
        HIGH,
        HIGHEST
    }

    default ConnectionPriority getPriority() {
        return ConnectionPriority.NORMAL;
    }
}
