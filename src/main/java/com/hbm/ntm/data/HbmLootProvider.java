package com.hbm.ntm.data;

import java.util.List;
import java.util.Set;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class HbmLootProvider extends LootTableProvider {
    public HbmLootProvider(final PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(HbmBlockLootProvider::new, LootContextParamSets.BLOCK)));
    }
}
