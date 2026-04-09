package com.hbm.ntm.common.item;

import net.minecraft.world.item.Item;

public class ShredderBladesItem extends Item {

    public ShredderBladesItem(final int durability) {
        super(new Item.Properties().stacksTo(1).durability(durability));
    }
}
