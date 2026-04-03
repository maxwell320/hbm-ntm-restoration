package com.hbm.ntm.common.block;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SellafieldOreBlock extends SellafieldSlakedBlock {
    private final SellafieldOreType type;

    public SellafieldOreBlock(final SellafieldOreType type, final BlockBehaviour.Properties properties) {
        super(properties);
        this.type = Objects.requireNonNull(type);
    }

    public SellafieldOreType type() {
        return type;
    }

    @Override
    public int getExpDrop(final @NotNull BlockState state, final @NotNull LevelReader reader, final @NotNull RandomSource random, final @NotNull BlockPos pos,
                          final int fortune, final int silkTouch) {
        if (silkTouch > 0) {
            return 0;
        }
        return switch (type) {
            case DIAMOND, EMERALD, RADGEM -> 3 + random.nextInt(5);
            case URANIUM_SCORCHED, SCHRABIDIUM -> 0;
        };
    }

    public boolean dropsSelf() {
        return switch (type) {
            case URANIUM_SCORCHED, SCHRABIDIUM -> true;
            case DIAMOND, EMERALD, RADGEM -> false;
        };
    }

    public net.minecraft.world.item.Item dropItem() {
        return switch (type) {
            case DIAMOND -> Items.DIAMOND;
            case EMERALD -> Items.EMERALD;
            case URANIUM_SCORCHED, SCHRABIDIUM -> asItem();
            case RADGEM -> Objects.requireNonNull(com.hbm.ntm.common.registration.HbmItems.GEM_RAD.get());
        };
    }
}
