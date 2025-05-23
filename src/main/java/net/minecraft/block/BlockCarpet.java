package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class BlockCarpet extends Block {
    public static final PropertyEnum<DyeColor> COLOR = PropertyEnum.create("color", DyeColor.class);

    protected BlockCarpet() {
        super(Material.CARPET);
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, DyeColor.WHITE));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.TAB_DECORATIONS);
        this.setBlockBoundsFromMeta(0);
    }

    public MapColor getMapColor(IBlockState state) {
        return state.getValue(COLOR).getMapColor();
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public void setBlockBoundsForItemRender() {
        this.setBlockBoundsFromMeta(0);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.setBlockBoundsFromMeta(0);
    }

    protected void setBlockBoundsFromMeta(int meta) {
        int i = 0;
        float f = ((1 + i)) / 16.0F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.checkForDrop(worldIn, pos, state);
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return !worldIn.isAirBlock(pos.down());
    }

    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, Direction side) {
        return side == Direction.UP ? true : super.shouldSideBeRendered(worldIn, pos, side);
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < 16; ++i) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(COLOR, DyeColor.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(COLOR).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, COLOR);
    }
}
