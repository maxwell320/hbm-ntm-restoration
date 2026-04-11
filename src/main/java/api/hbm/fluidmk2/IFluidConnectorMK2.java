package api.hbm.fluidmk2;

import com.hbm.inventory.fluid.FluidType;
import net.minecraft.core.Direction;

public interface IFluidConnectorMK2 {

    default boolean canConnect(final FluidType type, final Direction dir) {
        return dir != null;
    }
}
