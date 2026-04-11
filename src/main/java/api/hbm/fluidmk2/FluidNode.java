package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class FluidNode {
    private final FluidType type;
    private final BlockPos pos;
    private final FluidNetMK2 net;
    private Direction[] connections = Direction.values();

    public FluidNode(final FluidType type, final BlockPos pos) {
        this.type = type;
        this.pos = pos.immutable();
        this.net = new FluidNetMK2(type);
    }

    public FluidNode setConnections(final Direction... connections) {
        this.connections = connections.clone();
        return this;
    }

    public FluidType type() {
        return this.type;
    }

    public BlockPos pos() {
        return this.pos;
    }

    public FluidNetMK2 net() {
        return this.net;
    }

    public Direction[] connections() {
        return this.connections.clone();
    }
}
