package com.hbm.ntm.data;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.BasaltBlockType;
import com.hbm.ntm.common.item.CircuitItemType;
import com.hbm.ntm.common.item.StampItemType;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class HbmRecipeProvider extends RecipeProvider {
    public HbmRecipeProvider(final PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(final @NotNull Consumer<FinishedRecipe> recipeOutput) {
        for (final HbmMaterialDefinition material : HbmMaterials.ordered()) {
            buildNuggetPacking(recipeOutput, material);
            buildDustSmelting(recipeOutput, material);
        }

        buildBasaltRecipes(recipeOutput);
        buildCircuitRecipes(recipeOutput);
        buildStampRecipes(recipeOutput);
        buildFalloutRecipes(recipeOutput);
        buildReadoutToolRecipes(recipeOutput);
    }

    private void buildNuggetPacking(final Consumer<FinishedRecipe> recipeOutput, final HbmMaterialDefinition material) {
        if (!material.hasShape(HbmMaterialShape.INGOT) || !material.hasShape(HbmMaterialShape.NUGGET)) {
            return;
        }

        final ItemLike ingot = Objects.requireNonNull(HbmItems.getMaterialPart(material, HbmMaterialShape.INGOT).get());
        final ItemLike nugget = Objects.requireNonNull(HbmItems.getMaterialPart(material, HbmMaterialShape.NUGGET).get());

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ingot)
            .pattern("NNN")
            .pattern("NNN")
            .pattern("NNN")
            .define('N', nugget)
            .unlockedBy(getHasName(nugget), has(nugget))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, material.itemId(HbmMaterialShape.INGOT) + "_from_" + material.itemId(HbmMaterialShape.NUGGET))));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 9)
            .requires(ingot)
            .unlockedBy(getHasName(ingot), has(ingot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, material.itemId(HbmMaterialShape.NUGGET) + "_from_" + material.itemId(HbmMaterialShape.INGOT))));
    }

    private void buildDustSmelting(final Consumer<FinishedRecipe> recipeOutput, final HbmMaterialDefinition material) {
        if (!material.autoDustSmelting() || !material.hasShape(HbmMaterialShape.DUST) || !material.hasShape(HbmMaterialShape.INGOT)) {
            return;
        }

        final ItemLike dust = Objects.requireNonNull(HbmItems.getMaterialPart(material, HbmMaterialShape.DUST).get());
        final ItemLike ingot = Objects.requireNonNull(HbmItems.getMaterialPart(material, HbmMaterialShape.INGOT).get());
        final List<ItemLike> ingredients = List.of(dust);

        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, ingredients, RecipeCategory.MISC, ingot, 0.1F, 200, material.id(), "_from_smelting");
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, ingredients, RecipeCategory.MISC, ingot, 0.1F, 100, material.id(), "_from_blasting");
    }

    private void buildBasaltRecipes(final Consumer<FinishedRecipe> recipeOutput) {
        final ItemLike basalt = Objects.requireNonNull(HbmBlocks.getBasaltBlock(BasaltBlockType.BASALT).get());
        final ItemLike smoothBasalt = Objects.requireNonNull(HbmBlocks.getBasaltBlock(BasaltBlockType.BASALT_SMOOTH).get());
        final ItemLike polishedBasalt = Objects.requireNonNull(HbmBlocks.getBasaltBlock(BasaltBlockType.BASALT_POLISHED).get());
        final ItemLike basaltBrick = Objects.requireNonNull(HbmBlocks.getBasaltBlock(BasaltBlockType.BASALT_BRICK).get());
        final ItemLike basaltTiles = Objects.requireNonNull(HbmBlocks.getBasaltBlock(BasaltBlockType.BASALT_TILES).get());

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(basalt), RecipeCategory.BUILDING_BLOCKS, smoothBasalt, 0.1F, 200)
            .unlockedBy(getHasName(basalt), has(basalt))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, BasaltBlockType.BASALT_SMOOTH.blockId() + "_from_smelting")));

        buildTwoByTwoBlockRecipe(recipeOutput, polishedBasalt, smoothBasalt, BasaltBlockType.BASALT_POLISHED.blockId());
        buildTwoByTwoBlockRecipe(recipeOutput, basaltBrick, polishedBasalt, BasaltBlockType.BASALT_BRICK.blockId());
        buildTwoByTwoBlockRecipe(recipeOutput, basaltTiles, basaltBrick, BasaltBlockType.BASALT_TILES.blockId());
    }

    private void buildCircuitRecipes(final Consumer<FinishedRecipe> recipeOutput) {
        final ItemLike vacuumTube = Objects.requireNonNull(HbmItems.getCircuit(CircuitItemType.VACUUM_TUBE).get());
        final ItemLike capacitor = Objects.requireNonNull(HbmItems.getCircuit(CircuitItemType.CAPACITOR).get());
        final ItemLike pcb = Objects.requireNonNull(HbmItems.getCircuit(CircuitItemType.PCB).get());
        final ItemLike polymerPlate = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.POLYMER, HbmMaterialShape.PLATE).get());
        final ItemLike tungstenWire = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.TUNGSTEN, HbmMaterialShape.WIRE).get());
        final ItemLike carbonWire = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.CARBON, HbmMaterialShape.WIRE).get());
        final ItemLike niobiumNugget = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.NIOBIUM, HbmMaterialShape.NUGGET).get());
        final ItemLike aluminiumWire = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.ALUMINIUM, HbmMaterialShape.WIRE).get());
        final ItemLike copperWire = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.COPPER, HbmMaterialShape.WIRE).get());
        final ItemLike aluminiumDust = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.ALUMINIUM, HbmMaterialShape.DUST).get());
        final ItemLike copperPlate = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.COPPER, HbmMaterialShape.PLATE).get());
        final ItemLike goldPlate = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.GOLD, HbmMaterialShape.PLATE).get());

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, vacuumTube)
            .pattern("G")
            .pattern("W")
            .pattern("I")
            .define('G', Ingredient.of(Tags.Items.GLASS_PANES))
            .define('W', tungstenWire)
            .define('I', polymerPlate)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_vacuum_tube_from_tungsten")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, vacuumTube)
            .pattern("G")
            .pattern("W")
            .pattern("I")
            .define('G', Ingredient.of(Tags.Items.GLASS_PANES))
            .define('W', carbonWire)
            .define('I', polymerPlate)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_vacuum_tube_from_carbon")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, capacitor)
            .pattern("I")
            .pattern("N")
            .pattern("W")
            .define('I', polymerPlate)
            .define('N', niobiumNugget)
            .define('W', aluminiumWire)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_capacitor_from_aluminium_wire")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, capacitor)
            .pattern("I")
            .pattern("N")
            .pattern("W")
            .define('I', polymerPlate)
            .define('N', niobiumNugget)
            .define('W', copperWire)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_capacitor_from_copper_wire")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, capacitor, 2)
            .pattern("IAI")
            .pattern("W W")
            .define('I', polymerPlate)
            .define('A', aluminiumDust)
            .define('W', aluminiumWire)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_capacitor_double_from_aluminium_wire")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, capacitor, 2)
            .pattern("IAI")
            .pattern("W W")
            .define('I', polymerPlate)
            .define('A', aluminiumDust)
            .define('W', copperWire)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_capacitor_double_from_copper_wire")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, pcb)
            .pattern("I")
            .pattern("P")
            .define('I', polymerPlate)
            .define('P', copperPlate)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_pcb_from_copper_plate")));

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, pcb, 4)
            .pattern("I")
            .pattern("P")
            .define('I', polymerPlate)
            .define('P', goldPlate)
            .unlockedBy(getHasName(polymerPlate), has(polymerPlate))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "circuit_pcb_from_gold_plate")));
    }

    private void buildStampRecipes(final Consumer<FinishedRecipe> recipeOutput) {
        final ItemLike stoneFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.STONE_FLAT).get());
        final ItemLike ironFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.IRON_FLAT).get());
        final ItemLike steelFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.STEEL_FLAT).get());
        final ItemLike titaniumFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.TITANIUM_FLAT).get());
        final ItemLike obsidianFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.OBSIDIAN_FLAT).get());
        final ItemLike deshFlatStamp = Objects.requireNonNull(HbmItems.getStamp(StampItemType.DESH_FLAT).get());
        final ItemLike steelIngot = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.STEEL, HbmMaterialShape.INGOT).get());
        final ItemLike titaniumIngot = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.TITANIUM, HbmMaterialShape.INGOT).get());
        final ItemLike deshIngot = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.DESH, HbmMaterialShape.INGOT).get());
        final ItemLike ferrouraniumIngot = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.FERRORANIUM, HbmMaterialShape.INGOT).get());

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stoneFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.BRICK)
            .define('S', Ingredient.of(Tags.Items.STONE))
            .unlockedBy("has_brick", has(Items.BRICK))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_stone_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, stoneFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.NETHER_BRICK)
            .define('S', Ingredient.of(Tags.Items.STONE))
            .unlockedBy("has_nether_brick", has(Items.NETHER_BRICK))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_stone_flat_from_nether_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ironFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.BRICK)
            .define('S', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_brick", has(Items.BRICK))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_iron_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ironFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.NETHER_BRICK)
            .define('S', Tags.Items.INGOTS_IRON)
            .unlockedBy("has_nether_brick", has(Items.NETHER_BRICK))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_iron_flat_from_nether_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, steelFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.BRICK)
            .define('S', steelIngot)
            .unlockedBy(getHasName(steelIngot), has(steelIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_steel_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, steelFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.NETHER_BRICK)
            .define('S', steelIngot)
            .unlockedBy(getHasName(steelIngot), has(steelIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_steel_flat_from_nether_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, titaniumFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.BRICK)
            .define('S', titaniumIngot)
            .unlockedBy(getHasName(titaniumIngot), has(titaniumIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_titanium_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, titaniumFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.NETHER_BRICK)
            .define('S', titaniumIngot)
            .unlockedBy(getHasName(titaniumIngot), has(titaniumIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_titanium_flat_from_nether_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, obsidianFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.BRICK)
            .define('S', Blocks.OBSIDIAN)
            .unlockedBy(getHasName(Blocks.OBSIDIAN), has(Blocks.OBSIDIAN))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_obsidian_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, obsidianFlatStamp)
            .pattern("III")
            .pattern("SSS")
            .define('I', Items.NETHER_BRICK)
            .define('S', Blocks.OBSIDIAN)
            .unlockedBy(getHasName(Blocks.OBSIDIAN), has(Blocks.OBSIDIAN))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_obsidian_flat_from_nether_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, deshFlatStamp)
            .pattern("BDB")
            .pattern("DSD")
            .pattern("BDB")
            .define('B', Items.BRICK)
            .define('D', deshIngot)
            .define('S', ferrouraniumIngot)
            .unlockedBy(getHasName(deshIngot), has(deshIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_desh_flat_from_brick")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, deshFlatStamp)
            .pattern("BDB")
            .pattern("DSD")
            .pattern("BDB")
            .define('B', Items.NETHER_BRICK)
            .define('D', deshIngot)
            .define('S', ferrouraniumIngot)
            .unlockedBy(getHasName(deshIngot), has(deshIngot))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "stamp_desh_flat_from_nether_brick")));
    }

    private void buildFalloutRecipes(final Consumer<FinishedRecipe> recipeOutput) {
        final ItemLike fallout = Objects.requireNonNull(HbmItems.FALLOUT.get());
        final ItemLike falloutLayer = Objects.requireNonNull(HbmItems.FALLOUT_LAYER.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, falloutLayer, 2)
            .pattern("FF")
            .define('F', fallout)
            .unlockedBy(getHasName(fallout), has(fallout))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "fallout_layer")));
    }

    private void buildReadoutToolRecipes(final Consumer<FinishedRecipe> recipeOutput) {
        final ItemLike ironAnvil = Objects.requireNonNull(HbmBlocks.ANVIL_IRON.get());
        final ItemLike dosimeter = Objects.requireNonNull(HbmItems.DOSIMETER.get());
        final ItemLike geigerCounter = Objects.requireNonNull(HbmItems.GEIGER_COUNTER.get());
        final ItemLike geigerBlock = Objects.requireNonNull(HbmBlocks.GEIGER.get());
        final ItemLike vacuumTube = Objects.requireNonNull(HbmItems.getCircuit(CircuitItemType.VACUUM_TUBE).get());
        final ItemLike berylliumIngot = Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.BERYLLIUM, HbmMaterialShape.INGOT).get());

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ironAnvil)
            .pattern("III")
            .pattern(" B ")
            .pattern("III")
            .define('I', Tags.Items.INGOTS_IRON)
            .define('B', Blocks.IRON_BLOCK)
            .unlockedBy("has_iron_block", has(Blocks.IRON_BLOCK))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "anvil_iron")));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, dosimeter)
            .pattern("WGW")
            .pattern("WCW")
            .pattern("WBW")
            .define('W', Ingredient.of(ItemTags.PLANKS))
            .define('G', Ingredient.of(Tags.Items.GLASS_PANES))
            .define('C', vacuumTube)
            .define('B', berylliumIngot)
            .unlockedBy(getHasName(vacuumTube), has(vacuumTube))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "dosimeter")));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, geigerBlock)
            .requires(geigerCounter)
            .unlockedBy(getHasName(geigerCounter), has(geigerCounter))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "geiger_from_geiger_counter")));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, geigerCounter)
            .requires(geigerBlock)
            .unlockedBy(getHasName(geigerBlock), has(geigerBlock))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "geiger_counter_from_geiger")));
    }

    private void buildTwoByTwoBlockRecipe(final Consumer<FinishedRecipe> recipeOutput, final ItemLike output, final ItemLike ingredient, final String recipeName) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
            .pattern("CC")
            .pattern("CC")
            .define('C', ingredient)
            .unlockedBy(getHasName(ingredient), has(ingredient))
            .save(recipeOutput, Objects.requireNonNull(ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, recipeName)));
    }
}
