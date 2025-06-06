package net.minecraft.client.network;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.*;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private final NetworkManager netManager;
    private final GameProfile profile;
    private final GuiScreen guiScreenServer;
    private Minecraft gameController;
    private WorldClient clientWorldController;
    private boolean doneLoadingTerrain;
    private final Map<UUID, NetworkPlayerInfo> playerInfoMap = new HashMap<>();
    public int currentServerMaxPlayers = 20;
    private boolean field_147308_k = false;
    private final Random avRandomizer = new Random();

    public NetHandlerPlayClient(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager p_i46300_3_, GameProfile p_i46300_4_) {
        this.gameController = mcIn;
        this.guiScreenServer = p_i46300_2_;
        this.netManager = p_i46300_3_;
        this.profile = p_i46300_4_;
    }

    public void cleanup() {
        this.clientWorldController = null;
    }

    public void handleJoinGame(S01PacketJoinGame packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()), packetIn.getDimension(), packetIn.getDifficulty());
        this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.player.dimension = packetIn.getDimension();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        this.gameController.player.setEntityId(packetIn.getEntityId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.gameController.player.setReducedDebug(packetIn.isReducedDebugInfo());
        this.gameController.playerController.setGameType(packetIn.getGameType());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName())));
    }

    public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double d0 = packetIn.getX() / 32.0D;
        double d1 = packetIn.getY() / 32.0D;
        double d2 = packetIn.getZ() / 32.0D;
        Entity entity = null;

        switch (packetIn.getType()) {
            case 1 -> entity = new EntityBoat(this.clientWorldController, d0, d1, d2);
            case 2 -> entity = new EntityItem(this.clientWorldController, d0, d1, d2);
            case 10 ->
                    entity = EntityMinecart.getMinecart(this.clientWorldController, d0, d1, d2, EntityMinecart.MinecartType.byNetworkID(packetIn.getExtraData()));
            case 50 -> entity = new EntityTNTPrimed(this.clientWorldController, d0, d1, d2, null);
            case 51 -> entity = new EntityEnderCrystal(this.clientWorldController, d0, d1, d2);
            case 60 -> entity = new EntityArrow(this.clientWorldController, d0, d1, d2);
            case 61 -> entity = new EntitySnowball(this.clientWorldController, d0, d1, d2);
            case 62 -> entity = new EntityEgg(this.clientWorldController, d0, d1, d2);
            case 63 ->
                    entity = new EntityLargeFireball(this.clientWorldController, d0, d1, d2, packetIn.getSpeedX() / 8000.0D, packetIn.getSpeedY() / 8000.0D, packetIn.getSpeedZ() / 8000.0D);
            case 64 ->
                    entity = new EntitySmallFireball(this.clientWorldController, d0, d1, d2, packetIn.getSpeedX() / 8000.0D, packetIn.getSpeedY() / 8000.0D, packetIn.getSpeedZ() / 8000.0D);
            case 65 -> entity = new EntityEnderPearl(this.clientWorldController, d0, d1, d2);
            case 66 ->
                    entity = new EntityWitherSkull(this.clientWorldController, d0, d1, d2, packetIn.getSpeedX() / 8000.0D, packetIn.getSpeedY() / 8000.0D, packetIn.getSpeedZ() / 8000.0D);
            case 70 ->
                    entity = new EntityFallingBlock(this.clientWorldController, d0, d1, d2, Block.getStateById(packetIn.getExtraData() & 65535));
            case 71 ->
                    entity = new EntityItemFrame(this.clientWorldController, new BlockPos(MathHelper.floor_double(d0), MathHelper.floor_double(d1), MathHelper.floor_double(d2)), Direction.getHorizontal(packetIn.getExtraData()));
            case 72 -> entity = new EntityEnderEye(this.clientWorldController, d0, d1, d2);
            case 73 -> entity = new EntityPotion(this.clientWorldController, d0, d1, d2, packetIn.getExtraData());
            case 75 -> entity = new EntityExpBottle(this.clientWorldController, d0, d1, d2);
            case 76 -> entity = new EntityFireworkRocket(this.clientWorldController, d0, d1, d2, null);
            case 77 ->
                    entity = new EntityLeashKnot(this.clientWorldController, new BlockPos(MathHelper.floor_double(d0), MathHelper.floor_double(d1), MathHelper.floor_double(d2)));
            case 78 -> entity = new EntityArmorStand(this.clientWorldController, d0, d1, d2);
            case 90 -> {
                Entity entity1 = this.clientWorldController.getEntityByID(packetIn.getExtraData());

                if (entity1 instanceof EntityPlayer entityPlayer) {
                    entity = new EntityFishHook(this.clientWorldController, d0, d1, d2, entityPlayer);
                }
            }
        }

        if (entity != null) {
            entity.serverPosX = packetIn.getX();
            entity.serverPosY = packetIn.getY();
            entity.serverPosZ = packetIn.getZ();
            entity.rotationPitch = (packetIn.getPitch() * 360) / 256.0F;
            entity.rotationYaw = (packetIn.getYaw() * 360) / 256.0F;
            Entity[] aentity = entity.getParts();

            if (aentity != null) {
                int i = packetIn.getEntityID() - entity.getEntityId();

                for (Entity value : aentity) {
                    value.setEntityId(value.getEntityId() + i);
                }
            }

            entity.setEntityId(packetIn.getEntityID());
            this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);

            if (packetIn.getExtraData() > 0) {
                if (packetIn.getType() == 60) {
                    Entity entity2 = this.clientWorldController.getEntityByID(packetIn.getExtraData());

                    if (entity2 instanceof EntityLivingBase && entity instanceof EntityArrow entityArrow) {
                        entityArrow.shootingEntity = entity2;
                    }
                }

                entity.setVelocity(packetIn.getSpeedX() / 8000.0D, packetIn.getSpeedY() / 8000.0D, packetIn.getSpeedZ() / 8000.0D);
            }
        }
    }

    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = new EntityXPOrb(this.clientWorldController, packetIn.getX() / 32.0D, packetIn.getY() / 32.0D, packetIn.getZ() / 32.0D, packetIn.getXPValue());
        entity.serverPosX = packetIn.getX();
        entity.serverPosY = packetIn.getY();
        entity.serverPosZ = packetIn.getZ();
        entity.rotationYaw = 0.0F;
        entity.rotationPitch = 0.0F;
        entity.setEntityId(packetIn.getEntityID());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
    }

    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double d0 = packetIn.func_149051_d() / 32.0D;
        double d1 = packetIn.func_149050_e() / 32.0D;
        double d2 = packetIn.func_149049_f() / 32.0D;
        Entity entity = null;

        if (packetIn.func_149053_g() == 1) {
            entity = new EntityLightningBolt(this.clientWorldController, d0, d1, d2);
        }

        if (entity != null) {
            entity.serverPosX = packetIn.func_149051_d();
            entity.serverPosY = packetIn.func_149050_e();
            entity.serverPosZ = packetIn.func_149049_f();
            entity.rotationYaw = 0.0F;
            entity.rotationPitch = 0.0F;
            entity.setEntityId(packetIn.func_149052_c());
            this.clientWorldController.addWeatherEffect(entity);
        }
    }

    public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPainting entitypainting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entitypainting);
    }

    public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null) {
            entity.setVelocity(packetIn.getMotionX() / 8000.0D, packetIn.getMotionY() / 8000.0D, packetIn.getMotionZ() / 8000.0D);
        }
    }

    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null && packetIn.func_149376_c() != null) {
            entity.getDataWatcher().updateWatchedObjectsFromList(packetIn.func_149376_c());
        }
    }

    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double d0 = packetIn.getX() / 32.0D;
        double d1 = packetIn.getY() / 32.0D;
        double d2 = packetIn.getZ() / 32.0D;
        float f = (packetIn.getYaw() * 360) / 256.0F;
        float f1 = (packetIn.getPitch() * 360) / 256.0F;
        EntityOtherPlayerMP entityotherplayermp = new EntityOtherPlayerMP(this.gameController.world, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
        entityotherplayermp.prevPosX = entityotherplayermp.lastTickPosX = (entityotherplayermp.serverPosX = packetIn.getX());
        entityotherplayermp.prevPosY = entityotherplayermp.lastTickPosY = (entityotherplayermp.serverPosY = packetIn.getY());
        entityotherplayermp.prevPosZ = entityotherplayermp.lastTickPosZ = (entityotherplayermp.serverPosZ = packetIn.getZ());
        int i = packetIn.getCurrentItemID();

        if (i == 0) {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = null;
        } else {
            entityotherplayermp.inventory.mainInventory[entityotherplayermp.inventory.currentItem] = new ItemStack(Item.getItemById(i), 1, 0);
        }

        entityotherplayermp.setPositionAndRotation(d0, d1, d2, f, f1);
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityotherplayermp);
        List<DataWatcher.WatchableObject> list = packetIn.getMetaData();

        if (list != null) {
            entityotherplayermp.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null) {
            entity.serverPosX = packetIn.getX();
            entity.serverPosY = packetIn.getY();
            entity.serverPosZ = packetIn.getZ();
            double d0 = entity.serverPosX / 32.0D;
            double d1 = entity.serverPosY / 32.0D;
            double d2 = entity.serverPosZ / 32.0D;
            float f = (packetIn.getYaw() * 360) / 256.0F;
            float f1 = (packetIn.getPitch() * 360) / 256.0F;

            if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D) {
                entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
            } else {
                entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = packetIn.getOnGround();
        }
    }

    public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
            this.gameController.player.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
        }
    }

    public void handleEntityMovement(S14PacketEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null) {
            entity.serverPosX += packetIn.func_149062_c();
            entity.serverPosY += packetIn.func_149061_d();
            entity.serverPosZ += packetIn.func_149064_e();
            double d0 = entity.serverPosX / 32.0D;
            double d1 = entity.serverPosY / 32.0D;
            double d2 = entity.serverPosZ / 32.0D;
            float f = packetIn.func_149060_h() ? (packetIn.func_149066_f() * 360) / 256.0F : entity.rotationYaw;
            float f1 = packetIn.func_149060_h() ? (packetIn.func_149063_g() * 360) / 256.0F : entity.rotationPitch;
            entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, false);
            entity.onGround = packetIn.getOnGround();
        }
    }

    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null) {
            float f = (packetIn.getYaw() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (int i = 0; i < packetIn.getEntityIDs().length; ++i) {
            this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
        }
    }

    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        double d0 = packetIn.getX();
        double d1 = packetIn.getY();
        double d2 = packetIn.getZ();
        float f = packetIn.getYaw();
        float f1 = packetIn.getPitch();

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.Flag.X)) {
            d0 += entityplayer.posX;
        } else {
            entityplayer.motionX = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.Flag.Y)) {
            d1 += entityplayer.posY;
        } else {
            entityplayer.motionY = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.Flag.Z)) {
            d2 += entityplayer.posZ;
        } else {
            entityplayer.motionZ = 0.0D;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.Flag.X_ROT)) {
            f1 += entityplayer.rotationPitch;
        }

        if (packetIn.func_179834_f().contains(S08PacketPlayerPosLook.Flag.Y_ROT)) {
            f += entityplayer.rotationYaw;
        }

        entityplayer.setPositionAndRotation(d0, d1, d2, f, f1);
        this.netManager.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(entityplayer.posX, entityplayer.getEntityBoundingBox().minY, entityplayer.posZ, entityplayer.rotationYaw, entityplayer.rotationPitch, false));

        if (!this.doneLoadingTerrain) {
            this.gameController.player.prevPosX = this.gameController.player.posX;
            this.gameController.player.prevPosY = this.gameController.player.posY;
            this.gameController.player.prevPosZ = this.gameController.player.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen(null);
        }
    }

    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S22PacketMultiBlockChange.BlockUpdateData s22packetmultiblockchange$blockupdatedata : packetIn.getChangedBlocks()) {
            this.clientWorldController.invalidateRegionAndSetBlock(s22packetmultiblockchange$blockupdatedata.getPos(), s22packetmultiblockchange$blockupdatedata.getBlockState());
        }
    }

    public void handleChunkData(S21PacketChunkData packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.func_149274_i()) {
            if (packetIn.getExtractedSize() == 0) {
                this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
                return;
            }

            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
        }

        this.clientWorldController.invalidateBlockReceiveRegion(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
        Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
        chunk.fillChunk(packetIn.getExtractedDataBytes(), packetIn.getExtractedSize(), packetIn.func_149274_i());
        this.clientWorldController.markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);

        if (!packetIn.func_149274_i() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
        }
    }

    public void handleBlockChange(S23PacketBlockChange packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
    }

    public void handleDisconnect(S40PacketDisconnect packetIn) {
        this.netManager.closeChannel(packetIn.getReason());
    }

    public void onDisconnect(IChatComponent reason) {
        this.gameController.loadWorld(null);

        this.gameController.displayGuiScreen(new GuiDisconnected(Objects.requireNonNullElseGet(this.guiScreenServer, () -> new GuiMultiplayer(new GuiMainMenu())), "disconnect.lost", reason));
    }

    public void addToSendQueue(Packet<?> packet) {
        this.netManager.sendPacket(packet);
    }

    public void handleCollectItem(S0DPacketCollectItem packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
        EntityLivingBase entitylivingbase = (EntityLivingBase) this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entitylivingbase == null) {
            entitylivingbase = this.gameController.player;
        }

        if (entity != null) {
            if (entity instanceof EntityXPOrb) {
                this.clientWorldController.playSoundAtEntity(entity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            } else {
                this.clientWorldController.playSoundAtEntity(entity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entity, entitylivingbase, 0.5F));
            this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
        }
    }

    public void handleChat(S02PacketChat packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getType() == 2) {
            this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
        } else {
            this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
        }
    }

    public void handleAnimation(S0BPacketAnimation packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null) {
            if (packetIn.getAnimationType() == 0) {
                EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                entitylivingbase.swingItem();
            } else if (packetIn.getAnimationType() == 1) {
                entity.performHurtAnimation();
            } else if (packetIn.getAnimationType() == 2) {
                EntityPlayer entityplayer = (EntityPlayer) entity;
                entityplayer.wakeUpPlayer(false, false, false);
            } else if (packetIn.getAnimationType() == 4) {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, ParticleTypes.CRIT);
            } else if (packetIn.getAnimationType() == 5) {
                this.gameController.effectRenderer.emitParticleAtEntity(entity, ParticleTypes.CRIT_MAGIC);
            }
        }
    }

    public void handleUseBed(S0APacketUseBed packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getPosition());
    }

    public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        double d0 = packetIn.getX() / 32.0D;
        double d1 = packetIn.getY() / 32.0D;
        double d2 = packetIn.getZ() / 32.0D;
        float f = (packetIn.getYaw() * 360) / 256.0F;
        float f1 = (packetIn.getPitch() * 360) / 256.0F;
        EntityLivingBase entitylivingbase = (EntityLivingBase) EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.world);
        entitylivingbase.serverPosX = packetIn.getX();
        entitylivingbase.serverPosY = packetIn.getY();
        entitylivingbase.serverPosZ = packetIn.getZ();
        entitylivingbase.renderYawOffset = entitylivingbase.rotationYawHead = (packetIn.getHeadPitch() * 360) / 256.0F;
        Entity[] aentity = entitylivingbase.getParts();

        if (aentity != null) {
            int i = packetIn.getEntityID() - entitylivingbase.getEntityId();

            for (Entity entity : aentity) {
                entity.setEntityId(entity.getEntityId() + i);
            }
        }

        entitylivingbase.setEntityId(packetIn.getEntityID());
        entitylivingbase.setPositionAndRotation(d0, d1, d2, f, f1);
        entitylivingbase.motionX = (packetIn.getVelocityX() / 8000.0F);
        entitylivingbase.motionY = (packetIn.getVelocityY() / 8000.0F);
        entitylivingbase.motionZ = (packetIn.getVelocityZ() / 8000.0F);
        this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entitylivingbase);
        List<DataWatcher.WatchableObject> list = packetIn.func_149027_c();

        if (list != null) {
            entitylivingbase.getDataWatcher().updateWatchedObjectsFromList(list);
        }
    }

    public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.world.setTotalWorldTime(packetIn.getTotalWorldTime());
        this.gameController.world.setWorldTime(packetIn.getWorldTime());
    }

    public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.player.setSpawnPoint(packetIn.getSpawnPos(), true);
        this.gameController.world.getWorldInfo().setSpawn(packetIn.getSpawnPos());
    }

    public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
        Entity vehicle = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());

        if (packetIn.getLeash() == 0) {
            boolean flag = false;

            if (packetIn.getEntityId() == this.gameController.player.getEntityId()) {
                entity = this.gameController.player;

                if (vehicle instanceof EntityBoat entityBoat) {
                    entityBoat.setIsBoatEmpty(false);
                }

                flag = entity.ridingEntity == null && vehicle != null;
            } else if (vehicle instanceof EntityBoat entityBoat) {
                entityBoat.setIsBoatEmpty(true);
            }

            if (entity == null) {
                return;
            }

            entity.mountEntity(vehicle);

            if (flag) {
                GameSettings gamesettings = this.gameController.gameSettings;
                this.gameController.ingameGUI.setRecordPlaying(
                        I18n.format("mount.onboard", GameSettings.getKeyDisplayString(gamesettings.keyBindSneak.getKeyCode())),
                        false
                );
            }
        } else if (packetIn.getLeash() == 1 && entity instanceof EntityLiving entityLiving) {
            if (vehicle != null) {
                entityLiving.setLeashedToEntity(vehicle, false);
            } else {
                entityLiving.clearLeashed(false, false);
            }
        }
    }

    public void handleEntityStatus(S19PacketEntityStatus packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null) {
            if (packetIn.getOpCode() == 21) {
                this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian) entity));
            } else {
                entity.handleStatusUpdate(packetIn.getOpCode());
            }
        }
    }

    public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.player.setPlayerSPHealth(packetIn.getHealth());
        this.gameController.player.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.player.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
    }

    public void handleSetExperience(S1FPacketSetExperience packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.player.setXPStats(packetIn.func_149397_c(), packetIn.getTotalExperience(), packetIn.getLevel());
    }

    public void handleRespawn(S07PacketRespawn packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getDimensionID() != this.gameController.player.dimension) {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(this, new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.world.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()), packetIn.getDimensionID(), packetIn.getDifficulty());
            this.clientWorldController.setWorldScoreboard(scoreboard);
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.player.dimension = packetIn.getDimensionID();
            this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
        this.gameController.playerController.setGameType(packetIn.getGameType());
    }

    public void handleExplosion(S27PacketExplosion packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Explosion explosion = new Explosion(this.gameController.world, null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
        explosion.doExplosionB(true);
        this.gameController.player.motionX += packetIn.func_149149_c();
        this.gameController.player.motionY += packetIn.func_149144_d();
        this.gameController.player.motionZ += packetIn.func_149147_e();
    }

    public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayerSP entityplayersp = this.gameController.player;

        if ("minecraft:container".equals(packetIn.getGuiId())) {
            entityplayersp.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
            entityplayersp.displayVillagerTradeGui(new NpcMerchant(entityplayersp, packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        } else if ("EntityHorse".equals(packetIn.getGuiId())) {
            Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

            if (entity instanceof EntityHorse entityHorse) {
                entityplayersp.displayGUIHorse(entityHorse, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
                entityplayersp.openContainer.windowId = packetIn.getWindowId();
            }
        } else if (!packetIn.hasSlots()) {
            entityplayersp.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        } else {
            ContainerLocalMenu containerlocalmenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
            entityplayersp.displayGUIChest(containerlocalmenu);
            entityplayersp.openContainer.windowId = packetIn.getWindowId();
        }
    }

    public void handleSetSlot(S2FPacketSetSlot packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (packetIn.func_149175_c() == -1) {
            entityplayer.inventory.setItemStack(packetIn.func_149174_e());
        } else {
            boolean flag = false;

            if (this.gameController.currentScreen instanceof GuiContainerCreative guicontainercreative) {
                flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.TAB_INVENTORY.getTabIndex();
            }

            if (packetIn.func_149175_c() == 0 && packetIn.func_149173_d() >= 36 && packetIn.func_149173_d() < 45) {
                ItemStack itemstack = entityplayer.inventoryContainer.getSlot(packetIn.func_149173_d()).getStack();

                if (packetIn.func_149174_e() != null && (itemstack == null || itemstack.stackSize < packetIn.func_149174_e().stackSize)) {
                    packetIn.func_149174_e().animationsToGo = 5;
                }

                entityplayer.inventoryContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            } else if (packetIn.func_149175_c() == entityplayer.openContainer.windowId && (packetIn.func_149175_c() != 0 || !flag)) {
                entityplayer.openContainer.putStackInSlot(packetIn.func_149173_d(), packetIn.func_149174_e());
            }
        }
    }

    public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Container container = null;
        EntityPlayer entityplayer = this.gameController.player;

        if (packetIn.getWindowId() == 0) {
            container = entityplayer.inventoryContainer;
        } else if (packetIn.getWindowId() == entityplayer.openContainer.windowId) {
            container = entityplayer.openContainer;
        }

        if (container != null && !packetIn.func_148888_e()) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
        }
    }

    public void handleWindowItems(S30PacketWindowItems packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (packetIn.func_148911_c() == 0) {
            entityplayer.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
        } else if (packetIn.func_148911_c() == entityplayer.openContainer.windowId) {
            entityplayer.openContainer.putStacksInSlots(packetIn.getItemStacks());
        }
    }

    public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        TileEntity tileentity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());

        if (!(tileentity instanceof TileEntitySign)) {
            tileentity = new TileEntitySign();
            tileentity.setWorldObj(this.clientWorldController);
            tileentity.setPos(packetIn.getSignPosition());
        }

        this.gameController.player.openEditSign((TileEntitySign) tileentity);
    }

    public void handleUpdateSign(S33PacketUpdateSign packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean flag = false;

        if (this.gameController.world.isBlockLoaded(packetIn.getPos())) {
            TileEntity tileentity = this.gameController.world.getTileEntity(packetIn.getPos());

            if (tileentity instanceof TileEntitySign tileentitysign) {

                if (tileentitysign.getIsEditable()) {
                    System.arraycopy(packetIn.getLines(), 0, tileentitysign.signText, 0, 4);
                    tileentitysign.markDirty();
                }

                flag = true;
            }
        }

        if (!flag && this.gameController.player != null) {
            this.gameController.player.addChatMessage(new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ()));
        }
    }

    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (this.gameController.world.isBlockLoaded(packetIn.getPos())) {
            TileEntity tileentity = this.gameController.world.getTileEntity(packetIn.getPos());
            int i = packetIn.getTileEntityType();

            if (i == 1 && tileentity instanceof TileEntityMobSpawner || i == 2 && tileentity instanceof TileEntityCommandBlock || i == 3 && tileentity instanceof TileEntityBeacon || i == 4 && tileentity instanceof TileEntitySkull || i == 5 && tileentity instanceof TileEntityFlowerPot || i == 6 && tileentity instanceof TileEntityBanner) {
                tileentity.readFromNBT(packetIn.getNbtCompound());
            }
        }
    }

    public void handleWindowProperty(S31PacketWindowProperty packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;

        if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == packetIn.getWindowId()) {
            entityplayer.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
        }
    }

    public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());

        if (entity != null) {
            entity.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
        }
    }

    public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.player.closeScreenAndDropStack();
    }

    public void handleBlockAction(S24PacketBlockAction packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.world.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.world.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
    }

    public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (int i = 0; i < packetIn.getChunkCount(); ++i) {
            int j = packetIn.getChunkX(i);
            int k = packetIn.getChunkZ(i);
            this.clientWorldController.doPreChunk(j, k, true);
            this.clientWorldController.invalidateBlockReceiveRegion(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);
            Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(j, k);
            chunk.fillChunk(packetIn.getChunkBytes(i), packetIn.getChunkSize(i), true);
            this.clientWorldController.markBlockRangeForRenderUpdate(j << 4, 0, k << 4, (j << 4) + 15, 256, (k << 4) + 15);

            if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
                chunk.resetRelightChecks();
            }
        }
    }

    public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        int i = packetIn.getGameState();
        float f = packetIn.func_149137_d();
        int j = MathHelper.floor_float(f + 0.5F);

        if (i >= 0 && i < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[i] != null) {
            entityplayer.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[i]));
        }

        switch (i) {
            case 1 -> {
                this.clientWorldController.getWorldInfo().setRaining(true);
                this.clientWorldController.setRainStrength(0.0F);
            }
            case 2 -> {
                this.clientWorldController.getWorldInfo().setRaining(false);
                this.clientWorldController.setRainStrength(1.0F);
            }
            case 3 -> this.gameController.playerController.setGameType(WorldSettings.GameType.getByID(j));
            case 4 -> this.gameController.displayGuiScreen(new GuiWinGame()); // TODO: Only allow once per session
            // BUGFIX: Action 5, Shows Demo Screen
            case 6 ->
                    this.clientWorldController.playSound(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ, "random.successful_hit", 0.18F, 0.45F, false);
            case 7 -> // BUGFIX: HIGH VALUE -> LAG/CRASH | LOW VALUE -> WORLD COLOR CHANGES
                    this.clientWorldController.setRainStrength(Math.clamp(f, -2.0F, 2F)); // Allow leniency for servers to use.
            case 8 -> this.clientWorldController.setThunderStrength(f);
            case 10 -> {
                this.clientWorldController.spawnParticle(ParticleTypes.MOB_APPEARANCE, entityplayer.posX, entityplayer.posY, entityplayer.posZ, 0.0D, 0.0D, 0.0D);
                this.clientWorldController.playSound(entityplayer.posX, entityplayer.posY, entityplayer.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
            }
        }
    }

    public void handleMaps(S34PacketMaps packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        MapData mapdata = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.world);
        packetIn.setMapdataTo(mapdata);
        this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapdata);
    }

    public void handleEffect(S28PacketEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.isSoundServerwide()) {
            this.gameController.world.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        } else {
            this.gameController.world.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
        }
    }

    public void handleStatistics(S37PacketStatistics packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        boolean flag = false;

        for (Entry<StatBase, Integer> entry : packetIn.func_148974_c().entrySet()) {
            StatBase statbase = entry.getKey();
            int i = entry.getValue();

            if (statbase.isAchievement() && i > 0) {
                if (this.field_147308_k && this.gameController.player.getStatFileWriter().readStat(statbase) == 0) {
                    Achievement achievement = (Achievement) statbase;
                    this.gameController.guiAchievement.displayAchievement(achievement);

                    if (statbase == AchievementList.OPEN_INVENTORY) {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                flag = true;
            }

            this.gameController.player.getStatFileWriter().unlockAchievement(this.gameController.player, statbase, i);
        }

        if (!this.field_147308_k && !flag && this.gameController.gameSettings.showInventoryAchievementHint) {
            this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.OPEN_INVENTORY);
        }

        this.field_147308_k = true;

        if (this.gameController.currentScreen instanceof IProgressMeter iProgressMeter) {
            iProgressMeter.doneLoading();
        }
    }

    public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity instanceof EntityLivingBase entityLivingBase) {
            PotionEffect potioneffect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.func_179707_f());
            potioneffect.setPotionDurationMax(packetIn.func_149429_c());
            entityLivingBase.addPotionEffect(potioneffect);
        }
    }

    public void handleCombatEvent(S42PacketCombatEvent packetIn) {
    }// TODO: Possibly Fully remove packet?

    public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.world.getWorldInfo().setDifficulty(packetIn.getDifficulty());
        this.gameController.world.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
    }

    public void handleCamera(S43PacketCamera packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null) {
            this.gameController.setRenderViewEntity(entity);
        }
    }

    public void handleWorldBorder(S44PacketWorldBorder packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        packetIn.func_179788_a(this.clientWorldController.getWorldBorder());
    }

    public void handleTitle(S45PacketTitle packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        S45PacketTitle.Type type = packetIn.getType();
        String title = null;
        String subtitle = null;
        String packetText = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";

        switch (type) {
            case TITLE:
                title = packetText;
                break;

            case SUBTITLE:
                subtitle = packetText;
                break;

            case RESET:
                this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
                this.gameController.ingameGUI.setDefaultTitlesTimes();
                return;
        }

        this.gameController.ingameGUI.displayTitle(title, subtitle, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
    }

    public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
        if (!this.netManager.isLocalChannel()) {
            this.netManager.setCompressionThreshold(packetIn.getThreshold());
        }
    }

    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
        this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().isEmpty() ? null : packetIn.getHeader());
        this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().isEmpty() ? null : packetIn.getFooter());
    }

    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity instanceof EntityLivingBase entityLivingBase) {
            entityLivingBase.removePotionEffectClient(packetIn.getEffectId());
        }
    }

    public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        for (S38PacketPlayerListItem.AddPlayerData s38packetplayerlistitem$addplayerdata : packetIn.getEntries()) {
            if (packetIn.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
                this.playerInfoMap.remove(s38packetplayerlistitem$addplayerdata.getProfile().getId());
            } else {
                NetworkPlayerInfo networkplayerinfo = this.playerInfoMap.get(s38packetplayerlistitem$addplayerdata.getProfile().getId());

                if (packetIn.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                    networkplayerinfo = new NetworkPlayerInfo(s38packetplayerlistitem$addplayerdata);
                    this.playerInfoMap.put(networkplayerinfo.getGameProfile().getId(), networkplayerinfo);
                }

                if (networkplayerinfo != null) {
                    switch (packetIn.getAction()) {
                        case ADD_PLAYER:
                            networkplayerinfo.setGameType(s38packetplayerlistitem$addplayerdata.getGameMode());
                            networkplayerinfo.setResponseTime(s38packetplayerlistitem$addplayerdata.getPing());
                            break;

                        case UPDATE_GAME_MODE:
                            networkplayerinfo.setGameType(s38packetplayerlistitem$addplayerdata.getGameMode());
                            break;

                        case UPDATE_LATENCY:
                            networkplayerinfo.setResponseTime(s38packetplayerlistitem$addplayerdata.getPing());
                            break;

                        case UPDATE_DISPLAY_NAME:
                            networkplayerinfo.setDisplayName(s38packetplayerlistitem$addplayerdata.getDisplayName());
                    }
                }
            }
        }
    }

    public void handleKeepAlive(S00PacketKeepAlive packetIn) {
        this.addToSendQueue(new C00PacketKeepAlive(packetIn.func_149134_c()));
    }

    public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        EntityPlayer entityplayer = this.gameController.player;
        entityplayer.capabilities.isFlying = packetIn.isFlying();
        entityplayer.capabilities.isCreativeMode = packetIn.isCreativeMode();
        entityplayer.capabilities.disableDamage = packetIn.isInvulnerable();
        entityplayer.capabilities.allowFlying = packetIn.isAllowFlying();
        entityplayer.capabilities.setFlySpeed(packetIn.getFlySpeed());
        entityplayer.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
    }

    public void handleTabComplete(S3APacketTabComplete packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        String[] astring = packetIn.func_149630_c();

        if (this.gameController.currentScreen instanceof GuiChat guichat) {
            guichat.onAutocompleteResponse(astring);
        }
    }

    public void handleSoundEffect(S29PacketSoundEffect packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        this.gameController.world.playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
    }

    public void handleResourcePack(S48PacketResourcePackSend packetIn) {
        final String url = packetIn.getURL();
        final String hash = packetIn.getHash();

        try { // BUGFIX: Resource Pack Traversal Exploit
            // Check for unsupported protocols
            if (!url.matches("(http|https|level)://+.*")) {
                netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                throw new URISyntaxException(url, "Unsupported Protocol");
            }


            if (url.startsWith("level://")) {
                String s2 = url.substring("level://".length());

                // Check for invalid path
                String decode = URLDecoder.decode(s2, StandardCharsets.UTF_8);
                if (decode.contains("..") || !decode.endsWith("/resources.zip")) {
                    throw new URISyntaxException(url, "Invalid level storage resource pack path");
                }

                File file1 = new File(this.gameController.mcDataDir, "saves");
                File file2 = new File(file1, s2);

                if (file2.isFile()) {
                    this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                    Futures.addCallback(this.gameController.getResourcePackRepository().setResourcePackInstance(file2), new FutureCallback<>() {
                        public void onSuccess(Object p_onSuccess_1_) {
                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                        }

                        public void onFailure(Throwable throwable) {
                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                        }
                    }, Runnable::run);
                } else {
                    this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                }
            } else {
                if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() == ServerData.ServerResourceMode.ENABLED) {
                    this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                    Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new FutureCallback<>() {
                        public void onSuccess(Object p_onSuccess_1_) {
                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                        }

                        public void onFailure(Throwable throwable) {
                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                        }
                    }, Runnable::run);
                } else if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() != ServerData.ServerResourceMode.PROMPT) {
                    this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
                } else {
                    this.gameController.addScheduledTask(() -> NetHandlerPlayClient.this.gameController.displayGuiScreen(new GuiYesNo((result, id) -> {
                        NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();

                        if (result) {
                            if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
                            }

                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                            Futures.addCallback(NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new FutureCallback<>() {
                                public void onSuccess(Object p_onSuccess_1_) {
                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                }

                                public void onFailure(Throwable throwable) {
                                    NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                                }
                            }, Runnable::run);
                        } else {
                            if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.DISABLED);
                            }

                            NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
                        }

                        ServerList.func_147414_b(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                        NetHandlerPlayClient.this.gameController.displayGuiScreen(null);
                    }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0)));
                }
            }
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
    }

    public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = packetIn.getEntity(this.clientWorldController);

        if (entity != null) {
            entity.clientUpdateEntityNBT(packetIn.getTagCompound());
        }
    }

    public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if ("MC|TrList".equals(packetIn.getChannelName())) {
            PacketBuffer packetbuffer = packetIn.getBufferData();

            try {
                int i = packetbuffer.readInt();
                GuiScreen guiscreen = this.gameController.currentScreen;

                if (guiscreen != null && guiscreen instanceof GuiMerchant guiMerchant && i == this.gameController.player.openContainer.windowId) {
                    IMerchant imerchant = guiMerchant.getMerchant();
                    MerchantRecipeList merchantrecipelist = MerchantRecipeList.readFromBuf(packetbuffer);
                    imerchant.setRecipes(merchantrecipelist);
                }
            } catch (IOException exception) {
                LOGGER.error("Couldn't load trade info", exception);
            } finally {
                packetbuffer.release();
            }
        } else if ("MC|Brand".equals(packetIn.getChannelName())) {
            this.gameController.player.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
        } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
            ItemStack itemstack = this.gameController.player.getCurrentEquippedItem();

            if (itemstack != null && itemstack.getItem() == Items.WRITTEN_BOOK) {
                this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.player, itemstack, false));
            }
        }
    }

    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packetIn.func_149338_e() == 0) {
            ScoreObjective scoreobjective = scoreboard.addScoreObjective(packetIn.func_149339_c(), IScoreObjectiveCriteria.DUMMY);
            scoreobjective.setDisplayName(packetIn.func_149337_d());
            scoreobjective.setRenderType(packetIn.func_179817_d());
        } else {
            ScoreObjective scoreobjective1 = scoreboard.getObjective(packetIn.func_149339_c());

            if (packetIn.func_149338_e() == 1) {
                scoreboard.removeObjective(scoreobjective1);
            } else if (packetIn.func_149338_e() == 2) {
                scoreobjective1.setDisplayName(packetIn.func_149337_d());
                scoreobjective1.setRenderType(packetIn.func_179817_d());
            }
        }
    }

    public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.getObjectiveName());

        if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE) {
            Score score = scoreboard.getValueFromObjective(packetIn.getPlayerName(), scoreobjective);
            score.setScorePoints(packetIn.getScoreValue());
        } else if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE) {
            if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName())) {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), null);
            } else if (scoreobjective != null) {
                scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), scoreobjective);
            }
        }
    }

    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();

        if (packetIn.func_149370_d().isEmpty()) {
            scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), null);
        } else {
            ScoreObjective scoreobjective = scoreboard.getObjective(packetIn.func_149370_d());
            scoreboard.setObjectiveInDisplaySlot(packetIn.func_149371_c(), scoreobjective);
        }
    }

    public void handleTeams(S3EPacketTeams packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScorePlayerTeam scoreplayerteam;

        if (packetIn.getAction() == 0) {
            scoreplayerteam = scoreboard.createTeam(packetIn.getName());
        } else {
            scoreplayerteam = scoreboard.getTeam(packetIn.getName());
        }

        if (scoreplayerteam == null) return;

        if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {
            scoreplayerteam.setTeamName(packetIn.getDisplayName());
            scoreplayerteam.setNamePrefix(packetIn.getPrefix());
            scoreplayerteam.setNameSuffix(packetIn.getSuffix());
            scoreplayerteam.setChatFormat(Formatting.func_175744_a(packetIn.getColor()));
            scoreplayerteam.func_98298_a(packetIn.getFriendlyFlags());
            Team.EnumVisible team$enumvisible = Team.EnumVisible.func_178824_a(packetIn.getNameTagVisibility());

            if (team$enumvisible != null) {
                scoreplayerteam.setNameTagVisibility(team$enumvisible);
            }
        }

        if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
            for (String s : packetIn.getPlayers()) {
                scoreboard.addPlayerToTeam(s, packetIn.getName());
            }
        }

        if (packetIn.getAction() == 4) {
            for (String s1 : packetIn.getPlayers()) {
                scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
            }
        }

        if (packetIn.getAction() == 1) {
            scoreboard.removeTeam(scoreplayerteam);
        }
    }

    public void handleParticles(S2APacketParticles packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

        if (packetIn.getParticleCount() == 0) {
            double d0 = (packetIn.getParticleSpeed() * packetIn.getXOffset());
            double d2 = (packetIn.getParticleSpeed() * packetIn.getYOffset());
            double d4 = (packetIn.getParticleSpeed() * packetIn.getZOffset());

            try {
                this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate(), packetIn.getYCoordinate(), packetIn.getZCoordinate(), d0, d2, d4, packetIn.getParticleArgs());
            } catch (Throwable throwable) {
                LOGGER.warn("Could not spawn particle effect {}", packetIn.getParticleType());
            }
        } else {
            for (int i = 0; i < packetIn.getParticleCount(); ++i) {
                double d1 = this.avRandomizer.nextGaussian() * packetIn.getXOffset();
                double d3 = this.avRandomizer.nextGaussian() * packetIn.getYOffset();
                double d5 = this.avRandomizer.nextGaussian() * packetIn.getZOffset();
                double d6 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
                double d7 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
                double d8 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();

                try {
                    this.clientWorldController.spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.getXCoordinate() + d1, packetIn.getYCoordinate() + d3, packetIn.getZCoordinate() + d5, d6, d7, d8, packetIn.getParticleArgs());
                } catch (Throwable throwable) {
                    LOGGER.warn("Could not spawn particle effect {}", packetIn.getParticleType());
                    return;
                }
            }
        }
    }

    public void handleEntityProperties(S20PacketEntityProperties packetIn) {
        PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
        Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());

        if (entity != null) {
            if (!(entity instanceof EntityLivingBase entityLivingBase)) {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
            } else {
                BaseAttributeMap baseattributemap = entityLivingBase.getAttributeMap();

                for (S20PacketEntityProperties.Snapshot s20packetentityproperties$snapshot : packetIn.func_149441_d()) {
                    IAttributeInstance iattributeinstance = baseattributemap.getAttributeInstanceByName(s20packetentityproperties$snapshot.func_151409_a());

                    if (iattributeinstance == null) {
                        iattributeinstance = baseattributemap.registerAttribute(new RangedAttribute(null, s20packetentityproperties$snapshot.func_151409_a(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
                    }

                    iattributeinstance.setBaseValue(s20packetentityproperties$snapshot.func_151410_b());
                    iattributeinstance.removeAllModifiers();

                    for (AttributeModifier attributemodifier : s20packetentityproperties$snapshot.func_151408_c()) {
                        iattributeinstance.applyModifier(attributemodifier);
                    }
                }
            }
        }
    }

    public NetworkManager getNetworkManager() {
        return this.netManager;
    }

    public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
        return this.playerInfoMap.values();
    }

    public NetworkPlayerInfo getPlayerInfo(UUID p_175102_1_) {
        return this.playerInfoMap.get(p_175102_1_);
    }

    public NetworkPlayerInfo getPlayerInfo(String p_175104_1_) {
        for (NetworkPlayerInfo networkplayerinfo : this.playerInfoMap.values()) {
            if (networkplayerinfo.getGameProfile().getName().equals(p_175104_1_)) {
                return networkplayerinfo;
            }
        }

        return null;
    }

    public GameProfile getGameProfile() {
        return this.profile;
    }
}
