package com.hbm.ntm.client.screen;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.menu.BrickFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("null")
public class BrickFurnaceScreen extends MachineScreenBase<BrickFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "textures/gui/processing/gui_furnace_brick.png");

    public BrickFurnaceScreen(final BrickFurnaceMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    protected void renderMachineContents(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        if (this.menu.burnTime() > 0) {
            final int burnHeight = this.menu.burnTime() * 13 / this.menu.maxBurnTime();
            guiGraphics.blit(TEXTURE, this.leftPos + 62, this.topPos + 54 + 12 - burnHeight, 176, 12 - burnHeight, 14, burnHeight + 1);

            final int progressWidth = this.menu.progress() * 24 / 200;
            guiGraphics.blit(TEXTURE, this.leftPos + 85, this.topPos + 34, 176, 14, progressWidth + 1, 16);
        }
    }

    @Override
    protected void renderMachineLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        // Legacy GUIFurnaceBrick has no hover tooltip regions.
    }

    @Override
    protected int titleLabelColor() {
        return 0xFFFFFF;
    }

    @Override
    protected int inventoryLabelColor() {
        return 0xFFFFFF;
    }

    @Override
    protected ResourceLocation texture() {
        return TEXTURE;
    }
}
