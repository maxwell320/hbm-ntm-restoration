package com.hbm.ntm.common.menu;

import com.hbm.ntm.common.item.MachineUpgradeItem;
import java.util.EnumSet;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

@SuppressWarnings("null")
public class UpgradeSlotItemHandler extends FilteredSlotItemHandler {
    private final EnumSet<MachineUpgradeItem.UpgradeType> allowedTypes;

    public UpgradeSlotItemHandler(final IItemHandler itemHandler,
                                  final int index,
                                  final int xPosition,
                                  final int yPosition,
                                  final MachineUpgradeItem.UpgradeType... allowedTypes) {
        super(itemHandler, index, xPosition, yPosition, (slot, stack) -> true);
        final EnumSet<MachineUpgradeItem.UpgradeType> parsed = EnumSet.noneOf(MachineUpgradeItem.UpgradeType.class);
        if (allowedTypes != null) {
            for (final MachineUpgradeItem.UpgradeType type : allowedTypes) {
                if (type != null) {
                    parsed.add(type);
                }
            }
        }
        this.allowedTypes = parsed;
    }

    @Override
    public boolean mayPlace(final ItemStack stack) {
        if (!super.mayPlace(stack)) {
            return false;
        }
        if (!(stack.getItem() instanceof MachineUpgradeItem upgrade)) {
            return false;
        }
        return this.allowedTypes.isEmpty() || this.allowedTypes.contains(upgrade.type());
    }
}
