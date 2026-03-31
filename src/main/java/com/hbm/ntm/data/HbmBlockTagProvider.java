package com.hbm.ntm.data;

import com.hbm.ntm.HbmNtmMod;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class HbmBlockTagProvider extends BlockTagsProvider {
    public HbmBlockTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, HbmNtmMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(final @NotNull HolderLookup.Provider provider) {
    }

    @Override
    public String getName() {
        return "HBM Block Tags";
    }
}
