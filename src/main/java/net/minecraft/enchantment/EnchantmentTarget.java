package net.minecraft.enchantment;

import net.minecraft.item.*;

public enum EnchantmentTarget {
    ALL,
    ARMOR,
    ARMOR_FEET,
    ARMOR_LEGS,
    ARMOR_TORSO,
    ARMOR_HEAD,
    WEAPON,
    DIGGER,
    FISHING_ROD,
    BREAKABLE,
    BOW;

    public boolean canEnchantItem(Item p_77557_1_) {
        if (this == ALL) {
            return true;
        } else if (this == BREAKABLE && p_77557_1_.isDamageable()) {
            return true;
        } else if (p_77557_1_ instanceof ItemArmor) {
            if (this == ARMOR) {
                return true;
            } else {
                ItemArmor itemarmor = (ItemArmor) p_77557_1_;
                return itemarmor.armorType == 0 ? this == ARMOR_HEAD : (itemarmor.armorType == 2 ? this == ARMOR_LEGS : (itemarmor.armorType == 1 ? this == ARMOR_TORSO : (itemarmor.armorType == 3 ? this == ARMOR_FEET : false)));
            }
        } else {
            return p_77557_1_ instanceof ItemSword ? this == WEAPON : (p_77557_1_ instanceof ItemTool ? this == DIGGER : (p_77557_1_ instanceof ItemBow ? this == BOW : (p_77557_1_ instanceof ItemFishingRod ? this == FISHING_ROD : false)));
        }
    }
}
