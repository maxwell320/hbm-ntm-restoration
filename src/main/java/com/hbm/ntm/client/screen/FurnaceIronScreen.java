package com.hbm.ntm.client.screen;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.menu.FurnaceIronMenu;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("null")
public class FurnaceIronScreen extends MachineScreenBase<FurnaceIronMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "textures/gui/processing/gui_furnace_iron.png");

    public FurnaceIronScreen(final FurnaceIronMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 176, 166);
    }

    @Override
    protected void renderMachineContents(final GuiGraphics guiGraphics, final float partialTick, final int mouseX, final int mouseY) {
        final int progress = this.menu.progress();
        final int processingTime = this.menu.processingTime();
        if (processingTime > 0) {
            final int progressWidth = Math.max(1, Math.min(70, progress * 70 / processingTime));
            if (progressWidth > 0) {
                guiGraphics.blit(TEXTURE, this.leftPos + 53, this.topPos + 36, 176, 18, progressWidth, 5);
            }
        }

        final int burnTime = this.menu.burnTime();
        final int maxBurnTime = this.menu.maxBurnTime();
        if (burnTime > 0 && maxBurnTime > 0) {
            final int burnWidth = Math.max(1, Math.min(70, burnTime * 70 / maxBurnTime));
            guiGraphics.blit(TEXTURE, this.leftPos + 53, this.topPos + 45, 176, 23, burnWidth, 5);
        }
        if (this.menu.canSmelt()) {
            guiGraphics.blit(TEXTURE, this.leftPos + 70, this.topPos + 16, 176, 0, 18, 18);
        }
    }

    @Override
    protected void renderMachineLabels(final GuiGraphics guiGraphics, final int mouseX, final int mouseY) {
        if (this.inside(mouseX, mouseY, this.leftPos + 52, this.topPos + 35, 71, 7)) {
            final int percentage = this.menu.processingTime() <= 0 ? 0 : this.menu.progress() * 100 / this.menu.processingTime();
            guiGraphics.renderTooltip(this.font,
                List.of(Component.literal(percentage + "%")),
                Optional.empty(),
                mouseX,
                mouseY);
        }

        if (this.inside(mouseX, mouseY, this.leftPos + 52, this.topPos + 44, 71, 7)) {
            guiGraphics.renderTooltip(this.font,
                List.of(Component.literal((this.menu.burnTime() / 20) + "s")),
                Optional.empty(),
                mouseX,
                mouseY);
        }

    }

    @Override
    protected ResourceLocation texture() {
        return TEXTURE;
    }
}
