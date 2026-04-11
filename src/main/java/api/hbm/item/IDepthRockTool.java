package api.hbm.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IDepthRockTool {

    boolean canBreakRock(Level level, Player player, ItemStack tool, BlockState block, BlockPos pos);
}
