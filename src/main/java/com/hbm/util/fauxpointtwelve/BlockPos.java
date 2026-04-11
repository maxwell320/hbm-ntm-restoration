package com.hbm.util.fauxpointtwelve;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;

public class BlockPos implements Cloneable {
    private int x;
    private int y;
    private int z;

    public BlockPos(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos(final BlockEntity blockEntity) {
        this(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ());
    }

    public BlockPos(final double x, final double y, final double z) {
        this(Mth.floor(x), Mth.floor(y), Mth.floor(z));
    }

    public BlockPos mutate(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public boolean compare(final int x, final int y, final int z) {
        return this.x == x && this.y == y && this.z == z;
    }

    public BlockPos add(final int x, final int y, final int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(final double x, final double y, final double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    public BlockPos add(final BlockPos vec) {
        return this.add(vec.getX(), vec.getY(), vec.getZ());
    }

    public BlockPos rotate(final Rotation rotationIn) {
        return switch (rotationIn) {
            case NONE -> this;
            case CLOCKWISE_90 -> new BlockPos(-this.getZ(), this.getY(), this.getX());
            case CLOCKWISE_180 -> new BlockPos(-this.getX(), this.getY(), -this.getZ());
            case COUNTERCLOCKWISE_90 -> new BlockPos(this.getZ(), this.getY(), -this.getX());
        };
    }

    public BlockPos offset(final Direction dir) {
        return this.offset(dir, 1);
    }

    public BlockPos offset(final Direction dir, final int distance) {
        return new BlockPos(this.x + dir.getStepX() * distance, this.y + dir.getStepY() * distance, this.z + dir.getStepZ() * distance);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        return getIdentity(this.getX(), this.getY(), this.getZ());
    }

    public static int getIdentity(final int x, final int y, final int z) {
        return (y + z * 27644437) * 27644437 + x;
    }

    @Override
    public boolean equals(final Object toCompare) {
        if (this == toCompare) {
            return true;
        }
        if (!(toCompare instanceof final BlockPos pos)) {
            return false;
        }
        if (this.getX() != pos.getX()) {
            return false;
        }
        if (this.getY() != pos.getY()) {
            return false;
        }
        return this.getZ() == pos.getZ();
    }

    @Override
    public BlockPos clone() {
        try {
            return (BlockPos) super.clone();
        } catch (final CloneNotSupportedException e) {
            return null;
        }
    }
}
