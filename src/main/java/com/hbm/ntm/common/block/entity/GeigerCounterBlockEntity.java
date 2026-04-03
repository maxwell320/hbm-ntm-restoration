package com.hbm.ntm.common.block.entity;

import com.hbm.ntm.common.radiation.ChunkRadiationManager;
import com.hbm.ntm.common.registration.HbmBlockEntityTypes;
import com.hbm.ntm.common.registration.HbmSoundEvents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("null")
public class GeigerCounterBlockEntity extends BlockEntity {
    private int timer;
    private float ticker;

    public GeigerCounterBlockEntity(final BlockPos pos, final BlockState state) {
        super(HbmBlockEntityTypes.GEIGER.get(), pos, state);
    }

    public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final GeigerCounterBlockEntity blockEntity) {
        blockEntity.timer++;

        if (blockEntity.timer == 10) {
            blockEntity.timer = 0;
            blockEntity.ticker = blockEntity.check();
            level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }

        if (blockEntity.timer % 5 == 0) {
            final RandomSource random = level.random;
            if (blockEntity.ticker > 0.0F) {
                final List<Integer> choices = new ArrayList<>();
                if (blockEntity.ticker < 1.0F) {
                    choices.add(0);
                }
                if (blockEntity.ticker < 5.0F) {
                    choices.add(0);
                }
                if (blockEntity.ticker < 10.0F) {
                    choices.add(1);
                }
                if (blockEntity.ticker > 5.0F && blockEntity.ticker < 15.0F) {
                    choices.add(2);
                }
                if (blockEntity.ticker > 10.0F && blockEntity.ticker < 20.0F) {
                    choices.add(3);
                }
                if (blockEntity.ticker > 15.0F && blockEntity.ticker < 25.0F) {
                    choices.add(4);
                }
                if (blockEntity.ticker > 20.0F && blockEntity.ticker < 30.0F) {
                    choices.add(5);
                }
                if (blockEntity.ticker > 25.0F) {
                    choices.add(6);
                }

                final int sample = choices.get(random.nextInt(choices.size()));
                if (sample > 0) {
                    level.playSound(null, pos, soundFor(sample), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else if (random.nextInt(50) == 0) {
                level.playSound(null, pos, HbmSoundEvents.ITEM_GEIGER_1.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    public float check() {
        return level == null ? 0.0F : ChunkRadiationManager.getRadiation(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    private static SoundEvent soundFor(final int sample) {
        return switch (sample) {
            case 1 -> HbmSoundEvents.ITEM_GEIGER_1.get();
            case 2 -> HbmSoundEvents.ITEM_GEIGER_2.get();
            case 3 -> HbmSoundEvents.ITEM_GEIGER_3.get();
            case 4 -> HbmSoundEvents.ITEM_GEIGER_4.get();
            case 5 -> HbmSoundEvents.ITEM_GEIGER_5.get();
            case 6 -> HbmSoundEvents.ITEM_GEIGER_6.get();
            default -> HbmSoundEvents.ITEM_GEIGER_1.get();
        };
    }
}
