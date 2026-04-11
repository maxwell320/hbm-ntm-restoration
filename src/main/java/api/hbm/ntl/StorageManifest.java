package api.hbm.ntl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class StorageManifest {
    public final LinkedHashMap<Integer, MetaNode> itemMeta = new LinkedHashMap<>();

    public void writeStack(final ItemStack stack) {
        final int id = BuiltInRegistries.ITEM.getId(stack.getItem());
        final MetaNode meta = this.itemMeta.computeIfAbsent(id, ignored -> new MetaNode());
        final NBTNode nbt = meta.metaNBT.computeIfAbsent(stack.getDamageValue(), ignored -> new NBTNode());
        final CompoundTag compound = stack.hasTag() ? stack.getTag().copy() : null;
        final long amount = nbt.nbtAmount.getOrDefault(compound, 0L) + stack.getCount();
        nbt.nbtAmount.put(compound, amount);
    }

    public List<StorageStack> getStacks(final boolean sorted) {
        final List<StorageStack> stacks = new ArrayList<>();
        for (final Entry<Integer, MetaNode> itemNode : this.itemMeta.entrySet()) {
            for (final Entry<Integer, NBTNode> metaNode : itemNode.getValue().metaNBT.entrySet()) {
                for (final Entry<CompoundTag, Long> nbtNode : metaNode.getValue().nbtAmount.entrySet()) {
                    final Item item = BuiltInRegistries.ITEM.byId(itemNode.getKey());
                    if (item == null) {
                        continue;
                    }
                    final ItemStack itemStack = new ItemStack(item, 1);
                    itemStack.setDamageValue(metaNode.getKey());
                    if (nbtNode.getKey() != null) {
                        itemStack.setTag(nbtNode.getKey().copy());
                    }
                    stacks.add(new StorageStack(itemStack, nbtNode.getValue()));
                }
            }
        }
        if (sorted) {
            Collections.sort(stacks);
        }
        return stacks;
    }

    public static class MetaNode {
        public final LinkedHashMap<Integer, NBTNode> metaNBT = new LinkedHashMap<>();
    }

    public static class NBTNode {
        public final LinkedHashMap<CompoundTag, Long> nbtAmount = new LinkedHashMap<>();
    }
}
