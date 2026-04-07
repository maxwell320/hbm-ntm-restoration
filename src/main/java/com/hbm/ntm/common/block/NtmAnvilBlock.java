package com.hbm.ntm.common.block;

import com.hbm.ntm.common.anvil.NtmAnvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class NtmAnvilBlock extends AnvilBlock {
    public static final int TIER_IRON = 1;
    public static final int TIER_STEEL = 2;
    public static final int TIER_DESH = 3;
    public static final int TIER_FERRORANIUM = 4;
    public static final int TIER_RBMK = 5;
    public static final int TIER_SCHRABIDATE = 6;
    public static final int TIER_DNT = 7;
    public static final int TIER_OSMIRIDIUM = 8;

    private final int tier;

    public NtmAnvilBlock(final int tier, final Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public int tier() {
        return this.tier;
    }

    @Override
    public @NotNull InteractionResult use(final @NotNull BlockState state, final @NotNull Level level, final @NotNull BlockPos pos,
                                          final @NotNull Player player, final @NotNull InteractionHand hand, final @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && player instanceof final ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, menuProvider(level, pos), pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private MenuProvider menuProvider(final Level level, final BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) ->
            new NtmAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos), pos, this.tier),
            Component.translatable(this.getDescriptionId()));
    }
}
