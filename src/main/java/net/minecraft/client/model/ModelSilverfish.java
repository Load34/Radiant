package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelSilverfish extends ModelBase {
    private final ModelRenderer[] silverfishBodyParts = new ModelRenderer[7];
    private final ModelRenderer[] silverfishWings;
    private final float[] field_78170_c = new float[7];
    private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] SILVERFISH_TEXTURE_POSITIONS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public ModelSilverfish() {
        float f = -3.5F;

        for (int i = 0; i < this.silverfishBodyParts.length; ++i) {
            this.silverfishBodyParts[i] = new ModelRenderer(this, SILVERFISH_TEXTURE_POSITIONS[i][0], SILVERFISH_TEXTURE_POSITIONS[i][1]);
            this.silverfishBodyParts[i].addBox(SILVERFISH_BOX_LENGTH[i][0] * -0.5F, 0.0F, SILVERFISH_BOX_LENGTH[i][2] * -0.5F, SILVERFISH_BOX_LENGTH[i][0], SILVERFISH_BOX_LENGTH[i][1], SILVERFISH_BOX_LENGTH[i][2]);
            this.silverfishBodyParts[i].setRotationPoint(0.0F, (24 - SILVERFISH_BOX_LENGTH[i][1]), f);
            this.field_78170_c[i] = f;

            if (i < this.silverfishBodyParts.length - 1) {
                f += (SILVERFISH_BOX_LENGTH[i][2] + SILVERFISH_BOX_LENGTH[i + 1][2]) * 0.5F;
            }
        }

        this.silverfishWings = new ModelRenderer[3];
        this.silverfishWings[0] = new ModelRenderer(this, 20, 0);
        this.silverfishWings[0].addBox(-5.0F, 0.0F, SILVERFISH_BOX_LENGTH[2][2] * -0.5F, 10, 8, SILVERFISH_BOX_LENGTH[2][2]);
        this.silverfishWings[0].setRotationPoint(0.0F, 16.0F, this.field_78170_c[2]);
        this.silverfishWings[1] = new ModelRenderer(this, 20, 11);
        this.silverfishWings[1].addBox(-3.0F, 0.0F, SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 4, SILVERFISH_BOX_LENGTH[4][2]);
        this.silverfishWings[1].setRotationPoint(0.0F, 20.0F, this.field_78170_c[4]);
        this.silverfishWings[2] = new ModelRenderer(this, 20, 18);
        this.silverfishWings[2].addBox(-3.0F, 0.0F, SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 5, SILVERFISH_BOX_LENGTH[1][2]);
        this.silverfishWings[2].setRotationPoint(0.0F, 19.0F, this.field_78170_c[1]);
    }

    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);

        for (ModelRenderer silverfishBodyPart : this.silverfishBodyParts) {
            silverfishBodyPart.render(scale);
        }

        for (ModelRenderer silverfishWing : this.silverfishWings) {
            silverfishWing.render(scale);
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        for (int i = 0; i < this.silverfishBodyParts.length; ++i) {
            this.silverfishBodyParts[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (1 + Math.abs(i - 2));
            this.silverfishBodyParts[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * Math.abs(i - 2);
        }

        this.silverfishWings[0].rotateAngleY = this.silverfishBodyParts[2].rotateAngleY;
        this.silverfishWings[1].rotateAngleY = this.silverfishBodyParts[4].rotateAngleY;
        this.silverfishWings[1].rotationPointX = this.silverfishBodyParts[4].rotationPointX;
        this.silverfishWings[2].rotateAngleY = this.silverfishBodyParts[1].rotateAngleY;
        this.silverfishWings[2].rotationPointX = this.silverfishBodyParts[1].rotationPointX;
    }
}
