package com.hbm.interfaces;

import com.hbm.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ICopiable {

    CompoundTag getSettings(Level level, BlockPos pos);

    void pasteSettings(CompoundTag nbt, int index, Level level, Player player, BlockPos pos);

    default String getSettingsSourceID(final Either<BlockEntity, Block> self) {
        final Block block = self.isLeft() ? self.left().getBlockState().getBlock() : self.right();
        return block.getDescriptionId();
    }

    default String getSettingsSourceDisplay(final Either<BlockEntity, Block> self) {
        final Block block = self.isLeft() ? self.left().getBlockState().getBlock() : self.right();
        return Component.translatable(block.getDescriptionId()).getString();
    }

    default String[] infoForDisplay(final Level level, final BlockPos pos) {
        return null;
    }
}
