package com.hbm.ntm.common.fluid;

import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

public class HbmFluidType extends FluidType {
    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final int tintColor;
    private final FluidHazardProperties hazardProperties;

    public HbmFluidType(final Properties properties, final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final int tintColor) {
        this(properties, stillTexture, flowingTexture, tintColor, FluidHazardProperties.NONE);
    }

    public HbmFluidType(final Properties properties, final ResourceLocation stillTexture, final ResourceLocation flowingTexture, final int tintColor,
                        final FluidHazardProperties hazardProperties) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.tintColor = tintColor;
        this.hazardProperties = hazardProperties;
    }

    public FluidHazardProperties hazardProperties() {
        return this.hazardProperties;
    }

    public boolean isHot() {
        return this.hazardProperties.hot() || this.getTemperature() > 350;
    }

    public int getCorrosiveRating() {
        return this.hazardProperties.corrosiveRating();
    }

    public boolean isAntimatter() {
        return this.hazardProperties.antimatter();
    }

    public int getTintColor() {
        return this.tintColor;
    }

    @Override
    public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return flowingTexture;
            }

            @Override
            public int getTintColor() {
                return tintColor;
            }
        });
    }
}
