package com.hbm.ntm.common.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class MachineUpgradeItem extends Item {
    private final UpgradeType type;
    private final int tier;

    public MachineUpgradeItem(final UpgradeType type, final int tier) {
        this(type, tier, 1);
    }

    public MachineUpgradeItem(final UpgradeType type, final int tier, final int maxStackSize) {
        super(new Item.Properties().stacksTo(maxStackSize));
        this.type = type;
        this.tier = tier;
    }

    public UpgradeType type() {
        return this.type;
    }

    public int tier() {
        return this.tier;
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        switch (this.type) {
            case RADIUS, HEALTH, SMELTER, SHREDDER, CENTRIFUGE, CRYSTALLIZER, NULLIFIER -> {
                tooltip.add(Component.translatable(getDescriptionId() + ".desc1").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc2"));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc3"));
            }
            case SCREM -> {
                tooltip.add(Component.translatable(getDescriptionId() + ".desc1").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc2"));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc3"));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc4"));
            }
            case GC_SPEED -> {
                tooltip.add(Component.translatable(getDescriptionId() + ".desc1").withStyle(ChatFormatting.RED));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc2"));
                tooltip.add(Component.translatable(getDescriptionId() + ".desc3").withStyle(ChatFormatting.YELLOW));
            }
            default -> {
            }
        }
    }

    public enum UpgradeType {
        SPEED,
        EFFECT,
        POWER,
        FORTUNE,
        AFTERBURN,
        OVERDRIVE,
        SPECIAL,
        RADIUS,
        HEALTH,
        SMELTER(true),
        SHREDDER(true),
        CENTRIFUGE(true),
        CRYSTALLIZER(true),
        NULLIFIER,
        SCREM,
        GC_SPEED;

        private final boolean mutex;

        UpgradeType() {
            this(false);
        }

        UpgradeType(final boolean mutex) {
            this.mutex = mutex;
        }

        public boolean isMutex() {
            return this.mutex;
        }
    }
}
