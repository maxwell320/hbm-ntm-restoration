package api.hbm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IRadioControllable {

    String[] getVariables(Level level, BlockPos pos);

    void receiveSignal(Level level, BlockPos pos, String channel, String signal);
}
