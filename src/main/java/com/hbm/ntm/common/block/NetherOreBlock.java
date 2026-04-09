package com.hbm.ntm.common.block;

import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class NetherOreBlock extends Block {
    private final NetherOreType type;

    public NetherOreBlock(final NetherOreType type, final BlockBehaviour.Properties properties) {
        super(properties);
        this.type = type;
    }

    public NetherOreType oreType() {
        return this.type;
    }

    @Override
    @Deprecated
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final ItemStack tool = builder.getOptionalParameter(LootContextParams.TOOL);
        final int fortune = tool == null ? 0 : EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);

        return switch (this.type) {
            case SULFUR -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.SULFUR, HbmMaterialShape.DUST).get()), count));
            }
            case FIRE -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(Objects.requireNonNull(HbmItems.getMaterialPart(HbmMaterials.RED_PHOSPHORUS, HbmMaterialShape.DUST).get()), count));
            }
            case COAL -> {
                final int count = 2 + fortune + builder.getLevel().getRandom().nextInt(fortune + 2);
                yield List.of(new ItemStack(net.minecraft.world.item.Items.COAL, count));
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
            case SULFUR, FIRE -> Mth.nextInt(random, 1, 5);
            case COAL -> Mth.nextInt(random, 0, 2);
            case SCHRABIDIUM -> Mth.nextInt(random, 10, 50);
            default -> 0;
        };
    }
}
