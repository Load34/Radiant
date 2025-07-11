package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderPainting extends Render<EntityPainting> {
    private static final ResourceLocation KRISTOFFER_PAINTING_TEXTURE = new ResourceLocation("textures/painting/paintings_kristoffer_zetterstrand.png");

    public RenderPainting(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void doRender(EntityPainting entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.enableRescaleNormal();
        this.bindEntityTexture(entity);
        EntityPainting.PaintingType entitypainting$enumart = entity.art;
        float f = 0.0625F;
        GlStateManager.scale(f, f, f);
        this.renderPainting(entity, entitypainting$enumart.sizeX, entitypainting$enumart.sizeY, entitypainting$enumart.offsetX, entitypainting$enumart.offsetY);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityPainting entity) {
        return KRISTOFFER_PAINTING_TEXTURE;
    }

    private void renderPainting(EntityPainting painting, int width, int height, int textureU, int textureV) {
        float f = (-width) / 2.0F;
        float f1 = (-height) / 2.0F;
        float f2 = 0.5F;
        float f3 = 0.75F;
        float f4 = 0.8125F;
        float f5 = 0.0F;
        float f6 = 0.0625F;
        float f7 = 0.75F;
        float f8 = 0.8125F;
        float f9 = 0.001953125F;
        float f10 = 0.001953125F;
        float f11 = 0.7519531F;
        float f12 = 0.7519531F;
        float f13 = 0.0F;
        float f14 = 0.0625F;

        for (int i = 0; i < width / 16; ++i) {
            for (int j = 0; j < height / 16; ++j) {
                float f15 = f + ((i + 1) * 16);
                float f16 = f + (i * 16);
                float f17 = f1 + ((j + 1) * 16);
                float f18 = f1 + (j * 16);
                this.setLightmap(painting, (f15 + f16) / 2.0F, (f17 + f18) / 2.0F);
                float f19 = (textureU + width - i * 16) / 256.0F;
                float f20 = (textureU + width - (i + 1) * 16) / 256.0F;
                float f21 = (textureV + height - j * 16) / 256.0F;
                float f22 = (textureV + height - (j + 1) * 16) / 256.0F;
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
                worldrenderer.pos(f15, f18, (-f2)).tex(f20, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f16, f18, (-f2)).tex(f19, f21).normal(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f16, f17, (-f2)).tex(f19, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f15, f17, (-f2)).tex(f20, f22).normal(0.0F, 0.0F, -1.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f3, f5).normal(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f4, f5).normal(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f4, f6).normal(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f3, f6).normal(0.0F, 0.0F, 1.0F).endVertex();
                worldrenderer.pos(f15, f17, (-f2)).tex(f7, f9).normal(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, (-f2)).tex(f8, f9).normal(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f8, f10).normal(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f7, f10).normal(0.0F, 1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f7, f9).normal(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f8, f9).normal(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, (-f2)).tex(f8, f10).normal(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, (-f2)).tex(f7, f10).normal(0.0F, -1.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, f2).tex(f12, f13).normal(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, f2).tex(f12, f14).normal(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f18, (-f2)).tex(f11, f14).normal(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f15, f17, (-f2)).tex(f11, f13).normal(-1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, (-f2)).tex(f12, f13).normal(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, (-f2)).tex(f12, f14).normal(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f18, f2).tex(f11, f14).normal(1.0F, 0.0F, 0.0F).endVertex();
                worldrenderer.pos(f16, f17, f2).tex(f11, f13).normal(1.0F, 0.0F, 0.0F).endVertex();
                tessellator.draw();
            }
        }
    }

    private void setLightmap(EntityPainting painting, float p_77008_2_, float p_77008_3_) {
        int i = MathHelper.floor(painting.posX);
        int j = MathHelper.floor(painting.posY + (p_77008_3_ / 16.0F));
        int k = MathHelper.floor(painting.posZ);
        Direction enumfacing = painting.facingDirection;

        if (enumfacing == Direction.NORTH) {
            i = MathHelper.floor(painting.posX + (p_77008_2_ / 16.0F));
        }

        if (enumfacing == Direction.WEST) {
            k = MathHelper.floor(painting.posZ - (p_77008_2_ / 16.0F));
        }

        if (enumfacing == Direction.SOUTH) {
            i = MathHelper.floor(painting.posX - (p_77008_2_ / 16.0F));
        }

        if (enumfacing == Direction.EAST) {
            k = MathHelper.floor(painting.posZ + (p_77008_2_ / 16.0F));
        }

        int l = this.renderManager.worldObj.getCombinedLight(new BlockPos(i, j, k), 0);
        int i1 = l % 65536;
        int j1 = l / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i1, j1);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
    }
}
