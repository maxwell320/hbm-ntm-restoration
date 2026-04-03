package com.hbm.ntm.common.registration;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.RadLavaBlock;
import com.hbm.ntm.common.block.VolcanicLavaBlock;
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

public final class HbmFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, HbmNtmMod.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, HbmNtmMod.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, HbmNtmMod.MOD_ID);
    public static final FluidEntry VOLCANIC_LAVA = registerVolcanicLava();
    public static final FluidEntry RAD_LAVA = registerRadLava();

    private static final ResourceLocation VOLCANIC_LAVA_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/volcanic_lava_still"));
    private static final ResourceLocation VOLCANIC_LAVA_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/volcanic_lava_flowing"));
    private static final ResourceLocation RAD_LAVA_STILL = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/rad_lava_still"));
    private static final ResourceLocation RAD_LAVA_FLOWING = Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/rad_lava_flowing"));

    private HbmFluids() {
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
