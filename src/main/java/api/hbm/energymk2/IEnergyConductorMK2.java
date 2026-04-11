package api.hbm.energymk2;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyConductorMK2 extends IEnergyConnectorMK2 {

    default Nodespace.PowerNode createNode() {
        final BlockEntity blockEntity = (BlockEntity) this;
        final BlockPos pos = blockEntity.getBlockPos();
        return new Nodespace.PowerNode(pos).setConnections(Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH);
    }
}
