package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemSkull extends Item {
    private static final String[] SKULL_TYPES = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

    public ItemSkull() {
        this.setCreativeTab(CreativeTabs.TAB_DECORATIONS);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, Direction side, float hitX, float hitY, float hitZ) {
        if (side == Direction.DOWN) {
            return false;
        } else {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            Block block = iblockstate.getBlock();
            boolean flag = block.isReplaceable(worldIn, pos);

            if (!flag) {
                if (!worldIn.getBlockState(pos).getBlock().getMaterial().isSolid()) {
                    return false;
                }

                pos = pos.offset(side);
            }

            if (!playerIn.canPlayerEdit(pos, side, stack)) {
                return false;
            } else if (!Blocks.SKULL.canPlaceBlockAt(worldIn, pos)) {
                return false;
            } else {
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos, Blocks.SKULL.getDefaultState().withProperty(BlockSkull.FACING, side), 3);
                    int i = 0;

                    if (side == Direction.UP) {
                        i = MathHelper.floor((playerIn.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
                    }

                    TileEntity tileentity = worldIn.getTileEntity(pos);

                    if (tileentity instanceof TileEntitySkull tileentityskull) {

                        if (stack.getMetadata() == 3) {
                            GameProfile gameprofile = null;

                            if (stack.hasTagCompound()) {
                                NBTTagCompound nbttagcompound = stack.getTagCompound();

                                if (nbttagcompound.hasKey("SkullOwner", 10)) {
                                    gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                                } else if (nbttagcompound.hasKey("SkullOwner", 8) && !nbttagcompound.getString("SkullOwner").isEmpty()) {
                                    gameprofile = new GameProfile(null, nbttagcompound.getString("SkullOwner"));
                                }
                            }

                            tileentityskull.setPlayerProfile(gameprofile);
                        } else {
                            tileentityskull.setType(stack.getMetadata());
                        }

                        tileentityskull.setSkullRotation(i);
                        Blocks.SKULL.checkWitherSpawn(worldIn, pos, tileentityskull);
                    }

                    --stack.stackSize;
                }

                return true;
            }
        }
    }

    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < SKULL_TYPES.length; ++i) {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

    public int getMetadata(int damage) {
        return damage;
    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = stack.getMetadata();

        if (i < 0 || i >= SKULL_TYPES.length) {
            i = 0;
        }

        return super.getUnlocalizedName() + "." + SKULL_TYPES[i];
    }

    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() == 3 && stack.hasTagCompound()) {
            if (stack.getTagCompound().hasKey("SkullOwner", 8)) {
                return StatCollector.translateToLocalFormatted("item.skull.player.name", stack.getTagCompound().getString("SkullOwner"));
            }

            if (stack.getTagCompound().hasKey("SkullOwner", 10)) {
                NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("SkullOwner");

                if (nbttagcompound.hasKey("Name", 8)) {
                    return StatCollector.translateToLocalFormatted("item.skull.player.name", nbttagcompound.getString("Name"));
                }
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    public boolean updateItemStackNBT(NBTTagCompound nbt) {
        super.updateItemStackNBT(nbt);

        if (nbt.hasKey("SkullOwner", 8) && !nbt.getString("SkullOwner").isEmpty()) {
            GameProfile gameprofile = new GameProfile(null, nbt.getString("SkullOwner"));
            gameprofile = TileEntitySkull.updateGameprofile(gameprofile);
            nbt.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
            return true;
        } else {
            return false;
        }
    }
}
