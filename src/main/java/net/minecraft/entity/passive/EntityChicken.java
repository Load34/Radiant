package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityChicken extends EntityAnimal {
    public float wingRotation;
    public float destPos;
    public float field_70884_g;
    public float field_70888_h;
    public float wingRotDelta = 1.0F;
    public int timeUntilNextEgg;
    public boolean chickenJockey;

    public EntityChicken(World worldIn) {
        super(worldIn);
        this.setSize(0.4F, 0.7F);
        this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
        this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
        this.tasks.addTask(3, new EntityAITempt(this, 1.0D, Items.WHEAT_SEEDS, false));
        this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
    }

    public float getEyeHeight() {
        return this.height;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.field_70888_h = this.wingRotation;
        this.field_70884_g = this.destPos;
        this.destPos = (float) (this.destPos + (this.onGround ? -1 : 4) * 0.3D);
        this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);

        if (!this.onGround && this.wingRotDelta < 1.0F) {
            this.wingRotDelta = 1.0F;
        }

        this.wingRotDelta = (float) (this.wingRotDelta * 0.9D);

        if (!this.onGround && this.motionY < 0.0D) {
            this.motionY *= 0.6D;
        }

        this.wingRotation += this.wingRotDelta * 2.0F;

        if (!this.worldObj.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
            this.playSound("mob.chicken.plop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.dropItem(Items.EGG, 1);
            this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
        }
    }

    public void fall(float distance, float damageMultiplier) {
    }

    protected String getLivingSound() {
        return "mob.chicken.say";
    }

    protected String getHurtSound() {
        return "mob.chicken.hurt";
    }

    protected String getDeathSound() {
        return "mob.chicken.hurt";
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound("mob.chicken.step", 0.15F, 1.0F);
    }

    protected Item getDropItem() {
        return Items.FEATHER;
    }

    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        int i = this.rand.nextInt(3) + this.rand.nextInt(1 + lootingModifier);

        for (int j = 0; j < i; ++j) {
            this.dropItem(Items.FEATHER, 1);
        }

        if (this.isBurning()) {
            this.dropItem(Items.COOKED_CHICKEN, 1);
        } else {
            this.dropItem(Items.CHICKEN, 1);
        }
    }

    public EntityChicken createChild(EntityAgeable ageable) {
        return new EntityChicken(this.worldObj);
    }

    public boolean isBreedingItem(ItemStack stack) {
        return stack != null && stack.getItem() == Items.WHEAT_SEEDS;
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.chickenJockey = tagCompund.getBoolean("IsChickenJockey");

        if (tagCompund.hasKey("EggLayTime")) {
            this.timeUntilNextEgg = tagCompund.getInteger("EggLayTime");
        }
    }

    protected int getExperiencePoints(EntityPlayer player) {
        return this.isChickenJockey() ? 10 : super.getExperiencePoints(player);
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setBoolean("IsChickenJockey", this.chickenJockey);
        tagCompound.setInteger("EggLayTime", this.timeUntilNextEgg);
    }

    protected boolean canDespawn() {
        return this.isChickenJockey() && this.riddenByEntity == null;
    }

    public void updateRiderPosition() {
        super.updateRiderPosition();
        float f = MathHelper.sin(this.renderYawOffset * (float) Math.PI / 180.0F);
        float f1 = MathHelper.cos(this.renderYawOffset * (float) Math.PI / 180.0F);
        float f2 = 0.1F;
        float f3 = 0.0F;
        this.riddenByEntity.setPosition(this.posX + (f2 * f), this.posY + (this.height * 0.5F) + this.riddenByEntity.getYOffset() + f3, this.posZ - (f2 * f1));

        if (this.riddenByEntity instanceof EntityLivingBase entityLivingBase) {
            entityLivingBase.renderYawOffset = this.renderYawOffset;
        }
    }

    public boolean isChickenJockey() {
        return this.chickenJockey;
    }

    public void setChickenJockey(boolean jockey) {
        this.chickenJockey = jockey;
    }
}
