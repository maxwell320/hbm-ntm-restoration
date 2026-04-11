package com.hbm.util;

import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class Either<L, R> {
    public static <L, R> Either<L, R> left(final L value) {
        return new Either<>(value, true);
    }

    public static <L, R> Either<L, R> right(final R value) {
        return new Either<>(value, false);
    }

    private final Object value;
    private final boolean isLeft;

    private Either(final Object value, final boolean isLeft) {
        this.value = value;
        this.isLeft = isLeft;
    }

    public boolean isLeft() {
        return this.isLeft;
    }

    public boolean isRight() {
        return !this.isLeft;
    }

    public L left() {
        if (this.isLeft) {
            return (L) this.value;
        }
        throw new IllegalStateException("Tried accessing value as the L type, but was R type");
    }

    public R right() {
        if (!this.isLeft) {
            return (R) this.value;
        }
        throw new IllegalStateException("Tried accessing value as the R type, but was L type");
    }

    public L leftOrNull() {
        return this.isLeft ? (L) this.value : null;
    }

    public R rightOrNull() {
        return !this.isLeft ? (R) this.value : null;
    }

    public <V> V cast() {
        return (V) this.value;
    }

    public <T> T run(final Function<L, T> leftFunc, final Function<R, T> rightFunc) {
        return this.isLeft ? leftFunc.apply((L) this.value) : rightFunc.apply((R) this.value);
    }

    public <T> T runLeftOrNull(final Function<L, T> func) {
        return this.isLeft ? func.apply((L) this.value) : null;
    }

    public <T> T runRightOrNull(final Function<R, T> func) {
        return !this.isLeft ? func.apply((R) this.value) : null;
    }

    public <V, T> T runCasting(final Function<V, T> func) {
        return func.apply((V) this.value);
    }
}
