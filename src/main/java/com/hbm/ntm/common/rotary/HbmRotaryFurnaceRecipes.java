package com.hbm.ntm.common.rotary;

import com.hbm.ntm.common.item.CokeItemType;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.recipe.CountedIngredient;
import com.hbm.ntm.common.recipe.MachineRecipeRegistry;
import com.hbm.ntm.common.recipe.MachineRecipeUtil;
import com.hbm.ntm.common.registration.HbmFluids;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("null")
public final class HbmRotaryFurnaceRecipes {
    public static final int QUANTA_PER_INGOT = 72;

    private static final RotaryRecipeRegistry REGISTRY = new RotaryRecipeRegistry();
    private static final Ingredient ANY_COKE = Ingredient.of(
        Items.COAL,
        Objects.requireNonNull(HbmItems.getCoke(CokeItemType.COAL).get()),
        Objects.requireNonNull(HbmItems.getCoke(CokeItemType.LIGNITE).get()),
        Objects.requireNonNull(HbmItems.getCoke(CokeItemType.PETROLEUM).get()));
    private static final Ingredient ANY_IRON_INGOT = Ingredient.of(Tags.Items.INGOTS_IRON);

    private HbmRotaryFurnaceRecipes() {
    }

    public static List<RotaryRecipe> all() {
        return REGISTRY.all();
    }

    public static Optional<RotaryRecipe> findRecipe(final FluidStack fluid, final ItemStack first, final ItemStack second, final ItemStack third) {
        return REGISTRY.findFirst(recipe -> recipe.matches(fluid, List.of(stackOrEmpty(first), stackOrEmpty(second), stackOrEmpty(third))));
    }

    private static RotaryRecipe recipe(final int duration,
                                       final int steamPerTick,
                                       final HbmMaterialDefinition outputMaterial,
                                       final int outputIngots,
                                       final FluidStack fluidRequirement,
                                       final CountedIngredient... ingredients) {
        return new RotaryRecipe(
            List.of(ingredients),
            fluidRequirement == null ? FluidStack.EMPTY : fluidRequirement.copy(),
            Math.max(1, duration),
            Math.max(0, steamPerTick),
            Objects.requireNonNull(outputMaterial),
            Math.max(1, outputIngots) * QUANTA_PER_INGOT);
    }

    private static CountedIngredient ingredient(final ItemLike item, final int count) {
        return new CountedIngredient(Ingredient.of(item), count);
    }

    private static CountedIngredient ingredient(final Ingredient ingredient, final int count) {
        return new CountedIngredient(ingredient, count);
    }

    private static Item materialItem(final HbmMaterialDefinition material, final HbmMaterialShape shape) {
        return Objects.requireNonNull(HbmItems.getMaterialPart(material, shape).get());
    }

