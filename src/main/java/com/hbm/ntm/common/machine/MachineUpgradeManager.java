package com.hbm.ntm.common.machine;

import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import com.hbm.ntm.common.item.MachineUpgradeItem;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public class MachineUpgradeManager {
    private final MachineBlockEntity owner;
    private ItemStack[] cachedSlots = new ItemStack[0];
    private final Map<MachineUpgradeItem.UpgradeType, Integer> upgrades = new EnumMap<>(MachineUpgradeItem.UpgradeType.class);
    private MachineUpgradeItem.UpgradeType mutexType;

    public MachineUpgradeManager(final MachineBlockEntity owner) {
        this.owner = owner;
    }

    public void refresh() {
        final IUpgradeInfoProvider provider = this.owner;
        if (provider.getValidUpgrades().isEmpty()) {
            this.upgrades.clear();
            this.cachedSlots = new ItemStack[0];
            this.mutexType = null;
        }

        final ItemStack[] upgradeSlots = this.captureUpgradeSlots();
        if (Arrays.equals(upgradeSlots, this.cachedSlots)) {
            return;
        }

        this.cachedSlots = upgradeSlots.clone();
        this.upgrades.clear();
        this.mutexType = null;

        final Map<MachineUpgradeItem.UpgradeType, Integer> validUpgrades = provider.getValidUpgrades();
        if (validUpgrades == null || validUpgrades.isEmpty()) {
            return;
        }

        for (final ItemStack stack : upgradeSlots) {
            if (stack.isEmpty() || !(stack.getItem() instanceof final MachineUpgradeItem upgrade)) {
                continue;
            }
            if (!validUpgrades.containsKey(upgrade.type())) {
                continue;
            }
            if (upgrade.type().isMutex()) {
                if (this.mutexType == null || upgrade.type().ordinal() > this.mutexType.ordinal()) {
                    if (this.mutexType != null) {
                        this.upgrades.remove(this.mutexType);
                    }
                    this.mutexType = upgrade.type();
                    this.upgrades.put(upgrade.type(), 1);
                }
                continue;
            }
            final int previous = this.upgrades.getOrDefault(upgrade.type(), 0);
            final int max = Math.max(0, validUpgrades.get(upgrade.type()));
            this.upgrades.put(upgrade.type(), Math.min(previous + Math.max(0, upgrade.tier()), max));
        }
    }

    public int getLevel(final MachineUpgradeItem.UpgradeType type) {
        this.refresh();
        return this.upgrades.getOrDefault(type, 0);
    }

    public boolean hasUpgrade(final MachineUpgradeItem.UpgradeType type) {
        return this.getLevel(type) > 0;
    }

    public Map<MachineUpgradeItem.UpgradeType, Integer> getLevels() {
        this.refresh();
        return Map.copyOf(this.upgrades);
    }

    public void invalidate() {
        this.cachedSlots = new ItemStack[0];
    }

    private ItemStack[] captureUpgradeSlots() {
        final java.util.List<ItemStack> captured = new java.util.ArrayList<>();
        for (int slot = 0; slot < this.owner.getInternalItemHandler().getSlots(); slot++) {
            if (this.owner.isUpgradeInventorySlot(slot)) {
                captured.add(this.owner.getInternalItemHandler().getStackInSlot(slot).copy());
            }
        }
        return captured.toArray(ItemStack[]::new);
    }
}
