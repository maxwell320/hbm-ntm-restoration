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
public class CanisterItem extends Item {
    private static final String FLUID_TAG = "fluid";
    public static final int CAPACITY = 1_000;
    private static final Map<String, Integer> CANISTER_COLORS = createCanisterColors();
    private final boolean filledVariant;
    private final Supplier<Item> emptyItem;
    private final Supplier<Item> filledItem;

    public CanisterItem(final boolean filledVariant, final Supplier<Item> emptyItem, final Supplier<Item> filledItem, final Properties properties) {
        super(properties);
        this.filledVariant = filledVariant;
        this.emptyItem = emptyItem;
        this.filledItem = filledItem;
    }

    public static List<ItemStack> creativeStacks(final Item item) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (final String fluidPath : CANISTER_COLORS.keySet()) {
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

    public static int getColor(final ItemStack stack) {
        final ResourceLocation fluidId = getFluidId(stack);
        if (fluidId == null || !HbmNtmMod.MOD_ID.equals(fluidId.getNamespace())) {
            return 0xFFFFFF;
        }
        return CANISTER_COLORS.getOrDefault(fluidId.getPath(), 0xFFFFFF);
    }

    public static boolean supportsFluid(final ResourceLocation fluidId) {
        return HbmNtmMod.MOD_ID.equals(fluidId.getNamespace()) && CANISTER_COLORS.containsKey(fluidId.getPath());
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
        return new CanisterCapabilityProvider(stack, this.emptyItem, this.filledItem);
    }

    private static Map<String, Integer> createCanisterColors() {
        final Map<String, Integer> colors = new LinkedHashMap<>();
        colors.put("oil", 0x424242);
        colors.put("heavyoil", 0x513F39);
        colors.put("bitumen", 0x5A5877);
        colors.put("smear", 0x624F3B);
        colors.put("heatingoil", 0x694235);
        colors.put("reclaimed", 0xF65723);
        colors.put("petroil", 0x2369F6);
        colors.put("lubricant", 0xF1CC05);
        colors.put("naphtha", 0x5F6D44);
        colors.put("diesel", 0xFF2C2C);
        colors.put("lightoil", 0xB46B52);
        colors.put("kerosene", 0xFF377D);
        colors.put("biofuel", 0x9EB623);
        colors.put("nitan", 0x6B238C);
        colors.put("gasoline", 0x2F7747);
        colors.put("coalgas", 0x2E155F);
        colors.put("fracksol", 0x4F887F);
        colors.put("ethanol", 0xEAFFF3);
        colors.put("crackoil", 0x424242);
        colors.put("coaloil", 0x424242);
        colors.put("naphtha_crack", 0x5F6D44);
        colors.put("lightoil_crack", 0xB46B52);
        colors.put("diesel_crack", 0xFF2C2C);
        colors.put("petroil_leaded", 0x2331F6);
        colors.put("gasoline_leaded", 0x2F775A);
        colors.put("coalgas_leaded", 0x1E155F);
        colors.put("woodoil", 0xBF7E4F);
        colors.put("coalcreosote", 0x285A3F);
        colors.put("seedslurry", 0x7CC35E);
        colors.put("solvent", 0xE4E3EF);
        colors.put("heavyoil_vacuum", 0x513F39);
        colors.put("reformate", 0xD180D6);
        colors.put("lightoil_vacuum", 0xB46B52);
        colors.put("xylene", 0xA380D6);
        colors.put("heatingoil_vacuum", 0x694235);
        colors.put("diesel_reform", 0xFFC500);
        colors.put("diesel_crack_reform", 0xFFC500);
        colors.put("kerosene_reform", 0xFF377D);
        colors.put("oil_ds", 0x424242);
        colors.put("crackoil_ds", 0x424242);
        colors.put("naphtha_ds", 0x5F6D44);
        colors.put("lightoil_ds", 0xB46B52);
        return colors;
    }

    private static final class CanisterCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IFluidHandler> fluidHandler;

        private CanisterCapabilityProvider(final ItemStack stack, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
            this.fluidHandler = LazyOptional.of(() -> new CanisterFluidHandler(stack, emptyItem, filledItem));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable net.minecraft.core.Direction side) {
            return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.fluidHandler);
        }
    }

    private static final class CanisterFluidHandler implements IFluidHandlerItem {
        private ItemStack container;
        private final Supplier<Item> emptyItem;
        private final Supplier<Item> filledItem;

        private CanisterFluidHandler(final ItemStack container, final Supplier<Item> emptyItem, final Supplier<Item> filledItem) {
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
