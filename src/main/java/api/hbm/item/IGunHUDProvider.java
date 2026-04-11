package api.hbm.item;

import com.hbm.util.Tuple.Pair;
import com.hbm.util.Tuple.Triplet;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IGunHUDProvider {

    List<Triplet<Double, Integer, Integer>> getStatusBars(ItemStack stack, Player player);

    List<Pair<ResourceLocation, String>> getAmmoInfo(ItemStack stack, Player player);
}
