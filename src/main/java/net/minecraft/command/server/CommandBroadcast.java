package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.util.List;

public class CommandBroadcast extends CommandBase {
    public String getCommandName() {
        return "say";
    }

    public int getRequiredPermissionLevel() {
        return 1;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.say.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0 && !args[0].isEmpty()) {
            IChatComponent ichatcomponent = getChatComponentFromNthArg(sender, args, 0, true);
            MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("chat.type.announcement", sender.getDisplayName(), ichatcomponent));
        } else {
            throw new WrongUsageException("commands.say.usage");
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
