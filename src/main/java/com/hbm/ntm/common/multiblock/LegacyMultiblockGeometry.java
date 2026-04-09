package com.hbm.ntm.common.multiblock;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@SuppressWarnings("null")
public final class LegacyMultiblockGeometry {
    private LegacyMultiblockGeometry() {
    }

    public static int[] rotatedBounds(final int[] bounds, final Direction direction) {
        validate(bounds);
        return switch (direction) {
            case NORTH -> new int[]{bounds[0], bounds[1], bounds[3], bounds[2], bounds[5], bounds[4]};
            case EAST -> new int[]{bounds[0], bounds[1], bounds[5], bounds[4], bounds[2], bounds[3]};
            case WEST -> new int[]{bounds[0], bounds[1], bounds[4], bounds[5], bounds[3], bounds[2]};
            case SOUTH, UP, DOWN -> bounds.clone();
        };
    }

    public static List<BlockPos> positions(final BlockPos corePos, final int[] bounds, final Direction direction) {
        final int[] rotated = rotatedBounds(bounds, direction);
        final BlockPos minPos = corePos.offset(-rotated[4], -rotated[1], -rotated[2]);
        final BlockPos maxPos = corePos.offset(rotated[5], rotated[0], rotated[3]);
        final List<BlockPos> positions = new ArrayList<>();
        for (final BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
            positions.add(pos.immutable());
        }
        return positions;
    }

    public static void validate(final int[] bounds) {
        if (bounds == null || bounds.length != 6) {
            throw new IllegalArgumentException("Legacy multiblock bounds must be [up, down, north, south, west, east].");
        }
    }
}
