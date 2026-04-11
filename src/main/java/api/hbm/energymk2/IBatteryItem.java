package api.hbm.energymk2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IBatteryItem {

    void chargeBattery(ItemStack stack, long charge);

    void setCharge(ItemStack stack, long charge);

    void dischargeBattery(ItemStack stack, long charge);

    long getCharge(ItemStack stack);

    long getMaxCharge(ItemStack stack);

    long getChargeRate(ItemStack stack);

    long getDischargeRate(ItemStack stack);

    default String getChargeTagName() {
        return "charge";
    }

    static String getChargeTagName(final ItemStack stack) {
        return ((IBatteryItem) stack.getItem()).getChargeTagName();
    }

    static ItemStack emptyBattery(final ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof final IBatteryItem batteryItem) {
            final ItemStack copy = stack.copy();
            copy.getOrCreateTag().putLong(batteryItem.getChargeTagName(), 0L);
            return copy;
        }
        return ItemStack.EMPTY;
    }

    static ItemStack emptyBattery(final Item item) {
        return item instanceof IBatteryItem ? emptyBattery(new ItemStack(item)) : ItemStack.EMPTY;
    }
}
