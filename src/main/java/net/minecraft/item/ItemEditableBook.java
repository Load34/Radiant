package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class ItemEditableBook extends Item {
    public ItemEditableBook() {
        this.setMaxStackSize(1);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (!ItemWritableBook.isNBTValid(nbt)) {
            return false;
        } else if (!nbt.hasKey("title", 8)) {
            return false;
        } else {
            String s = nbt.getString("title");
            return s != null && s.length() <= 32 && nbt.hasKey("author", 8);
        }
    }

    public static int getGeneration(ItemStack book) {
        return book.getTagCompound().getInteger("generation");
    }

    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString("title");

            if (!StringUtils.isNullOrEmpty(s)) {
                return s;
            }
        }

        return super.getItemStackDisplayName(stack);
    }

    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();
            String s = nbttagcompound.getString("author");

            if (!StringUtils.isNullOrEmpty(s)) {
                tooltip.add(Formatting.GRAY + StatCollector.translateToLocalFormatted("book.byAuthor", new Object[]{s}));
            }

            tooltip.add(Formatting.GRAY + StatCollector.translateToLocal("book.generation." + nbttagcompound.getInteger("generation")));
        }
    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (!worldIn.isRemote) {
            this.resolveContents(itemStackIn, playerIn);
        }

        playerIn.displayGUIBook(itemStackIn);
        playerIn.triggerAchievement(StatList.OBJECT_USE_STATS[Item.getIdFromItem(this)]);
        return itemStackIn;
    }

    private void resolveContents(ItemStack stack, EntityPlayer player) {
        if (stack != null && stack.getTagCompound() != null) {
            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (!nbttagcompound.getBoolean("resolved")) {
                nbttagcompound.setBoolean("resolved", true);

                if (validBookTagContents(nbttagcompound)) {
                    NBTTagList nbttaglist = nbttagcompound.getTagList("pages", 8);

                    for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                        String s = nbttaglist.getStringTagAt(i);
                        IChatComponent ichatcomponent;

                        try {
                            ichatcomponent = IChatComponent.Serializer.jsonToComponent(s);
                            ichatcomponent = ChatComponentProcessor.processComponent(player, ichatcomponent, player);
                        } catch (Exception exception) {
                            ichatcomponent = new ChatComponentText(s);
                        }

                        nbttaglist.set(i, new NBTTagString(IChatComponent.Serializer.componentToJson(ichatcomponent)));
                    }

                    nbttagcompound.setTag("pages", nbttaglist);

                    if (player instanceof EntityPlayerMP entityPlayerMP && player.getCurrentEquippedItem() == stack) {
                        Slot slot = player.openContainer.getSlotFromInventory(player.inventory, player.inventory.currentItem);
                        entityPlayerMP.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, slot.slotNumber, stack));
                    }
                }
            }
        }
    }

    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}
