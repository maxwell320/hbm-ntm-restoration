package com.hbm.ntm.common.item;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.fluid.HbmFluidType;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
public class FluidTankItem extends Item {
    private static final String FLUID_TAG = "fluid";
    private static final Set<String> LEAD_ONLY_FLUIDS = Set.of("death", "redmud");
    private static final Set<String> NO_CONTAINER_FLUIDS = new LinkedHashSet<>(List.of(
        "wastefluid",
        "wastegas",
        "spentsteam",
        "plasma_dt",
        "plasma_hd",
        "plasma_ht",
        "plasma_xm",
        "plasma_bf",
        "plasma_dh3",
        "chlorocalcite_solution",
        "chlorocalcite_mix",
        "chlorocalcite_cleaned",
        "potassium_chloride",
        "calcium_chloride",
        "calcium_solution",
        "smoke",
        "smoke_leaded",
        "smoke_poison"
    ));
    private final boolean filledVariant;
    private final boolean leadVariant;
    private final int capacity;
    private final Supplier<Item> emptyItem;
    private final Supplier<Item> filledItem;

    public FluidTankItem(final boolean filledVariant, final boolean leadVariant, final int capacity, final Supplier<Item> emptyItem, final Supplier<Item> filledItem,
                         final Properties properties) {
        super(properties);
        this.filledVariant = filledVariant;
        this.leadVariant = leadVariant;
        this.capacity = capacity;
        this.emptyItem = emptyItem;
        this.filledItem = filledItem;
    }

    public static List<ItemStack> creativeStacks(final Item item, final boolean leadVariant) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final ResourceLocation fluidId : ForgeRegistries.FLUIDS.getKeys()) {
            if (!supportsFluid(fluidId, leadVariant)) {
                continue;
            }
            stacks.add(withFluid(item, fluidId));
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

    public static int getColor(final ItemStack stack) {
        final Fluid fluid = getStoredFluid(stack);
        if (fluid == null) {
            return 0xFFFFFF;
        }
        final var fluidType = fluid.getFluidType();
        return fluidType instanceof HbmFluidType hbmFluidType ? hbmFluidType.getTintColor() : 0xFFFFFF;
    }

    public static boolean supportsFluid(final ResourceLocation fluidId, final boolean leadVariant) {
        if (!HbmNtmMod.MOD_ID.equals(fluidId.getNamespace())) {
            return false;
        }
        final String path = fluidId.getPath();
        if (NO_CONTAINER_FLUIDS.contains(path)) {
            return false;
        }
        if (!leadVariant && LEAD_ONLY_FLUIDS.contains(path)) {
            return false;
        }
        return ForgeRegistries.FLUIDS.containsKey(fluidId);
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
        return Component.translatable(this.getDescriptionId(), fluid.getFluidType().getDescription());
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(final ItemStack stack, final @Nullable CompoundTag nbt) {
        return new FluidTankCapabilityProvider(stack, this.leadVariant, this.capacity, this.emptyItem, this.filledItem);
    }

    private static final class FluidTankCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IFluidHandler> fluidHandler;

        private FluidTankCapabilityProvider(final ItemStack stack, final boolean leadVariant, final int capacity, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
            this.fluidHandler = LazyOptional.of(() -> new FluidTankFluidHandler(stack, leadVariant, capacity, emptyItem, filledItem));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable net.minecraft.core.Direction side) {
            return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.fluidHandler);
        }
    }

    private static final class FluidTankFluidHandler implements IFluidHandlerItem {
        private ItemStack container;
        private final boolean leadVariant;
        private final int capacity;
        private final Supplier<Item> emptyItem;
        private final Supplier<Item> filledItem;

        private FluidTankFluidHandler(final ItemStack container, final boolean leadVariant, final int capacity, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
            this.container = container;
            this.leadVariant = leadVariant;
            this.capacity = capacity;
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
            return fluid == null ? FluidStack.EMPTY : new FluidStack(fluid, this.capacity);
        }

        @Override
        public int getTankCapacity(final int tank) {
            return tank == 0 ? this.capacity : 0;
        }

        @Override
        public boolean isFluidValid(final int tank, final FluidStack stack) {
            if (tank != 0 || stack.isEmpty()) {
                return false;
            }
            final ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(stack.getFluid());
            return fluidId != null && supportsFluid(fluidId, this.leadVariant);
        }

        @Override
        public int fill(final FluidStack resource, final FluidAction action) {
            if (resource.isEmpty() || !this.getFluidInTank(0).isEmpty() || resource.getAmount() < this.capacity) {
                return 0;
            }
            final ResourceLocation fluidId = ForgeRegistries.FLUIDS.getKey(resource.getFluid());
            if (fluidId == null || !supportsFluid(fluidId, this.leadVariant)) {
                return 0;
            }
            if (action.execute()) {
                this.container = withFluid(this.filledItem.get(), fluidId);
            }
            return this.capacity;
        }

        @Override
        public FluidStack drain(final FluidStack resource, final FluidAction action) {
            if (resource.isEmpty()) {
                return FluidStack.EMPTY;
            }
            final FluidStack stored = this.getFluidInTank(0);
            if (stored.isEmpty() || !stored.getFluid().isSame(resource.getFluid()) || resource.getAmount() < this.capacity) {
                return FluidStack.EMPTY;
            }
            return this.drain(this.capacity, action);
        }

        @Override
        public FluidStack drain(final int maxDrain, final FluidAction action) {
            final FluidStack stored = this.getFluidInTank(0);
            if (stored.isEmpty() || maxDrain < this.capacity) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                this.container = new ItemStack(Objects.requireNonNull(this.emptyItem.get()));
            }
            return stored;
        }
    }
}
