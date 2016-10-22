package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;
import com.tlf.HN.common.TLFUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
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
		list.add(HideNames.commandName2);
		return list;
	}

	public String getCommandUsage(ICommandSender sender) {
		if (TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName()) ||
				(!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer() && FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isSinglePlayer())) {
			return "/name(s) <all|set|toggle|hide|show|option>";
		} else {
			return "/name(s) <toggle|hide|show>";
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName().toLowerCase()) || HideNames.allowCommand;
	}

	// TODO: Rewrite
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		boolean isOp = TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName());

		if (!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()) {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName()) || FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner().equalsIgnoreCase(player.getCommandSenderEntity().getName());
		} else {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName());
		}

		if (args.length > 0) {
			if ("toggle".equalsIgnoreCase(args[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName().toLowerCase(), !HideNames.instance.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()));

				player.addChatMessage(new TextComponentString("Your name is now: " + (HideNames.instance.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()) ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));

			} else if ("on".equalsIgnoreCase(args[0]) || "show".equalsIgnoreCase(args[0]) || "visible".equalsIgnoreCase(args[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName(), false);
				player.addChatMessage(new TextComponentString("Your name is now: " + TextFormatting.DARK_RED + "Visible"));

			} else if ("off".equalsIgnoreCase(args[0]) || "hide".equalsIgnoreCase(args[0]) || "hidden".equalsIgnoreCase(args[0])) {

				HideNames.instance.updateHiddenPlayers(player.getCommandSenderEntity().getName(), true);
				player.addChatMessage(new TextComponentString("Your name is now: " + TextFormatting.GREEN + "Hidden"));

			} else if ("all".equalsIgnoreCase(args[0])) {

				if (isOp) {
					if (args.length > 1) {
						if ("on".equalsIgnoreCase(args[1]) || "show".equalsIgnoreCase(args[1])) {

							player.addChatMessage(new TextComponentString("All names are now: " + TextFormatting.DARK_RED + "Visible"));
							HideNames.instance.setAll(sender.getCommandSenderEntity().getName(), false);

						} else if ("off".equalsIgnoreCase(args[1]) || "hide".equalsIgnoreCase(args[1])) {

							player.addChatMessage(new TextComponentString("All names are now: " + TextFormatting.GREEN + "Hidden"));
							HideNames.instance.setAll(sender.getCommandSenderEntity().getName(), true);

						} else {
							throw new WrongUsageException("/name all <on|off>");
						}
					} else {
						throw new WrongUsageException("/name all <on|off>");
					}
				} else {
					player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}
			} else if ("option".equalsIgnoreCase(args[0]) || "options".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length > 1) {
						if ("default".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if (isOp) {
									if ("on".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
										player.addChatMessage(new TextComponentString("All new players names will be: " + TextFormatting.DARK_RED + "Visible"));
										HideNames.defaultHiddenStatus = false;
									} else if ("off".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
										player.addChatMessage(new TextComponentString("All new players names will be: " + TextFormatting.GREEN + "Hidden"));
										HideNames.defaultHiddenStatus = true;
									} else if (args[2] != null) {
										throw new WrongUsageException("/name option default <on|off>");
									}
								} else {
									player.addChatMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
								}
							} else {
								player.addChatMessage(new TextComponentString("Default: " + (HideNames.defaultHiddenStatus ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));
							}
						} else if ("clear".equalsIgnoreCase(args[1])) {
							HideNames.instance.clearHiddenPlayers();
							player.addChatMessage(new TextComponentString("All hidden players have been cleared."));
							player.addChatMessage(new TextComponentString("Generating new file with all online users."));
						} else if ("clearOffline".equalsIgnoreCase(args[1])) {

							HideNames.instance.removeOfflinePlayers();
							player.addChatMessage(new TextComponentString("All offline players have been removed"));

						} else if ("saveOfflinePlayers".equalsIgnoreCase(args[1])) {

							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = true;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = false;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option saveOfflinePlayers [true|false]");
								}
							} else {
								player.addChatMessage(new TextComponentString("SaveOfflinePlayers: " + HideNames.colorBool(HideNames.saveOfflinePlayers)));
							}
						} else if ("allowCommand".equalsIgnoreCase(args[1])) {

							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = true;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = false;
									player.addChatMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option allowCommand [true|false]");
								}
							} else {
								player.addChatMessage(new TextComponentString("allowCommand: " + HideNames.colorBool(HideNames.allowCommand)));
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
			} else if ("set".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length == 3) {
						EntityPlayerMP targetPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(args[1]);
						if ("on".equalsIgnoreCase(args[2]) || "show".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), false);
							targetPlayer.addChatMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.DARK_RED + "Visible"));
							player.addChatMessage(new TextComponentString(targetPlayer.getCommandSenderEntity().getName() + "'s name is now: " + TextFormatting.DARK_RED + "Visible"));
						} else if ("off".equalsIgnoreCase(args[2]) || "hide".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
							HideNames.instance.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), true);
							targetPlayer.addChatMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.GREEN + "Hidden"));
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
				throw new WrongUsageException(this.getCommandUsage(sender));
			}
		} else {
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos bpos) {
		boolean isOp = TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName()) ||
				(!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer() && FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isSinglePlayer());

		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, (isOp) ? new String[]{ "all", "set", "toggle", "hide", "show", "status", "option" } : new String[]{ "toggle", "hide", "show", "status", "default" });
		} else if (args.length == 2 && (isOp) && (args[0].equalsIgnoreCase("option") || args[0].equalsIgnoreCase("options"))) {
			return getListOfStringsMatchingLastWord(args, "default", "clear", "clearOffline", "saveOfflinePlayers", "allowCommand");
		} else if (args.length == 2 && (isOp) && (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("default"))) {
			return getListOfStringsMatchingLastWord(args, "on", "off");
		} else if (args.length == 3 && (isOp) && (args[0].equalsIgnoreCase("option") || args[0].equalsIgnoreCase("options")) && args[1].equalsIgnoreCase("saveOfflinePlayers")) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else if ((args.length == 2 || args.length == 3) && (isOp) && args[0].equalsIgnoreCase("set")) {
			if (args.length == 2) {
				String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getAllUsernames();
				String[] nonSenderUsers = new String[users.length - 1];
				String senderName = sender.getCommandSenderEntity().getName();
				int pos = 0;
				for (String user : users) {
					if (!user.equals(senderName)) {
						nonSenderUsers[pos] = user;
						pos++;
					}
				}
				return getListOfStringsMatchingLastWord(args, nonSenderUsers);
			} else if (args.length == 3) {
				return getListOfStringsMatchingLastWord(args, "on", "off");
			}
		}
		return null;
	}
}