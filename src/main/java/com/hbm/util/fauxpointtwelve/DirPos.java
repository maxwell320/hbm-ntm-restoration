package com.hbm.util.fauxpointtwelve;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DirPos extends BlockPos {
    protected Direction dir;

    public DirPos(final int x, final int y, final int z, final Direction dir) {
        super(x, y, z);
        this.dir = dir;
    }

    public DirPos(final BlockEntity blockEntity, final Direction dir) {
        super(blockEntity);
        this.dir = dir;
    }

    public DirPos(final double x, final double y, final double z, final Direction dir) {
        super(x, y, z);
        this.dir = dir;
    }

    @Override
    public DirPos rotate(final Rotation rotationIn) {
        return switch (rotationIn) {
            case NONE -> this;
            case CLOCKWISE_90 -> new DirPos(-this.getZ(), this.getY(), this.getX(), rotateClockwise(this.getDir()));
            case CLOCKWISE_180 -> new DirPos(-this.getX(), this.getY(), -this.getZ(), this.getDir().getOpposite());
            case COUNTERCLOCKWISE_90 -> new DirPos(this.getZ(), this.getY(), -this.getX(), rotateCounterClockwise(this.getDir()));
        };
    }

    public Direction getDir() {
        return this.dir;
    }

    private static Direction rotateClockwise(final Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            case WEST -> Direction.NORTH;
            default -> dir;
        };
    }

    private static Direction rotateCounterClockwise(final Direction dir) {
        return switch (dir) {
            case NORTH -> Direction.WEST;
            case WEST -> Direction.SOUTH;
            case SOUTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            default -> dir;
        };
    }
}