    private static Optional<Item> optionalSimpleItem(final String id) {
        try {
            return Optional.of(Objects.requireNonNull(HbmItems.getSimpleItem(id).get()));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private static ItemStack stackOrEmpty(final ItemStack stack) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public record RotaryRecipe(List<CountedIngredient> ingredients,
                               FluidStack fluidRequirement,
                               int duration,
                               int steamPerTick,
                               HbmMaterialDefinition outputMaterial,
                               int outputAmount) {
        public RotaryRecipe {
            ingredients = List.copyOf(ingredients);
            fluidRequirement = fluidRequirement.copy();
            duration = Math.max(1, duration);
            steamPerTick = Math.max(0, steamPerTick);
            outputAmount = Math.max(1, outputAmount);
        }

        public boolean matches(final FluidStack fluid, final List<ItemStack> inputs) {
            return MachineRecipeUtil.matchesFluidRequirement(fluid, this.fluidRequirement)
                && MachineRecipeUtil.matchesShapelessIngredients(inputs, this.ingredients);
        }

        public FluidStack fluidRequirementCopy() {
            return this.fluidRequirement.copy();
        }
    }

    private static final class RotaryRecipeRegistry extends MachineRecipeRegistry<RotaryRecipe> {
        @Override
        protected void registerDefaults() {
            final List<RotaryRecipe> recipes = new ArrayList<>();

            recipes.add(recipe(100, 100,
                HbmMaterials.STEEL, 1,
                FluidStack.EMPTY,
                ingredient(ANY_IRON_INGOT, 1),
                ingredient(Items.COAL, 1)));

            recipes.add(recipe(100, 100,
                HbmMaterials.STEEL, 1,
                FluidStack.EMPTY,
                ingredient(ANY_IRON_INGOT, 1),
                ingredient(ANY_COKE, 1)));

            recipes.add(recipe(200, 25,
                HbmMaterials.STEEL, 2,
                FluidStack.EMPTY,
                ingredient(materialItem(HbmMaterials.IRON, HbmMaterialShape.FRAGMENT), 9),
                ingredient(Items.COAL, 1)));

            recipes.add(recipe(200, 25,
                HbmMaterials.STEEL, 3,
                FluidStack.EMPTY,
                ingredient(materialItem(HbmMaterials.IRON, HbmMaterialShape.FRAGMENT), 9),
                ingredient(ANY_COKE, 1)));

            recipes.add(recipe(400, 25,
                HbmMaterials.STEEL, 4,
                FluidStack.EMPTY,
                ingredient(materialItem(HbmMaterials.IRON, HbmMaterialShape.FRAGMENT), 9),
                ingredient(ANY_COKE, 1),
                ingredient(materialItem(HbmMaterials.FLUX, HbmMaterialShape.DUST), 1)));

            optionalSimpleItem("powder_desh_ready").ifPresent(deshReady ->
                recipes.add(recipe(100, 200,
                    HbmMaterials.DESH, 1,
                    new FluidStack(HbmFluids.LIGHTOIL.getStillFluid(), 100),
                    ingredient(deshReady, 1))));

            recipes.add(recipe(200, 100,
                HbmMaterials.GUNMETAL, 4,
                FluidStack.EMPTY,
                ingredient(materialItem(HbmMaterials.COPPER, HbmMaterialShape.INGOT), 3),
                ingredient(materialItem(HbmMaterials.ALUMINIUM, HbmMaterialShape.INGOT), 1)));

            recipes.add(recipe(200, 400,
                HbmMaterials.WEAPONSTEEL, 1,
                new FluidStack(HbmFluids.GAS_COKER.getStillFluid(), 100),
                ingredient(materialItem(HbmMaterials.STEEL, HbmMaterialShape.INGOT), 1),
                ingredient(materialItem(HbmMaterials.FLUX, HbmMaterialShape.DUST), 2)));

            recipes.add(recipe(200, 400,
                HbmMaterials.SATURNITE, 2,
                new FluidStack(HbmFluids.REFORMGAS.getStillFluid(), 250),
                ingredient(materialItem(HbmMaterials.DURA_STEEL, HbmMaterialShape.DUST), 4),
                ingredient(materialItem(HbmMaterials.COPPER, HbmMaterialShape.DUST), 1)));

            recipes.add(recipe(200, 300,
                HbmMaterials.SATURNITE, 4,
                new FluidStack(HbmFluids.REFORMGAS.getStillFluid(), 250),
                ingredient(materialItem(HbmMaterials.DURA_STEEL, HbmMaterialShape.DUST), 4),
                ingredient(materialItem(HbmMaterials.COPPER, HbmMaterialShape.DUST), 1),
                ingredient(materialItem(HbmMaterials.BORAX, HbmMaterialShape.DUST), 1)));

            recipes.add(recipe(100, 400,
                HbmMaterials.ALUMINIUM, 2,
                new FluidStack(HbmFluids.SODIUM_ALUMINATE.getStillFluid(), 150)));

            recipes.add(recipe(40, 200,
                HbmMaterials.ALUMINIUM, 3,
                new FluidStack(HbmFluids.SODIUM_ALUMINATE.getStillFluid(), 150),
                ingredient(materialItem(HbmMaterials.FLUX, HbmMaterialShape.DUST), 2)));

            this.addAllRecipes(recipes);
        }
    }
}
