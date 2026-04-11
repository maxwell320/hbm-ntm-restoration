package api.hbm.block;

import com.hbm.inventory.RecipesCommon.ComparableStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public interface IToolable {

    boolean onScrew(Level level, Player player, BlockPos pos, Direction side, BlockHitResult hitResult, ToolType tool);

    enum ToolType {
        SCREWDRIVER,
        HAND_DRILL,
        DEFUSER,
        WRENCH,
        TORCH,
        BOLT;

        public final List<ItemStack> stacksForDisplay = new ArrayList<>();
        private static final Map<ComparableStack, ToolType> MAP = new HashMap<>();

        public void register(final ItemStack stack) {
            this.stacksForDisplay.add(stack.copy());
            MAP.clear();
        }

        public static ToolType getType(final ItemStack stack) {
            if (MAP.isEmpty()) {
                for (final ToolType type : values()) {
                    for (final ItemStack tool : type.stacksForDisplay) {
                        MAP.put(new ComparableStack(tool), type);
                    }
                }
            }
            return MAP.get(new ComparableStack(stack));
        }
    }
}
