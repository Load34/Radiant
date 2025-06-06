package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes {
    public RecipesMapExtending() {
        super(3, 3, new ItemStack[]{new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(Items.FILLED_MAP, 0, 32767), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER), new ItemStack(Items.PAPER)}, new ItemStack(Items.MAP, 0, 0));
    }

    public boolean matches(InventoryCrafting inv, World worldIn) {
        if (!super.matches(inv, worldIn)) {
            return false;
        } else {
            ItemStack itemstack = null;

            for (int i = 0; i < inv.getSizeInventory() && itemstack == null; ++i) {
                ItemStack itemstack1 = inv.getStackInSlot(i);

                if (itemstack1 != null && itemstack1.getItem() == Items.FILLED_MAP) {
                    itemstack = itemstack1;
                }
            }

            if (itemstack == null) {
                return false;
            } else {
                MapData mapdata = Items.FILLED_MAP.getMapData(itemstack, worldIn);
                return mapdata != null && mapdata.scale < 4;
            }
        }
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack itemstack = null;

        for (int i = 0; i < inv.getSizeInventory() && itemstack == null; ++i) {
            ItemStack itemstack1 = inv.getStackInSlot(i);

            if (itemstack1 != null && itemstack1.getItem() == Items.FILLED_MAP) {
                itemstack = itemstack1;
            }
        }

        itemstack = itemstack.copy();
        itemstack.stackSize = 1;

        if (itemstack.getTagCompound() == null) {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.getTagCompound().setBoolean("map_is_scaling", true);
        return itemstack;
    }
}
