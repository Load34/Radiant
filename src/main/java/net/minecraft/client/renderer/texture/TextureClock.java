package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite {
    private double currentAngle;
    private double angleDelta;

    public TextureClock(String iconName) {
        super(iconName);
    }

    public void updateAnimation() {
        if (!this.framesTextureData.isEmpty()) {
            Minecraft minecraft = Minecraft.getMinecraft();
            double d0 = 0.0D;

            if (minecraft.world != null && minecraft.player != null) {
                d0 = minecraft.world.getCelestialAngle(1.0F);

                if (!minecraft.world.provider.isSurfaceWorld()) {
                    d0 = Math.random();
                }
            }

            double d1;

            for (d1 = d0 - this.currentAngle; d1 < -0.5D; ++d1) {
            }

            while (d1 >= 0.5D) {
                --d1;
            }

            d1 = MathHelper.clamp(d1, -1.0D, 1.0D);
            this.angleDelta += d1 * 0.1D;
            this.angleDelta *= 0.8D;
            this.currentAngle += this.angleDelta;
            int i;

            for (i = (int) ((this.currentAngle + 1.0D) * this.framesTextureData.size()) % this.framesTextureData.size(); i < 0; i = (i + this.framesTextureData.size()) % this.framesTextureData.size()) {
            }

            if (i != this.frameCounter) {
                this.frameCounter = i;
                TextureUtil.uploadTextureMipmap(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
