package net.minecraft.entity.boss;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.List;

public class EntityWither extends EntityMob implements IBossDisplayData, IRangedAttackMob {
    private final float[] field_82220_d = new float[2];
    private final float[] field_82221_e = new float[2];
    private final float[] field_82217_f = new float[2];
    private final float[] field_82218_g = new float[2];
    private final int[] field_82223_h = new int[2];
    private final int[] field_82224_i = new int[2];
    private int blockBreakCounter;
    private static final Predicate<Entity> ATTACK_ENTITY_SELECTOR = p_apply_1_ -> p_apply_1_ instanceof EntityLivingBase entityLivingBase && entityLivingBase.getCreatureAttribute() != EntityGroup.UNDEAD;

    public EntityWither(World worldIn) {
        super(worldIn);
        this.setHealth(this.getMaxHealth());
        this.setSize(0.9F, 3.5F);
        this.isImmuneToFire = true;
        ((PathNavigateGround) this.getNavigator()).setCanSwim(true);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 40, 20.0F));
        this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, ATTACK_ENTITY_SELECTOR));
        this.experienceValue = 50;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(17, 0);
        this.dataWatcher.addObject(18, 0);
        this.dataWatcher.addObject(19, 0);
        this.dataWatcher.addObject(20, 0);
    }

    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Invul", this.getInvulTime());
    }

    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setInvulTime(tagCompund.getInteger("Invul"));
    }

    protected String getLivingSound() {
        return "mob.wither.idle";
    }

    protected String getHurtSound() {
        return "mob.wither.hurt";
    }

    protected String getDeathSound() {
        return "mob.wither.death";
    }

    public void onLivingUpdate() {
        this.motionY *= 0.6000000238418579D;

        if (!this.worldObj.isRemote && this.getWatchedTargetId(0) > 0) {
            Entity entity = this.worldObj.getEntityByID(this.getWatchedTargetId(0));

            if (entity != null) {
                if (this.posY < entity.posY || !this.isArmored() && this.posY < entity.posY + 5.0D) {
                    if (this.motionY < 0.0D) {
                        this.motionY = 0.0D;
                    }

                    this.motionY += (0.5D - this.motionY) * 0.6000000238418579D;
                }

                double d0 = entity.posX - this.posX;
                double d1 = entity.posZ - this.posZ;
                double d3 = d0 * d0 + d1 * d1;

                if (d3 > 9.0D) {
                    double d5 = MathHelper.sqrt(d3);
                    this.motionX += (d0 / d5 * 0.5D - this.motionX) * 0.6000000238418579D;
                    this.motionZ += (d1 / d5 * 0.5D - this.motionZ) * 0.6000000238418579D;
                }
            }
        }

        if (this.motionX * this.motionX + this.motionZ * this.motionZ > 0.05000000074505806D) {
            this.rotationYaw = (float) MathHelper.atan2(this.motionZ, this.motionX) * (180.0F / (float) Math.PI) - 90.0F;
        }

        super.onLivingUpdate();

        for (int i = 0; i < 2; ++i) {
            this.field_82218_g[i] = this.field_82221_e[i];
            this.field_82217_f[i] = this.field_82220_d[i];
        }

        for (int j = 0; j < 2; ++j) {
            int k = this.getWatchedTargetId(j + 1);
            Entity entity1 = null;

            if (k > 0) {
                entity1 = this.worldObj.getEntityByID(k);
            }

            if (entity1 != null) {
                double d11 = this.func_82214_u(j + 1);
                double d12 = this.func_82208_v(j + 1);
                double d13 = this.func_82213_w(j + 1);
                double d6 = entity1.posX - d11;
                double d7 = entity1.posY + entity1.getEyeHeight() - d12;
                double d8 = entity1.posZ - d13;
                double d9 = MathHelper.sqrt(d6 * d6 + d8 * d8);
                float f = (float) (MathHelper.atan2(d8, d6) * 180.0D / Math.PI) - 90.0F;
                float f1 = (float) (-(MathHelper.atan2(d7, d9) * 180.0D / Math.PI));
                this.field_82220_d[j] = this.func_82204_b(this.field_82220_d[j], f1, 40.0F);
                this.field_82221_e[j] = this.func_82204_b(this.field_82221_e[j], f, 10.0F);
            } else {
                this.field_82221_e[j] = this.func_82204_b(this.field_82221_e[j], this.renderYawOffset, 10.0F);
            }
        }

        boolean flag = this.isArmored();

        for (int l = 0; l < 3; ++l) {
            double d10 = this.func_82214_u(l);
            double d2 = this.func_82208_v(l);
            double d4 = this.func_82213_w(l);
            this.worldObj.spawnParticle(ParticleTypes.SMOKE_NORMAL, d10 + this.rand.nextGaussian() * 0.30000001192092896D, d2 + this.rand.nextGaussian() * 0.30000001192092896D, d4 + this.rand.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);

            if (flag && this.worldObj.rand.nextInt(4) == 0) {
                this.worldObj.spawnParticle(ParticleTypes.SPELL_MOB, d10 + this.rand.nextGaussian() * 0.30000001192092896D, d2 + this.rand.nextGaussian() * 0.30000001192092896D, d4 + this.rand.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.getInvulTime() > 0) {
            for (int i1 = 0; i1 < 3; ++i1) {
                this.worldObj.spawnParticle(ParticleTypes.SPELL_MOB, this.posX + this.rand.nextGaussian(), this.posY + (this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }
    }

    protected void updateAITasks() {
        if (this.getInvulTime() > 0) {
            int j1 = this.getInvulTime() - 1;

            if (j1 <= 0) {
                this.worldObj.newExplosion(this, this.posX, this.posY + this.getEyeHeight(), this.posZ, 7.0F, false, this.worldObj.getGameRules().getBoolean("mobGriefing"));
                this.worldObj.playBroadcastSound(1013, new BlockPos(this), 0);
            }

            this.setInvulTime(j1);

            if (this.ticksExisted % 10 == 0) {
                this.heal(10.0F);
            }
        } else {
            super.updateAITasks();

            for (int i = 1; i < 3; ++i) {
                if (this.ticksExisted >= this.field_82223_h[i - 1]) {
                    this.field_82223_h[i - 1] = this.ticksExisted + 10 + this.rand.nextInt(10);

                    if (this.worldObj.getDifficulty() == Difficulty.NORMAL || this.worldObj.getDifficulty() == Difficulty.HARD) {
                        int j3 = i - 1;
                        int k3 = this.field_82224_i[i - 1];
                        this.field_82224_i[j3] = this.field_82224_i[i - 1] + 1;

                        if (k3 > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.getRandomDoubleInRange(this.rand, this.posX - f, this.posX + f);
                            double d1 = MathHelper.getRandomDoubleInRange(this.rand, this.posY - f1, this.posY + f1);
                            double d2 = MathHelper.getRandomDoubleInRange(this.rand, this.posZ - f, this.posZ + f);
                            this.launchWitherSkullToCoords(i + 1, d0, d1, d2, true);
                            this.field_82224_i[i - 1] = 0;
                        }
                    }

                    int k1 = this.getWatchedTargetId(i);

                    if (k1 > 0) {
                        Entity entity = this.worldObj.getEntityByID(k1);

                        if (entity != null && entity.isEntityAlive() && this.getDistanceSqToEntity(entity) <= 900.0D && this.canEntityBeSeen(entity)) {
                            if (entity instanceof EntityPlayer entityPlayer && entityPlayer.capabilities.disableDamage) {
                                this.updateWatchedTargetId(i, 0);
                            } else {
                                this.launchWitherSkullToEntity(i + 1, (EntityLivingBase) entity);
                                this.field_82223_h[i - 1] = this.ticksExisted + 40 + this.rand.nextInt(20);
                                this.field_82224_i[i - 1] = 0;
                            }
                        } else {
                            this.updateWatchedTargetId(i, 0);
                        }
                    } else {
                        List<EntityLivingBase> list = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(20.0D, 8.0D, 20.0D), Predicates.and(ATTACK_ENTITY_SELECTOR, EntitySelectors.NOT_SPECTATING));

                        for (int j2 = 0; j2 < 10 && !list.isEmpty(); ++j2) {
                            EntityLivingBase entitylivingbase = list.get(this.rand.nextInt(list.size()));

                            if (entitylivingbase != this && entitylivingbase.isEntityAlive() && this.canEntityBeSeen(entitylivingbase)) {
                                if (entitylivingbase instanceof EntityPlayer entityPlayer) {
                                    if (!entityPlayer.capabilities.disableDamage) {
                                        this.updateWatchedTargetId(i, entitylivingbase.getEntityId());
                                    }
                                } else {
                                    this.updateWatchedTargetId(i, entitylivingbase.getEntityId());
                                }

                                break;
                            }

                            list.remove(entitylivingbase);
                        }
                    }
                }
            }

            if (this.getAttackTarget() != null) {
                this.updateWatchedTargetId(0, this.getAttackTarget().getEntityId());
            } else {
                this.updateWatchedTargetId(0, 0);
            }

            if (this.blockBreakCounter > 0) {
                --this.blockBreakCounter;

                if (this.blockBreakCounter == 0 && this.worldObj.getGameRules().getBoolean("mobGriefing")) {
                    int i1 = MathHelper.floor(this.posY);
                    int l1 = MathHelper.floor(this.posX);
                    int i2 = MathHelper.floor(this.posZ);
                    boolean flag = false;

                    for (int k2 = -1; k2 <= 1; ++k2) {
                        for (int l2 = -1; l2 <= 1; ++l2) {
                            for (int j = 0; j <= 3; ++j) {
                                int i3 = l1 + k2;
                                int k = i1 + j;
                                int l = i2 + l2;
                                BlockPos blockpos = new BlockPos(i3, k, l);
                                Block block = this.worldObj.getBlockState(blockpos).getBlock();

                                if (block.getMaterial() != Material.AIR && canDestroyBlock(block)) {
                                    flag = this.worldObj.destroyBlock(blockpos, true) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.worldObj.playAuxSFXAtEntity(null, 1012, new BlockPos(this), 0);
                    }
                }
            }

            if (this.ticksExisted % 20 == 0) {
                this.heal(1.0F);
            }
        }
    }

    public static boolean canDestroyBlock(Block p_181033_0_) {
        return p_181033_0_ != Blocks.BEDROCK && p_181033_0_ != Blocks.END_PORTAL && p_181033_0_ != Blocks.END_PORTAL_FRAME && p_181033_0_ != Blocks.COMMAND_BLOCK && p_181033_0_ != Blocks.BARRIER;
    }

    public void func_82206_m() {
        this.setInvulTime(220);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    public void setInWeb() {
    }

    public int getTotalArmorValue() {
        return 4;
    }

    private double func_82214_u(int p_82214_1_) {
        if (p_82214_1_ <= 0) {
            return this.posX;
        } else {
            float f = (this.renderYawOffset + (180 * (p_82214_1_ - 1))) / 180.0F * (float) Math.PI;
            float f1 = MathHelper.cos(f);
            return this.posX + f1 * 1.3D;
        }
    }

    private double func_82208_v(int p_82208_1_) {
        return p_82208_1_ <= 0 ? this.posY + 3.0D : this.posY + 2.2D;
    }

    private double func_82213_w(int p_82213_1_) {
        if (p_82213_1_ <= 0) {
            return this.posZ;
        } else {
            float f = (this.renderYawOffset + (180 * (p_82213_1_ - 1))) / 180.0F * (float) Math.PI;
            float f1 = MathHelper.sin(f);
            return this.posZ + f1 * 1.3D;
        }
    }

    private float func_82204_b(float p_82204_1_, float p_82204_2_, float p_82204_3_) {
        float f = MathHelper.wrapAngle(p_82204_2_ - p_82204_1_);

        if (f > p_82204_3_) {
            f = p_82204_3_;
        }

        if (f < -p_82204_3_) {
            f = -p_82204_3_;
        }

        return p_82204_1_ + f;
    }

    private void launchWitherSkullToEntity(int p_82216_1_, EntityLivingBase p_82216_2_) {
        this.launchWitherSkullToCoords(p_82216_1_, p_82216_2_.posX, p_82216_2_.posY + p_82216_2_.getEyeHeight() * 0.5D, p_82216_2_.posZ, p_82216_1_ == 0 && this.rand.nextFloat() < 0.001F);
    }

    private void launchWitherSkullToCoords(int p_82209_1_, double x, double y, double z, boolean invulnerable) {
        this.worldObj.playAuxSFXAtEntity(null, 1014, new BlockPos(this), 0);
        double d0 = this.func_82214_u(p_82209_1_);
        double d1 = this.func_82208_v(p_82209_1_);
        double d2 = this.func_82213_w(p_82209_1_);
        double d3 = x - d0;
        double d4 = y - d1;
        double d5 = z - d2;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.worldObj, this, d3, d4, d5);

        if (invulnerable) {
            entitywitherskull.setInvulnerable(true);
        }

        entitywitherskull.posY = d1;
        entitywitherskull.posX = d0;
        entitywitherskull.posZ = d2;
        this.worldObj.spawnEntityInWorld(entitywitherskull);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_) {
        this.launchWitherSkullToEntity(0, target);
    }

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else if (source != DamageSource.DROWN && !(source.getEntity() instanceof EntityWither)) {
            if (this.getInvulTime() > 0 && source != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (this.isArmored()) {
                    Entity entity = source.getSourceOfDamage();

                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                Entity entity1 = source.getEntity();

                if (entity1 != null && !(entity1 instanceof EntityPlayer) && entity1 instanceof EntityLivingBase entityLivingBase && entityLivingBase.getCreatureAttribute() == this.getCreatureAttribute()) {
                    return false;
                } else {
                    if (this.blockBreakCounter <= 0) {
                        this.blockBreakCounter = 20;
                    }

                    for (int i = 0; i < this.field_82224_i.length; ++i) {
                        this.field_82224_i[i] += 3;
                    }

                    return super.attackEntityFrom(source, amount);
                }
            }
        } else {
            return false;
        }
    }

    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        EntityItem entityitem = this.dropItem(Items.NETHER_STAR, 1);

        if (entityitem != null) {
            entityitem.setNoDespawn();
        }

        if (!this.worldObj.isRemote) {
            for (EntityPlayer entityplayer : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(50.0D, 100.0D, 50.0D))) {
                entityplayer.triggerAchievement(AchievementList.KILL_WITHER);
            }
        }
    }

    protected void despawnEntity() {
        this.entityAge = 0;
    }

    public int getBrightnessForRender(float partialTicks) {
        return 15728880;
    }

    public void fall(float distance, float damageMultiplier) {
    }

    public void addPotionEffect(PotionEffect potioneffectIn) {
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(300.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6000000238418579D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
    }

    public float func_82207_a(int p_82207_1_) {
        return this.field_82221_e[p_82207_1_];
    }

    public float func_82210_r(int p_82210_1_) {
        return this.field_82220_d[p_82210_1_];
    }

    public int getInvulTime() {
        return this.dataWatcher.getWatchableObjectInt(20);
    }

    public void setInvulTime(int p_82215_1_) {
        this.dataWatcher.updateObject(20, p_82215_1_);
    }

    public int getWatchedTargetId(int p_82203_1_) {
        return this.dataWatcher.getWatchableObjectInt(17 + p_82203_1_);
    }

    public void updateWatchedTargetId(int targetOffset, int newId) {
        this.dataWatcher.updateObject(17 + targetOffset, newId);
    }

    public boolean isArmored() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    public EntityGroup getCreatureAttribute() {
        return EntityGroup.UNDEAD;
    }

    public void mountEntity(Entity entityIn) {
        this.ridingEntity = null;
    }
}
