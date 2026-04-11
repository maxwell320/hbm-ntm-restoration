package cofh.api.energy;

import net.minecraft.core.Direction;

public interface IEnergyHandler extends IEnergyProvider, IEnergyReceiver {

    @Override
    int receiveEnergy(Direction from, int maxReceive, boolean simulate);

    @Override
    int extractEnergy(Direction from, int maxExtract, boolean simulate);

    @Override
    int getEnergyStored(Direction from);

    @Override
    int getMaxEnergyStored(Direction from);
}
