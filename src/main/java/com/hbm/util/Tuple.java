package com.hbm.util;

public final class Tuple {

    private Tuple() {
    }

    public record Pair<X, Y>(X key, Y value) {
        public X getKey() {
            return this.key;
        }

        public Y getValue() {
            return this.value;
        }
    }

    public record Triplet<X, Y, Z>(X x, Y y, Z z) {
        public X getX() {
            return this.x;
        }

        public Y getY() {
            return this.y;
        }

        public Z getZ() {
            return this.z;
        }
    }

    public static final class Quartet<W, X, Y, Z> {
        private W w;
        private X x;
        private Y y;
        private Z z;

        public Quartet(final W w, final X x, final Y y, final Z z) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public W getW() {
            return this.w;
        }

        public X getX() {
            return this.x;
        }

        public Y getY() {
            return this.y;
        }

        public Z getZ() {
            return this.z;
        }

        public void mangle(final W w, final X x, final Y y, final Z z) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Quartet<W, X, Y, Z> clone() {
            return new Quartet<>(this.w, this.x, this.y, this.z);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + (this.w == null ? 0 : this.w.hashCode());
            result = 31 * result + (this.x == null ? 0 : this.x.hashCode());
            result = 31 * result + (this.y == null ? 0 : this.y.hashCode());
            result = 31 * result + (this.z == null ? 0 : this.z.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof final Quartet<?, ?, ?, ?> other)) {
                return false;
            }
            return java.util.Objects.equals(this.w, other.w)
                && java.util.Objects.equals(this.x, other.x)
                && java.util.Objects.equals(this.y, other.y)
                && java.util.Objects.equals(this.z, other.z);
        }
    }
}
