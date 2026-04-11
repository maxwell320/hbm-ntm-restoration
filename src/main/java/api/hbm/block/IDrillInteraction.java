package api.hbm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated
public interface IDrillInteraction {

    boolean canBreak(Level level, BlockPos pos, BlockState state, IMiningDrill drill);

    ItemStack extractResource(Level level, BlockPos pos, BlockState state, IMiningDrill drill);

    float getRelativeHardness(Level level, BlockPos pos, BlockState state, IMiningDrill drill);
}
