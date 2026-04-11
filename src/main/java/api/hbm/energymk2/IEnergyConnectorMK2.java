package api.hbm.energymk2;

import net.minecraft.core.Direction;

public interface IEnergyConnectorMK2 {

    default boolean canConnect(final Direction dir) {
        return dir != null;
    }
}
