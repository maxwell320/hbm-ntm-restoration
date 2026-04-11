package api.hbm.ntl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class StorageStack implements Comparable<StorageStack> {
    private final int cachedItemId;
    private final ItemStack type;
    private final long amount;

    public StorageStack(final ItemStack type) {
        this(type, type.getCount());
    }

    public StorageStack(final ItemStack type, final long amount) {
        this.type = type.copy();
        this.cachedItemId = BuiltInRegistries.ITEM.getId(this.type.getItem());
        this.amount = amount;
        this.type.setCount(0);
    }

    public ItemStack getType() {
        return this.type.copy();
    }

    public long getAmount() {
        return this.amount;
    }

    @Override
    public int compareTo(final StorageStack other) {
        if (this.cachedItemId != other.cachedItemId) {
            return Integer.compare(this.cachedItemId, other.cachedItemId);
        }
        if (this.type.getDamageValue() != other.type.getDamageValue()) {
            return Integer.compare(this.type.getDamageValue(), other.type.getDamageValue());
        }
        final String thisTag = this.type.getTag() == null ? "" : this.type.getTag().toString();
        final String otherTag = other.type.getTag() == null ? "" : other.type.getTag().toString();
        final int tagCompare = thisTag.compareTo(otherTag);
        if (tagCompare != 0) {
            return tagCompare;
        }
        return Integer.compare(this.type.getCount(), other.type.getCount());
    }
}
