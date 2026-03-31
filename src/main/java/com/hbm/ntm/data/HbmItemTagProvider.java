package com.hbm.ntm.data;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.material.HbmMaterialDefinition;
import com.hbm.ntm.common.material.HbmMaterialShape;
import com.hbm.ntm.common.material.HbmMaterials;
import com.hbm.ntm.common.registration.HbmItems;
import com.hbm.ntm.common.tag.HbmItemTags;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class HbmItemTagProvider extends ItemTagsProvider {
    public HbmItemTagProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider, final CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, final ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, HbmNtmMod.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("null")
    protected void addTags(final @NotNull HolderLookup.Provider provider) {
        for (final HbmMaterialShape shape : HbmMaterialShape.values()) {
            tag(Objects.requireNonNull(HbmItemTags.shape(shape)));
        }

        for (final HbmMaterialDefinition material : HbmMaterials.ordered()) {
            final var materialTag = tag(Objects.requireNonNull(HbmItemTags.material(material)));

            for (final HbmMaterialShape shape : material.shapes()) {
                final Item partItem = Objects.requireNonNull(HbmItems.getMaterialPart(material, shape).get());
                materialTag.add(partItem);
                tag(Objects.requireNonNull(HbmItemTags.shape(shape))).add(partItem);
                tag(Objects.requireNonNull(HbmItemTags.materialShape(material, shape))).add(partItem);
                tag(Objects.requireNonNull(HbmItemTags.forgeMaterialShape(material, shape))).add(partItem);
            }
        }
    }

    @Override
    public String getName() {
        return "HBM Item Tags";
    }
}
