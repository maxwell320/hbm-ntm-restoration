package com.hbm.ntm.common.item;

import com.hbm.ntm.HbmNtmMod;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class GasTankItem extends Item {
    private static final String FLUID_TAG = "fluid";
    public static final int CAPACITY = 1_000;
    private static final Map<String, GasTankColors> GAS_TANK_COLORS = createGasTankColors();
    private final boolean filledVariant;
    private final Supplier<Item> emptyItem;
    private final Supplier<Item> filledItem;

    public GasTankItem(final boolean filledVariant, final Supplier<Item> emptyItem, final Supplier<Item> filledItem, final Properties properties) {
        super(properties);
        this.filledVariant = filledVariant;
        this.emptyItem = emptyItem;
        this.filledItem = filledItem;
    }

    public static List<ItemStack> creativeStacks(final Item item) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final String fluidPath : GAS_TANK_COLORS.keySet()) {
            final ResourceLocation fluidId = ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, fluidPath);
            if (ForgeRegistries.FLUIDS.containsKey(fluidId)) {
                stacks.add(withFluid(item, fluidId));
            }
        }
        return stacks;
    }

    public static ItemStack withFluid(final Item item, final ResourceLocation fluidId) {
        final ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString(FLUID_TAG, fluidId.toString());
        return stack;
    }

    public static @Nullable ResourceLocation getFluidId(final ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }
        return ResourceLocation.tryParse(stack.getOrCreateTag().getString(FLUID_TAG));
    }

    public static int getBottleColor(final ItemStack stack) {
        final ResourceLocation fluidId = getFluidId(stack);
        if (fluidId == null || !HbmNtmMod.MOD_ID.equals(fluidId.getNamespace())) {
            return 0xFFFFFF;
        }
        final GasTankColors colors = GAS_TANK_COLORS.get(fluidId.getPath());
        return colors == null ? 0xFFFFFF : colors.bottleColor();
    }

    public static int getLabelColor(final ItemStack stack) {
        final ResourceLocation fluidId = getFluidId(stack);
        if (fluidId == null || !HbmNtmMod.MOD_ID.equals(fluidId.getNamespace())) {
            return 0xFFFFFF;
        }
        final GasTankColors colors = GAS_TANK_COLORS.get(fluidId.getPath());
        return colors == null ? 0xFFFFFF : colors.labelColor();
    }

    public static boolean supportsFluid(final ResourceLocation fluidId) {
        return HbmNtmMod.MOD_ID.equals(fluidId.getNamespace()) && GAS_TANK_COLORS.containsKey(fluidId.getPath());
    }

    public static @Nullable Fluid getStoredFluid(final ItemStack stack) {
        final ResourceLocation fluidId = getFluidId(stack);
        if (fluidId == null || !ForgeRegistries.FLUIDS.containsKey(fluidId)) {
            return null;
        }
        return ForgeRegistries.FLUIDS.getValue(fluidId);
    }

    @Override
    public Component getName(final ItemStack stack) {
        if (!this.filledVariant) {
            return super.getName(stack);
        }
        final Fluid fluid = getStoredFluid(stack);
        if (fluid == null) {
            return super.getName(stack);
        }
        return Component.empty().append(super.getName(stack)).append(" ").append(fluid.getFluidType().getDescription());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(final ItemStack stack, final @Nullable CompoundTag nbt) {
        return new GasTankCapabilityProvider(stack, this.emptyItem, this.filledItem);
    }

    private static Map<String, GasTankColors> createGasTankColors() {
        final Map<String, GasTankColors> colors = new LinkedHashMap<>();
        colors.put("deuterium", new GasTankColors(0x0000FF, 0xFFFFFF));
        colors.put("tritium", new GasTankColors(0x000099, 0xE9FFAA));
        colors.put("gas", new GasTankColors(0xFF4545, 0xFFE97F));
        colors.put("petroleum", new GasTankColors(0x5E7CFF, 0xFFE97F));
        colors.put("biogas", new GasTankColors(0xC8FF1F, 0x303030));
        colors.put("hydrogen", new GasTankColors(0x4286F4, 0xFFFFFF));
        colors.put("oxygen", new GasTankColors(0x98BDF9, 0xFFFFFF));
        colors.put("xenon", new GasTankColors(0x8C21FF, 0x303030));
        colors.put("helium3", new GasTankColors(0xFD631F, 0xFFFFFF));
        colors.put("aromatics", new GasTankColors(0x68A09A, 0xEDCF27));
        colors.put("unsaturateds", new GasTankColors(0x628FAE, 0xEDCF27));
        colors.put("syngas", new GasTankColors(0xFFFFFF, 0x131313));
        colors.put("chlorine", new GasTankColors(0xBAB572, 0x887B34));
        colors.put("sourgas", new GasTankColors(0xC9BE0D, 0x303030));
        colors.put("reformgas", new GasTankColors(0x9392FF, 0xFFB992));
        colors.put("phosgene", new GasTankColors(0xCFC4A4, 0x361414));
        colors.put("mustardgas", new GasTankColors(0xBAB572, 0x361414));
        colors.put("helium4", new GasTankColors(0xFD631F, 0xFFFF00));
        return colors;
    }

    private record GasTankColors(int bottleColor, int labelColor) {
    }

    private static final class GasTankCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IFluidHandler> fluidHandler;

        private GasTankCapabilityProvider(final ItemStack stack, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
            this.fluidHandler = LazyOptional.of(() -> new GasTankFluidHandler(stack, emptyItem, filledItem));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable net.minecraft.core.Direction side) {
            return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.fluidHandler);
        }
    }

    private static final class GasTankFluidHandler implements IFluidHandlerItem {
        private ItemStack container;
        private final Supplier<Item> emptyItem;
        private final Supplier<Item> filledItem;

        private GasTankFluidHandler(final ItemStack container, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
            this.container = container;
            this.emptyItem = emptyItem;
            this.filledItem = filledItem;
        }

        @Override
        public ItemStack getContainer() {
            return this.container;
        }

        @Override
        public int getTanks() {
            return 1;
        }

        @Override
        public FluidStack getFluidInTank(final int tank) {
            if (tank != 0) {
                return FluidStack.EMPTY;
            }
            final Fluid fluid = getStoredFluid(this.container);
            return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, CAPACITY);
        }

        @Override
        public int getTankCapacity(final int tank) {
            return tank == 0 ? CAPACITY : 0;
        }

        @Override
        public boolean isFluidValid(final int tank, final FluidStack stack) {
            if (tank != 0 || stack.isEmpty()) {
                return false;
            }
            final ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
            return fluidId != null && supportsFluid(fluidId);
        }

        @Override
        public int fill(final FluidStack resource, final FluidAction action) {
            if (resource.isEmpty() || !this.getFluidInTank(0).isEmpty() || resource.getAmount() < CAPACITY) {
                return 0;
            }
            final ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(resource.getFluid());
            if (fluidId == null || !supportsFluid(fluidId)) {
                return 0;
            }
            if (action.execute()) {
                this.container = withFluid(this.filledItem.get(), fluidId);
            }
            return CAPACITY;
        }

        @Override
        public FluidStack drain(final FluidStack resource, final FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            final FluidStack stored = this.getFluidInTank(0);
            if (stored.isEmpty() || !stored.getFluid().isSame(resource.getFluid()) || resource.getAmount() < CAPACITY) {
                return FluidStack.EMPTY;
            }
            return this.drain(CAPACITY, action);
        }

        @Override
        public FluidStack drain(final int maxDrain, final FluidAction action) {
            final FluidStack stored = this.getFluidInTank(0);
            if (stored.isEmpty() || maxDrain < CAPACITY) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                this.container = new ItemStack(Objects.requireNonNull(this.emptyItem.get()));
            }
            return stored;
        }
    }
}
