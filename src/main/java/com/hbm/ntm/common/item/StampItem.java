package com.hbm.ntm.common.item;

import net.minecraft.world.item.Item;

public class StampItem extends Item {
    private final StampItemType type;

    public StampItem(final StampItemType type) {
        super(createProperties(type));
        this.type = type;
    }

    private static Properties createProperties(final StampItemType type) {
        final Properties properties = new Properties().stacksTo(1);
        if (type.durability() > 0) {
            properties.durability(type.durability());
        }
        return properties;
    }

    public StampItemType type() {
        return this.type;
    }
}
