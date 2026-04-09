package com.hbm.ntm.common.machine;

import com.hbm.ntm.common.item.MachineUpgradeItem;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;

public interface IUpgradeInfoProvider {
    boolean canProvideInfo(MachineUpgradeItem.UpgradeType type, int level, boolean extendedInfo);

    void provideInfo(MachineUpgradeItem.UpgradeType type, int level, List<Component> info, boolean extendedInfo);

    Map<MachineUpgradeItem.UpgradeType, Integer> getValidUpgrades();
}
