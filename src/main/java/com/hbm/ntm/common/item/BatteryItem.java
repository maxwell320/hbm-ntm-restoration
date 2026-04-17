package com.hbm.ntm.common.item;

import com.hbm.ntm.common.energy.HbmEnergy;
import com.hbm.ntm.common.energy.HbmEnergyStorage;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BatteryItem extends Item {
    private static final String ENERGY_TAG = "energy";
    private final int capacity;
    private final int chargeRate;
    private final int dischargeRate;

    public BatteryItem(final int capacity, final int chargeRate, final int dischargeRate, final Properties properties) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.chargeRate = chargeRate;
        this.dischargeRate = dischargeRate;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getChargeRate() {
        return this.chargeRate;
    }

    public int getDischargeRate() {
        return this.dischargeRate;
    }

    public int getStoredEnergy(final ItemStack stack) {
        if (!stack.hasTag()) {
            return 0;
        }
        return Mth.clamp(stack.getOrCreateTag().getInt(ENERGY_TAG), 0, this.capacity);
    }

    public ItemStack withStoredEnergy(final ItemStack stack, final int energy) {
        stack.getOrCreateTag().putInt(ENERGY_TAG, Mth.clamp(energy, 0, this.capacity));
        return stack;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(final ItemStack stack, final @Nullable CompoundTag nbt) {
        return new BatteryCapabilityProvider(stack, this);
    }

    @Override
    public boolean isBarVisible(final ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(final ItemStack stack) {
        return Math.round(13.0F * this.getStoredEnergy(stack) / (float) this.capacity);
    }

    @Override
    public int getBarColor(final ItemStack stack) {
        final float ratio = this.getStoredEnergy(stack) / (float) this.capacity;
        return Mth.hsvToRgb(Math.max(0.0F, ratio) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        tooltip.add(Component.literal("Energy stored: " + this.getStoredEnergy(stack) + "/" + this.capacity + HbmEnergy.UNIT));
        tooltip.add(Component.literal("Charge rate: " + this.chargeRate + HbmEnergy.UNIT + "/t"));
        tooltip.add(Component.literal("Discharge rate: " + this.dischargeRate + HbmEnergy.UNIT + "/t"));
    }

    private static final class BatteryCapabilityProvider implements ICapabilityProvider {
        private final LazyOptional<IEnergyStorage> energyStorage;

        private BatteryCapabilityProvider(final ItemStack stack, final BatteryItem item) {
            this.energyStorage = LazyOptional.of(() -> new BatteryStackEnergyStorage(stack, item));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(final @NotNull Capability<T> cap, final @Nullable Direction side) {
            return ForgeCapabilities.ENERGY.orEmpty(cap, this.energyStorage);
        }
    }

    private static final class BatteryStackEnergyStorage extends HbmEnergyStorage {
        private final ItemStack stack;
        private final BatteryItem item;

        private BatteryStackEnergyStorage(final ItemStack stack, final BatteryItem item) {
            super(item.getCapacity(), item.getChargeRate(), item.getDischargeRate(), item.getStoredEnergy(stack));
            this.stack = stack;
            this.item = item;
        }

        @Override
        protected void onEnergyChanged() {
            this.item.withStoredEnergy(this.stack, this.getEnergyStored());
        }
    }
}
