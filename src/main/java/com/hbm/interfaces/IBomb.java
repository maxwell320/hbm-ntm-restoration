package com.hbm.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IBomb {

    BombReturnCode explode(Level level, BlockPos pos);

    enum BombReturnCode {
        UNDEFINED(false, ""),
        DETONATED(true, "bomb.detonated"),
        TRIGGERED(true, "bomb.triggered"),
        LAUNCHED(true, "bomb.launched"),
        ERROR_MISSING_COMPONENT(false, "bomb.missingComponent"),
        ERROR_INCOMPATIBLE(false, "bomb.incompatible"),
        ERROR_NO_BOMB(false, "bomb.nobomb");

        private final String unloc;
        private final boolean success;

        BombReturnCode(final boolean success, final String unloc) {
            this.unloc = unloc;
            this.success = success;
        }

        public String getUnlocalizedMessage() {
            return this.unloc;
        }

        public boolean wasSuccessful() {
            return this.success;
        }
    }
}
