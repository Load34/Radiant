package net.minecraft.command.server;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CommandSetBlock extends CommandBase {
    public String getCommandName() {
        return "setblock";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.setblock.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException("commands.setblock.usage");
        } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            Block block = CommandBase.getBlockByText(sender, args[3]);
            int i = 0;

            if (args.length >= 5) {
                i = parseInt(args[4], 0, 15);
            }

            World world = sender.getEntityWorld();

            if (!world.isBlockLoaded(blockpos)) {
                throw new CommandException("commands.setblock.outOfWorld");
            } else {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                boolean flag = false;

                if (args.length >= 7 && block.hasTileEntity()) {
                    String s = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();

                    try {
                        nbttagcompound = JsonToNBT.getTagFromJson(s);
                        flag = true;
                    } catch (NBTException exception) {
                        throw new CommandException("commands.setblock.tagError", exception.getMessage());
                    }
                }

                if (args.length >= 6) {
                    if (args[5].equals("destroy")) {
                        world.destroyBlock(blockpos, true);

                        if (block == Blocks.AIR) {
                            notifyOperators(sender, this, "commands.setblock.success");
                            return;
                        }
                    } else if (args[5].equals("keep") && !world.isAirBlock(blockpos)) {
                        throw new CommandException("commands.setblock.noChange");
                    }
                }

                TileEntity tileentity1 = world.getTileEntity(blockpos);

                if (tileentity1 != null) {
                    if (tileentity1 instanceof IInventory iInventory) {
                        iInventory.clear();
                    }

                    world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), block == Blocks.AIR ? 2 : 4);
                }

                IBlockState iblockstate = block.getStateFromMeta(i);

                if (!world.setBlockState(blockpos, iblockstate, 2)) {
                    throw new CommandException("commands.setblock.noChange");
                } else {
                    if (flag) {
                        TileEntity tileentity = world.getTileEntity(blockpos);

                        if (tileentity != null) {
                            nbttagcompound.setInteger("x", blockpos.getX());
                            nbttagcompound.setInteger("y", blockpos.getY());
                            nbttagcompound.setInteger("z", blockpos.getZ());
                            tileentity.readFromNBT(nbttagcompound);
                        }
                    }

                    world.notifyNeighborsRespectDebug(blockpos, iblockstate.getBlock());
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                    notifyOperators(sender, this, "commands.setblock.success");
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : (args.length == 4 ? getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : (args.length == 6 ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep") : null));
    }
}
