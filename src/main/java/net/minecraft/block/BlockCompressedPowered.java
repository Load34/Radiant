package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends Block {
    public BlockCompressedPowered(Material p_i46386_1_, MapColor p_i46386_2_) {
        super(p_i46386_1_, p_i46386_2_);
    }

    public boolean canProvidePower() {
        return true;
    }

    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, Direction side) {
        return 15;
    }
}
