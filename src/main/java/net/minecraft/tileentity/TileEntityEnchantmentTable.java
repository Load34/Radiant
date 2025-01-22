package net.minecraft.tileentity;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements ITickable, IInteractionObject {
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float field_145932_k;
    public float field_145929_l;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation;
    public float bookRotationPrev;
    public float field_145924_q;
    private static final Random RANDOM = new Random();
    private String customName;

    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("CustomName", 8)) {
            this.customName = compound.getString("CustomName");
        }
    }

    public void update() {
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((this.pos.getX() + 0.5F), (this.pos.getY() + 0.5F), (this.pos.getZ() + 0.5F), 3.0D);

        if (entityplayer != null) {
            double d0 = entityplayer.posX - (this.pos.getX() + 0.5F);
            double d1 = entityplayer.posZ - (this.pos.getZ() + 0.5F);
            this.field_145924_q = (float) MathHelper.atan2(d1, d0);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || RANDOM.nextInt(40) == 0) {
                float f1 = this.field_145932_k;

                while (true) {
                    this.field_145932_k += (RANDOM.nextInt(4) - RANDOM.nextInt(4));

                    if (f1 != this.field_145932_k) {
                        break;
                    }
                }
            }
        } else {
            this.field_145924_q += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation >= (float) Math.PI) {
            this.bookRotation -= ((float) Math.PI * 2.0F);
        }

        while (this.bookRotation < -(float) Math.PI) {
            this.bookRotation += ((float) Math.PI * 2.0F);
        }

        while (this.field_145924_q >= (float) Math.PI) {
            this.field_145924_q -= ((float) Math.PI * 2.0F);
        }

        while (this.field_145924_q < -(float) Math.PI) {
            this.field_145924_q += ((float) Math.PI * 2.0F);
        }

        float f2;

        for (f2 = this.field_145924_q - this.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2.0F)) {
        }

        while (f2 < -(float) Math.PI) {
            f2 += ((float) Math.PI * 2.0F);
        }

        this.bookRotation += f2 * 0.4F;
        this.bookSpread = MathHelper.clamp_float(this.bookSpread, 0.0F, 1.0F);
        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.field_145932_k - this.pageFlip) * 0.4F;
        float f3 = 0.2F;
        f = MathHelper.clamp_float(f, -f3, f3);
        this.field_145929_l += (f - this.field_145929_l) * 0.9F;
        this.pageFlip += this.field_145929_l;
    }

    public String getName() {
        return this.hasCustomName() ? this.customName : "container.enchant";
    }

    public boolean hasCustomName() {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String customNameIn) {
        this.customName = customNameIn;
    }

    public IChatComponent getDisplayName() {
        return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
    }

    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerEnchantment(playerInventory, this.worldObj, this.pos);
    }

    public String getGuiID() {
        return "minecraft:enchanting_table";
    }
}
