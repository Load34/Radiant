package net.minecraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.Formatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GuiEnchantment extends GuiContainer {
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private static final ModelBook MODEL_BOOK = new ModelBook();
    private final InventoryPlayer playerInventory;
    private final Random random = new Random();
    private final ContainerEnchantment container;
    public int field_147073_u;
    public float field_147071_v;
    public float field_147069_w;
    public float field_147082_x;
    public float field_147081_y;
    public float field_147080_z;
    public float field_147076_A;
    ItemStack field_147077_B;
    private final IWorldNameable field_175380_I;

    public GuiEnchantment(InventoryPlayer inventory, World worldIn, IWorldNameable p_i45502_3_) {
        super(new ContainerEnchantment(inventory, worldIn));
        this.playerInventory = inventory;
        this.container = (ContainerEnchantment) this.inventorySlots;
        this.field_175380_I = p_i45502_3_;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(this.field_175380_I.getDisplayName().getUnformattedText(), 12, 5, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    public void updateScreen() {
        super.updateScreen();
        this.func_147068_g();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        for (int k = 0; k < 3; ++k) {
            int l = mouseX - (i + 60);
            int i1 = mouseY - (j + 14 + 19 * k);

            if (l >= 0 && i1 >= 0 && l < 108 && i1 < 19 && this.container.enchantItem(this.mc.player, k)) {
                this.mc.playerController.sendEnchantPacket(this.container.windowId, k);
            }
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.viewport((scaledresolution.getScaledWidth() - 320) / 2 * scaledresolution.getScaleFactor(), (scaledresolution.getScaledHeight() - 240) / 2 * scaledresolution.getScaleFactor(), 320 * scaledresolution.getScaleFactor(), 240 * scaledresolution.getScaleFactor());
        GlStateManager.translate(-0.34F, 0.23F, 0.0F);

        Matrix4f projectionMatrix = new Matrix4f().perspective(
                (float) Math.toRadians(90.0F),
                1.3333334F,
                9.0F,
                80.0F
        );
        FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
        projectionMatrix.get(projectionBuffer);
        GlStateManager.multMatrix(projectionBuffer);

        float f = 1.0F;
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(0.0F, 3.3F, -16.0F);
        GlStateManager.scale(f, f, f);
        float f1 = 5.0F;
        GlStateManager.scale(f1, f1, f1);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_BOOK_TEXTURE);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        float f2 = this.field_147076_A + (this.field_147080_z - this.field_147076_A) * partialTicks;
        GlStateManager.translate((1.0F - f2) * 0.2F, (1.0F - f2) * 0.1F, (1.0F - f2) * 0.25F);
        GlStateManager.rotate(-(1.0F - f2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        float f3 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.25F;
        float f4 = this.field_147069_w + (this.field_147071_v - this.field_147069_w) * partialTicks + 0.75F;
        f3 = (f3 - MathHelper.truncateDoubleToInt(f3)) * 1.6F - 0.3F;
        f4 = (f4 - MathHelper.truncateDoubleToInt(f4)) * 1.6F - 0.3F;

        if (f3 < 0.0F) {
            f3 = 0.0F;
        }

        if (f4 < 0.0F) {
            f4 = 0.0F;
        }

        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        if (f4 > 1.0F) {
            f4 = 1.0F;
        }

        GlStateManager.enableRescaleNormal();
        MODEL_BOOK.render(null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.xpSeed);
        int k = this.container.getLapisAmount();

        for (int l = 0; l < 3; ++l) {
            int i1 = i + 60;
            int j1 = i1 + 20;
            int k1 = 86;
            String s = EnchantmentNameParts.getInstance().generateNewRandomName();
            this.zLevel = 0.0F;
            this.mc.getTextureManager().bindTexture(ENCHANTMENT_TABLE_GUI_TEXTURE);
            int l1 = this.container.enchantLevels[l];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (l1 == 0) {
                this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 185, 108, 19);
            } else {
                String s1 = "" + l1;
                FontRenderer fontrenderer = this.mc.standardGalacticFontRenderer;
                int i2 = 6839882;

                if ((k < l + 1 || this.mc.player.experienceLevel < l1) && !this.mc.player.capabilities.isCreativeMode) {
                    this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 185, 108, 19);
                    this.drawTexturedModalRect(i1 + 1, j + 15 + 19 * l, 16 * l, 239, 16, 16);
                    fontrenderer.drawSplitString(s, j1, j + 16 + 19 * l, k1, (i2 & 16711422) >> 1);
                    i2 = 4226832;
                } else {
                    int j2 = mouseX - (i + 60);
                    int k2 = mouseY - (j + 14 + 19 * l);

                    if (j2 >= 0 && k2 >= 0 && j2 < 108 && k2 < 19) {
                        this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 204, 108, 19);
                        i2 = 16777088;
                    } else {
                        this.drawTexturedModalRect(i1, j + 14 + 19 * l, 0, 166, 108, 19);
                    }

                    this.drawTexturedModalRect(i1 + 1, j + 15 + 19 * l, 16 * l, 223, 16, 16);
                    fontrenderer.drawSplitString(s, j1, j + 16 + 19 * l, k1, i2);
                    i2 = 8453920;
                }

                fontrenderer = this.mc.fontRendererObj;
                fontrenderer.drawStringWithShadow(s1, (j1 + 86 - fontrenderer.getStringWidth(s1)), (j + 16 + 19 * l + 7), i2);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        boolean flag = this.mc.player.capabilities.isCreativeMode;
        int i = this.container.getLapisAmount();

        for (int j = 0; j < 3; ++j) {
            int k = this.container.enchantLevels[j];
            int l = this.container.enchantmentIds[j];
            int i1 = j + 1;

            if (this.isPointInRegion(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && k > 0 && l >= 0) {
                List<String> list = new ArrayList<>();

                if (l >= 0 && Enchantment.getEnchantmentById(l & 255) != null) {
                    String s = Enchantment.getEnchantmentById(l & 255).getTranslatedName((l & 65280) >> 8);
                    list.add(Formatting.WHITE.toString() + Formatting.ITALIC + I18n.format("container.enchant.clue", s));
                }

                if (!flag) {
                    if (l >= 0) {
                        list.add("");
                    }

                    if (this.mc.player.experienceLevel < k) {
                        list.add(Formatting.RED + "Level Requirement: " + this.container.enchantLevels[j]);
                    } else {
                        String s1;

                        if (i1 == 1) {
                            s1 = I18n.format("container.enchant.lapis.one");
                        } else {
                            s1 = I18n.format("container.enchant.lapis.many", i1);
                        }

                        if (i >= i1) {
                            list.add(Formatting.GRAY + s1);
                        } else {
                            list.add(Formatting.RED + s1);
                        }

                        if (i1 == 1) {
                            s1 = I18n.format("container.enchant.level.one");
                        } else {
                            s1 = I18n.format("container.enchant.level.many", i1);
                        }

                        list.add(Formatting.GRAY + s1);
                    }
                }

                this.drawHoveringText(list, mouseX, mouseY);
                break;
            }
        }
    }

    public void func_147068_g() {
        ItemStack itemstack = this.inventorySlots.getSlot(0).getStack();

        if (!ItemStack.areItemStacksEqual(itemstack, this.field_147077_B)) {
            this.field_147077_B = itemstack;

            while (true) {
                this.field_147082_x += (this.random.nextInt(4) - this.random.nextInt(4));

                if (this.field_147071_v > this.field_147082_x + 1.0F || this.field_147071_v < this.field_147082_x - 1.0F) {
                    break;
                }
            }
        }

        ++this.field_147073_u;
        this.field_147069_w = this.field_147071_v;
        this.field_147076_A = this.field_147080_z;
        boolean flag = false;

        for (int i = 0; i < 3; ++i) {
            if (this.container.enchantLevels[i] != 0) {
                flag = true;
                break;
            }
        }

        if (flag) {
            this.field_147080_z += 0.2F;
        } else {
            this.field_147080_z -= 0.2F;
        }

        this.field_147080_z = MathHelper.clamp(this.field_147080_z, 0.0F, 1.0F);
        float f1 = (this.field_147082_x - this.field_147071_v) * 0.4F;
        float f = 0.2F;
        f1 = MathHelper.clamp(f1, -f, f);
        this.field_147081_y += (f1 - this.field_147081_y) * 0.9F;
        this.field_147071_v += this.field_147081_y;
    }
}
