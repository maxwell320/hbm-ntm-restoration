package com.hbm.ntm.common.assembly;

import com.hbm.ntm.common.item.FluidTankItem;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.recipe.MachineRecipeRegistry;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class HbmAssemblyRecipes {
    private static final int FLUID_PACK_CAPACITY = 32_000;
    private static final AssemblyRecipeRegistry REGISTRY = new AssemblyRecipeRegistry();

    private HbmAssemblyRecipes() {
    }

    public static List<AssemblyRecipe> all() {
        return REGISTRY.all();
    }

    public static Optional<AssemblyRecipe> findRecipe(final List<ItemStack> itemInputs, final FluidStack fluidInput) {
        return REGISTRY.findFirst(recipe -> recipe.matches(itemInputs, fluidInput == null ? FluidStack.EMPTY : fluidInput));
    }

    public static Optional<AssemblyRecipe> findRecipe(final FluidStack fluidInput, final ItemStack... itemInputs) {
        return findRecipe(List.of(itemInputs), fluidInput);
    }

    private static void registerDefaults(final AssemblyRecipeRegistry registry) {
        registry.add(new AssemblyRecipe(
            new ItemStack(item(HbmItems.FLUID_PACK_EMPTY)),
            FluidStack.EMPTY,
            FluidStack.EMPTY,
            40,
            100,
            List.of(
                ingredient(Ingredient.of(materialItem(HbmMaterials.TITANIUM, HbmMaterialShape.PLATE)), materialItem(HbmMaterials.TITANIUM, HbmMaterialShape.PLATE), 4),
                ingredient(
                    Ingredient.of(
                        materialItem(HbmMaterials.POLYMER, HbmMaterialShape.INGOT),
                        materialItem(HbmMaterials.BAKELITE, HbmMaterialShape.INGOT)),
                    materialItem(HbmMaterials.POLYMER, HbmMaterialShape.INGOT),
                    2))));

        for (final ResourceLocation fluidId : ForgeRegistries.FLUIDS.getKeys()) {
            if (!isPackagedFluid(fluidId)) {
                continue;
            }
            final Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
            if (fluid == null) {
                continue;
            }
            final ItemStack packedStack = FluidTankItem.withFluid(item(HbmItems.FLUID_PACK_FULL), fluidId);
            final FluidStack fluidStack = new FluidStack(fluid, FLUID_PACK_CAPACITY);
            registry.add(new AssemblyRecipe(
                packedStack,
                fluidStack,
                FluidStack.EMPTY,
                40,
                100,
                List.of(ingredient(Ingredient.of(item(HbmItems.FLUID_PACK_EMPTY)), item(HbmItems.FLUID_PACK_EMPTY), 1))));
            registry.add(new AssemblyRecipe(
                new ItemStack(item(HbmItems.FLUID_PACK_EMPTY)),
                FluidStack.EMPTY,
                fluidStack,
                40,
                100,
                List.of(exact(packedStack, 1))));
        }

        registry.add(new AssemblyRecipe(
            new ItemStack(item(HbmItems.MACHINE_SHREDDER)),
            FluidStack.EMPTY,
            FluidStack.EMPTY,
            100,
            100,
            List.of(
                ingredient(Ingredient.of(materialItem(HbmMaterials.STEEL, HbmMaterialShape.PLATE)), materialItem(HbmMaterials.STEEL, HbmMaterialShape.PLATE), 8),
                ingredient(Ingredient.of(materialItem(HbmMaterials.COPPER, HbmMaterialShape.PLATE)), materialItem(HbmMaterials.COPPER, HbmMaterialShape.PLATE), 4),
                ingredient(Ingredient.of(item(HbmItems.MOTOR)), item(HbmItems.MOTOR), 2))));
    }

    private static boolean isPackagedFluid(final ResourceLocation fluidId) {
        return !fluidId.getPath().startsWith("flowing_") && FluidTankItem.supportsFluid(fluidId, false);
    }

    private static IngredientRequirement ingredient(final Ingredient ingredient, final Item displayItem, final int count) {
        return new IngredientRequirement(ingredient, new ItemStack(displayItem), count);
    }

    private static ExactStackRequirement exact(final ItemStack stack, final int count) {
        return new ExactStackRequirement(stack, count);
    }

    private static Item item(final RegistryObject<Item> registryObject) {
        return Objects.requireNonNull(registryObject.get());
    }

    private static Item materialItem(final HbmMaterialDefinition material, final HbmMaterialShape shape) {
        return item(HbmItems.getMaterialPart(material, shape));
    }

    public sealed interface AssemblyRequirement permits IngredientRequirement, ExactStackRequirement {
        boolean matches(ItemStack stack);

        ItemStack displayStack();

        int count();
    }

    public record IngredientRequirement(Ingredient ingredient, ItemStack displayStack, int count) implements AssemblyRequirement {
        public IngredientRequirement {
            ingredient = Objects.requireNonNull(ingredient, "ingredient");
            displayStack = displayStack.copy();
            displayStack.setCount(Math.max(1, count));
            count = Math.max(1, count);
        }

        @Override
        public boolean matches(final ItemStack stack) {
            return !stack.isEmpty() && stack.getCount() >= this.count && this.ingredient.test(stack);
        }

        @Override
        public ItemStack displayStack() {
            return this.displayStack.copy();
        }
    }

    public record ExactStackRequirement(ItemStack displayStack, int count) implements AssemblyRequirement {
        public ExactStackRequirement {
            displayStack = displayStack.copy();
            displayStack.setCount(Math.max(1, count));
            count = Math.max(1, count);
        }

        @Override
        public boolean matches(final ItemStack stack) {
            return !stack.isEmpty() && stack.getCount() >= this.count && ItemStack.isSameItemSameTags(stack, this.displayStack);
        }

        @Override
        public ItemStack displayStack() {
            return this.displayStack.copy();
        }
    }

    public record AssemblyRecipe(ItemStack output, FluidStack fluidInput, FluidStack fluidOutput, int duration, long consumption,
                                 List<AssemblyRequirement> itemInputs) {
        public AssemblyRecipe {
            output = output.copy();
            fluidInput = fluidInput.copy();
            fluidOutput = fluidOutput.copy();
            duration = Math.max(1, duration);
            consumption = Math.max(0L, consumption);
            itemInputs = List.copyOf(itemInputs);
        }

        public boolean matches(final List<ItemStack> itemInputs, final FluidStack fluidInput) {
            if (!matchesFluid(fluidInput)) {
                return false;
            }
            final List<AssemblyRequirement> remaining = new ArrayList<>(this.itemInputs);
            for (final ItemStack input : itemInputs) {
                if (input == null || input.isEmpty()) {
                    continue;
                }
                boolean matched = false;
                for (int i = 0; i < remaining.size(); i++) {
                    if (remaining.get(i).matches(input)) {
                        remaining.remove(i);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
            return remaining.isEmpty();
        }

        public ItemStack outputCopy() {
            return this.output.copy();
        }

        public FluidStack fluidInputCopy() {
            return this.fluidInput.copy();
        }

        public FluidStack fluidOutputCopy() {
            return this.fluidOutput.copy();
        }

        private boolean matchesFluid(final FluidStack input) {
            if (this.fluidInput.isEmpty()) {
                return true;
            }
            return !input.isEmpty() && input.getAmount() >= this.fluidInput.getAmount() && input.isFluidEqual(this.fluidInput);
        }
    }

    private static final class AssemblyRecipeRegistry extends MachineRecipeRegistry<AssemblyRecipe> {
        @Override
        protected void registerDefaults() {
            HbmAssemblyRecipes.registerDefaults(this);
        }

        private void add(final AssemblyRecipe recipe) {
            this.addRecipe(recipe);
        }
    }
}
