package cofh.api.energy;

import net.minecraft.core.Direction;

public interface IEnergyReceiver extends IEnergyConnection {

    int receiveEnergy(Direction from, int maxReceive, boolean simulate);

    int getEnergyStored(Direction from);

    int getMaxEnergyStored(Direction from);
}
