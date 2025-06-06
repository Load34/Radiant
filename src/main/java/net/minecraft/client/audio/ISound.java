package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public interface ISound {
    ResourceLocation getSoundLocation();

    boolean canRepeat();

    int getRepeatDelay();

    float getVolume();

    float getPitch();

    float getXPosF();

    float getYPosF();

    float getZPosF();

    AttenuationType getAttenuationType();

    enum AttenuationType {
        NONE(0),
        LINEAR(2);

        private final int type;

        AttenuationType(int typeIn) {
            this.type = typeIn;
        }

        public int getTypeInt() {
            return this.type;
        }
    }
}
