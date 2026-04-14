package com.hbm.ntm.common.fluid;

import com.hbm.ntm.common.registration.HbmFluids;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/**
 * Legacy combustible fuel grades and combustion energies used by early generators.
 */
@SuppressWarnings("null")
public final class CombustibleFuelRegistry {
    private static final Map<ResourceLocation, FuelData> FUEL_DATA = new HashMap<>();
    private static final Map<ResourceLocation, BurnPollution> BURN_POLLUTION = new HashMap<>();

    private static final float SOOT_PER_SECOND = 1.0F / 25.0F;
    private static final float HEAVY_METAL_PER_SECOND = 1.0F / 50.0F;

    private static final float SOOT_UNREFINED_OIL = SOOT_PER_SECOND * 0.1F;
    private static final float SOOT_REFINED_OIL = SOOT_PER_SECOND * 0.025F;
    private static final float SOOT_GAS = SOOT_PER_SECOND * 0.005F;
    private static final float LEAD_FUEL = HEAVY_METAL_PER_SECOND * 0.025F;

    private static final BurnPollution NO_POLLUTION = new BurnPollution(0.0F, 0.0F, 0.0F);
    private static final BurnPollution OIL_BURN_POLLUTION = new BurnPollution(SOOT_UNREFINED_OIL, 0.0F, 0.0F);
    private static final BurnPollution FUEL_BURN_POLLUTION = new BurnPollution(SOOT_REFINED_OIL, 0.0F, 0.0F);
    private static final BurnPollution FUEL_LEADED_BURN_POLLUTION = new BurnPollution(SOOT_REFINED_OIL, LEAD_FUEL, 0.0F);
    private static final BurnPollution GAS_BURN_POLLUTION = new BurnPollution(SOOT_GAS, 0.0F, 0.0F);
    private static final BurnPollution LIQUID_GAS_BURN_POLLUTION = new BurnPollution(SOOT_GAS * 2.0F, 0.0F, 0.0F);

    public enum FuelGrade {
        LOW,
        MEDIUM,
        HIGH,
        AERO,
        GAS
    }

    public record FuelData(FuelGrade grade, int combustionEnergy) {
    }

    public record BurnPollution(float soot, float heavyMetal, float poison) {
        public boolean isEmpty() {
            return this.soot <= 0.0F && this.heavyMetal <= 0.0F && this.poison <= 0.0F;
        }
    }

