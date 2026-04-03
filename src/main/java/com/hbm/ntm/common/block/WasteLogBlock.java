package com.hbm.ntm.common.block;

import com.hbm.ntm.common.registration.HbmItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

public class WasteLogBlock extends RotatedPillarBlock {
    public WasteLogBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(final @NotNull BlockState state, final @NotNull LootParams.Builder builder) {
        final RandomSource random = builder.getLevel().getRandom();
        if (random.nextInt(1000) == 0) {
            return List.of(new ItemStack(Objects.requireNonNull(HbmItems.BURNT_BARK.get())));
        }
        return List.of(new ItemStack(Items.CHARCOAL, 2 + random.nextInt(3)));
    }
}
