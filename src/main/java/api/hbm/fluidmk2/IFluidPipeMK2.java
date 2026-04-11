package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFluidPipeMK2 extends IFluidConnectorMK2 {

    default FluidNode createNode(final FluidType type) {
        final BlockEntity blockEntity = (BlockEntity) this;
        final BlockPos pos = blockEntity.getBlockPos();
        return new FluidNode(type, pos).setConnections(Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH);
    }
}
