package com.hbm.ntm.common.item;

import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class UndefinedItem extends Item {
    private static final String[] NAMES = {"THE DEFAULT", "NEXT ONE", "ANOTHER ONE", "NON-STANDARD NAME", "AMBIGUOUS TITLE", "SHORT"};
    private static final Random NAME_RANDOM = new Random();
    private static final ScramblingName SCRAMBLING_NAME = new ScramblingName(NAMES[0]);
    private static long lastAge;

    public UndefinedItem() {
        super(new Properties());
    }

    @Override
    public @NotNull Component getName(final @NotNull ItemStack stack) {
        if (stack.getDamageValue() != 99) {
            return super.getName(stack);
        }

        updateSystem();
        return Component.literal(SCRAMBLING_NAME.getResult());
    }

    @Override
    public void appendHoverText(final ItemStack stack, final @Nullable Level level, final List<Component> tooltip, final TooltipFlag flag) {
        if (level == null) {
            tooltip.add(Component.literal("UNDEFINED").withStyle(ChatFormatting.DARK_RED));
            return;
        }

        if (level.random.nextInt(10) == 0) {
            tooltip.add(Component.literal("UNDEFINED").withStyle(ChatFormatting.DARK_RED));
            return;
        }

        final var items = ForgeRegistries.ITEMS.getValues().stream().toList();
        if (items.isEmpty()) {
            tooltip.add(Component.literal("UNDEFINED").withStyle(ChatFormatting.DARK_RED));
            return;
        }

        final Random random = new Random(System.currentTimeMillis() / 500L);
        final Item randomItem = items.get(random.nextInt(items.size()));
        tooltip.add(new ItemStack(randomItem).getHoverName());
    }

    private static void updateSystem() {
        final long age = System.currentTimeMillis() / 50L;
        while (lastAge < age) {
            lastAge++;
            SCRAMBLING_NAME.updateTick(NAMES);
        }
    }

    private static final class ScramblingName {
        private String previous;
        private String next;
        private String[] previousFrags;
        private String[] nextFrags;
        private String[] frags;
        private int[] mask;
        private int age;

        private ScramblingName(final String init) {
            this.previous = init;
            this.next = init;
            this.frags = init.split("");
            this.mask = new int[this.frags.length];
            this.previousFrags = chop(this.previous, this.frags.length);
            this.nextFrags = chop(this.next, this.frags.length);
        }

        private String getResult() {
            return String.join("", this.frags);
        }

        private void updateTick(final String[] nextNames) {
            this.age++;

            if (this.age % 200 == 0) {
                nextName(nextNames);
            }
            if (this.age % 5 == 0) {
                scramble();
            }
        }

        private void nextName(final String[] nextNames) {
            if (nextNames.length < 2) {
                return;
            }

            this.previous = this.next;
            String initial = this.next;
            while (initial.equals(this.next)) {
                this.next = nextNames[NAME_RANDOM.nextInt(nextNames.length)];
            }

            final int length = Math.min(this.previous.length(), this.next.length());
            this.previousFrags = chop(this.previous, length);
            this.frags = chop(this.previous, length);
            this.nextFrags = chop(this.next, length);
            this.mask = new int[length];
        }

        private void scramble() {
            final List<Integer> indices = new java.util.ArrayList<>();

            for (int i = 0; i < this.mask.length; i++) {
                final int value = this.mask[i];
                if (value == 0) {
                    indices.add(i);
                }
                if (value > 0 && value <= 5) {
                    this.mask[i]++;
                }
                if (value > 5) {
                    this.frags[i] = this.nextFrags[i];
                }
            }

            if (!indices.isEmpty()) {
                final int toSwitch = indices.get(NAME_RANDOM.nextInt(indices.size()));
                this.mask[toSwitch] = 1;
                this.frags[toSwitch] = ChatFormatting.OBFUSCATED + this.previousFrags[toSwitch] + ChatFormatting.RESET;
            }
        }

        private static String[] chop(final String name, final int parts) {
            if (parts == name.length()) {
                return name.split("");
            }

            double index = 0;
            final double incrementPerStep = (double) name.length() / (double) parts;
            final List<String> slices = new java.util.ArrayList<>();

            for (int i = 0; i < parts; i++) {
                final int end = i == parts - 1 ? name.length() : (int) (index + incrementPerStep);
                slices.add(name.substring((int) index, end));
                index += incrementPerStep;
            }

            return slices.toArray(new String[parts]);
        }
    }
}
