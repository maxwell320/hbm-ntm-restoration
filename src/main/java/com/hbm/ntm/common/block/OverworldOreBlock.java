package com.hbm.ntm.common.block;

import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class OverworldOreBlock extends Block {
    private final OverworldOreType type;

    public OverworldOreBlock(final OverworldOreType type, final BlockBehaviour.Properties properties) {
        super(properties);
        this.type = type;
    }

    public OverworldOreType oreType() {
        return this.type;
    }

    @Override
    @Deprecated
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        final int fortune = tool == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);

        return switch (this.type) {
            case FLUORITE -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.FLUORITE, HbmMaterialShape.DUST).get()), count));
            }
            case NITER -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.KNO, HbmMaterialShape.DUST).get()), count));
            }
            case LIGNITE -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.LIGNITE, HbmMaterialShape.GEM).get()), count));
            }
            case CINNEBAR -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.CINNABAR, HbmMaterialShape.GEM).get()), count));
            }
            case RARE -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.RARE_EARTH, HbmMaterialShape.CRYSTAL).get()), count));
            }
            case COLTAN -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.COLTAN, HbmMaterialShape.FRAGMENT).get()), count));
            }
            case COBALT -> {
                final int count = 1 + fortune + builder.getLevel().getRandom().nextInt(fortune + 1);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.COBALT, HbmMaterialShape.FRAGMENT).get()), count));
            }
            case ALEXANDRITE -> {
                final int count = 1 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.SULFUR, HbmMaterialShape.CRYSTAL).get()), count));
            }
            case ASBESTOS -> {
                final int count = 1 + fortune + builder.getLevel().getRandom().nextInt(fortune + 1);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.ASBESTOS, HbmMaterialShape.INGOT).get()), count));
            }
            default -> List.of(new ItemStack(this));
        };
    }

    @Override
    public int getExpDrop(final BlockState state, final net.minecraft.world.level.LevelAccessor level, final RandomSource random, final BlockPos pos, final int fortuneLevel, final int silkTouchLevel) {
        if (silkTouchLevel > 0) {
            return 0;
        }
        return switch (this.type) {
            case FLUORITE, NITER, CINNEBAR, ALEXANDRITE -> Mth.nextInt(random, 1, 5);
            case RARE, COLTAN -> Mth.nextInt(random, 2, 5);
            case LIGNITE -> Mth.nextInt(random, 0, 2);
            case SCHRABIDIUM -> Mth.nextInt(random, 10, 50);
            default -> 0;
        };
    }

    @Override
    public void playerDestroy(final @NotNull Level level, final @NotNull Player player, final @NotNull BlockPos pos, final @NotNull BlockState state,
                              final @Nullable BlockEntity blockEntity, final @NotNull ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
        replaceWithAsbestosGas(level, pos);
    }

    @Override
    public void wasExploded(final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull Explosion explosion) {
        super.wasExploded(level, pos, explosion);
        replaceWithAsbestosGas(level, pos);
    }

    private void replaceWithAsbestosGas(final @NotNull Level level, final @NotNull BlockPos pos) {
        if (!level.isClientSide() && this.type == OverworldOreType.ASBESTOS) {
            level.setBlock(pos, Objects.requireNonNull(HbmBlocks.GAS_ASBESTOS.get().defaultBlockState()), Block.UPDATE_ALL);
        }
    }
}
