package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.Random;

public class EntitySpider extends EntityMob {
    public EntitySpider(World worldIn) {
        super(worldIn);
        this.setSize(1.4F, 0.9F);
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new AISpiderAttack(this, EntityPlayer.class));
        this.tasks.addTask(4, new AISpiderAttack(this, EntityIronGolem.class));
        this.tasks.addTask(5, new EntityAIWander(this, 0.8D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(6, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new AISpiderTarget<>(this, EntityPlayer.class));
        this.targetTasks.addTask(3, new AISpiderTarget<>(this, EntityIronGolem.class));
    }

    public double getMountedYOffset() {
        return (this.height * 0.5F);
    }

    protected PathNavigate getNewNavigator(World worldIn) {
        return new PathNavigateClimber(this, worldIn);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte) 0);
    }

    public void onUpdate() {
        super.onUpdate();

        if (!this.worldObj.isRemote) {
            this.setBesideClimbableBlock(this.isCollidedHorizontally);
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
    }

    protected String getLivingSound() {
        return "mob.spider.say";
    }

    protected String getHurtSound() {
        return "mob.spider.say";
    }

    protected String getDeathSound() {
        return "mob.spider.death";
    }

    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound("mob.spider.step", 0.15F, 1.0F);
    }

    protected Item getDropItem() {
        return Items.STRING;
    }

    protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
        super.dropFewItems(wasRecentlyHit, lootingModifier);

        if (wasRecentlyHit && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + lootingModifier) > 0)) {
            this.dropItem(Items.SPIDER_EYE, 1);
        }
    }

    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public void setInWeb() {
    }

    public EntityGroup getCreatureAttribute() {
        return EntityGroup.ARTHROPOD;
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return potioneffectIn.getPotionID() != Potion.POISON.id && super.isPotionApplicable(potioneffectIn);
    }

    public boolean isBesideClimbableBlock() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean p_70839_1_) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(16);

        if (p_70839_1_) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 = (byte) (b0 & -2);
        }

        this.dataWatcher.updateObject(16, b0);
    }

    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);

        if (this.worldObj.rand.nextInt(100) == 0) {
            EntitySkeleton entityskeleton = new EntitySkeleton(this.worldObj);
            entityskeleton.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
            entityskeleton.onInitialSpawn(difficulty, null);
            this.worldObj.spawnEntityInWorld(entityskeleton);
            entityskeleton.mountEntity(this);
        }

        if (livingdata == null) {
            livingdata = new GroupData();

            if (this.worldObj.getDifficulty() == Difficulty.HARD && this.worldObj.rand.nextFloat() < 0.1F * difficulty.getClampedAdditionalDifficulty()) {
                ((GroupData) livingdata).func_111104_a(this.worldObj.rand);
            }
        }

        if (livingdata instanceof GroupData groupData) {
            int i = groupData.potionEffectId;

            if (i > 0 && Potion.POTION_TYPES[i] != null) {
                this.addPotionEffect(new PotionEffect(i, Integer.MAX_VALUE));
            }
        }

        return livingdata;
    }

    public float getEyeHeight() {
        return 0.65F;
    }

    static class AISpiderAttack extends EntityAIAttackOnCollide {
        public AISpiderAttack(EntitySpider spider, Class<? extends Entity> targetClass) {
            super(spider, targetClass, 1.0D, true);
        }

        public boolean continueExecuting() {
            float f = this.attacker.getBrightness(1.0F);

            if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
                this.attacker.setAttackTarget(null);
                return false;
            } else {
                return super.continueExecuting();
            }
        }

        protected double func_179512_a(EntityLivingBase attackTarget) {
            return (4.0F + attackTarget.width);
        }
    }

    static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget {
        public AISpiderTarget(EntitySpider spider, Class<T> classTarget) {
            super(spider, classTarget, true);
        }

        public boolean shouldExecute() {
            float f = this.taskOwner.getBrightness(1.0F);
            return !(f >= 0.5F) && super.shouldExecute();
        }
    }

    public static class GroupData implements IEntityLivingData {
        public int potionEffectId;

        public void func_111104_a(Random rand) {
            int i = rand.nextInt(5);

            if (i <= 1) {
                this.potionEffectId = Potion.MOVE_SPEED.id;
            } else if (i == 2) {
                this.potionEffectId = Potion.DAMAGE_BOOST.id;
            } else if (i == 3) {
                this.potionEffectId = Potion.REGENERATION.id;
            } else if (i <= 4) {
                this.potionEffectId = Potion.INVISIBILITY.id;
            }
        }
    }
}
