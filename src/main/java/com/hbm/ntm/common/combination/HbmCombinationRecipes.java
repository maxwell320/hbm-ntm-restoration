package com.hbm.ntm.common.combination;

import com.hbm.ntm.common.item.BriquetteItemType;
import com.hbm.ntm.common.item.ChunkOreItemType;
import com.hbm.ntm.common.item.CokeItemType;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.recipe.MachineRecipeRegistry;
import com.hbm.ntm.common.registration.HbmFluids;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class HbmCombinationRecipes {
    private static final CombinationRecipeRegistry REGISTRY = new CombinationRecipeRegistry();

    private HbmCombinationRecipes() {
    }

    public static List<CombinationRecipe> all() {
        return REGISTRY.all();
    }

    public static Optional<CombinationRecipe> findRecipe(final ItemStack inputStack) {
        if (inputStack.isEmpty()) {
            return Optional.empty();
        }
        return REGISTRY.findFirst(recipe -> recipe.matches(inputStack));
    }

    public static CombinationOutput getOutput(final ItemStack inputStack) {
        return findRecipe(inputStack).map(CombinationRecipe::outputCopy).orElse(CombinationOutput.EMPTY);
    }

    private static void registerDefaults(final CombinationRecipeRegistry registry) {
        registry.add(Ingredient.of(Items.COAL), new ItemStack(item(HbmItems.getCoke(CokeItemType.COAL))), fluid(HbmFluids.COALCREOSOTE, 100));
        registry.add(Ingredient.of(item(HbmMaterials.COAL, HbmMaterialShape.DUST)), new ItemStack(item(HbmItems.getCoke(CokeItemType.COAL))), fluid(HbmFluids.COALCREOSOTE, 100));
        registry.add(Ingredient.of(item(HbmItems.getBriquette(BriquetteItemType.COAL))), new ItemStack(item(HbmItems.getCoke(CokeItemType.COAL))), fluid(HbmFluids.COALCREOSOTE, 150));

        registry.add(Ingredient.of(item(HbmMaterials.LIGNITE, HbmMaterialShape.GEM)), new ItemStack(item(HbmItems.getCoke(CokeItemType.LIGNITE))), fluid(HbmFluids.COALCREOSOTE, 50));
        registry.add(Ingredient.of(item(HbmMaterials.LIGNITE, HbmMaterialShape.DUST)), new ItemStack(item(HbmItems.getCoke(CokeItemType.LIGNITE))), fluid(HbmFluids.COALCREOSOTE, 50));
        registry.add(Ingredient.of(item(HbmItems.getBriquette(BriquetteItemType.LIGNITE))), new ItemStack(item(HbmItems.getCoke(CokeItemType.LIGNITE))), fluid(HbmFluids.COALCREOSOTE, 100));

        registry.add(Ingredient.of(item(HbmMaterials.CHLOROCALCITE, HbmMaterialShape.DUST)), new ItemStack(item(HbmMaterials.CALCIUM, HbmMaterialShape.DUST)), fluid(HbmFluids.CHLORINE, 250));
        registry.add(Ingredient.of(item(HbmMaterials.MOLYSITE, HbmMaterialShape.DUST)), new ItemStack(Items.IRON_INGOT), fluid(HbmFluids.CHLORINE, 250));
        registry.add(Ingredient.of(Items.GLOWSTONE_DUST), new ItemStack(item(HbmMaterials.SULFUR, HbmMaterialShape.DUST)), fluid(HbmFluids.CHLORINE, 100));
        registry.add(Ingredient.of(item(HbmMaterials.SODALITE, HbmMaterialShape.GEM)), new ItemStack(item(HbmMaterials.SODIUM, HbmMaterialShape.DUST)), fluid(HbmFluids.CHLORINE, 100));
        registry.add(Ingredient.of(item(HbmItems.getChunkOre(ChunkOreItemType.CRYOLITE))), new ItemStack(item(HbmMaterials.ALUMINIUM, HbmMaterialShape.DUST)), fluid(HbmFluids.LYE, 150));
        registry.add(Ingredient.of(item(HbmMaterials.SODIUM, HbmMaterialShape.DUST)), ItemStack.EMPTY, fluid(HbmFluids.SODIUM, 100));
        registry.add(Ingredient.of(item(HbmMaterials.LIMESTONE, HbmMaterialShape.DUST)), new ItemStack(item(HbmMaterials.CALCIUM, HbmMaterialShape.DUST)), fluid(HbmFluids.CARBONDIOXIDE, 50));

        registry.add(Ingredient.of(ItemTags.LOGS), new ItemStack(Items.CHARCOAL), fluid(HbmFluids.WOODOIL, 250));
        registry.add(Ingredient.of(ItemTags.SAPLINGS), new ItemStack(Objects.requireNonNull(HbmItems.POWDER_ASH.get())), fluid(HbmFluids.WOODOIL, 50));
        registry.add(Ingredient.of(item(HbmItems.getBriquette(BriquetteItemType.WOOD))), new ItemStack(Items.CHARCOAL), fluid(HbmFluids.WOODOIL, 500));

        registry.add(Ingredient.of(Items.SUGAR_CANE), new ItemStack(Items.SUGAR, 2), fluid(HbmFluids.ETHANOL, 50));
        registry.add(Ingredient.of(Blocks.CLAY.asItem()), new ItemStack(Blocks.BRICKS), FluidStack.EMPTY);
    }

    private static Item item(final HbmMaterialDefinition material, final HbmMaterialShape shape) {
        return Objects.requireNonNull(HbmItems.getMaterialPart(material, shape).get());
    }

    private static Item item(final RegistryObject<Item> item) {
        return Objects.requireNonNull(item.get());
    }

    private static FluidStack fluid(final HbmFluids.FluidEntry entry, final int amount) {
        return amount <= 0 ? FluidStack.EMPTY : new FluidStack(entry.getStillFluid(), amount);
    }

    private static final class CombinationRecipeRegistry extends MachineRecipeRegistry<CombinationRecipe> {
        @Override
        protected void registerDefaults() {
            HbmCombinationRecipes.registerDefaults(this);
        }

        private void add(final Ingredient input, final ItemStack itemOutput, final FluidStack fluidOutput) {
            this.addRecipe(new CombinationRecipe(input, itemOutput.copy(), fluidOutput.copy()));
        }
    }
}
