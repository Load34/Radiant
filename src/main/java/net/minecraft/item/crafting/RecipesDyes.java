package net.minecraft.item.crafting;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipesDyes {
    public void addRecipes(CraftingManager p_77607_1_) {
        for (int i = 0; i < 16; ++i) {
            p_77607_1_.addShapelessRecipe(new ItemStack(Blocks.WOOL, 1, i), new ItemStack(Items.DYE, 1, 15 - i), new ItemStack(Item.getItemFromBlock(Blocks.WOOL), 1, 0));
            p_77607_1_.addRecipe(new ItemStack(Blocks.STAINED_HARDENED_CLAY, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.HARDENED_CLAY), 'X', new ItemStack(Items.DYE, 1, i));
            p_77607_1_.addRecipe(new ItemStack(Blocks.STAINED_GLASS, 8, 15 - i), "###", "#X#", "###", '#', new ItemStack(Blocks.GLASS), 'X', new ItemStack(Items.DYE, 1, i));
            p_77607_1_.addRecipe(new ItemStack(Blocks.STAINED_GLASS_PANE, 16, i), "###", "###", '#', new ItemStack(Blocks.STAINED_GLASS, 1, i));
        }

        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getDyeDamage()), new ItemStack(Blocks.YELLOW_FLOWER, 1, BlockFlower.FlowerType.DANDELION.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.POPPY.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 3, DyeColor.WHITE.getDyeDamage()), Items.BONE);
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.PINK.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.ORANGE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.LIME.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.GREEN.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.GRAY.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.SILVER.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.GRAY.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 3, DyeColor.SILVER.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLACK.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.LIGHT_BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.CYAN.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.GREEN.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.PURPLE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.PURPLE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.PINK.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 3, DyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.PINK.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 4, DyeColor.MAGENTA.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.BLUE.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Items.DYE, 1, DyeColor.WHITE.getDyeDamage()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.LIGHT_BLUE.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.BLUE_ORCHID.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.ALLIUM.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.HOUSTONIA.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.RED.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.RED_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.ORANGE.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.ORANGE_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.WHITE_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.PINK_TULIP.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 1, DyeColor.SILVER.getDyeDamage()), new ItemStack(Blocks.RED_FLOWER, 1, BlockFlower.FlowerType.OXEYE_DAISY.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.YELLOW.getDyeDamage()), new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SUNFLOWER.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getDyeDamage()), new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.SYRINGA.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.RED.getDyeDamage()), new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.ROSE.getMeta()));
        p_77607_1_.addShapelessRecipe(new ItemStack(Items.DYE, 2, DyeColor.PINK.getDyeDamage()), new ItemStack(Blocks.DOUBLE_PLANT, 1, BlockDoublePlant.EnumPlantType.PAEONIA.getMeta()));

        for (int j = 0; j < 16; ++j) {
            p_77607_1_.addRecipe(new ItemStack(Blocks.CARPET, 3, j), "##", '#', new ItemStack(Blocks.WOOL, 1, j));
        }
    }
}
