package api.hbm.energymk2;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

public interface IEnergyConnectorBlock {

    boolean canConnect(BlockGetter level, BlockPos pos, Direction dir);
}
