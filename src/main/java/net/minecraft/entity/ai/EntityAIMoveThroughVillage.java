package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

import java.util.ArrayList;
import java.util.List;

public class EntityAIMoveThroughVillage extends EntityAIBase {
    private final EntityCreature theEntity;
    private final double movementSpeed;
    private PathEntity entityPathNavigate;
    private VillageDoorInfo doorInfo;
    private final boolean isNocturnal;
    private final List<VillageDoorInfo> doorList = new ArrayList<>();

    public EntityAIMoveThroughVillage(EntityCreature theEntityIn, double movementSpeedIn, boolean isNocturnalIn) {
        this.theEntity = theEntityIn;
        this.movementSpeed = movementSpeedIn;
        this.isNocturnal = isNocturnalIn;
        this.setMutexBits(1);

        if (!(theEntityIn.getNavigator() instanceof PathNavigateGround)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    public boolean shouldExecute() {
        this.resizeDoorList();

        if (this.isNocturnal && this.theEntity.worldObj.isDaytime()) {
            return false;
        } else {
            Village village = this.theEntity.worldObj.getVillageCollection().getNearestVillage(new BlockPos(this.theEntity), 0);

            if (village == null) {
                return false;
            } else {
                this.doorInfo = this.findNearestDoor(village);

                if (this.doorInfo == null) {
                    return false;
                } else {
                    PathNavigateGround pathnavigateground = (PathNavigateGround) this.theEntity.getNavigator();
                    boolean flag = pathnavigateground.getEnterDoors();
                    pathnavigateground.setBreakDoors(false);
                    this.entityPathNavigate = pathnavigateground.getPathToPos(this.doorInfo.getDoorBlockPos());
                    pathnavigateground.setBreakDoors(flag);

                    if (this.entityPathNavigate != null) {
                        return true;
                    } else {
                        Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 10, 7, new Vec3(this.doorInfo.getDoorBlockPos().getX(), this.doorInfo.getDoorBlockPos().getY(), this.doorInfo.getDoorBlockPos().getZ()));

                        if (vec3 == null) {
                            return false;
                        } else {
                            pathnavigateground.setBreakDoors(false);
                            this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                            pathnavigateground.setBreakDoors(flag);
                            return this.entityPathNavigate != null;
                        }
                    }
                }
            }
        }
    }

    public boolean continueExecuting() {
        if (this.theEntity.getNavigator().noPath()) {
            return false;
        } else {
            float f = this.theEntity.width + 4.0F;
            return this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) > (f * f);
        }
    }

    public void startExecuting() {
        this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
    }

    public void resetTask() {
        if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.getDoorBlockPos()) < 16.0D) {
            this.doorList.add(this.doorInfo);
        }
    }

    private VillageDoorInfo findNearestDoor(Village villageIn) {
        VillageDoorInfo villagedoorinfo = null;
        int i = Integer.MAX_VALUE;

        for (VillageDoorInfo villagedoorinfo1 : villageIn.getVillageDoorInfoList()) {
            int j = villagedoorinfo1.getDistanceSquared(MathHelper.floor(this.theEntity.posX), MathHelper.floor(this.theEntity.posY), MathHelper.floor(this.theEntity.posZ));

            if (j < i && !this.doesDoorListContain(villagedoorinfo1)) {
                villagedoorinfo = villagedoorinfo1;
                i = j;
            }
        }

        return villagedoorinfo;
    }

    private boolean doesDoorListContain(VillageDoorInfo doorInfoIn) {
        for (VillageDoorInfo villagedoorinfo : this.doorList) {
            if (doorInfoIn.getDoorBlockPos().equals(villagedoorinfo.getDoorBlockPos())) {
                return true;
            }
        }

        return false;
    }

    private void resizeDoorList() {
        if (this.doorList.size() > 15) {
            this.doorList.removeFirst();
        }
    }
}
