package net.minecraft.client.renderer.block.statemap;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StateMap extends StateMapperBase {
    private final IProperty<?> name;
    private final String suffix;
    private final List<IProperty<?>> ignored;

    private StateMap(IProperty<?> name, String suffix, List<IProperty<?>> ignored) {
        this.name = name;
        this.suffix = suffix;
        this.ignored = ignored;
    }

    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
        Map<IProperty, Comparable> map = new LinkedHashMap<>(state.getProperties());
        String s;

        if (this.name == null) {
            s = Block.blockRegistry.getNameForObject(state.getBlock()).toString();
        } else {
            s = ((IProperty) this.name).getName(map.remove(this.name));
        }

        if (this.suffix != null) {
            s = s + this.suffix;
        }

        for (IProperty<?> iproperty : this.ignored) {
            map.remove(iproperty);
        }

        return new ModelResourceLocation(s, this.getPropertyString(map));
    }

    public static class Builder {
        private IProperty<?> name;
        private String suffix;
        private final List<IProperty<?>> ignored = Lists.newArrayList();

        public Builder withName(IProperty<?> builderPropertyIn) {
            this.name = builderPropertyIn;
            return this;
        }

        public Builder withSuffix(String builderSuffixIn) {
            this.suffix = builderSuffixIn;
            return this;
        }

        public Builder ignore(IProperty<?>... p_178442_1_) {
            Collections.addAll(this.ignored, p_178442_1_);
            return this;
        }

        public StateMap build() {
            return new StateMap(this.name, this.suffix, this.ignored);
        }
    }
}
