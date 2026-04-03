package com.hbm.ntm.common.item;

import com.hbm.ntm.common.radiation.ChunkRadiationManager;
import com.hbm.ntm.common.radiation.RadiationUtil;
import com.hbm.ntm.common.registration.HbmSoundEvents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
public class GeigerCounterItem extends Item {
    private final RandomSource random = RandomSource.create();

    public GeigerCounterItem(final Properties properties) {
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
            if (radiation < 1.0F) {
                choices.add(0);
            }
            if (radiation < 5.0F) {
                choices.add(0);
            }
            if (radiation < 10.0F) {
                choices.add(1);
            }
            if (radiation > 5.0F && radiation < 15.0F) {
                choices.add(2);
            }
            if (radiation > 10.0F && radiation < 20.0F) {
                choices.add(3);
            }
            if (radiation > 15.0F && radiation < 25.0F) {
                choices.add(4);
            }
            if (radiation > 20.0F && radiation < 30.0F) {
                choices.add(5);
            }
            if (radiation > 25.0F) {
                choices.add(6);
            }
            final int sample = choices.get(random.nextInt(choices.size()));
            if (sample > 0) {
                playGeiger(level, livingEntity.blockPosition(), sample);
            }
        } else if (random.nextInt(50) == 0) {
            playGeiger(level, livingEntity.blockPosition(), 1);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(final @NotNull Level level, final @NotNull Player player, final @NotNull InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            level.playSound(null, player.blockPosition(), HbmSoundEvents.ITEM_TECH_BOOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            printGeigerData(player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static int check(final Level level, final BlockPos pos) {
        return (int) Math.ceil(ChunkRadiationManager.getRadiation(level, pos.getX(), pos.getY(), pos.getZ()));
    }

    public static void printGeigerData(final Player player) {
        final Level level = player.level();
        final double playerRad = roundTenths(RadiationUtil.getRadiation(player));
        final double chunkRad = roundTenths(ChunkRadiationManager.getRadiation(level, player.getBlockX(), player.getBlockY(), player.getBlockZ()));
        final double envRad = roundTenths(RadiationUtil.getRadBuf(player));
        final double resistance = ((int) (10000.0D - RadiationUtil.calculateRadiationMod(player) * 10000.0D)) / 100.0D;
        final double resistanceCoeff = ((int) (100.0D * com.hbm.ntm.common.radiation.RadiationResistanceRegistry.getResistance(player))) / 100.0D;

        player.sendSystemMessage(Component.literal("===== ☢ ").withStyle(ChatFormatting.GOLD)
            .append(Component.translatable("geiger.title").withStyle(ChatFormatting.GOLD))
            .append(Component.literal(" ☢ =====").withStyle(ChatFormatting.GOLD)));
        player.sendSystemMessage(Component.translatable("geiger.chunkRad").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" ").append(coloredRad(chunkRad)).append(Component.literal(chunkRad + " RAD/s"))));
        player.sendSystemMessage(Component.translatable("geiger.envRad").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" ").append(coloredRad(envRad)).append(Component.literal(envRad + " RAD/s"))));
        player.sendSystemMessage(Component.translatable("geiger.playerRad").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" ").append(coloredPlayerRad(playerRad)).append(Component.literal(playerRad + " RAD"))));
        final MutableComponent resistanceValue = Component.literal(String.valueOf(resistance)).withStyle(resistanceCoeff > 0.0D ? ChatFormatting.GREEN : ChatFormatting.WHITE)
            .append(Component.literal("% (" + resistanceCoeff + ")"));
        player.sendSystemMessage(Component.translatable("geiger.playerRes").withStyle(ChatFormatting.YELLOW)
            .append(Component.literal(" "))
            .append(resistanceValue));
    }

    public static ChatFormatting colorFromRad(final double radiation) {
        if (radiation == 0.0D) {
            return ChatFormatting.GREEN;
        }
        if (radiation < 1.0D) {
            return ChatFormatting.YELLOW;
        }
        if (radiation < 10.0D) {
            return ChatFormatting.GOLD;
        }
        if (radiation < 100.0D) {
            return ChatFormatting.RED;
        }
        if (radiation < 1000.0D) {
            return ChatFormatting.DARK_RED;
        }
        return ChatFormatting.DARK_GRAY;
    }

    private static MutableComponent coloredRad(final double radiation) {
        return Component.empty().append(Component.literal("").withStyle(colorFromRad(radiation)));
    }

    private static MutableComponent coloredPlayerRad(final double radiation) {
        if (radiation < 200.0D) {
            return Component.literal("").withStyle(ChatFormatting.GREEN);
        }
        if (radiation < 400.0D) {
            return Component.literal("").withStyle(ChatFormatting.YELLOW);
        }
        if (radiation < 600.0D) {
            return Component.literal("").withStyle(ChatFormatting.GOLD);
        }
        if (radiation < 800.0D) {
            return Component.literal("").withStyle(ChatFormatting.RED);
        }
        if (radiation < 1000.0D) {
            return Component.literal("").withStyle(ChatFormatting.DARK_RED);
        }
        return Component.literal("").withStyle(ChatFormatting.DARK_GRAY);
    }

    private void playGeiger(final Level level, final BlockPos pos, final int sample) {
        level.playSound(null, pos, switch (sample) {
            case 1 -> HbmSoundEvents.ITEM_GEIGER_1.get();
            case 2 -> HbmSoundEvents.ITEM_GEIGER_2.get();
            case 3 -> HbmSoundEvents.ITEM_GEIGER_3.get();
            case 4 -> HbmSoundEvents.ITEM_GEIGER_4.get();
            case 5 -> HbmSoundEvents.ITEM_GEIGER_5.get();
            case 6 -> HbmSoundEvents.ITEM_GEIGER_6.get();
            default -> HbmSoundEvents.ITEM_GEIGER_1.get();
        }, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static double roundTenths(final float value) {
        return ((int) (value * 10.0F)) / 10.0D;
    }
}
