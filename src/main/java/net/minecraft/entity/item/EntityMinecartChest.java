package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
    public EntityMinecartChest(World worldIn) {
        super(worldIn);
    }

    public EntityMinecartChest(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public void killMinecart(DamageSource source) {
        super.killMinecart(source);

        if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
            this.dropItemWithOffset(Item.getItemFromBlock(Blocks.CHEST), 1, 0.0F);
        }
    }

    public int getSizeInventory() {
        return 27;
    }

    public MinecartType getMinecartType() {
        return MinecartType.CHEST;
    }

    public IBlockState getDefaultDisplayTile() {
        return Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, Direction.NORTH);
    }

    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    public String getGuiID() {
        return "minecraft:chest";
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, this, playerIn);
    }
}
