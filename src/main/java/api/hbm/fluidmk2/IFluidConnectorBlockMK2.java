package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;

public interface IFluidConnectorBlockMK2 {

    boolean canConnect(FluidType type, BlockGetter level, BlockPos pos, Direction dir);
}
