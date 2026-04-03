package com.hbm.ntm.common.item;

import com.hbm.ntm.common.radiation.RadiationUtil;
import com.hbm.ntm.common.registration.HbmSoundEvents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("null")
public class DosimeterItem extends Item {
    private final RandomSource random = RandomSource.create();

    public DosimeterItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void inventoryTick(final @NotNull ItemStack stack, final @NotNull Level level, final @NotNull Entity entity, final int slotId, final boolean isSelected) {
        if (level.isClientSide() || !(entity instanceof final LivingEntity livingEntity) || level.getGameTime() % 5L != 0L) {
            return;
        }

        final float radiation = RadiationUtil.getRadBuf(livingEntity);
        if (radiation > 1.0E-5F) {
            final List<Integer> choices = new ArrayList<>();
            if (radiation < 0.5F) {
                choices.add(0);
            }
            if (radiation < 1.0F) {
                choices.add(1);
            }
            if (radiation >= 0.5F && radiation < 2.0F) {
                choices.add(2);
            }
            if (radiation >= 2.0F) {
                choices.add(3);
            }
            final int sample = choices.get(random.nextInt(choices.size()));
            if (sample > 0) {
                playGeiger(level, livingEntity.blockPosition(), sample);
            }
        } else if (random.nextInt(100) == 0) {
            playGeiger(level, livingEntity.blockPosition(), 1);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level level, final @NotNull Player player, final @NotNull InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            level.playSound(null, player.blockPosition(), HbmSoundEvents.ITEM_TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            printDosimeterData(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static void printDosimeterData(final Player player) {
        double envRad = ((int) (RadiationUtil.getRadBuf(player) * 10.0F)) / 10.0D;
        boolean limit = false;
        if (envRad > 3.6D) {
            envRad = 3.6D;
            limit = true;
        }

        player.sendSystemMessage(Component.literal("===== ☢ ").withStyle(ChatFormatting.GOLD)
            .append(Component.translatable("geiger.title.dosimeter").withStyle(ChatFormatting.GOLD))
            .append(Component.literal(" ☢ =====").withStyle(ChatFormatting.GOLD)));
        player.sendSystemMessage(Component.translatable("geiger.envRad").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" "))
            .append(Component.literal((limit ? ">" : "") + envRad + " RAD/s").withStyle(GeigerCounterItem.colorFromRad(envRad))));
    }

    private void playGeiger(final Level level, final net.minecraft.core.BlockPos pos, final int sample) {
        level.playSound(null, pos, switch (sample) {
            case 1 -> HbmSoundEvents.ITEM_GEIGER_1.get();
            case 2 -> HbmSoundEvents.ITEM_GEIGER_2.get();
            case 3 -> HbmSoundEvents.ITEM_GEIGER_3.get();
            default -> HbmSoundEvents.ITEM_GEIGER_1.get();
        }, SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
