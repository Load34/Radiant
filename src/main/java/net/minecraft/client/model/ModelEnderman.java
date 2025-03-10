package net.minecraft.client.model;

import net.minecraft.entity.Entity;

public class ModelEnderman extends ModelBiped {
    public boolean isCarrying;
    public boolean isAttacking;

    public ModelEnderman(float p_i46305_1_) {
        super(0.0F, -14.0F, 64, 32);
        float f = -14.0F;
        this.bipedHeadwear = new ModelRenderer(this, 0, 16);
        this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46305_1_ - 0.5F);
        this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedBody = new ModelRenderer(this, 32, 16);
        this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46305_1_);
        this.bipedBody.setRotationPoint(0.0F, 0.0F + f, 0.0F);
        this.bipedRightArm = new ModelRenderer(this, 56, 0);
        this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedRightArm.setRotationPoint(-3.0F, 2.0F + f, 0.0F);
        this.bipedLeftArm = new ModelRenderer(this, 56, 0);
        this.bipedLeftArm.mirror = true;
        this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + f, 0.0F);
        this.bipedRightLeg = new ModelRenderer(this, 56, 0);
        this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedRightLeg.setRotationPoint(-2.0F, 12.0F + f, 0.0F);
        this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
        this.bipedLeftLeg.mirror = true;
        this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, p_i46305_1_);
        this.bipedLeftLeg.setRotationPoint(2.0F, 12.0F + f, 0.0F);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.bipedHead.showModel = true;
        float f = -14.0F;
        this.bipedBody.rotateAngleX = 0.0F;
        this.bipedBody.rotationPointY = f;
        this.bipedBody.rotationPointZ = -0.0F;
        this.bipedRightLeg.rotateAngleX -= 0.0F;
        this.bipedLeftLeg.rotateAngleX -= 0.0F;
        this.bipedRightArm.rotateAngleX = (float) (this.bipedRightArm.rotateAngleX * 0.5D);
        this.bipedLeftArm.rotateAngleX = (float) (this.bipedLeftArm.rotateAngleX * 0.5D);
        this.bipedRightLeg.rotateAngleX = (float) (this.bipedRightLeg.rotateAngleX * 0.5D);
        this.bipedLeftLeg.rotateAngleX = (float) (this.bipedLeftLeg.rotateAngleX * 0.5D);
        float f1 = 0.4F;

        if (this.bipedRightArm.rotateAngleX > f1) {
            this.bipedRightArm.rotateAngleX = f1;
        }

        if (this.bipedLeftArm.rotateAngleX > f1) {
            this.bipedLeftArm.rotateAngleX = f1;
        }

        if (this.bipedRightArm.rotateAngleX < -f1) {
            this.bipedRightArm.rotateAngleX = -f1;
        }

        if (this.bipedLeftArm.rotateAngleX < -f1) {
            this.bipedLeftArm.rotateAngleX = -f1;
        }

        if (this.bipedRightLeg.rotateAngleX > f1) {
            this.bipedRightLeg.rotateAngleX = f1;
        }

        if (this.bipedLeftLeg.rotateAngleX > f1) {
            this.bipedLeftLeg.rotateAngleX = f1;
        }

        if (this.bipedRightLeg.rotateAngleX < -f1) {
            this.bipedRightLeg.rotateAngleX = -f1;
        }

        if (this.bipedLeftLeg.rotateAngleX < -f1) {
            this.bipedLeftLeg.rotateAngleX = -f1;
        }

        if (this.isCarrying) {
            this.bipedRightArm.rotateAngleX = -0.5F;
            this.bipedLeftArm.rotateAngleX = -0.5F;
            this.bipedRightArm.rotateAngleZ = 0.05F;
            this.bipedLeftArm.rotateAngleZ = -0.05F;
        }

        this.bipedRightArm.rotationPointZ = 0.0F;
        this.bipedLeftArm.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointZ = 0.0F;
        this.bipedLeftLeg.rotationPointZ = 0.0F;
        this.bipedRightLeg.rotationPointY = 9.0F + f;
        this.bipedLeftLeg.rotationPointY = 9.0F + f;
        this.bipedHead.rotationPointZ = -0.0F;
        this.bipedHead.rotationPointY = f + 1.0F;
        this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
        this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
        this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
        this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
        this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
        this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;

        if (this.isAttacking) {
            float f2 = 1.0F;
            this.bipedHead.rotationPointY -= f2 * 5.0F;
        }
    }
}
