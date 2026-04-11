package com.hbm.ntm.common.item;

import api.hbm.block.IToolable;
import com.hbm.ntm.common.block.entity.MachineBlockEntity;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlowtorchItem extends Item implements MachineRepairToolItem {
    private static final String GAS_KEY = "gas";
    private static final String UNSATURATEDS_KEY = "unsaturateds";
    private static final String OXYGEN_KEY = "oxygen";

    private final FuelProfile profile;

    public BlowtorchItem(final FuelProfile profile) {
        super(new Item.Properties().stacksTo(1));
        this.profile = profile;
    }

    @Override
    public boolean canRepairMachine(final ItemStack stack, final Player player, final MachineBlockEntity machine) {
        return this.hasFuelForRepair(stack) || player.getAbilities().instabuild;
    }

    @Override
    public void onMachineRepairUsed(final ItemStack stack, final Player player, final MachineBlockEntity machine) {
        if (player.getAbilities().instabuild) {
            return;
        }
        this.consumeRepairFuel(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(final @NotNull UseOnContext context) {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        final BlockPos pos = context.getClickedPos();
        if (!(level.getBlockState(pos).getBlock() instanceof final IToolable toolable)) {
            return InteractionResult.PASS;
        }

        final ItemStack stack = context.getItemInHand();
        if (!player.getAbilities().instabuild && !this.hasFuelForRepair(stack)) {
            return InteractionResult.FAIL;
        }

        final BlockHitResult hitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false);
        if (!toolable.onScrew(level, player, pos, context.getClickedFace(), hitResult, IToolable.ToolType.TORCH)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && !player.getAbilities().instabuild) {
            this.consumeRepairFuel(stack);
            if (level instanceof final ServerLevel serverLevel) {
                final double x = context.getClickLocation().x();
                final double y = context.getClickLocation().y();
                final double z = context.getClickLocation().z();
                serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 8, 0.1D, 0.1D, 0.1D, 0.0D);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public boolean isBarVisible(final @NotNull ItemStack stack) {
        return this.getFuelRatio(stack) < 1.0F;
    }

    @Override
    public int getBarWidth(final @NotNull ItemStack stack) {
        return Mth.clamp(Math.round(13.0F * this.getFuelRatio(stack)), 0, 13);
    }

    @Override
    public int getBarColor(final @NotNull ItemStack stack) {
        return this.profile == FuelProfile.ACETYLENE ? 0x59D0FF : 0xFFD34D;
    }

    @Override
    public void appendHoverText(final @NotNull ItemStack stack, final @Nullable Level level, final @NotNull List<Component> tooltip,
                                final @NotNull TooltipFlag flag) {
        final CompoundTag tag = this.ensureFuelData(stack);

        if (this.profile == FuelProfile.BLOWTORCH) {
            tooltip.add(gaugeLine(
                "fluid.hbmntm.gas",
                tag.getInt(GAS_KEY),
                FuelProfile.BLOWTORCH.gasCapacity,
                ChatFormatting.YELLOW));
            return;
        }

        tooltip.add(gaugeLine(
            "fluid.hbmntm.unsaturateds",
            tag.getInt(UNSATURATEDS_KEY),
            FuelProfile.ACETYLENE.unsaturatedCapacity,
            ChatFormatting.YELLOW));
        tooltip.add(gaugeLine(
            "fluid.hbmntm.oxygen",
            tag.getInt(OXYGEN_KEY),
            FuelProfile.ACETYLENE.oxygenCapacity,
            ChatFormatting.AQUA));
    }

    private boolean hasFuelForRepair(final ItemStack stack) {
        final CompoundTag tag = this.ensureFuelData(stack);

        if (this.profile == FuelProfile.BLOWTORCH) {
            return tag.getInt(GAS_KEY) >= this.profile.gasPerRepair;
        }

        return tag.getInt(UNSATURATEDS_KEY) >= this.profile.unsaturatedsPerRepair
            && tag.getInt(OXYGEN_KEY) >= this.profile.oxygenPerRepair;
    }

    private void consumeRepairFuel(final ItemStack stack) {
        final CompoundTag tag = this.ensureFuelData(stack);

        if (this.profile == FuelProfile.BLOWTORCH) {
            tag.putInt(GAS_KEY, Math.max(0, tag.getInt(GAS_KEY) - this.profile.gasPerRepair));
            return;
        }

        tag.putInt(UNSATURATEDS_KEY, Math.max(0, tag.getInt(UNSATURATEDS_KEY) - this.profile.unsaturatedsPerRepair));
        tag.putInt(OXYGEN_KEY, Math.max(0, tag.getInt(OXYGEN_KEY) - this.profile.oxygenPerRepair));
    }

    private float getFuelRatio(final ItemStack stack) {
        final CompoundTag tag = this.ensureFuelData(stack);

        if (this.profile == FuelProfile.BLOWTORCH) {
            return ratio(tag.getInt(GAS_KEY), this.profile.gasCapacity);
        }

        final float unsaturatedRatio = ratio(tag.getInt(UNSATURATEDS_KEY), this.profile.unsaturatedCapacity);
        final float oxygenRatio = ratio(tag.getInt(OXYGEN_KEY), this.profile.oxygenCapacity);
        return Math.min(unsaturatedRatio, oxygenRatio);
    }

    private CompoundTag ensureFuelData(final ItemStack stack) {
        final CompoundTag tag = stack.getOrCreateTag();

        if (this.profile == FuelProfile.BLOWTORCH) {
            if (!tag.contains(GAS_KEY)) {
                tag.putInt(GAS_KEY, this.profile.gasCapacity);
            }
            return tag;
        }

        if (!tag.contains(UNSATURATEDS_KEY)) {
            tag.putInt(UNSATURATEDS_KEY, this.profile.unsaturatedCapacity);
        }
        if (!tag.contains(OXYGEN_KEY)) {
            tag.putInt(OXYGEN_KEY, this.profile.oxygenCapacity);
        }
        return tag;
    }

    private static @NotNull Component gaugeLine(final @NotNull String fluidTranslationKey, final int amount, final int maxAmount,
                                                final @NotNull ChatFormatting color) {
        final @NotNull String fluidName = Objects.requireNonNull(Component.translatable(fluidTranslationKey).getString());
        return Component.literal(formatGauge(fluidName, amount, maxAmount)).withStyle(color);
    }

    private static float ratio(final int amount, final int maxAmount) {
        if (maxAmount <= 0) {
            return 0.0F;
        }
        return Mth.clamp(amount / (float) maxAmount, 0.0F, 1.0F);
    }

    public enum FuelProfile {
        BLOWTORCH(4_000, 250, 0, 0, 0, 0),
        ACETYLENE(0, 0, 8_000, 20, 16_000, 10);

        private final int gasCapacity;
        private final int gasPerRepair;
        private final int unsaturatedCapacity;
        private final int unsaturatedsPerRepair;
        private final int oxygenCapacity;
        private final int oxygenPerRepair;

        FuelProfile(final int gasCapacity, final int gasPerRepair, final int unsaturatedCapacity, final int unsaturatedsPerRepair,
                    final int oxygenCapacity, final int oxygenPerRepair) {
            this.gasCapacity = gasCapacity;
            this.gasPerRepair = gasPerRepair;
            this.unsaturatedCapacity = unsaturatedCapacity;
            this.unsaturatedsPerRepair = unsaturatedsPerRepair;
            this.oxygenCapacity = oxygenCapacity;
            this.oxygenPerRepair = oxygenPerRepair;
        }
    }

    private static @NotNull String formatGauge(final @NotNull String fluidName, final int amount, final int maxAmount) {
        return fluidName + ": " + String.format(Locale.US, "%,d", amount) + " / " + String.format(Locale.US, "%,d", maxAmount);
    }
}