    static {
        register(HbmFluids.GAS, FuelGrade.GAS, 75_000, GAS_BURN_POLLUTION);
        register(HbmFluids.GAS_COKER, FuelGrade.GAS, 93_700, GAS_BURN_POLLUTION);
        register(HbmFluids.HEAVYOIL, FuelGrade.LOW, 68_700, OIL_BURN_POLLUTION);
        register(HbmFluids.SMEAR, FuelGrade.LOW, 82_300, OIL_BURN_POLLUTION);
        register(HbmFluids.RECLAIMED, FuelGrade.LOW, 141_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.PETROIL, FuelGrade.MEDIUM, 195_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.PETROIL_LEADED, FuelGrade.MEDIUM, 293_000, FUEL_LEADED_BURN_POLLUTION);
        register(HbmFluids.HEATINGOIL, FuelGrade.LOW, 489_000, OIL_BURN_POLLUTION);
        register(HbmFluids.NAPHTHA, FuelGrade.MEDIUM, 165_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.NAPHTHA_DS, FuelGrade.MEDIUM, 330_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.NAPHTHA_CRACK, FuelGrade.MEDIUM, 128_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.NAPHTHA_COKER, FuelGrade.MEDIUM, 187_000, OIL_BURN_POLLUTION);
        register(HbmFluids.GASOLINE, FuelGrade.HIGH, 1_510_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.GASOLINE_LEADED, FuelGrade.HIGH, 2_260_000, FUEL_LEADED_BURN_POLLUTION);
        register(HbmFluids.DIESEL, FuelGrade.HIGH, 1_370_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.DIESEL_CRACK, FuelGrade.HIGH, 1_280_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.LIGHTOIL, FuelGrade.MEDIUM, 2_200_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.LIGHTOIL_DS, FuelGrade.MEDIUM, 4_400_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.LIGHTOIL_CRACK, FuelGrade.MEDIUM, 1_370_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.KEROSENE, FuelGrade.AERO, 3_850_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.PETROLEUM, FuelGrade.GAS, 2_470_000, GAS_BURN_POLLUTION);
        register(HbmFluids.LPG, FuelGrade.HIGH, 4_530_000, LIQUID_GAS_BURN_POLLUTION);
        register(HbmFluids.HEAVYOIL_VACUUM, FuelGrade.LOW, 234_000, OIL_BURN_POLLUTION);
        register(HbmFluids.REFORMATE, FuelGrade.HIGH, 6_000_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.LIGHTOIL_VACUUM, FuelGrade.MEDIUM, 4_500_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.XYLENE, FuelGrade.HIGH, 7_870_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.HEATINGOIL_VACUUM, FuelGrade.LOW, 1_640_000, OIL_BURN_POLLUTION);
        register(HbmFluids.REFORMGAS, FuelGrade.GAS, 15_700_000, GAS_BURN_POLLUTION);
        register(HbmFluids.DIESEL_REFORM, FuelGrade.HIGH, 3_430_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.DIESEL_CRACK_REFORM, FuelGrade.HIGH, 3_210_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.KEROSENE_REFORM, FuelGrade.AERO, 9_600_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.COALGAS, FuelGrade.MEDIUM, 8_660_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.COALGAS_LEADED, FuelGrade.MEDIUM, 12_900_000, FUEL_LEADED_BURN_POLLUTION);
        register(HbmFluids.ETHANOL, FuelGrade.HIGH, 687_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.BIOGAS, FuelGrade.GAS, 78_100, GAS_BURN_POLLUTION);
        register(HbmFluids.BIOFUEL, FuelGrade.HIGH, 1_250_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.SYNGAS, FuelGrade.GAS, 1_870_000, NO_POLLUTION);
        register(HbmFluids.OXYHYDROGEN, FuelGrade.GAS, 15_000, NO_POLLUTION);
        register(HbmFluids.NITAN, FuelGrade.HIGH, 160_000_000, FUEL_BURN_POLLUTION);
        register(HbmFluids.BALEFIRE, FuelGrade.HIGH, 640_000_000, FUEL_BURN_POLLUTION);
    }

    private CombustibleFuelRegistry() {
    }

    public static @Nullable FuelData dataFor(final @Nullable Fluid fluid) {
        if (fluid == null) {
            return null;
        }
        final ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        if (id == null) {
            return null;
        }
        return FUEL_DATA.get(id);
    }

    public static BurnPollution burnPollutionFor(final @Nullable Fluid fluid) {
        if (fluid == null) {
            return NO_POLLUTION;
        }
        final ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        if (id == null) {
            return NO_POLLUTION;
        }
        return BURN_POLLUTION.getOrDefault(id, NO_POLLUTION);
    }

    private static void register(final HbmFluids.FluidEntry entry,
                                 final FuelGrade grade,
                                 final int combustionEnergy,
                                 final BurnPollution burnPollution) {
        final FuelData data = new FuelData(grade, combustionEnergy);
        final ResourceLocation stillId = ForgeRegistries.FLUIDS.getKey(entry.getStillFluid());
        if (stillId != null) {
            FUEL_DATA.put(stillId, data);
            BURN_POLLUTION.put(stillId, burnPollution);
        }
        final ResourceLocation flowingId = ForgeRegistries.FLUIDS.getKey(entry.getFlowingFluid());
        if (flowingId != null) {
            FUEL_DATA.put(flowingId, data);
            BURN_POLLUTION.put(flowingId, burnPollution);
        }
    }
}
