package api.hbm.energymk2;

import api.hbm.tile.ILoadedTile;
import com.hbm.util.CompatEnergyControl;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public interface IEnergyHandlerMK2 extends IEnergyConnectorMK2, ILoadedTile {

    long getPower();

    void setPower(long power);

    long getMaxPower();

    boolean particleDebug = false;

    default Vec3 getDebugParticlePosMK2(final BlockPos pos) {
        return Vec3.atCenterOf(pos).add(0.0D, 0.5D, 0.0D);
    }

    default void provideInfoForECMK2(final CompoundTag data) {
        data.putLong(CompatEnergyControl.L_ENERGY_HE, this.getPower());
        data.putLong(CompatEnergyControl.L_CAPACITY_HE, this.getMaxPower());
    }
}
