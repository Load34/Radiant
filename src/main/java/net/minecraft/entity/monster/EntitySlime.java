package net.minecraft.entity.monster;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

public class EntitySlime extends EntityLiving implements IMob {
    public float squishAmount;
    public float squishFactor;
    public float prevSquishFactor;
    private boolean wasOnGround;

    public EntitySlime(World worldIn) {
        super(worldIn);
        this.moveHelper = new SlimeMoveHelper(this);
        this.tasks.addTask(1, new AISlimeFloat(this));
        this.tasks.addTask(2, new AISlimeAttack(this));
        this.tasks.addTask(3, new AISlimeFaceRandom(this));
        this.tasks.addTask(5, new AISlimeHop(this));
        this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
        this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte) 1);
    }

    protected void setSlimeSize(int size) {
        this.dataWatcher.updateObject(16, (byte) size);
        this.setSize(0.51000005F * size, 0.51000005F * size);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((size * size));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((0.2F + 0.1F * size));
        this.setHealth(this.getMaxHealth());
        this.experienceValue = size;
    }

    public int getSlimeSize() {
        return this.dataWatcher.getWatchableObjectByte(16);
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Size", this.getSlimeSize() - 1);
        tagCompound.setBoolean("wasOnGround", this.wasOnGround);
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        int i = tagCompund.getInteger("Size");

        if (i < 0) {
            i = 0;
        }

        this.setSlimeSize(i + 1);
        this.wasOnGround = tagCompund.getBoolean("wasOnGround");
    }

    protected ParticleTypes getParticleType() {
        return ParticleTypes.SLIME;
    }

    protected String getJumpSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    public void onUpdate() {
        if (!this.worldObj.isRemote && this.worldObj.getDifficulty() == Difficulty.PEACEFUL && this.getSlimeSize() > 0) {
            this.isDead = true;
        }

        this.squishFactor += (this.squishAmount - this.squishFactor) * 0.5F;
        this.prevSquishFactor = this.squishFactor;
        super.onUpdate();

        if (this.onGround && !this.wasOnGround) {
            int i = this.getSlimeSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.rand.nextFloat() * (float) Math.PI * 2.0F;
                float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * i * 0.5F * f1;
                World world = this.worldObj;
                ParticleTypes enumparticletypes = this.getParticleType();
                double d0 = this.posX + f2;
                double d1 = this.posZ + f3;
                world.spawnParticle(enumparticletypes, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D);
            }

            if (this.makesSoundOnLand()) {
                this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.squishAmount = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.squishAmount = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.alterSquishAmount();
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.6F;
    }

    protected int getJumpDelay() {
        return this.rand.nextInt(20) + 10;
    }

    protected EntitySlime createInstance() {
        return new EntitySlime(this.worldObj);
    }

    public void onDataWatcherUpdate(int dataID) {
        if (dataID == 16) {
            int i = this.getSlimeSize();
            this.setSize(0.51000005F * i, 0.51000005F * i);
            this.rotationYaw = this.rotationYawHead;
            this.renderYawOffset = this.rotationYawHead;

            if (this.isInWater() && this.rand.nextInt(20) == 0) {
                this.resetHeight();
            }
        }

        super.onDataWatcherUpdate(dataID);
    }

    public void setDead() {
        int i = this.getSlimeSize();

        if (!this.worldObj.isRemote && i > 1 && this.getHealth() <= 0.0F) {
            int j = 2 + this.rand.nextInt(3);

            for (int k = 0; k < j; ++k) {
                float f = ((k % 2) - 0.5F) * i / 4.0F;
                float f1 = ((k / 2) - 0.5F) * i / 4.0F;
                EntitySlime entityslime = this.createInstance();

                if (this.hasCustomName()) {
                    entityslime.setCustomNameTag(this.getCustomNameTag());
                }

                if (this.isNoDespawnRequired()) {
                    entityslime.enablePersistence();
                }

                entityslime.setSlimeSize(i / 2);
                entityslime.setLocationAndAngles(this.posX + f, this.posY + 0.5D, this.posZ + f1, this.rand.nextFloat() * 360.0F, 0.0F);
                this.worldObj.spawnEntityInWorld(entityslime);
            }
        }

        super.setDead();
    }

    public void applyEntityCollision(Entity entityIn) {
        super.applyEntityCollision(entityIn);

        if (entityIn instanceof EntityIronGolem && this.canDamagePlayer()) {
            this.func_175451_e((EntityLivingBase) entityIn);
        }
    }

    public void onCollideWithPlayer(EntityPlayer entityIn) {
        if (this.canDamagePlayer()) {
            this.func_175451_e(entityIn);
        }
    }

    protected void func_175451_e(EntityLivingBase p_175451_1_) {
        int i = this.getSlimeSize();

        if (this.canEntityBeSeen(p_175451_1_) && this.getDistanceSqToEntity(p_175451_1_) < 0.6D * i * 0.6D * i && p_175451_1_.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
            this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.applyEnchantments(this, p_175451_1_);
        }
    }

    public float getEyeHeight() {
        return 0.625F * this.height;
    }

    protected boolean canDamagePlayer() {
        return this.getSlimeSize() > 1;
    }

    protected int getAttackStrength() {
        return this.getSlimeSize();
    }

    protected String getHurtSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    protected String getDeathSound() {
        return "mob.slime." + (this.getSlimeSize() > 1 ? "big" : "small");
    }

    protected Item getDropItem() {
        return this.getSlimeSize() == 1 ? Items.SLIME_BALL : null;
    }

    public boolean getCanSpawnHere() {
        BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
        Chunk chunk = this.worldObj.getChunkFromBlockCoords(blockpos);

        if (this.worldObj.getWorldInfo().getTerrainType() == WorldType.FLAT && this.rand.nextInt(4) != 1) {
            return false;
        } else {
            if (this.worldObj.getDifficulty() != Difficulty.PEACEFUL) {
                BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos);

                if (biomegenbase == BiomeGenBase.SWAMPLAND && this.posY > 50.0D && this.posY < 70.0D && this.rand.nextFloat() < 0.5F && this.rand.nextFloat() < this.worldObj.getCurrentMoonPhaseFactor() && this.worldObj.getLightFromNeighbors(new BlockPos(this)) <= this.rand.nextInt(8)) {
                    return super.getCanSpawnHere();
                }

                if (this.rand.nextInt(10) == 0 && chunk.getRandomWithSeed(987234911L).nextInt(10) == 0 && this.posY < 40.0D) {
                    return super.getCanSpawnHere();
                }
            }

            return false;
        }
    }

    protected float getSoundVolume() {
        return 0.4F * this.getSlimeSize();
    }

    public int getVerticalFaceSpeed() {
        return 0;
    }

    protected boolean makesSoundOnJump() {
        return this.getSlimeSize() > 0;
    }

    protected boolean makesSoundOnLand() {
        return this.getSlimeSize() > 2;
    }

    protected void jump() {
        this.motionY = 0.41999998688697815D;
        this.isAirBorne = true;
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        int i = this.rand.nextInt(3);

        if (i < 2 && this.rand.nextFloat() < 0.5F * difficulty.getClampedAdditionalDifficulty()) {
            ++i;
        }

        int j = 1 << i;
        this.setSlimeSize(j);
        return super.onInitialSpawn(difficulty, livingdata);
    }

    static class AISlimeAttack extends EntityAIBase {
        private final EntitySlime slime;
        private int field_179465_b;

        public AISlimeAttack(EntitySlime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(2);
        }

        public boolean shouldExecute() {
            EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
            return entitylivingbase != null && (entitylivingbase.isEntityAlive() && (!(entitylivingbase instanceof EntityPlayer entityPlayer) || !entityPlayer.capabilities.disableDamage));
        }

        public void startExecuting() {
            this.field_179465_b = 300;
            super.startExecuting();
        }

        public boolean continueExecuting() {
            EntityLivingBase entitylivingbase = this.slime.getAttackTarget();
            return entitylivingbase != null && (entitylivingbase.isEntityAlive() && ((!(entitylivingbase instanceof EntityPlayer entityPlayer) || !entityPlayer.capabilities.disableDamage) && --this.field_179465_b > 0));
        }

        public void updateTask() {
            this.slime.faceEntity(this.slime.getAttackTarget(), 10.0F, 10.0F);
            ((SlimeMoveHelper) this.slime.getMoveHelper()).func_179920_a(this.slime.rotationYaw, this.slime.canDamagePlayer());
        }
    }

    static class AISlimeFaceRandom extends EntityAIBase {
        private final EntitySlime slime;
        private float field_179459_b;
        private int field_179460_c;

        public AISlimeFaceRandom(EntitySlime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(2);
        }

        public boolean shouldExecute() {
            return this.slime.getAttackTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.isInLava());
        }

        public void updateTask() {
            if (--this.field_179460_c <= 0) {
                this.field_179460_c = 40 + this.slime.getRNG().nextInt(60);
                this.field_179459_b = this.slime.getRNG().nextInt(360);
            }

            ((SlimeMoveHelper) this.slime.getMoveHelper()).func_179920_a(this.field_179459_b, false);
        }
    }

    static class AISlimeFloat extends EntityAIBase {
        private final EntitySlime slime;

        public AISlimeFloat(EntitySlime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(5);
            ((PathNavigateGround) slimeIn.getNavigator()).setCanSwim(true);
        }

        public boolean shouldExecute() {
            return this.slime.isInWater() || this.slime.isInLava();
        }

        public void updateTask() {
            if (this.slime.getRNG().nextFloat() < 0.8F) {
                this.slime.getJumpHelper().setJumping();
            }

            ((SlimeMoveHelper) this.slime.getMoveHelper()).setSpeed(1.2D);
        }
    }

    static class AISlimeHop extends EntityAIBase {
        private final EntitySlime slime;

        public AISlimeHop(EntitySlime slimeIn) {
            this.slime = slimeIn;
            this.setMutexBits(5);
        }

        public boolean shouldExecute() {
            return true;
        }

        public void updateTask() {
            ((SlimeMoveHelper) this.slime.getMoveHelper()).setSpeed(1.0D);
        }
    }

    static class SlimeMoveHelper extends EntityMoveHelper {
        private float field_179922_g;
        private int field_179924_h;
        private final EntitySlime slime;
        private boolean field_179923_j;

        public SlimeMoveHelper(EntitySlime slimeIn) {
            super(slimeIn);
            this.slime = slimeIn;
        }

        public void func_179920_a(float p_179920_1_, boolean p_179920_2_) {
            this.field_179922_g = p_179920_1_;
            this.field_179923_j = p_179920_2_;
        }

        public void setSpeed(double speedIn) {
            this.speed = speedIn;
            this.update = true;
        }

        public void onUpdateMoveHelper() {
            this.entity.rotationYaw = this.limitAngle(this.entity.rotationYaw, this.field_179922_g, 30.0F);
            this.entity.rotationYawHead = this.entity.rotationYaw;
            this.entity.renderYawOffset = this.entity.rotationYaw;

            if (!this.update) {
                this.entity.setMoveForward(0.0F);
            } else {
                this.update = false;

                if (this.entity.onGround) {
                    this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));

                    if (this.field_179924_h-- <= 0) {
                        this.field_179924_h = this.slime.getJumpDelay();

                        if (this.field_179923_j) {
                            this.field_179924_h /= 3;
                        }

                        this.slime.getJumpHelper().setJumping();

                        if (this.slime.makesSoundOnJump()) {
                            this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), ((this.slime.getRNG().nextFloat() - this.slime.getRNG().nextFloat()) * 0.2F + 1.0F) * 0.8F);
                        }
                    } else {
                        this.slime.moveStrafing = this.slime.moveForward = 0.0F;
                        this.entity.setAIMoveSpeed(0.0F);
                    }
                } else {
                    this.entity.setAIMoveSpeed((float) (this.speed * this.entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue()));
                }
            }
        }
    }
}
