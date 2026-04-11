package com.hbm.util.function;

import com.hbm.util.BobMathUtil;
import java.util.Locale;
import net.minecraft.ChatFormatting;

public abstract class Function {
    protected double div = 1D;
    protected double off = 0D;

    public abstract double effonix(double x);

    public abstract String getLabelForFuel();

    public abstract String getDangerFromFuel();

    public Function withDiv(final double div) {
        this.div = div;
        return this;
    }

    public Function withOff(final double off) {
        this.off = off;
        return this;
    }

    public double getX(final double x) {
        return x / this.div + this.off;
    }

    public String getXName() {
        return this.getXName(true);
    }

    public String getXName(final boolean brackets) {
        String x = "x";
        boolean mod = false;
        if (this.div != 1D) {
            x += " / " + String.format(Locale.US, "%,.1f", this.div);
            mod = true;
        }
        if (this.off != 0D) {
            x += " + " + String.format(Locale.US, "%,.1f", this.off);
            mod = true;
        }
        if (mod && brackets) {
            x = "(" + x + ")";
        }
        return x;
    }

    public abstract static class FunctionSingleArg extends Function {
        protected double level;

        public FunctionSingleArg(final double level) {
            this.level = level;
        }
    }

    public abstract static class FunctionDoubleArg extends Function {
        protected double level;
        protected double vOff;

        public FunctionDoubleArg(final double level, final double vOff) {
            this.level = level;
            this.vOff = vOff;
        }
    }

    public static class FunctionLogarithmic extends FunctionSingleArg {
        public FunctionLogarithmic(final double level) {
            super(level);
            this.withOff(1D);
        }

        @Override
        public double effonix(final double x) {
            return Math.log10(this.getX(x)) * this.level;
        }

        @Override
        public String getLabelForFuel() {
            return "log10(" + this.getXName(false) + ") * " + String.format(Locale.US, "%,.1f", this.level);
        }

        @Override
        public String getDangerFromFuel() {
            return ChatFormatting.YELLOW + "MEDIUM / LOGARITHMIC";
        }
    }

    public static class FunctionPassive extends FunctionSingleArg {
        public FunctionPassive(final double level) {
            super(level);
        }

        @Override
        public double effonix(final double x) {
            return this.level;
        }

        @Override
        public String getLabelForFuel() {
            return String.format(Locale.US, "%,.1f", this.level);
        }

        @Override
        public String getDangerFromFuel() {
            return ChatFormatting.DARK_GREEN + "SAFE / PASSIVE";
        }
    }

    public static class FunctionSqrt extends FunctionSingleArg {
        public FunctionSqrt(final double level) {
            super(level);
        }

        @Override
        public double effonix(final double x) {
            return BobMathUtil.squirt(this.getX(x)) * this.level;
        }

        @Override
        public String getLabelForFuel() {
            return "sqrt(" + this.getXName(false) + ") * " + String.format(Locale.US, "%,.3f", this.level);
        }

        @Override
        public String getDangerFromFuel() {
            return ChatFormatting.YELLOW + "MEDIUM / SQUARE ROOT";
        }
    }

    public static class FunctionSqrtFalling extends FunctionSqrt {
        public FunctionSqrtFalling(final double fallFactor) {
            super(1D / fallFactor);
            this.withOff(fallFactor * fallFactor);
        }
    }

    public static class FunctionLinear extends FunctionSingleArg {
        public FunctionLinear(final double level) {
            super(level);
        }

        @Override
        public double effonix(final double x) {
            return this.getX(x) * this.level;
        }

        @Override
        public String getLabelForFuel() {
            return this.getXName(true) + " * " + String.format(Locale.US, "%,.1f", this.level);
        }

        @Override
        public String getDangerFromFuel() {
            return ChatFormatting.RED + "DANGEROUS / LINEAR";
        }
    }

    public static class FunctionQuadratic extends FunctionDoubleArg {
        public FunctionQuadratic(final double level) {
            this(level, 0D);
        }

        public FunctionQuadratic(final double level, final double vOff) {
            super(level, vOff);
        }

        @Override
        public double effonix(final double x) {
            return this.getX(x) * this.getX(x) * this.level + this.vOff;
        }

        @Override
        public String getLabelForFuel() {
            return this.getXName(true) + "² * " + String.format(Locale.US, "%,.1f", this.level)
                + (this.vOff != 0D ? " + " + String.format(Locale.US, "%,.1f", this.vOff) : "");
        }

        @Override
        public String getDangerFromFuel() {
            return ChatFormatting.RED + "DANGEROUS / QUADRATIC";
        }
    }
}
