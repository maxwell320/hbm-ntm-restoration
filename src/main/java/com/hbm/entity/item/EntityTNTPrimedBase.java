package com.hbm.entity.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public abstract class EntityTNTPrimedBase extends PrimedTnt {

    protected EntityTNTPrimedBase(final EntityType<? extends PrimedTnt> entityType, final Level level) {
        super(entityType, level);
    }
}
