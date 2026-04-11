package api.hbm.energymk2;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class Nodespace {
    private static final Map<Key, PowerNode> NODES = new HashMap<>();

    private Nodespace() {
    }

    public static PowerNode getNode(final Level level, final BlockPos pos) {
        return NODES.get(new Key(level.dimension().location(), pos));
    }

    public static void createNode(final Level level, final PowerNode node) {
        NODES.put(new Key(level.dimension().location(), node.pos()), node);
    }

    public static void destroyNode(final Level level, final BlockPos pos) {
        NODES.remove(new Key(level.dimension().location(), pos));
    }

    private record Key(ResourceLocation dimension, BlockPos pos) {
    }

    public static final class PowerNode {
        private final BlockPos pos;
        private final PowerNetMK2 net;
        private Direction[] connections = Direction.values();

        public PowerNode(final BlockPos pos) {
            this.pos = pos.immutable();
            this.net = new PowerNetMK2();
        }

        public PowerNode setConnections(final Direction... connections) {
            this.connections = connections.clone();
            return this;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public PowerNetMK2 net() {
            return this.net;
        }

        public Direction[] connections() {
            return this.connections.clone();
        }
    }
}
