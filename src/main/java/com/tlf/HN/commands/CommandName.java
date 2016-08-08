package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;
import com.tlf.HN.common.TLFUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.List;

public class CommandName extends CommandBase {
	@Override
	public String getCommandName() {
		return HideNames.commandName1;
	}

	@Override
	public int getRequiredPermissionLevel() {

		return HideNames.commandPermissionLevel;
	}

	@Override
	public List getCommandAliases() {
		List list = new ArrayList();
		list.add(HideNames.commandName1);
		return list;
	}

	public String getCommandUsage(ICommandSender par1ICommandSender) {
		if (TLFUtils.isPlayerOp(par1ICommandSender.getCommandSenderEntity().getName()) || (!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer() && Minecraft.getMinecraft().isSingleplayer())) {
			return "/name(s) <all|set|toggle|hide|show|option>";
		} else {
			return "/name(s) <toggle|hide|show>";
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName().toLowerCase()) || HideNames.allowCommand;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender par1ICommandSender, String[] par2ArrayOfStr) throws WrongUsageException, PlayerNotFoundException {

		EntityPlayerMP player = getCommandSenderAsPlayer(par1ICommandSender);
		boolean isOp = false;

		if (!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()) {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName()) || FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getServerOwner().equalsIgnoreCase(player.getCommandSenderEntity().getName());
		} else {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName());
		}

		if (par2ArrayOfStr.length > 0) {
			if ("toggle".equalsIgnoreCase(par2ArrayOfStr[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName().toLowerCase(), !HideNames.instance.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()));

				player.addChatMessage(new TextComponentString("Your name is now: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()) ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));

			} else if ("on".equalsIgnoreCase(par2ArrayOfStr[0]) || "show".equalsIgnoreCase(par2ArrayOfStr[0]) || "visible".equalsIgnoreCase(par2ArrayOfStr[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName(), false);
				player.addChatMessage(new TextComponentString("Your name is now: " + TextFormatting.DARK_RED + "Visible"));

			} else if ("off".equalsIgnoreCase(par2ArrayOfStr[0]) || "hide".equalsIgnoreCase(par2ArrayOfStr[0]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName(), true);
				player.addChatMessage(new TextComponentString("Your name is now: " + TextFormatting.GREEN + "Hidden"));

			} else if ("all".equalsIgnoreCase(par2ArrayOfStr[0])) {

				if (isOp) {
					if (par2ArrayOfStr.length > 1) {
						if ("on".equalsIgnoreCase(par2ArrayOfStr[1]) || "show".equalsIgnoreCase(par2ArrayOfStr[1])) {

							player.addChatMessage(new TextComponentString("All names are now: " + TextFormatting.DARK_RED + "Visible"));
							HideNames.instance.setAll(par1ICommandSender.getCommandSenderEntity().getName(), false);

						} else if ("off".equalsIgnoreCase(par2ArrayOfStr[1]) || "hide".equalsIgnoreCase(par2ArrayOfStr[1])) {

							player.addChatMessage(new TextComponentString("All names are now: " + TextFormatting.GREEN + "Hidden"));
							HideNames.instance.setAll(par1ICommandSender.getCommandSenderEntity().getName(), true);

						} else {
							throw new WrongUsageException("/name all <on|off>");
						}
					} else {
						throw new WrongUsageException("/name all <on|off>");
					}
				} else {
					player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}
			} else if ("option".equalsIgnoreCase(par2ArrayOfStr[0]) || "options".equalsIgnoreCase(par2ArrayOfStr[0])) {
				if (isOp) {
					if (par2ArrayOfStr.length > 1) {
						if ("default".equalsIgnoreCase(par2ArrayOfStr[1])) {

							if (par2ArrayOfStr.length == 3) {
								if (isOp) {
									if ("on".equalsIgnoreCase(par2ArrayOfStr[2]) || "visible".equalsIgnoreCase(par2ArrayOfStr[2])) {
										player.addChatMessage(new TextComponentString("All new players names will be: " + TextFormatting.DARK_RED + "Visible"));
										HideNames.defaultHiddenStatus = false;
									} else if ("off".equalsIgnoreCase(par2ArrayOfStr[2]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[2])) {
										player.addChatMessage(new TextComponentString("All new players names will be: " + TextFormatting.GREEN + "Hidden"));
										HideNames.defaultHiddenStatus = true;
									} else if (par2ArrayOfStr[2] != null) {
										throw new WrongUsageException("/name option default <on|off>");
									}
								} else {
									player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
								}
							} else {
								player.addChatMessage(new TextComponentString("Default: " + (HideNames.defaultHiddenStatus ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));
							}
						} else if ("clear".equalsIgnoreCase(par2ArrayOfStr[1])) {
							HideNames.instance.clearHiddenPlayers();
							player.addChatMessage(new TextComponentString("All hidden players have been cleared."));
							player.addChatMessage(new TextComponentString("Generating new file with all online users."));
						} else if ("clearOffline".equalsIgnoreCase(par2ArrayOfStr[1])) {

							HideNames.instance.removeOfflinePlayers();
							player.addChatMessage(new TextComponentString("All offline players have been removed"));

						} else if ("saveOfflinePlayers".equalsIgnoreCase(par2ArrayOfStr[1])) {

							if (par2ArrayOfStr.length == 3) {
								if ("true".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.saveOfflinePlayers = true;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.saveOfflinePlayers = false;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (par2ArrayOfStr[2] != null) {
									throw new WrongUsageException("/name(s) option saveOfflinePlayers [true|false]");
								}
							} else {
								player.addChatMessage(new TextComponentString("SaveOfflinePlayers: " + HideNames.colorBool(HideNames.saveOfflinePlayers, false)));
							}
						} else if ("allowCommand".equalsIgnoreCase(par2ArrayOfStr[1])) {

							if (par2ArrayOfStr.length == 3) {
								if ("true".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.allowCommand = true;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(par2ArrayOfStr[2])) {
									HideNames.allowCommand = false;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (par2ArrayOfStr[2] != null) {
									throw new WrongUsageException("/name(s) option allowCommand [true|false]");
								}
							} else {
								player.addChatMessage(new TextComponentString("allowCommand: " + HideNames.colorBool(HideNames.allowCommand, false)));
							}
						} else {
							throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand>");
						}
					} else {
						throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand>");
					}
				} else {
					player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}
			} else if ("set".equalsIgnoreCase(par2ArrayOfStr[0])) {
				if (isOp) {
					if (par2ArrayOfStr.length == 3) {
						EntityPlayerMP targetPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(par2ArrayOfStr[1]);
						if ("on".equalsIgnoreCase(par2ArrayOfStr[2]) || "show".equalsIgnoreCase(par2ArrayOfStr[2]) || "visible".equalsIgnoreCase(par2ArrayOfStr[2])) {
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), false);
							targetPlayer.addChatMessage(new TextComponentString(par1ICommandSender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.DARK_RED + "Visible"));
							player.addChatMessage(new TextComponentString(targetPlayer.getCommandSenderEntity().getName() + "'s name is now: " + TextFormatting.DARK_RED + "Visible"));
						} else if ("off".equalsIgnoreCase(par2ArrayOfStr[2]) || "hide".equalsIgnoreCase(par2ArrayOfStr[2]) || "hidden".equalsIgnoreCase(par2ArrayOfStr[2])) {
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), true);
							targetPlayer.addChatMessage(new TextComponentString(par1ICommandSender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.GREEN + "Hidden"));
							player.addChatMessage(new TextComponentString(targetPlayer.getCommandSenderEntity().getName() + "'s name is now: " + TextFormatting.GREEN + "Hidden"));
						} else {
							throw new WrongUsageException("/name set <player> <on|off>");
						}
					} else {
						throw new WrongUsageException("/name set <player> <on|off>");
					}
				} else {
					player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}
			} else {
				throw new WrongUsageException(this.getCommandUsage(par1ICommandSender));
			}
		} else {
			throw new WrongUsageException(this.getCommandUsage(par1ICommandSender));
		}
	}

	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		boolean isOp = TLFUtils.isPlayerOp(par1ICommandSender.getCommandSenderEntity().getName()) || (!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer() && Minecraft.getMinecraft().isSingleplayer());

		if (par2ArrayOfStr.length == 1) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, (isOp) ? new String[]{ "all", "set", "toggle", "hide", "show", "status", "option" } : new String[]{ "toggle", "hide", "show", "status", "default" });
		} else if (par2ArrayOfStr.length == 2 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("option") || par2ArrayOfStr[0].equalsIgnoreCase("options"))) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, "default", "clear", "clearOffline", "saveOfflinePlayers", "allowCommand");
		} else if (par2ArrayOfStr.length == 2 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("all") || par2ArrayOfStr[0].equalsIgnoreCase("default"))) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, "on", "off");
		} else if (par2ArrayOfStr.length == 3 && (isOp) && (par2ArrayOfStr[0].equalsIgnoreCase("option") || par2ArrayOfStr[0].equalsIgnoreCase("options")) && par2ArrayOfStr[1].equalsIgnoreCase("saveOfflinePlayers")) {
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, "true", "false");
		} else if ((par2ArrayOfStr.length == 2 || par2ArrayOfStr.length == 3) && (isOp) && par2ArrayOfStr[0].equalsIgnoreCase("set")) {
			if (par2ArrayOfStr.length == 2) {
				String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getAllUsernames();
				String[] nonSenderUsers = new String[users.length - 1];
				String senderName = par1ICommandSender.getCommandSenderEntity().getName();
				int pos = 0;
				for (String user : users) {
					if (user != senderName) {
						nonSenderUsers[pos] = user;
						pos++;
					}
				}
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, nonSenderUsers);
			} else if (par2ArrayOfStr.length == 3) {
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, "on", "off");
			}
		}
		return null;
	}
}