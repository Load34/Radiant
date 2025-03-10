package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class RenderVillager extends RenderLiving<EntityVillager> {
    private static final ResourceLocation VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/villager.png");
    private static final ResourceLocation FARMER_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/farmer.png");
    private static final ResourceLocation LIBRARIAN_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/librarian.png");
    private static final ResourceLocation PRIEST_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/priest.png");
    private static final ResourceLocation SMITH_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/smith.png");
    private static final ResourceLocation BUTCHER_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/villager/butcher.png");

    public RenderVillager(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelVillager(0.0F), 0.5F);
        this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
    }

    public ModelVillager getMainModel() {
        return (ModelVillager) super.getMainModel();
    }

    protected ResourceLocation getEntityTexture(EntityVillager entity) {
        return switch (entity.getProfession()) {
            case 0 -> FARMER_VILLAGER_TEXTURES;
            case 1 -> LIBRARIAN_VILLAGER_TEXTURES;
            case 2 -> PRIEST_VILLAGER_TEXTURES;
            case 3 -> SMITH_VILLAGER_TEXTURES;
            case 4 -> BUTCHER_VILLAGER_TEXTURES;
            default -> VILLAGER_TEXTURES;
        };
    }

    protected void preRenderCallback(EntityVillager entitylivingbaseIn, float partialTickTime) {
        float f = 0.9375F;

        if (entitylivingbaseIn.getGrowingAge() < 0) {
            f = (float) (f * 0.5D);
            this.shadowSize = 0.25F;
        } else {
            this.shadowSize = 0.5F;
        }

        GlStateManager.scale(f, f, f);
    }
}
