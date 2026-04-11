package api.hbm.block;

import com.hbm.entity.item.EntityTNTPrimedBase;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IFuckingExplode {

    void explodeEntity(Level level, Vec3 pos, EntityTNTPrimedBase entity);
}
