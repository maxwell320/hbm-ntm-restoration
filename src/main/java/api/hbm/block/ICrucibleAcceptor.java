package api.hbm.block;

import com.hbm.inventory.material.Mats.MaterialStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public interface ICrucibleAcceptor {

    boolean canAcceptPartialPour(Level level, BlockPos pos, Vec3 hitPos, Direction side, MaterialStack stack);

    MaterialStack pour(Level level, BlockPos pos, Vec3 hitPos, Direction side, MaterialStack stack);

    boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack);

    MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack);
}
