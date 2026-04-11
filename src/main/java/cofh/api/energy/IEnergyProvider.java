package cofh.api.energy;

import net.minecraft.core.Direction;

public interface IEnergyProvider extends IEnergyConnection {

    int extractEnergy(Direction from, int maxExtract, boolean simulate);

    int getEnergyStored(Direction from);

    int getMaxEnergyStored(Direction from);
}
