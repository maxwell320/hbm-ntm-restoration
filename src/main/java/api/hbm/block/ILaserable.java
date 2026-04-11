package api.hbm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface ILaserable {

    void addEnergy(Level level, BlockPos pos, long energy, Direction direction);
}
