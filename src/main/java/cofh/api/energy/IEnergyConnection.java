package cofh.api.energy;

import net.minecraft.core.Direction;

public interface IEnergyConnection {

    boolean canConnectEnergy(Direction from);
}
