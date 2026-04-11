package api.hbm.item;

import com.hbm.util.ArmorRegistry.HazardClass;
import java.util.ArrayList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IGasMask {

    ArrayList<HazardClass> getBlacklist(ItemStack stack, LivingEntity entity);

    ItemStack getFilter(ItemStack stack, LivingEntity entity);

    boolean isFilterApplicable(ItemStack stack, LivingEntity entity, ItemStack filter);

    void installFilter(ItemStack stack, LivingEntity entity, ItemStack filter);

    void damageFilter(ItemStack stack, LivingEntity entity, int damage);
}
