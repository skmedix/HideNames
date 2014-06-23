package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommandName extends CommandBase
{	
	@Override
	public String getCommandName()
	{
		return HideNames.commandName1;
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return HideNames.instance.commandPermissionLevel;
	}
	
	@Override
	public List getCommandAliases()
	{
		List list = new ArrayList();
		list.add(HideNames.commandName1);
		list.add(HideNames.commandName2);
		
		return list;
	}
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
	{
		Set ops = MinecraftServer.getServer().getConfigurationManager().getOps();
		if (ops.contains(par1ICommandSender.getCommandSenderName().toLowerCase()) || (!MinecraftServer.getServer().isDedicatedServer() && Minecraft.getMinecraft().isSingleplayer())) {
			return "/name(s) <all|set|toggle|hide|show|option>";
		} else {
			return "/name(s) <toggle|hide|show>";
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return MinecraftServer.getServer().getConfigurationManager().getOps().contains(sender.getCommandSenderName().toLowerCase()) ? true : HideNames.allowCommand;
	}
	
	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		
		EntityPlayerMP player = getCommandSenderAsPlayer(par1ICommandSender);
		Set ops = MinecraftServer.getServer().getConfigurationManager().getOps();
		boolean isOp = false;
		
		if (!MinecraftServer.getServer().isDedicatedServer()) {
			isOp = ops.contains(player.getCommandSenderName().toLowerCase()) || MinecraftServer.getServer().getServerOwner().equalsIgnoreCase(player.getCommandSenderName());
		} else {
			isOp = ops.contains(player.getCommandSenderName().toLowerCase());
		}
		
		if (par2ArrayOfStr.length > 0) {
			if ("toggle".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName().toLowerCase(), !HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()));
				
				player.addChatMessage(new ChatComponentText("Your name is now: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()) ? EnumChatFormatting.GREEN+"Hidden" : EnumChatFormatting.DARK_RED+"Visible")));
				
			} else if ("on".equalsIgnoreCase(par2ArrayOfStr[0]) || "show".equalsIgnoreCase(par2ArrayOfStr[0]) || "visible".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName(), false);
				player.addChatMessage(new ChatComponentText("Your name is now: "+EnumChatFormatting.DARK_RED+"Visible"));
				
			} else if ("off".equalsIgnoreCase(par2ArrayOfStr[0]) || "hide".equalsIgnoreCase(par2ArrayOfStr[0]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName(), true);
				player.addChatMessage(new ChatComponentText("Your name is now: "+EnumChatFormatting.GREEN+"Hidden"));
				
			} else if ("all".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					if (par2ArrayOfStr.length > 1) {
						if ("on".equalsIgnoreCase(par2ArrayOfStr[1]) || "show".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							player.addChatMessage(new ChatComponentText("All names are now: "+EnumChatFormatting.DARK_RED+"Visible"));
							HideNames.instance.setAll(par1ICommandSender.getCommandSenderName(), false);
							
						} else if ("off".equalsIgnoreCase(par2ArrayOfStr[1]) || "hide".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							player.addChatMessage(new ChatComponentText("All names are now: "+EnumChatFormatting.GREEN+"Hidden"));
							HideNames.instance.setAll(par1ICommandSender.getCommandSenderName(), true);
							
						} else {
							throw new WrongUsageException("/name all <on|off>", new Object[0]);
						}
					} else {
						throw new WrongUsageException("/name all <on|off>", new Object[0]);
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"You do not have permission to use this command."));
				}
				
			} else if ("option".equalsIgnoreCase(par2ArrayOfStr[0]) || "options".equalsIgnoreCase(par2ArrayOfStr[0])) {
				if (isOp) {
					if (par2ArrayOfStr.length > 1) {
						if ("default".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							if (par2ArrayOfStr.length == 3) {
								if (isOp) {
									if ("on".equalsIgnoreCase(par2ArrayOfStr[2]) || "visible".equalsIgnoreCase(par2ArrayOfStr[2])) {
										player.addChatMessage(new ChatComponentText("All new players names will be: "+EnumChatFormatting.DARK_RED+"Visible"));
										HideNames.instance.defaultHiddenStatus = false;
									} else if ("off".equalsIgnoreCase(par2ArrayOfStr[2]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[2])) {
										player.addChatMessage(new ChatComponentText("All new players names will be: "+EnumChatFormatting.GREEN+"Hidden"));
										HideNames.instance.defaultHiddenStatus = true;
									} else if (par2ArrayOfStr[2] != null) {
										throw new WrongUsageException("/name default <on|off>", new Object[0]);
									}
								} else {
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"You do not have permission to use this command."));
								}
							} else {
								player.addChatMessage(new ChatComponentText("Default: " + (HideNames.instance.defaultHiddenStatus ? EnumChatFormatting.GREEN+"Hidden" : EnumChatFormatting.DARK_RED+"Visible")));
							}
						} else if ("clear".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							HideNames.instance.clearHiddenPlayers();
							player.addChatMessage(new ChatComponentText("All hidden players have been cleared."));
							player.addChatMessage(new ChatComponentText("Generating new file with all online users."));
							
						} else if ("clearOffline".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							HideNames.instance.removeOfflinePlayers();
							player.addChatMessage(new ChatComponentText("All offline players have been removed"));
							
						} else if ("saveOfflinePlayers".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							if (par2ArrayOfStr.length == 3) {
								if ("true".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.instance.saveOfflinePlayers = true;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+"SaveOfflinePlayers"+EnumChatFormatting.RESET+" set to: "+EnumChatFormatting.GREEN+"true"));
								} else if ("false".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.instance.saveOfflinePlayers = false;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+"SaveOfflinePlayers"+EnumChatFormatting.RESET+" set to: "+EnumChatFormatting.DARK_RED+"false"));
								} else if (par2ArrayOfStr[2] != null) {
									throw new WrongUsageException("/name(s) saveOfflinePlayers [true|false]", new Object[0]);
								}
							} else {
								player.addChatMessage(new ChatComponentText("SaveOfflinePlayers: " + HideNames.colorBool(HideNames.saveOfflinePlayers, false)));
							}
						} else if ("allowCommand".equalsIgnoreCase(par2ArrayOfStr[1])) {
							
							if (par2ArrayOfStr.length == 3) {
								if ("true".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.instance.allowCommand = true;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+"allowCommand"+EnumChatFormatting.RESET+" set to: "+EnumChatFormatting.GREEN+"true"));
								} else if ("false".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.instance.allowCommand = false;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+"allowCommand"+EnumChatFormatting.RESET+" set to: "+EnumChatFormatting.DARK_RED+"false"));
								} else if (par2ArrayOfStr[2] != null) {
									throw new WrongUsageException("/name(s) allowCommand [true|false]", new Object[0]);
								}
							} else {
								player.addChatMessage(new ChatComponentText("allowCommand: " + HideNames.colorBool(HideNames.allowCommand, false)));
							}
							
						} else {
							throw new WrongUsageException("/name(s) option <default|clear|clearOffline|safeOfflinePlayers|allowCommand>", new Object[0]);
						}
					} else {
						throw new WrongUsageException("/name(s) option <default|clear|clearOffline|safeOfflinePlayers|allowCommand>", new Object[0]);
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"You do not have permission to use this command."));
				}
			} else if ("set".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					if (par2ArrayOfStr.length == 3) {
						EntityPlayerMP targetPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[1]);
						if ("on".equalsIgnoreCase(par2ArrayOfStr[2]) || "show".equalsIgnoreCase(par2ArrayOfStr[2]) || "visible".equalsIgnoreCase(par2ArrayOfStr[2]))
						{
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderName(), false);
							targetPlayer.addChatMessage(new ChatComponentText(par1ICommandSender.getCommandSenderName() + " set your name to be: "+EnumChatFormatting.DARK_RED+"Visible"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: "+EnumChatFormatting.DARK_RED+"Visible"));
						}
						else if ("off".equalsIgnoreCase(par2ArrayOfStr[2]) || "hide".equalsIgnoreCase(par2ArrayOfStr[2]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[2]))
						{
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderName(), true);
							targetPlayer.addChatMessage(new ChatComponentText(par1ICommandSender.getCommandSenderName() + " set your name to be: "+EnumChatFormatting.GREEN+"Hidden"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: "+EnumChatFormatting.GREEN+"Hidden"));
						}
						else
						{
							throw new WrongUsageException("/name set <player> <on|off>", new Object[0]);
						}
					} else {
						throw new WrongUsageException("/name set <player> <on|off>", new Object[0]);
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"You do not have permission to use this command."));
				}
				
			} else {
				throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
			}
		} else {
			player.addChatMessage(new ChatComponentText("Your name is: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()) ? EnumChatFormatting.GREEN+"Hidden" : EnumChatFormatting.DARK_RED+"Visible")));
		}
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{	
		Set ops = MinecraftServer.getServer().getConfigurationManager().getOps();
		boolean isOp = ops.contains(par1ICommandSender.getCommandSenderName().toLowerCase()) || (!MinecraftServer.getServer().isDedicatedServer() && Minecraft.getMinecraft().isSingleplayer());
		
		if (par2ArrayOfStr.length == 1)
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, (isOp) ? new String[] { "all", "set", "toggle", "hide", "show", "status", "option"} : new String[] {"toggle", "hide", "show", "status", "default"});
		}
		else if (par2ArrayOfStr.length == 2 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("option") || par2ArrayOfStr[0].equalsIgnoreCase("options")))
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"default", "clear", "clearOffline", "saveOfflinePlayers", "allowCommand"});
		}
		else if (par2ArrayOfStr.length == 2 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("all") || par2ArrayOfStr[0].equalsIgnoreCase("default")))
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"on", "off"});
		}
		else if (par2ArrayOfStr.length == 3 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("option") || par2ArrayOfStr[0].equalsIgnoreCase("options")) && par2ArrayOfStr[1].equalsIgnoreCase("saveOfflinePlayers"))
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"true", "false"});
		}
		else if ((par2ArrayOfStr.length == 2 || par2ArrayOfStr.length == 3) && (isOp) && par2ArrayOfStr[0].equalsIgnoreCase("set"))
		{
			if (par2ArrayOfStr.length == 2) {
				String[] users = MinecraftServer.getServer().getConfigurationManager().getAllUsernames();
				String[] nonSenderUsers = new String[users.length-1];
				String senderName = par1ICommandSender.getCommandSenderName();
				int pos = 0;
				for (int i = 0; i < users.length; i++) {
					if (users[i] != senderName) {
						nonSenderUsers[pos] = users[i];
						pos++;
					}
				}
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, nonSenderUsers);
			} else if (par2ArrayOfStr.length == 3) {
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"on", "off"});
			}
		}
		
		return null;
	}
}