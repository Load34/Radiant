package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public class BlockStateMapper {
    private final Map<Block, IStateMapper> blockStateMap = new IdentityHashMap<>();
    private final Set<Block> setBuiltInBlocks = Sets.newIdentityHashSet();

    public void registerBlockStateMapper(Block p_178447_1_, IStateMapper p_178447_2_) {
        this.blockStateMap.put(p_178447_1_, p_178447_2_);
    }

    public void registerBuiltInBlocks(Block... p_178448_1_) {
        Collections.addAll(this.setBuiltInBlocks, p_178448_1_);
    }

    public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
        Map<IBlockState, ModelResourceLocation> map = new IdentityHashMap<>();

        for (Block block : Block.blockRegistry) {
            if (!this.setBuiltInBlocks.contains(block)) {
                map.putAll(MoreObjects.firstNonNull(this.blockStateMap.get(block), new DefaultStateMapper()).putStateModelLocations(block));
            }
        }

        return map;
    }
}
