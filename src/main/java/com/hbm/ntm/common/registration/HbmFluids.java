package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.RadLavaBlock;
import com.hbm.ntm.common.block.VolcanicLavaBlock;
import com.hbm.ntm.common.fluid.FluidHazardProperties;
import com.hbm.ntm.common.fluid.HbmFluidType;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class HbmFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HbmNtmMod.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, HbmNtmMod.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HbmNtmMod.MOD_ID);
    private static final ResourceLocation COOLANT_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/coolant_still"));
    private static final ResourceLocation COOLANT_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/coolant_flowing"));
    private static final ResourceLocation COOLANT_HOT_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/coolant_hot_still"));
    private static final ResourceLocation COOLANT_HOT_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/coolant_hot_flowing"));
    private static final ResourceLocation OIL_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/oil_still"));
    private static final ResourceLocation OIL_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/oil_flowing"));
    private static final ResourceLocation VOLCANIC_LAVA_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/volcanic_lava_still"));
    private static final ResourceLocation HYDROGEN_STILL = COOLANT_STILL;
    private static final ResourceLocation HYDROGEN_FLOWING = COOLANT_FLOWING;
    private static final ResourceLocation VOLCANIC_LAVA_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/volcanic_lava_flowing"));
    private static final ResourceLocation RAD_LAVA_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/rad_lava_still"));
    private static final ResourceLocation RAD_LAVA_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/rad_lava_flowing"));
    public static final FluidEntry COOLANT = registerCoolant("coolant", 0xFFD8FCFF, 1_000, COOLANT_STILL, COOLANT_FLOWING, MapColor.ICE);
    public static final FluidEntry COOLANT_HOT = registerCoolant("coolant_hot", 0xFF99525E, 1_000, COOLANT_HOT_STILL, COOLANT_HOT_FLOWING, MapColor.COLOR_RED);
    public static final FluidEntry HYDROGEN = registerContainedFluid("hydrogen", 0xFF4286F4, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry DEUTERIUM = registerContainedFluid("deuterium", 0xFF0000FF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry TRITIUM = registerContainedFluid("tritium", 0xFF000099, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry OXYGEN = registerContainedFluid("oxygen", 0xFF98BDF9, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry XENON = registerContainedFluid("xenon", 0xFFBA45E8, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HELIUM3 = registerContainedFluid("helium3", 0xFFFCF0C4, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CRYOGEL = registerContainedFluid("cryogel", 0xFF32FFFF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CARBONDIOXIDE = registerContainedFluid("carbondioxide", 0xFF404040, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEAVYWATER = registerContainedFluid("heavywater", 0xFF00A0B0, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEAVYWATER_HOT = registerContainedFluid("heavywater_hot", 0xFF4D007B, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SODIUM = registerContainedFluid("sodium", 0xFFCCD4D5, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SODIUM_HOT = registerContainedFluid("sodium_hot", 0xFFE2ADC1, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry THORIUM_SALT = registerContainedFluid("thorium_salt", 0xFF7A5542, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry THORIUM_SALT_HOT = registerContainedFluid("thorium_salt_hot", 0xFF3E3627, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry THORIUM_SALT_DEPLETED = registerContainedFluid("thorium_salt_depleted", 0xFF302D1C, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CHLORINE = registerContainedFluid("chlorine", 0xFFBAB572, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 25, false));
    public static final FluidEntry REDMUD = registerContainedFluid("redmud", 0xFFD85638, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SCHRABIDIC = registerContainedFluid("schrabidic", 0xFF006B6B, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 75, false));
    public static final FluidEntry VITRIOL = registerContainedFluid("vitriol", 0xFF6E5222, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SLOP = registerContainedFluid("slop", 0xFF929D45, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LEAD = registerContainedFluid("lead", 0xFF666672, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LEAD_HOT = registerContainedFluid("lead_hot", 0xFF776563, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry FULLERENE = registerContainedFluid("fullerene", 0xFFFF7FED, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PHEROMONE = registerContainedFluid("pheromone", 0xFF5FA6E8, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PHEROMONE_M = registerContainedFluid("pheromone_m", 0xFF48C9B0, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry OIL_DS = registerContainedFluid("oil_ds", 0xFF121212, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HOTOIL_DS = registerContainedFluid("hotoil_ds", 0xFF3F180F, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CRACKOIL_DS = registerContainedFluid("crackoil_ds", 0xFF2A1C11, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HOTCRACKOIL_DS = registerContainedFluid("hotcrackoil_ds", 0xFF3A1A28, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NAPHTHA_DS = registerContainedFluid("naphtha_ds", 0xFF63614E, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LIGHTOIL_DS = registerContainedFluid("lightoil_ds", 0xFF63543E, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SYNGAS = registerContainedFluid("syngas", 0xFF131313, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry OXYHYDROGEN = registerContainedFluid("oxyhydrogen", 0xFF483FC1, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry RADIOSOLVENT = registerContainedFluid("radiosolvent", 0xFFA4D7DD, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 50, false));
    public static final FluidEntry SOURGAS = registerContainedFluid("sourgas", 0xFFC9BE0D, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 10, false));
    public static final FluidEntry REFORMGAS = registerContainedFluid("reformgas", 0xFF6362AE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PHOSGENE = registerContainedFluid("phosgene", 0xFFCFC4A4, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry MUSTARDGAS = registerContainedFluid("mustardgas", 0xFFBAB572, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry IONGEL = registerContainedFluid("iongel", 0xFFB8FFFF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEAVYOIL_VACUUM = registerContainedFluid("heavyoil_vacuum", 0xFF131214, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry REFORMATE = registerContainedFluid("reformate", 0xFF835472, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LIGHTOIL_VACUUM = registerContainedFluid("lightoil_vacuum", 0xFF8C8851, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry XYLENE = registerContainedFluid("xylene", 0xFF5C4E76, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEATINGOIL_VACUUM = registerContainedFluid("heatingoil_vacuum", 0xFF211D06, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry DIESEL_REFORM = registerContainedFluid("diesel_reform", 0xFFCDC3C6, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry DIESEL_CRACK_REFORM = registerContainedFluid("diesel_crack_reform", 0xFFCDC3CC, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry KEROSENE_REFORM = registerContainedFluid("kerosene_reform", 0xFFFFA5F3, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry COLLOID = registerContainedFluid("colloid", 0xFF787878, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry OIL_COKER = registerContainedFluid("oil_coker", 0xFF001802, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NAPHTHA_COKER = registerContainedFluid("naphtha_coker", 0xFF495944, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry GAS_COKER = registerContainedFluid("gas_coker", 0xFFDEF4CA, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry EGG = registerContainedFluid("egg", 0xFFD2C273, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CHOLESTEROL = registerContainedFluid("cholesterol", 0xFFD6D2BD, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry ESTRADIOL = registerContainedFluid("estradiol", 0xFFCDD5D8, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry FISHOIL = registerContainedFluid("fishoil", 0xFF4B4A45, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SUNFLOWEROIL = registerContainedFluid("sunfloweroil", 0xFFCBAD45, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NITROGLYCERIN = registerContainedFluid("nitroglycerin", 0xFF92ACA6, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CHLOROCALCITE_SOLUTION = registerContainedFluid("chlorocalcite_solution", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CHLOROCALCITE_MIX = registerContainedFluid("chlorocalcite_mix", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CHLOROCALCITE_CLEANED = registerContainedFluid("chlorocalcite_cleaned", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry POTASSIUM_CHLORIDE = registerContainedFluid("potassium_chloride", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CALCIUM_CHLORIDE = registerContainedFluid("calcium_chloride", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CALCIUM_SOLUTION = registerContainedFluid("calcium_solution", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry AROMATICS = registerContainedFluid("aromatics", 0xFF68A09A, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry UNSATURATEDS = registerContainedFluid("unsaturateds", 0xFF628FAE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry AIR = registerContainedFluid("air", 0xFFE7EAEB, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SALIENT = registerContainedFluid("salient", 0xFF457F2D, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry XPJUICE = registerContainedFluid("xpjuice", 0xFFBBFF09, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry ENDERJUICE = registerContainedFluid("enderjuice", 0xFF127766, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PETROIL_LEADED = registerContainedFluid("petroil_leaded", 0xFF44413D, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry GASOLINE_LEADED = registerContainedFluid("gasoline_leaded", 0xFF445772, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry COALGAS_LEADED = registerContainedFluid("coalgas_leaded", 0xFF445772, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry WOODOIL = registerContainedFluid("woodoil", 0xFF847D54, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry COALCREOSOTE = registerContainedFluid("coalcreosote", 0xFF51694F, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SEEDSLURRY = registerContainedFluid("seedslurry", 0xFF7CC35E, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BLOOD = registerContainedFluid("blood", 0xFFB22424, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BLOOD_HOT = registerContainedFluid("blood_hot", 0xFFF22419, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry MUG = registerContainedFluid("mug", 0xFF4B2D28, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry MUG_HOT = registerContainedFluid("mug_hot", 0xFF6B2A20, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry COALOIL = registerContainedFluid("coaloil", 0xFF020202, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HOTCRACKOIL = registerContainedFluid("hotcrackoil", 0xFF300900, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NAPHTHA_CRACK = registerContainedFluid("naphtha_crack", 0xFF595744, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LIGHTOIL_CRACK = registerContainedFluid("lightoil_crack", 0xFF8C7451, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry DIESEL_CRACK = registerContainedFluid("diesel_crack", 0xFFF2EED5, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEAVYOIL = registerContainedFluid("heavyoil", 0xFF141312, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HEATINGOIL = registerContainedFluid("heatingoil", 0xFF211806, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PETROIL = registerContainedFluid("petroil", 0xFF44413D, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NAPHTHA = registerContainedFluid("naphtha", 0xFF595744, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry DIESEL = registerContainedFluid("diesel", 0xFFF2EED5, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LIGHTOIL = registerContainedFluid("lightoil", 0xFF8C7451, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry KEROSENE = registerContainedFluid("kerosene", 0xFFFFA5D2, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry GASOLINE = registerContainedFluid("gasoline", 0xFF445772, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry RECLAIMED = registerContainedFluid("reclaimed", 0xFF332B22, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LUBRICANT = registerContainedFluid("lubricant", 0xFF606060, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry GAS = registerContainedFluid("gas", 0xFFFFFEED, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PETROLEUM = registerContainedFluid("petroleum", 0xFF7CB7C9, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LPG = registerContainedFluid("lpg", 0xFF4747EA, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BIOGAS = registerContainedFluid("biogas", 0xFFBFD37C, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BIOFUEL = registerContainedFluid("biofuel", 0xFFEEF274, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry ETHANOL = registerContainedFluid("ethanol", 0xFFE0FFFF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry COALGAS = registerContainedFluid("coalgas", 0xFF445772, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HOTOIL = registerContainedFluid("hotoil", 0xFF300900, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CRACKOIL = registerContainedFluid("crackoil", 0xFF020202, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NITAN = registerContainedFluid("nitan", 0xFF8018AD, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry FRACKSOL = registerContainedFluid("fracksol", 0xFF798A6B, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 15, false));
    public static final FluidEntry PAIN = registerContainedFluid("pain", 0xFF938541, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 30, false));
    public static final FluidEntry DEATH = registerContainedFluid("death", 0xFF717A88, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 80, false));
    public static final FluidEntry WATZ = registerContainedFluid("watz", 0xFF86653E, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 60, false));
    public static final FluidEntry DHC = registerContainedFluid("dhc", 0xFFD2AFFF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry UF6 = registerContainedFluid("uf6", 0xFFD1CEBE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PUF6 = registerContainedFluid("puf6", 0xFF4C4C4C, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SAS3 = registerContainedFluid("sas3", 0xFF4FFFFC, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry STEAM = registerContainedFluid("steam", 0xFFE5E5E5, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry HOTSTEAM = registerContainedFluid("hotsteam", 0xFFE7D6D6, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SUPERHOTSTEAM = registerContainedFluid("superhotsteam", 0xFFE7B7B7, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry ULTRAHOTSTEAM = registerContainedFluid("ultrahotsteam", 0xFFE39393, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SPENTSTEAM = registerContainedFluid("spentsteam", 0xFF445772, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BITUMEN = registerContainedFluid("bitumen", 0xFF1F2426, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SMEAR = registerContainedFluid("smear", 0xFF190F01, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry AMAT = registerContainedFluid("amat", 0xFF010101, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 0, true));
    public static final FluidEntry ASCHRAB = registerContainedFluid("aschrab", 0xFFB50000, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 0, true));
    public static final FluidEntry PLASMA_DT = registerContainedFluid("plasma_dt", 0xFFF7AFDE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PLASMA_HD = registerContainedFluid("plasma_hd", 0xFFF0ADF4, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PLASMA_HT = registerContainedFluid("plasma_ht", 0xFFD1ABF2, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PLASMA_XM = registerContainedFluid("plasma_xm", 0xFFC6A5FF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PLASMA_BF = registerContainedFluid("plasma_bf", 0xFFA7F1A3, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PLASMA_DH3 = registerContainedFluid("plasma_dh3", 0xFFFF83AA, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry WASTEFLUID = registerContainedFluid("wastefluid", 0xFF544400, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry WASTEGAS = registerContainedFluid("wastegas", 0xFFB8B8B8, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SMOKE = registerContainedFluid("smoke", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SMOKE_LEADED = registerContainedFluid("smoke_leaded", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SMOKE_POISON = registerContainedFluid("smoke_poison", 0xFF808080, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BALEFIRE = registerContainedFluid("balefire", 0xFF28E02E, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(true, 50, false));
    public static final FluidEntry STELLAR_FLUX = registerContainedFluid("stellar_flux", 0xFFE300FF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry CONCRETE = registerContainedFluid("concrete", 0xFFA2A2A2, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry NITRIC_ACID = registerContainedFluid("nitric_acid", 0xFFAD6A0E, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 60, false));
    public static final FluidEntry SULFURIC_ACID = registerContainedFluid("sulfuric_acid", 0xFFB0AA64, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 50, false));
    public static final FluidEntry PEROXIDE = registerContainedFluid("peroxide", 0xFFFFF7AA, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 40, false));
    public static final FluidEntry SOLVENT = registerContainedFluid("solvent", 0xFFE4E3EF, HYDROGEN_STILL, HYDROGEN_FLOWING, new FluidHazardProperties(false, 30, false));
    public static final FluidEntry HELIUM4 = registerContainedFluid("helium4", 0xFFE54B0A, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PERFLUOROMETHYL = registerContainedFluid("perfluoromethyl", 0xFFBDC8DC, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PERFLUOROMETHYL_COLD = registerContainedFluid("perfluoromethyl_cold", 0xFF99DADE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry PERFLUOROMETHYL_HOT = registerContainedFluid("perfluoromethyl_hot", 0xFFB899DE, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry LYE = registerContainedFluid("lye", 0xFFFFECCC, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry SODIUM_ALUMINATE = registerContainedFluid("sodium_aluminate", 0xFFFFD191, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry BAUXITE_SOLUTION = registerContainedFluid("bauxite_solution", 0xFFE2560F, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry ALUMINA = registerContainedFluid("alumina", 0xFFDDFFFF, HYDROGEN_STILL, HYDROGEN_FLOWING);
    public static final FluidEntry OIL = registerOil();
    public static final FluidEntry VOLCANIC_LAVA = registerVolcanicLava();
    public static final FluidEntry RAD_LAVA = registerRadLava();

    private HbmFluids() {
    }

    private static FluidEntry registerCoolant(final String id, final int tintColor, final int density, final ResourceLocation stillTexture,
                                              final ResourceLocation flowingTexture, final MapColor mapColor) {
        final FluidEntry entry = new FluidEntry();
        final ResourceLocation fluidId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, id));
        final FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
            .density(density)
            .viscosity(1_000)
            .descriptionId(Util.makeDescriptionId("fluid", fluidId));
        final ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(entry::getFluidType, entry::getStillFluid, entry::getFlowingFluid)
            .block(entry::getBlock)
            .levelDecreasePerBlock(1)
            .slopeFindDistance(2)
            .tickRate(10)
            .explosionResistance(100.0F);

        entry.fluidType = FLUID_TYPES.register(id, () -> new HbmFluidType(fluidTypeProperties, stillTexture, flowingTexture, tintColor));
        entry.stillFluid = FLUIDS.register(id, () -> new ForgeFlowingFluid.Source(fluidProperties));
        entry.flowingFluid = FLUIDS.register("flowing_" + id, () -> new ForgeFlowingFluid.Flowing(fluidProperties));
        entry.block = BLOCKS.register(id + "_block", () -> new LiquidBlock(entry::getStillFluid,
            Objects.requireNonNull(BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid().mapColor(Objects.requireNonNull(mapColor)))));
        return entry;
    }

    public static final FluidEntry registerContainedFluid(final String id, final int tintColor, final ResourceLocation stillTexture,
                                                     final ResourceLocation flowingTexture) {
        return registerContainedFluid(id, tintColor, stillTexture, flowingTexture, FluidHazardProperties.NONE);
    }

    private static FluidEntry registerContainedFluid(final String id, final int tintColor, final ResourceLocation stillTexture,
                                                     final ResourceLocation flowingTexture, final FluidHazardProperties hazardProperties) {
        final FluidEntry entry = new FluidEntry();
        final ResourceLocation fluidId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, id));
        final FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
            .descriptionId(Util.makeDescriptionId("fluid", fluidId));
        final ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(entry::getFluidType, entry::getStillFluid, entry::getFlowingFluid)
            .levelDecreasePerBlock(1)
            .slopeFindDistance(2)
            .tickRate(10)
            .explosionResistance(100.0F);

        entry.fluidType = FLUID_TYPES.register(id, () -> new HbmFluidType(fluidTypeProperties, stillTexture, flowingTexture, tintColor, hazardProperties));
        entry.stillFluid = FLUIDS.register(id, () -> new ForgeFlowingFluid.Source(fluidProperties));
        entry.flowingFluid = FLUIDS.register("flowing_" + id, () -> new ForgeFlowingFluid.Flowing(fluidProperties));
        return entry;
    }

    private static FluidEntry registerOil() {
        final FluidEntry entry = new FluidEntry();
        final ResourceLocation fluidId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "oil"));
        final FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
            .density(1_200)
            .viscosity(2_000)
            .descriptionId(Util.makeDescriptionId("fluid", fluidId));
        final ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(entry::getFluidType, entry::getStillFluid, entry::getFlowingFluid)
            .block(entry::getBlock)
            .levelDecreasePerBlock(1)
            .slopeFindDistance(2)
            .tickRate(25)
            .explosionResistance(100.0F);

        entry.fluidType = FLUID_TYPES.register("oil", () -> new HbmFluidType(fluidTypeProperties, OIL_STILL, OIL_FLOWING, 0xFF020202));
        entry.stillFluid = FLUIDS.register("oil", () -> new ForgeFlowingFluid.Source(fluidProperties));
        entry.flowingFluid = FLUIDS.register("flowing_oil", () -> new ForgeFlowingFluid.Flowing(fluidProperties));
        entry.block = BLOCKS.register("oil_block", () -> new LiquidBlock(entry::getStillFluid,
            Objects.requireNonNull(BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid().mapColor(Objects.requireNonNull(MapColor.COLOR_BLACK)))));
        return entry;
    }

    private static FluidEntry registerVolcanicLava() {
        final FluidEntry entry = new FluidEntry();
        final ResourceLocation blockId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "volcanic_lava_block"));
        final FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
            .lightLevel(15)
            .density(3000)
            .viscosity(3000)
            .temperature(1300)
            .descriptionId(Util.makeDescriptionId("block", blockId));
        final ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(entry::getFluidType, entry::getStillFluid, entry::getFlowingFluid)
            .block(entry::getBlock)
            .levelDecreasePerBlock(1)
            .slopeFindDistance(2)
            .tickRate(30)
            .explosionResistance(500.0F);

        entry.fluidType = FLUID_TYPES.register("volcanic_lava", () -> new HbmFluidType(fluidTypeProperties, VOLCANIC_LAVA_STILL, VOLCANIC_LAVA_FLOWING, 0xFFFFFFFF));
        entry.stillFluid = FLUIDS.register("volcanic_lava", () -> new ForgeFlowingFluid.Source(fluidProperties));
        entry.flowingFluid = FLUIDS.register("flowing_volcanic_lava", () -> new ForgeFlowingFluid.Flowing(fluidProperties));
        entry.block = BLOCKS.register("volcanic_lava_block", () -> new VolcanicLavaBlock(entry::getStillFluid,
            Objects.requireNonNull(BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid().mapColor(MapColor.FIRE).randomTicks())));
        return entry;
    }

    private static FluidEntry registerRadLava() {
        final FluidEntry entry = new FluidEntry();
        final ResourceLocation blockId = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "rad_lava_block"));
        final FluidType.Properties fluidTypeProperties = FluidType.Properties.create()
            .lightLevel(15)
            .density(3000)
            .viscosity(3000)
            .temperature(1300)
            .descriptionId(Util.makeDescriptionId("block", blockId));
        final ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(entry::getFluidType, entry::getStillFluid, entry::getFlowingFluid)
            .block(entry::getBlock)
            .levelDecreasePerBlock(1)
            .slopeFindDistance(2)
            .tickRate(30)
            .explosionResistance(500.0F);

        entry.fluidType = FLUID_TYPES.register("rad_lava", () -> new HbmFluidType(fluidTypeProperties, RAD_LAVA_STILL, RAD_LAVA_FLOWING, 0xFFFFFFFF));
        entry.stillFluid = FLUIDS.register("rad_lava", () -> new ForgeFlowingFluid.Source(fluidProperties));
        entry.flowingFluid = FLUIDS.register("flowing_rad_lava", () -> new ForgeFlowingFluid.Flowing(fluidProperties));
        entry.block = BLOCKS.register("rad_lava_block", () -> new RadLavaBlock(entry::getStillFluid,
            Objects.requireNonNull(BlockBehaviour.Properties.of().noCollission().strength(100.0F).noLootTable().replaceable().pushReaction(PushReaction.DESTROY).liquid().mapColor(MapColor.FIRE).randomTicks())));
        return entry;
    }

    public static final class FluidEntry {
        private RegistryObject<FluidType> fluidType;
        private RegistryObject<FlowingFluid> stillFluid;
        private RegistryObject<FlowingFluid> flowingFluid;
        private RegistryObject<LiquidBlock> block;

        private FluidEntry() {
        }

        public FluidType getFluidType() {
            return Objects.requireNonNull(fluidType.get());
        }

        public FlowingFluid getStillFluid() {
            return Objects.requireNonNull(stillFluid.get());
        }

        public FlowingFluid getFlowingFluid() {
            return Objects.requireNonNull(flowingFluid.get());
        }

        public LiquidBlock getBlock() {
            return Objects.requireNonNull(block.get());
        }
    }
}
