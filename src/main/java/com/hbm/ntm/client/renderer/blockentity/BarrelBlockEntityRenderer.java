package com.hbm.ntm.client.renderer.blockentity;

import com.hbm.ntm.HbmNtmMod;
import com.hbm.ntm.common.block.BarrelBlock;
import com.hbm.ntm.common.block.entity.BarrelBlockEntity;
import com.hbm.ntm.common.fluid.FluidHazardProperties;
import com.hbm.ntm.common.fluid.FluidHazardSymbol;
import com.hbm.ntm.common.fluid.HbmFluidType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("null")
public class BarrelBlockEntityRenderer implements BlockEntityRenderer<BarrelBlockEntity> {
    private static final ResourceLocation DANGER_DIAMOND_TEXTURE = ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "textures/models/misc/danger_diamond.png");
    private static final float DANGER_UV = 1.0F / 256.0F;
    private static final float DANGER_SCALE = 1.0F / 139.0F;

    public BarrelBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(final BarrelBlockEntity barrel, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource,
                       final int packedLight, final int packedOverlay) {
        final FluidStack fluid = barrel.getFluid();
        if (fluid.isEmpty()) {
            return;
        }
        final ResourceLocation texture = this.texture(barrel);
        if (texture == null) {
            return;
        }
        final @Nullable FluidHazardProperties hazards = this.hazards(fluid);
        final TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
        final VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(InventoryMenu.BLOCK_ATLAS));
        final @Nullable VertexConsumer dangerConsumer = hazards == null ? null : bufferSource.getBuffer(RenderType.entityTranslucent(DANGER_DIAMOND_TEXTURE));

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            poseStack.pushPose();
            this.rotateToDirection(poseStack, direction);
            if (this.shouldRenderConnector(barrel, direction, fluid)) {
                this.renderConnectorBox(poseStack, consumer, sprite, packedLight, packedOverlay);
            }
            if (hazards != null && dangerConsumer != null) {
                this.renderHazardDiamond(poseStack, dangerConsumer, hazards, packedLight, packedOverlay);
            }
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private @Nullable ResourceLocation texture(final BarrelBlockEntity barrel) {
        if (barrel.getBlockState().getBlock() instanceof final BarrelBlock barrelBlock) {
            return ResourceLocation.fromNamespaceAndPath(HbmNtmMod.MOD_ID, "block/" + barrelBlock.barrelType().blockId());
        }
        return null;
    }

    private boolean shouldRenderConnector(final BarrelBlockEntity barrel, final Direction direction, final FluidStack fluid) {
        final Level level = barrel.getLevel();
        if (level == null) {
            return false;
        }
        final BlockEntity neighbor = level.getBlockEntity(barrel.getBlockPos().relative(direction));
        if (neighbor == null) {
            return false;
        }
        return neighbor.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
            .map(handler -> this.canConnect(handler, fluid))
            .orElse(false);
    }

    private boolean canConnect(final IFluidHandler handler, final FluidStack fluid) {
        final FluidStack probe = fluid.copy();
        probe.setAmount(1);
        if (handler.fill(probe, IFluidHandler.FluidAction.SIMULATE) > 0) {
            return true;
        }
        final FluidStack drained = handler.drain(probe, IFluidHandler.FluidAction.SIMULATE);
        return !drained.isEmpty() && drained.getFluid().isSame(probe.getFluid());
    }

    private @Nullable FluidHazardProperties hazards(final FluidStack fluid) {
        if (fluid.getFluid().getFluidType() instanceof final HbmFluidType hbmFluidType) {
            final FluidHazardProperties hazards = hbmFluidType.hazardProperties();
            if (hazards.poison() > 0 || hazards.flammability() > 0 || hazards.reactivity() > 0 || hazards.symbol() != FluidHazardSymbol.NONE) {
                return hazards;
            }
        }
        return null;
    }

    private void rotateToDirection(final PoseStack poseStack, final Direction direction) {
        switch (direction) {
            case SOUTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
            case WEST -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
            case NORTH -> poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
            default -> {
            }
        }
    }

    private void renderHazardDiamond(final PoseStack poseStack, final VertexConsumer consumer, final FluidHazardProperties hazards,
                                     final int packedLight, final int packedOverlay) {
        final float width = 10.0F * DANGER_SCALE;
        final float height = 14.0F * DANGER_SCALE;
        final float symbolSize = 59.0F * 0.5F * DANGER_SCALE;

        poseStack.pushPose();
        poseStack.translate(0.4D, 0.30D, -0.24D);
        poseStack.scale(1.0F, 0.25F, 0.25F);
        this.addDangerQuad(poseStack, consumer, 0.0F, 0.5F, -0.5F, 0.0F, 0.5F, 0.5F, 0.0F, -0.5F, 0.5F, 0.0F, -0.5F, -0.5F,
            144, 45, 5, 45, 5, 184, 144, 184, packedLight, packedOverlay);
        this.renderHazardNumber(poseStack, consumer, hazards.poison(), 0.0F, 33.0F * DANGER_SCALE, width, height, packedLight, packedOverlay);
        this.renderHazardNumber(poseStack, consumer, hazards.flammability(), 33.0F * DANGER_SCALE, 0.0F, width, height, packedLight, packedOverlay);
        this.renderHazardNumber(poseStack, consumer, hazards.reactivity(), 0.0F, -33.0F * DANGER_SCALE, width, height, packedLight, packedOverlay);
        if (hazards.symbol() != FluidHazardSymbol.NONE) {
            this.addDangerQuad(poseStack, consumer, 0.01F, symbolSize - 33.0F * DANGER_SCALE, -symbolSize, 0.01F, symbolSize - 33.0F * DANGER_SCALE, symbolSize,
                0.01F, -symbolSize - 33.0F * DANGER_SCALE, symbolSize, 0.01F, -symbolSize - 33.0F * DANGER_SCALE, -symbolSize,
                hazards.symbol().u() + 59, hazards.symbol().v(), hazards.symbol().u(), hazards.symbol().v(), hazards.symbol().u(), hazards.symbol().v() + 59,
                hazards.symbol().u() + 59, hazards.symbol().v() + 59, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    private void renderHazardNumber(final PoseStack poseStack, final VertexConsumer consumer, final int value, final float offsetY, final float offsetZ,
                                    final float width, final float height, final int packedLight, final int packedOverlay) {
        if (value < 0 || value > 5) {
            return;
        }
        final int u = value == 0 ? 125 : 5 + (value - 1) * 24;
        this.addDangerQuad(poseStack, consumer, 0.01F, height + offsetY, -width + offsetZ, 0.01F, height + offsetY, width + offsetZ,
            0.01F, -height + offsetY, width + offsetZ, 0.01F, -height + offsetY, -width + offsetZ,
            u + 20, 5, u, 5, u, 33, u + 20, 33, packedLight, packedOverlay);
    }

    private void addDangerQuad(final PoseStack poseStack, final VertexConsumer consumer,
                               final float x1, final float y1, final float z1,
                               final float x2, final float y2, final float z2,
                               final float x3, final float y3, final float z3,
                               final float x4, final float y4, final float z4,
                               final int u1, final int v1,
                               final int u2, final int v2,
                               final int u3, final int v3,
                               final int u4, final int v4,
                               final int packedLight, final int packedOverlay) {
        this.addQuad(consumer, poseStack.last().pose(), poseStack.last().normal(),
            x1, y1, z1,
            x2, y2, z2,
            x3, y3, z3,
            x4, y4, z4,
            u1 * DANGER_UV, v1 * DANGER_UV,
            u2 * DANGER_UV, v2 * DANGER_UV,
            u3 * DANGER_UV, v3 * DANGER_UV,
            u4 * DANGER_UV, v4 * DANGER_UV,
            1.0F, 0.0F, 0.0F, packedLight, packedOverlay);
    }

    private void renderConnectorBox(final PoseStack poseStack, final VertexConsumer consumer, final TextureAtlasSprite sprite,
                                    final int packedLight, final int packedOverlay) {
        final PoseStack.Pose pose = poseStack.last();
        final Matrix4f poseMatrix = pose.pose();
        final Matrix3f normalMatrix = pose.normal();

        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, 0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.270833F),
            0.500000F, -0.187500F, -0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.395833F),
            0.500000F, 0.187500F, -0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.270833F),
            1.0F, 0.0F, 0.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, 0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.375000F),
            0.375000F, -0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.250000F),
            0.500000F, -0.187500F, 0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.250000F),
            0.0F, 0.0F, 1.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.395833F),
            0.375000F, 0.187500F, 0.187500F, uvU(sprite, 0.041667F), uvV(sprite, 0.416667F),
            0.500000F, 0.187500F, 0.187500F, uvU(sprite, 0.041667F), uvV(sprite, 0.395833F),
            0.0F, 1.0F, 0.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, -0.187500F, -0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.291667F),
            0.375000F, 0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.416667F),
            0.500000F, 0.187500F, -0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.416667F),
            0.0F, 0.0F, -1.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, -0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.270833F),
            0.375000F, -0.187500F, -0.187500F, uvU(sprite, 0.125000F), uvV(sprite, 0.250000F),
            0.500000F, -0.187500F, -0.187500F, uvU(sprite, 0.125000F), uvV(sprite, 0.270833F),
            0.0F, -1.0F, 0.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, 0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.270833F),
            0.500000F, -0.187500F, 0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.395833F),
            0.500000F, -0.187500F, -0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.395833F),
            1.0F, 0.0F, 0.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, 0.187500F, uvU(sprite, 0.020833F), uvV(sprite, 0.375000F),
            0.375000F, 0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.375000F),
            0.375000F, -0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.250000F),
            0.0F, 0.0F, 1.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, 0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.395833F),
            0.375000F, 0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.416667F),
            0.375000F, 0.187500F, 0.187500F, uvU(sprite, 0.041667F), uvV(sprite, 0.416667F),
            0.0F, 1.0F, 0.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, -0.187500F, -0.187500F, uvU(sprite, 0.145833F), uvV(sprite, 0.291667F),
            0.375000F, -0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.291667F),
            0.375000F, 0.187500F, -0.187500F, uvU(sprite, 0.166667F), uvV(sprite, 0.416667F),
            0.0F, 0.0F, -1.0F, packedLight, packedOverlay);
        this.addTriangle(consumer, poseMatrix, normalMatrix,
            0.500000F, -0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.270833F),
            0.375000F, -0.187500F, 0.187500F, uvU(sprite, 0.000000F), uvV(sprite, 0.250000F),
            0.375000F, -0.187500F, -0.187500F, uvU(sprite, 0.125000F), uvV(sprite, 0.250000F),
            0.0F, -1.0F, 0.0F, packedLight, packedOverlay);
    }

    private static float uvU(final TextureAtlasSprite sprite, final float coord) {
        return sprite.getU0() + (sprite.getU1() - sprite.getU0()) * coord;
    }

    private static float uvV(final TextureAtlasSprite sprite, final float coord) {
        return sprite.getV0() + (sprite.getV1() - sprite.getV0()) * coord;
    }

    private void addTriangle(final VertexConsumer consumer, final Matrix4f poseMatrix, final Matrix3f normalMatrix,
                             final float x1, final float y1, final float z1, final float u1, final float v1,
                             final float x2, final float y2, final float z2, final float u2, final float v2,
                             final float x3, final float y3, final float z3, final float u3, final float v3,
                             final float normalX, final float normalY, final float normalZ,
                             final int packedLight, final int packedOverlay) {
        this.vertex(consumer, poseMatrix, normalMatrix, x1, y1, z1, u1, v1, normalX, normalY, normalZ, packedLight, packedOverlay);
        this.vertex(consumer, poseMatrix, normalMatrix, x2, y2, z2, u2, v2, normalX, normalY, normalZ, packedLight, packedOverlay);
        this.vertex(consumer, poseMatrix, normalMatrix, x3, y3, z3, u3, v3, normalX, normalY, normalZ, packedLight, packedOverlay);
    }

    private void addQuad(final VertexConsumer consumer, final Matrix4f poseMatrix, final Matrix3f normalMatrix,
                         final float x1, final float y1, final float z1,
                         final float x2, final float y2, final float z2,
                         final float x3, final float y3, final float z3,
                         final float x4, final float y4, final float z4,
                         final float u1, final float v1,
                         final float u2, final float v2,
                         final float u3, final float v3,
                         final float u4, final float v4,
                         final float normalX, final float normalY, final float normalZ,
                         final int packedLight, final int packedOverlay) {
        this.vertex(consumer, poseMatrix, normalMatrix, x1, y1, z1, u1, v1, normalX, normalY, normalZ, packedLight, packedOverlay);
        this.vertex(consumer, poseMatrix, normalMatrix, x2, y2, z2, u2, v2, normalX, normalY, normalZ, packedLight, packedOverlay);
        this.vertex(consumer, poseMatrix, normalMatrix, x3, y3, z3, u3, v3, normalX, normalY, normalZ, packedLight, packedOverlay);
        this.vertex(consumer, poseMatrix, normalMatrix, x4, y4, z4, u4, v4, normalX, normalY, normalZ, packedLight, packedOverlay);
    }

    private void vertex(final VertexConsumer consumer, final Matrix4f poseMatrix, final Matrix3f normalMatrix,
                        final float x, final float y, final float z,
                        final float u, final float v,
                        final float normalX, final float normalY, final float normalZ,
                        final int packedLight, final int packedOverlay) {
        consumer.vertex(poseMatrix, x, y, z)
            .color(255, 255, 255, 255)
            .uv(u, v)
            .overlayCoords(packedOverlay)
            .uv2(packedLight)
            .normal(normalMatrix, normalX, normalY, normalZ)
            .endVertex();
    }
}
