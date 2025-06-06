package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.RenderLayer;
import net.minecraft.world.World;

import java.util.Random;

public class BlockWeb extends Block {
    public BlockWeb() {
        super(Material.WEB);
        this.setCreativeTab(CreativeTabs.TAB_DECORATIONS);
    }

    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.setInWeb();
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    public boolean isFullCube() {
        return false;
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.STRING;
    }

    protected boolean canSilkHarvest() {
        return true;
    }

    public RenderLayer getBlockLayer() {
        return RenderLayer.CUTOUT;
    }
}
