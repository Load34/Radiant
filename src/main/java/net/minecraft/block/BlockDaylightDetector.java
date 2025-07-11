package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDaylightDetector;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockDaylightDetector extends BlockContainer {
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private final boolean inverted;

    public BlockDaylightDetector(boolean inverted) {
        super(Material.WOOD);
        this.inverted = inverted;
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, 0));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
        this.setCreativeTab(CreativeTabs.TAB_REDSTONE);
        this.setHardness(0.2F);
        this.setStepSound(soundTypeWood);
        this.setUnlocalizedName("daylightDetector");
    }

    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.375F, 1.0F);
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, Direction side) {
        return state.getValue(POWER);
    }

    public void updatePower(World worldIn, BlockPos pos) {
        if (!worldIn.provider.getHasNoSky()) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            int i = worldIn.getLightFor(LightType.SKY, pos) - worldIn.getSkylightSubtracted();
            float f = worldIn.getCelestialAngleRadians(1.0F);
            float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2.0F);
            f = f + (f1 - f) * 0.2F;
            i = Math.round(i * MathHelper.cos(f));
            i = MathHelper.clamp(i, 0, 15);

            if (this.inverted) {
                i = 15 - i;
            }

            if (iblockstate.getValue(POWER) != i) {
                worldIn.setBlockState(pos, iblockstate.withProperty(POWER, i), 3);
            }
        }
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, Direction side, float hitX, float hitY, float hitZ) {
        if (playerIn.isAllowEdit()) {
            if (!worldIn.isRemote) {
                if (this.inverted) {
                    worldIn.setBlockState(pos, Blocks.DAYLIGHT_DETECTOR.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR.updatePower(worldIn, pos);
                } else {
                    worldIn.setBlockState(pos, Blocks.DAYLIGHT_DETECTOR_INVERTED.getDefaultState().withProperty(POWER, state.getValue(POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR_INVERTED.updatePower(worldIn, pos);
                }

            }
            return true;
        } else {
            return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
        }
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR);
    }

    public Item getItem(World worldIn, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR);
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return 3;
    }

    public boolean canProvidePower() {
        return true;
    }

    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDaylightDetector();
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, meta);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWER);
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if (!this.inverted) {
            super.getSubBlocks(itemIn, tab, list);
        }
    }
}
