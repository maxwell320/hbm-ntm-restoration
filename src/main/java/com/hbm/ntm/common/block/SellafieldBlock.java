package com.hbm.ntm.common.block;

import com.hbm.ntm.common.item.SellafieldBlockItem;
import com.hbm.ntm.common.radiation.ChunkRadiationManager;
import com.hbm.ntm.common.registration.HbmBlocks;
import com.hbm.ntm.common.registration.HbmItems;
import com.hbm.ntm.common.registration.HbmMobEffects;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class SellafieldBlock extends Block {
    public static final IntegerProperty VARIANT = SellafieldSlakedBlock.VARIANT;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5);
    public static final int MAX_LEVEL = 5;
    private static final int[] LEVEL_COLORS = new int[] {
        0x4C7939,
        0x418223,
        0x338C0E,
        0x1C9E00,
        0x02B200,
        0x00D300
    };

    public SellafieldBlock(final BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(VARIANT, 0).setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT, LEVEL);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(final BlockPlaceContext context) {
        return stateFor(context.getClickedPos(), 0);
    }

    public BlockState stateFor(final BlockPos pos, final int level) {
        return defaultBlockState().setValue(VARIANT, SellafieldSlakedBlock.variantFor(pos)).setValue(LEVEL, Mth.clamp(level, 0, MAX_LEVEL));
    }

    @Override
    public void stepOn(final @NotNull Level level, final @NotNull BlockPos pos, final @NotNull BlockState state, final @NotNull Entity entity) {
        if (!level.isClientSide() && entity instanceof final LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(HbmMobEffects.RADIATION.get(), 30 * 20, amplifierFor(state.getValue(LEVEL))));
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void randomTick(final @NotNull BlockState state, final @NotNull ServerLevel level, final @NotNull BlockPos pos, final @NotNull RandomSource random) {
        final int currentLevel = state.getValue(LEVEL);
        ChunkRadiationManager.incrementRad(level, pos.getX(), pos.getY(), pos.getZ(), 0.5F * (currentLevel + 1));

        if (random.nextInt(currentLevel == 0 ? 25 : 15) == 0) {
            if (currentLevel > 0) {
                level.setBlock(pos, stateFor(pos, currentLevel - 1), Block.UPDATE_ALL);
            } else {
                level.setBlock(pos, ((SellafieldSlakedBlock) Objects.requireNonNull(HbmBlocks.SELLAFIELD_SLAKED.get())).stateFor(pos, 0), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        return List.of(SellafieldBlockItem.withLevel(Objects.requireNonNull(HbmItems.SELLAFIELD.get()), state.getValue(LEVEL)));
    }

    public static int colorFromState(final @NotNull BlockState state) {
        return colorFromLevel(state.getValue(LEVEL));
    }

    public static int colorFromLevel(final int level) {
        return LEVEL_COLORS[Mth.clamp(level, 0, MAX_LEVEL)];
    }

    private static int amplifierFor(final int level) {
        return level < MAX_LEVEL ? level : level * 2;
    }
}
