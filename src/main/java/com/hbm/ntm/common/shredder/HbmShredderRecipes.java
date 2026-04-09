package com.hbm.ntm.common.shredder;

import com.hbm.ntm.common.block.MaterialBlockType;
import com.hbm.ntm.common.block.StoneResourceType;
import com.hbm.ntm.common.item.SellafieldBlockItem;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public final class HbmShredderRecipes {

    private static final Map<Item, ItemStack> RECIPES = new HashMap<>();
    private static boolean initialized;

    private HbmShredderRecipes() {
    }

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerDefaults();
        registerMaterialRecipes();
    }

    public static @Nullable ItemStack getResult(final ItemStack input) {
        if (input.isEmpty()) {
            return null;
        }

        final @Nullable ItemStack specialResult = getSpecialResult(input);
        if (specialResult != null) {
            return specialResult;
        }

        final ItemStack result = RECIPES.get(input.getItem());
        if (result != null) {
            return result.copy();
        }

        return HbmItems.SCRAP.get().getDefaultInstance();
    }

    public static Map<Item, ItemStack> getAllRecipes() {
        ensureInitialized();
        return Map.copyOf(RECIPES);
    }

    private static void setRecipe(final Item input, final ItemStack output) {
        if (input != null && !output.isEmpty()) {
            RECIPES.putIfAbsent(input, output);
        }
    }

    private static void setRecipe(final ItemStack input, final ItemStack output) {
        if (!input.isEmpty()) {
            setRecipe(input.getItem(), output);
        }
    }

    private static void registerDefaults() {
        setRecipe(Items.GLOWSTONE, new ItemStack(Items.GLOWSTONE_DUST, 4));

        setRecipe(Items.STONE, new ItemStack(Items.GRAVEL, 1));
        setRecipe(Items.COBBLESTONE, new ItemStack(Items.GRAVEL, 1));
        setRecipe(Blocks.STONE_BRICKS.asItem(), new ItemStack(Items.GRAVEL, 1));
        setRecipe(Items.GRAVEL, new ItemStack(Items.SAND, 1));

        setRecipe(Blocks.BRICKS.asItem(), new ItemStack(Items.CLAY_BALL, 4));
        setRecipe(Blocks.BRICK_STAIRS.asItem(), new ItemStack(Items.CLAY_BALL, 3));
        setRecipe(Items.FLOWER_POT, new ItemStack(Items.CLAY_BALL, 3));
        setRecipe(Items.BRICK, new ItemStack(Items.CLAY_BALL, 1));

        setRecipe(Blocks.SANDSTONE.asItem(), new ItemStack(Items.SAND, 4));
        setRecipe(Blocks.SANDSTONE_STAIRS.asItem(), new ItemStack(Items.SAND, 6));

        setRecipe(Blocks.CLAY.asItem(), new ItemStack(Items.CLAY_BALL, 4));
        setRecipe(Blocks.TERRACOTTA.asItem(), new ItemStack(Items.CLAY_BALL, 4));

        setRecipe(Blocks.TNT.asItem(), new ItemStack(Items.GUNPOWDER, 5));

        setRecipe(Items.SUGAR_CANE, new ItemStack(Items.SUGAR, 3));
        setRecipe(Items.APPLE, new ItemStack(Items.SUGAR, 1));
        setRecipe(Items.CARROT, new ItemStack(Items.SUGAR, 1));

        for (final Item dye : new Item[] {
            Blocks.WHITE_WOOL.asItem(), Blocks.ORANGE_WOOL.asItem(), Blocks.MAGENTA_WOOL.asItem(),
            Blocks.LIGHT_BLUE_WOOL.asItem(), Blocks.YELLOW_WOOL.asItem(), Blocks.LIME_WOOL.asItem(),
            Blocks.PINK_WOOL.asItem(), Blocks.GRAY_WOOL.asItem(), Blocks.LIGHT_GRAY_WOOL.asItem(),
            Blocks.CYAN_WOOL.asItem(), Blocks.PURPLE_WOOL.asItem(), Blocks.BLUE_WOOL.asItem(),
            Blocks.BROWN_WOOL.asItem(), Blocks.GREEN_WOOL.asItem(), Blocks.RED_WOOL.asItem(),
            Blocks.BLACK_WOOL.asItem()
        }) {
            setRecipe(dye, new ItemStack(Items.STRING, 4));
        }

        for (final Item terracotta : new Item[] {
            Blocks.WHITE_TERRACOTTA.asItem(), Blocks.ORANGE_TERRACOTTA.asItem(), Blocks.MAGENTA_TERRACOTTA.asItem(),
            Blocks.LIGHT_BLUE_TERRACOTTA.asItem(), Blocks.YELLOW_TERRACOTTA.asItem(), Blocks.LIME_TERRACOTTA.asItem(),
            Blocks.PINK_TERRACOTTA.asItem(), Blocks.GRAY_TERRACOTTA.asItem(), Blocks.LIGHT_GRAY_TERRACOTTA.asItem(),
            Blocks.CYAN_TERRACOTTA.asItem(), Blocks.PURPLE_TERRACOTTA.asItem(), Blocks.BLUE_TERRACOTTA.asItem(),
            Blocks.BROWN_TERRACOTTA.asItem(), Blocks.GREEN_TERRACOTTA.asItem(), Blocks.RED_TERRACOTTA.asItem(),
            Blocks.BLACK_TERRACOTTA.asItem()
        }) {
            setRecipe(terracotta, new ItemStack(Items.CLAY_BALL, 4));
        }

        final @Nullable Item sawdust = HbmItems.POWDER_SAWDUST.get();
        if (sawdust != null) {
            for (final Item log : new Item[] {
                Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG,
                Items.ACACIA_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG,
                Items.STRIPPED_OAK_LOG, Items.STRIPPED_SPRUCE_LOG, Items.STRIPPED_BIRCH_LOG,
                Items.STRIPPED_JUNGLE_LOG, Items.STRIPPED_ACACIA_LOG, Items.STRIPPED_DARK_OAK_LOG
            }) {
                setRecipe(log, new ItemStack(sawdust, 4));
            }
            for (final Item plank : new Item[] {
                Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS,
                Items.ACACIA_PLANKS, Items.DARK_OAK_PLANKS, Items.MANGROVE_PLANKS, Items.CHERRY_PLANKS,
                Items.BAMBOO_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS
            }) {
                setRecipe(plank, new ItemStack(sawdust, 1));
            }
        }

        for (final Item sapling : new Item[] {
            Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BIRCH_SAPLING, Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING, Items.DARK_OAK_SAPLING, Items.MANGROVE_PROPAGULE, Items.CHERRY_SAPLING
        }) {
            setRecipe(sapling, new ItemStack(Items.STICK, 1));
        }

        registerLegacySpecialRecipes();

        // TODO: poison powder, magic powder, enchanted book, fermented spider eye
    }

    private static void registerLegacySpecialRecipes() {
        final @Nullable Item quartzDust = getShapeItem(HbmMaterials.QUARTZ, HbmMaterialShape.DUST);
        if (quartzDust != null) {
            setRecipe(Blocks.QUARTZ_BLOCK.asItem(), new ItemStack(quartzDust, 4));
            setRecipe(Blocks.CHISELED_QUARTZ_BLOCK.asItem(), new ItemStack(quartzDust, 4));
            setRecipe(Blocks.QUARTZ_PILLAR.asItem(), new ItemStack(quartzDust, 4));
            setRecipe(Blocks.QUARTZ_STAIRS.asItem(), new ItemStack(quartzDust, 3));
            setRecipe(Blocks.QUARTZ_SLAB.asItem(), new ItemStack(quartzDust, 2));
            setRecipe(Items.QUARTZ, new ItemStack(quartzDust, 1));
            setRecipe(Blocks.NETHER_QUARTZ_ORE.asItem(), new ItemStack(quartzDust, 2));
        }

        final @Nullable Item limestoneDust = getShapeItem(HbmMaterials.LIMESTONE, HbmMaterialShape.DUST);
        if (limestoneDust != null) {
            setRecipe(HbmBlocks.getStoneResource(StoneResourceType.LIMESTONE).get().asItem(), new ItemStack(limestoneDust, 4));
        }

        setRecipe(HbmBlocks.SELLAFIELD_SLAKED.get().asItem(), new ItemStack(Items.GRAVEL, 1));

        for (final Item skull : new Item[] {
            Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.PLAYER_HEAD, Items.ZOMBIE_HEAD, Items.CREEPER_HEAD
        }) {
            setRecipe(skull, new ItemStack(HbmItems.BIOMASS.get(), 4));
        }

        final @Nullable Item schraraniumIngot = getShapeItem(HbmMaterials.SCHRARANIUM, HbmMaterialShape.INGOT);
        final @Nullable Item schrabidiumNugget = getShapeItem(HbmMaterials.SCHRABIDIUM, HbmMaterialShape.NUGGET);
        if (schraraniumIngot != null && schrabidiumNugget != null) {
            setRecipe(schraraniumIngot, new ItemStack(schrabidiumNugget, 2));
        }

        registerLegacyFragmentRecipes();
        registerLegacyCrystalRecipes();
        registerLegacyRecyclingRecipes();
    }

    private static void registerLegacyFragmentRecipes() {
        setMaterialRecipe(HbmMaterials.NEODYMIUM, HbmMaterialShape.FRAGMENT, HbmMaterials.NEODYMIUM, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.COBALT, HbmMaterialShape.FRAGMENT, HbmMaterials.COBALT, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.NIOBIUM, HbmMaterialShape.FRAGMENT, HbmMaterials.NIOBIUM, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.CERIUM, HbmMaterialShape.FRAGMENT, HbmMaterials.CERIUM, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.LANTHANIUM, HbmMaterialShape.FRAGMENT, HbmMaterials.LANTHANIUM, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.ACTINIUM, HbmMaterialShape.FRAGMENT, HbmMaterials.ACTINIUM, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.BORON, HbmMaterialShape.FRAGMENT, HbmMaterials.BORON, HbmMaterialShape.DUST_TINY, 1);
        setMaterialRecipe(HbmMaterials.METEORITE, HbmMaterialShape.FRAGMENT, HbmMaterials.METEORITE, HbmMaterialShape.DUST_TINY, 1);
    }

    private static void registerLegacyCrystalRecipes() {
        setMaterialRecipe(HbmMaterials.COAL, HbmMaterialShape.CRYSTAL, HbmMaterials.COAL, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.IRON, HbmMaterialShape.CRYSTAL, HbmMaterials.IRON, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.GOLD, HbmMaterialShape.CRYSTAL, HbmMaterials.GOLD, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.LAPIS, HbmMaterialShape.CRYSTAL, HbmMaterials.LAPIS, HbmMaterialShape.DUST, 8);
        setMaterialRecipe(HbmMaterials.DIAMOND, HbmMaterialShape.CRYSTAL, HbmMaterials.DIAMOND, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.URANIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.URANIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.PLUTONIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.PLUTONIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.TH232, HbmMaterialShape.CRYSTAL, HbmMaterials.TH232, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.TITANIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.TITANIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.SULFUR, HbmMaterialShape.CRYSTAL, HbmMaterials.SULFUR, HbmMaterialShape.DUST, 8);
        setMaterialRecipe(HbmMaterials.KNO, HbmMaterialShape.CRYSTAL, HbmMaterials.KNO, HbmMaterialShape.DUST, 8);
        setMaterialRecipe(HbmMaterials.COPPER, HbmMaterialShape.CRYSTAL, HbmMaterials.COPPER, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.TUNGSTEN, HbmMaterialShape.CRYSTAL, HbmMaterials.TUNGSTEN, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.ALUMINIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.ALUMINIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.FLUORITE, HbmMaterialShape.CRYSTAL, HbmMaterials.FLUORITE, HbmMaterialShape.DUST, 8);
        setMaterialRecipe(HbmMaterials.BERYLLIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.BERYLLIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.LEAD, HbmMaterialShape.CRYSTAL, HbmMaterials.LEAD, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.SCHRABIDIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.SCHRABIDIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.RED_PHOSPHORUS, HbmMaterialShape.CRYSTAL, HbmMaterials.RED_PHOSPHORUS, HbmMaterialShape.DUST, 8);
        setMaterialRecipe(HbmMaterials.LITHIUM, HbmMaterialShape.CRYSTAL, HbmMaterials.LITHIUM, HbmMaterialShape.DUST, 3);
        setMaterialRecipe(HbmMaterials.STARMETAL, HbmMaterialShape.CRYSTAL, HbmMaterials.STARMETAL, HbmMaterialShape.DUST, 6);
        setMaterialRecipe(HbmMaterials.COBALT, HbmMaterialShape.CRYSTAL, HbmMaterials.COBALT, HbmMaterialShape.DUST, 3);

        final @Nullable Item redstoneCrystal = getShapeItem(HbmMaterials.REDSTONE, HbmMaterialShape.CRYSTAL);
        if (redstoneCrystal != null) {
            setRecipe(redstoneCrystal, new ItemStack(Items.REDSTONE, 8));
        }

        final @Nullable Item schraraniumCrystal = getShapeItem(HbmMaterials.SCHRARANIUM, HbmMaterialShape.CRYSTAL);
        final @Nullable Item schrabidiumNugget = getShapeItem(HbmMaterials.SCHRABIDIUM, HbmMaterialShape.NUGGET);
        if (schraraniumCrystal != null && schrabidiumNugget != null) {
            setRecipe(schraraniumCrystal, new ItemStack(schrabidiumNugget, 3));
        }
    }

    private static void registerLegacyRecyclingRecipes() {
        setMaterialRecipe(HbmMaterials.COPPER, HbmMaterialShape.DENSE_WIRE, HbmMaterials.RED_COPPER, HbmMaterialShape.DUST, 1);
        setMaterialRecipe(HbmMaterials.ADVANCED_ALLOY, HbmMaterialShape.DENSE_WIRE, HbmMaterials.ADVANCED_ALLOY, HbmMaterialShape.DUST, 1);
        setMaterialRecipe(HbmMaterials.GOLD, HbmMaterialShape.DENSE_WIRE, HbmMaterials.GOLD, HbmMaterialShape.DUST, 1);
        setMaterialRecipe(HbmMaterials.TUNGSTEN, HbmMaterialShape.DENSE_WIRE, HbmMaterials.TUNGSTEN, HbmMaterialShape.DUST, 1);
        setMaterialRecipe(HbmMaterials.MAGNETIZED_TUNGSTEN, HbmMaterialShape.DENSE_WIRE, HbmMaterials.MAGNETIZED_TUNGSTEN, HbmMaterialShape.DUST, 1);
        setMaterialRecipe(HbmMaterials.STEEL, HbmMaterialShape.PIPE, HbmMaterials.STEEL, HbmMaterialShape.DUST, 27);

        final @Nullable Item redCopperDust = getShapeItem(HbmMaterials.RED_COPPER, HbmMaterialShape.DUST);
        if (redCopperDust != null) {
            setRecipe(HbmItems.COIL_COPPER_TORUS.get(), new ItemStack(redCopperDust, 2));
        }

        final @Nullable Item advancedAlloyDust = getShapeItem(HbmMaterials.ADVANCED_ALLOY, HbmMaterialShape.DUST);
        if (advancedAlloyDust != null) {
            setRecipe(HbmItems.COIL_ADVANCED_TORUS.get(), new ItemStack(advancedAlloyDust, 2));
        }

        final @Nullable Item goldDust = getShapeItem(HbmMaterials.GOLD, HbmMaterialShape.DUST);
        if (goldDust != null) {
            setRecipe(HbmItems.COIL_GOLD_TORUS.get(), new ItemStack(goldDust, 2));
        }

        final @Nullable Item ironDust = getShapeItem(HbmMaterials.IRON, HbmMaterialShape.DUST);
        if (ironDust != null) {
            setRecipe(Blocks.ANVIL.asItem(), new ItemStack(ironDust, 31));
            setRecipe(Blocks.CHIPPED_ANVIL.asItem(), new ItemStack(ironDust, 31));
            setRecipe(Blocks.DAMAGED_ANVIL.asItem(), new ItemStack(ironDust, 31));
        }
    }

    private static void registerMaterialRecipes() {
        for (final HbmMaterialDefinition material : HbmMaterials.ordered()) {
            final @Nullable Item dust = getShapeItem(material, HbmMaterialShape.DUST);
            if (dust == null) {
                continue;
            }

            final @Nullable Item ingot = getShapeItem(material, HbmMaterialShape.INGOT);
            if (ingot != null) {
                setRecipe(ingot, new ItemStack(dust, 1));
            }

            final @Nullable Item plate = getShapeItem(material, HbmMaterialShape.PLATE);
            if (plate != null) {
                setRecipe(plate, new ItemStack(dust, 1));
            }

            final @Nullable Item gem = getShapeItem(material, HbmMaterialShape.GEM);
            if (gem != null) {
                setRecipe(gem, new ItemStack(dust, 1));
            }

            final @Nullable Item crystal = getShapeItem(material, HbmMaterialShape.CRYSTAL);
            if (crystal != null) {
                setRecipe(crystal, new ItemStack(dust, 1));
            }

            final @Nullable MaterialBlockType blockType = getMaterialBlockType(material);
            if (blockType != null) {
                final int blockCount = ingot != null || gem != null ? 9 : 4;
                setRecipe(HbmBlocks.getMaterialBlock(blockType).get().asItem(), new ItemStack(dust, blockCount));
            }
        }
    }

    private static void setMaterialRecipe(final HbmMaterialDefinition inputMaterial, final HbmMaterialShape inputShape,
                                           final HbmMaterialDefinition outputMaterial, final HbmMaterialShape outputShape,
                                           final int count) {
        final @Nullable Item input = getShapeItem(inputMaterial, inputShape);
        final @Nullable Item output = getShapeItem(outputMaterial, outputShape);
        if (input != null && output != null) {
            setRecipe(input, new ItemStack(output, count));
        }
    }

    private static @Nullable MaterialBlockType getMaterialBlockType(final HbmMaterialDefinition material) {
        for (final MaterialBlockType type : MaterialBlockType.values()) {
            if (type.material() == material) {
                return type;
            }
        }
        return null;
    }

    private static @Nullable ItemStack getSpecialResult(final ItemStack input) {
        if (input.is(HbmItems.SELLAFIELD.get())) {
            return new ItemStack(HbmItems.SCRAP_NUCLEAR.get(), switch (SellafieldBlockItem.getLevel(input)) {
                case 1 -> 2;
                case 2 -> 3;
                case 3 -> 5;
                case 4 -> 7;
                case 5 -> 15;
                default -> 1;
            });
        }
        return null;
    }

    private static @Nullable Item getShapeItem(final HbmMaterialDefinition material, final HbmMaterialShape shape) {
        if (!material.shapes().contains(shape)) {
            return null;
        }
        final String itemId = material.itemId(shape);
        if (itemId == null) {
            return null;
        }
        try {
            return HbmItems.getMaterialPart(material, shape).get();
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }
}
