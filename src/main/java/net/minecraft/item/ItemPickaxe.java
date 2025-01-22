package net.minecraft.item;

import com.google.common.collect.Sets;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemPickaxe extends ItemTool {
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.activator_rail, Blocks.coal_ore, Blocks.cobblestone, Blocks.detector_rail, Blocks.diamond_block, Blocks.diamond_ore, Blocks.double_stone_slab, Blocks.golden_rail, Blocks.gold_block, Blocks.gold_ore, Blocks.ice, Blocks.iron_block, Blocks.iron_ore, Blocks.lapis_block, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.mossy_cobblestone, Blocks.netherrack, Blocks.packed_ice, Blocks.rail, Blocks.redstone_ore, Blocks.sandstone, Blocks.red_sandstone, Blocks.stone, Blocks.stone_slab);

    protected ItemPickaxe(Item.ToolMaterial material) {
        super(2.0F, material, EFFECTIVE_ON);
    }

    public boolean canHarvestBlock(Block blockIn) {
        return blockIn == Blocks.obsidian ? this.toolMaterial.getHarvestLevel() == 3 : (blockIn != Blocks.diamond_block && blockIn != Blocks.diamond_ore ? (blockIn != Blocks.emerald_ore && blockIn != Blocks.emerald_block ? (blockIn != Blocks.gold_block && blockIn != Blocks.gold_ore ? (blockIn != Blocks.iron_block && blockIn != Blocks.iron_ore ? (blockIn != Blocks.lapis_block && blockIn != Blocks.lapis_ore ? (blockIn != Blocks.redstone_ore && blockIn != Blocks.lit_redstone_ore ? (blockIn.getMaterial() == Material.ROCK ? true : (blockIn.getMaterial() == Material.IRON ? true : blockIn.getMaterial() == Material.ANVIL)) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 1) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2) : this.toolMaterial.getHarvestLevel() >= 2);
    }

    public float getStrVsBlock(ItemStack stack, Block state) {
        return state.getMaterial() != Material.IRON && state.getMaterial() != Material.ANVIL && state.getMaterial() != Material.ROCK ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
    }
}
