package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.*;

public class BlockSponge extends Block {
    public static final PropertyBool WET = PropertyBool.create("wet");

    protected BlockSponge() {
        super(Material.SPONGE);
        this.setDefaultState(this.blockState.getBaseState().withProperty(WET, Boolean.FALSE));
        this.setCreativeTab(CreativeTabs.TAB_BLOCK);
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".dry.name");
    }

    public int damageDropped(IBlockState state) {
        return state.getValue(WET) ? 1 : 0;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.tryAbsorb(worldIn, pos, state);
    }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.tryAbsorb(worldIn, pos, state);
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
    }

    protected void tryAbsorb(World worldIn, BlockPos pos, IBlockState state) {
        if (!state.getValue(WET) && this.absorb(worldIn, pos)) {
            worldIn.setBlockState(pos, state.withProperty(WET, Boolean.TRUE), 2);
            worldIn.playAuxSFX(2001, pos, Block.getIdFromBlock(Blocks.WATER));
        }
    }

    private boolean absorb(World worldIn, BlockPos pos) {
        Queue<Tuple<BlockPos, Integer>> queue = new LinkedList<>();
        ArrayList<BlockPos> arraylist = new ArrayList<>();
        queue.add(new Tuple<>(pos, 0));
        int i = 0;

        while (!queue.isEmpty()) {
            Tuple<BlockPos, Integer> tuple = queue.poll();
            BlockPos blockpos = tuple.getFirst();
            int j = tuple.getSecond();

            for (Direction enumfacing : Direction.values()) {
                BlockPos blockpos1 = blockpos.offset(enumfacing);

                if (worldIn.getBlockState(blockpos1).getBlock().getMaterial() == Material.WATER) {
                    worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 2);
                    arraylist.add(blockpos1);
                    ++i;

                    if (j < 6) {
                        queue.add(new Tuple<>(blockpos1, j + 1));
                    }
                }
            }

            if (i > 64) {
                break;
            }
        }

        for (BlockPos blockpos2 : arraylist) {
            worldIn.notifyNeighborsOfStateChange(blockpos2, Blocks.AIR);
        }

        return i > 0;
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(WET, (meta & 1) == 1);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(WET) ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, WET);
    }

    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(WET)) {
            Direction enumfacing = Direction.random(rand);

            if (enumfacing != Direction.UP && !World.doesBlockHaveSolidTopSurface(worldIn, pos.offset(enumfacing))) {
                double d0 = pos.getX();
                double d1 = pos.getY();
                double d2 = pos.getZ();

                if (enumfacing == Direction.DOWN) {
                    d1 = d1 - 0.05D;
                    d0 += rand.nextDouble();
                    d2 += rand.nextDouble();
                } else {
                    d1 = d1 + rand.nextDouble() * 0.8D;

                    if (enumfacing.getAxis() == Direction.Axis.X) {
                        d2 += rand.nextDouble();

                        if (enumfacing == Direction.EAST) {
                            ++d0;
                        } else {
                            d0 += 0.05D;
                        }
                    } else {
                        d0 += rand.nextDouble();

                        if (enumfacing == Direction.SOUTH) {
                            ++d2;
                        } else {
                            d2 += 0.05D;
                        }
                    }
                }

                worldIn.spawnParticle(ParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
