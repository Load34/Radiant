package net.minecraft.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class ResourceLocation {
    protected final String resourceDomain;
    protected final String resourcePath;

    protected ResourceLocation(int unused, String... resourceName) {
        this.resourceDomain = StringUtils.isEmpty(resourceName[0]) ? "minecraft" : resourceName[0].toLowerCase();
        this.resourcePath = resourceName[1];
        Objects.requireNonNull(this.resourcePath);
    }

    public ResourceLocation(String resourceName) {
        this(0, splitObjectName(resourceName));
    }

    public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
        this(0, resourceDomainIn, resourcePathIn);
    }

    protected static String[] splitObjectName(String toSplit) {
        String[] astring = new String[]{null, toSplit};
        int i = toSplit.indexOf(58);

        if (i >= 0) {
            astring[1] = toSplit.substring(i + 1);

            if (i > 1) {
                astring[0] = toSplit.substring(0, i);
            }
        }

        return astring;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public String getResourceDomain() {
        return this.resourceDomain;
    }

    public String toString() {
        return this.resourceDomain + ':' + this.resourcePath;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof ResourceLocation resourcelocation)) {
            return false;
        } else {
            return this.resourceDomain.equals(resourcelocation.resourceDomain) && this.resourcePath.equals(resourcelocation.resourcePath);
        }
    }

    public int hashCode() {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
