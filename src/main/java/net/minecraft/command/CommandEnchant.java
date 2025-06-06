package net.minecraft.command;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandEnchant extends CommandBase {
    public String getCommandName() {
        return "enchant";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.enchant.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.enchant.usage");
        } else {
            EntityPlayer entityplayer = getPlayer(sender, args[0]);
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            int i;

            try {
                i = parseInt(args[1], 0);
            } catch (NumberInvalidException exception) {
                Enchantment enchantment = Enchantment.getEnchantmentByLocation(args[1]);

                if (enchantment == null) {
                    throw exception;
                }

                i = enchantment.effectId;
            }

            int j = 1;
            ItemStack itemstack = entityplayer.getCurrentEquippedItem();

            if (itemstack == null) {
                throw new CommandException("commands.enchant.noItem");
            } else {
                Enchantment enchantment1 = Enchantment.getEnchantmentById(i);

                if (enchantment1 == null) {
                    throw new NumberInvalidException("commands.enchant.notFound", i);
                } else if (!enchantment1.canApply(itemstack)) {
                    throw new CommandException("commands.enchant.cantEnchant");
                } else {
                    if (args.length >= 3) {
                        j = parseInt(args[2], enchantment1.getMinLevel(), enchantment1.getMaxLevel());
                    }

                    if (itemstack.hasTagCompound()) {
                        NBTTagList nbttaglist = itemstack.getEnchantmentTagList();

                        if (nbttaglist != null) {
                            for (int k = 0; k < nbttaglist.tagCount(); ++k) {
                                int l = nbttaglist.getCompoundTagAt(k).getShort("id");

                                if (Enchantment.getEnchantmentById(l) != null) {
                                    Enchantment enchantment2 = Enchantment.getEnchantmentById(l);

                                    if (!enchantment2.canApplyTogether(enchantment1)) {
                                        throw new CommandException("commands.enchant.cantCombine", enchantment1.getTranslatedName(j), enchantment2.getTranslatedName(nbttaglist.getCompoundTagAt(k).getShort("lvl")));
                                    }
                                }
                            }
                        }
                    }

                    itemstack.addEnchantment(enchantment1, j);
                    notifyOperators(sender, this, "commands.enchant.success");
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.func_181077_c()) : null);
    }

    protected String[] getListOfPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
