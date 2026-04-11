package com.hbm.util.fauxpointtwelve;

import com.hbm.interfaces.Spaghetti;
import net.minecraft.core.Direction;

public enum Rotation {
    NONE,
    CLOCKWISE_90,
    CLOCKWISE_180,
    COUNTERCLOCKWISE_90;

    @Spaghetti("thanks for the cool code, mojang")
    public Rotation add(final Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 -> switch (this) {
                case NONE -> CLOCKWISE_180;
                case CLOCKWISE_90 -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_180 -> NONE;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_90;
            };
            case COUNTERCLOCKWISE_90 -> switch (this) {
                case NONE -> COUNTERCLOCKWISE_90;
                case CLOCKWISE_90 -> NONE;
                case CLOCKWISE_180 -> CLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> CLOCKWISE_180;
            };
            case CLOCKWISE_90 -> switch (this) {
                case NONE -> CLOCKWISE_90;
                case CLOCKWISE_90 -> CLOCKWISE_180;
                case CLOCKWISE_180 -> COUNTERCLOCKWISE_90;
                case COUNTERCLOCKWISE_90 -> NONE;
            };
            default -> this;
        };
    }

    public static Rotation getBlockRotation(final Direction dir) {
        return switch (dir) {
            case NORTH -> Rotation.NONE;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case EAST -> Rotation.COUNTERCLOCKWISE_90;
            case WEST -> Rotation.CLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }
}
