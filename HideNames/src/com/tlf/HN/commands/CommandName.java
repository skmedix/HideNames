package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

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
			return "/name(s) <all|toggle|on|off|set|default|safeofflineplayers|status>";
		} else {
			return "/name(s) <toggle|on|off|status>";
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{

		EntityPlayerMP player = getCommandSenderAsPlayer(par1ICommandSender);
		Set ops = MinecraftServer.getServer().getConfigurationManager().getOps();
		boolean isOp;
		
		if (!MinecraftServer.getServer().isDedicatedServer()) {
			isOp = ops.contains(player.getCommandSenderName().toLowerCase()) || MinecraftServer.getServer().getServerOwner().equalsIgnoreCase(player.getCommandSenderName());
		} else {
			isOp = ops.contains(player.getCommandSenderName().toLowerCase());
		}
		
		if (par2ArrayOfStr.length > 0) {
			if ("toggle".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName().toLowerCase(), !HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()));
				
				player.addChatMessage(new ChatComponentText("Your name is now: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()) ? "\u00a7aHidden" : "\u00a74Visible")));

			} else if ("on".equalsIgnoreCase(par2ArrayOfStr[0]) || "show".equalsIgnoreCase(par2ArrayOfStr[0]) || "visible".equalsIgnoreCase(par2ArrayOfStr[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName(), false);
				player.addChatMessage(new ChatComponentText("Your name is now: \u00a74Visible"));

			} else if ("off".equalsIgnoreCase(par2ArrayOfStr[0]) || "hide".equalsIgnoreCase(par2ArrayOfStr[0]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderName(), true);
				player.addChatMessage(new ChatComponentText("Your name is now: \u00a7aHidden"));

			} else if ("status".equalsIgnoreCase(par2ArrayOfStr[0])) {

				player.addChatMessage(new ChatComponentText("Your name is: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()) ? "\u00a7aHidden" : "\u00a74Visible")));

			} else if ("all".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					if ("on".equalsIgnoreCase(par2ArrayOfStr[1]) || "show".equalsIgnoreCase(par2ArrayOfStr[1])) {

						player.addChatMessage(new ChatComponentText("All names are now: \u00a74Visible"));
						HideNames.instance.setAll(false);

					} else if ("off".equalsIgnoreCase(par2ArrayOfStr[1]) || "hide".equalsIgnoreCase(par2ArrayOfStr[1])) {

						player.addChatMessage(new ChatComponentText("All names are now: \u00a7aHidden"));
						HideNames.instance.setAll(true);

					} else {
						throw new WrongUsageException("/name all <on|off>", new Object[0]);
					}
				} else {
					player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
				}

			} else if ("default".equalsIgnoreCase(par2ArrayOfStr[0])) {

				if (par2ArrayOfStr.length == 2) {
					if (isOp) {
						if ("on".equalsIgnoreCase(par2ArrayOfStr[1]) || "visible".equalsIgnoreCase(par2ArrayOfStr[1])) {
							player.addChatMessage(new ChatComponentText("All new players names will be: \u00a74Visible"));
							HideNames.instance.defaultHiddenStatus = false;
						} else if ("off".equalsIgnoreCase(par2ArrayOfStr[1]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[1])) {
							player.addChatMessage(new ChatComponentText("All new players names will be: \u00a7aHidden"));
							HideNames.instance.defaultHiddenStatus = true;
						} else if (par2ArrayOfStr[1] != null) {
							throw new WrongUsageException("/name default <on|off>", new Object[0]);
						}
					} else {
						player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
					}
				} else {
					player.addChatMessage(new ChatComponentText("Default: " + (HideNames.instance.defaultHiddenStatus ? "\u00a7aHidden" : "\u00a74Visible")));
				}

			} else if ("clear".equalsIgnoreCase(par2ArrayOfStr[0])) {

				if (isOp) {
					HideNames.instance.clearHiddenPlayers();
					player.addChatMessage(new ChatComponentText("All hidden players have been cleared."));
					player.addChatMessage(new ChatComponentText("Generating new file with all online users."));
				} else {
					player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
				}
				
			} else if ("clearOffline".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					HideNames.instance.removeOfflinePlayers();
					player.addChatMessage(new ChatComponentText("All offline players have been removed"));
				} else {
					player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
				}
					
			} else if ("saveOfflinePlayers".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					if (par2ArrayOfStr.length == 2) {
						if ("true".equalsIgnoreCase(par2ArrayOfStr[1])) {
							HideNames.instance.saveOfflinePlayers = true;
							player.addChatMessage(new ChatComponentText("SaveOfflinePlayers set to: true"));
						} else if ("false".equalsIgnoreCase(par2ArrayOfStr[1])) {
							HideNames.instance.saveOfflinePlayers = false;
							player.addChatMessage(new ChatComponentText("SaveOfflinePlayers set to: false"));
						} else if (par2ArrayOfStr[1] != null) {
							throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
						}
					} else {
						player.addChatMessage(new ChatComponentText("SaveOfflinePlayers: " + (HideNames.instance.saveOfflinePlayers ? "true" : "false")));
					}
				} else {
					player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
				}
			} else if ("set".equalsIgnoreCase(par2ArrayOfStr[0])) {
				
				if (isOp) {
					if (par2ArrayOfStr.length == 3) {
						EntityPlayerMP targetPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(par2ArrayOfStr[1]);
						if ("on".equalsIgnoreCase(par2ArrayOfStr[2]) || "show".equalsIgnoreCase(par2ArrayOfStr[2]) || "visible".equalsIgnoreCase(par2ArrayOfStr[2]))
						{
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderName(), false);
							targetPlayer.addChatMessage(new ChatComponentText(par1ICommandSender.getCommandSenderName() + " set your name to \u00a74Visible"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: \u00a74Visible"));
						}
						else if ("off".equalsIgnoreCase(par2ArrayOfStr[2]) || "hide".equalsIgnoreCase(par2ArrayOfStr[2]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[2]))
						{
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderName(), true);
							targetPlayer.addChatMessage(new ChatComponentText(par1ICommandSender.getCommandSenderName() + " set your name to \u00a7aHidden"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: \u00a7aHidden"));
						}
						else
						{
							throw new WrongUsageException("/name set <player> <on|off>", new Object[0]);
						}
					} else {
						throw new WrongUsageException("/name set <player> <on|off>", new Object[0]);
					}
				} else {
					player.addChatMessage(new ChatComponentText("\u00a7cYou do not have permission to use this command."));
				}
				
			} else {
				throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
			}
		} else {
			throw new WrongUsageException(this.getCommandUsage(par1ICommandSender), new Object[0]);
		}
	}

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{	
		Set ops = MinecraftServer.getServer().getConfigurationManager().getOps();
		boolean isOp = ops.contains(par1ICommandSender.getCommandSenderName().toLowerCase()) || (!MinecraftServer.getServer().isDedicatedServer() && Minecraft.getMinecraft().isSingleplayer());
		
		if (par2ArrayOfStr.length == 1)
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, (isOp) ? new String[] { "all", "toggle", "on", "off", "status", "default", "saveOfflinePlayers", "clearOffline", "clear", "set"} : new String[] {"toggle", "on", "off", "status", "default"});
		}
		else if (par2ArrayOfStr.length == 2 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("all") || par2ArrayOfStr[0].equalsIgnoreCase("default")))
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"on", "off"});
		}
		else if (par2ArrayOfStr.length == 2 && (isOp) && par2ArrayOfStr[0].equalsIgnoreCase("saveOfflinePlayers"))
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"true", "false"});
		}
		else if (par2ArrayOfStr.length >= 2 && (isOp) && par2ArrayOfStr[0].equalsIgnoreCase("set"))
		{
			if (par2ArrayOfStr.length == 2) {
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getConfigurationManager().getAllUsernames());
			} else if (par2ArrayOfStr.length == 3) {
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"on", "off"});
			} else {
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}