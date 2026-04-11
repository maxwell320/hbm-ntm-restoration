package api.hbm.conveyor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IEnterableBlock {

    boolean canItemEnter(Level level, BlockPos pos, Direction direction, IConveyorItem entity);

    void onItemEnter(Level level, BlockPos pos, Direction direction, IConveyorItem entity);

    boolean canPackageEnter(Level level, BlockPos pos, Direction direction, IConveyorPackage entity);

    void onPackageEnter(Level level, BlockPos pos, Direction direction, IConveyorPackage entity);
}
