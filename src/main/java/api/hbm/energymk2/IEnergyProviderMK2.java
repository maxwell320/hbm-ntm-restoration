package api.hbm.energymk2;

import api.hbm.tile.ILoadedTile.TileAccessCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyProviderMK2 extends IEnergyHandlerMK2 {

    default void usePower(final long power) {
        this.setPower(this.getPower() - power);
    }

    default long getProviderSpeed() {
        return this.getMaxPower();
    }

    default void tryProvide(final Level level, final BlockPos pos, final Direction dir) {
        final BlockEntity blockEntity = TileAccessCache.getTileOrCache(level, pos);
        if (blockEntity instanceof IEnergyConductorMK2 conductor && conductor.canConnect(dir.getOpposite())) {
            final Nodespace.PowerNode node = Nodespace.getNode(level, pos);
            if (node != null && node.net() != null) {
                node.net().addProvider(this);
            }
        }
        if (blockEntity instanceof IEnergyReceiverMK2 receiver && blockEntity != this && receiver.canConnect(dir.getOpposite()) && receiver.allowDirectProvision()) {
            final long provided = Math.min(this.getPower(), this.getProviderSpeed());
            final long requested = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
            long toTransfer = Math.min(provided, requested);
            toTransfer -= receiver.transferPower(toTransfer);
            this.usePower(toTransfer);
        }
    }
}
