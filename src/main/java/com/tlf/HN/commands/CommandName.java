package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;
import com.tlf.HN.common.TLFUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import javax.annotation.Nonnull;
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
	public List<String> getCommandAliases() {
		List list = new ArrayList<>();
		list.add(HideNames.commandName1);
		list.add(HideNames.commandName2);
		return list;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		if (TLFUtils.isPlayerOp(sender.getCommandSenderName()) ||
				(!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()
						&& FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isSinglePlayer())) {
			return "/name(s) <all|set|toggle|hide|show|option>";
		} else {
			return "/name(s) <toggle|hide|show>";
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return TLFUtils.isPlayerOp(sender.getCommandSenderName().toLowerCase()) || HideNames.allowCommand;
	}

	@Override
	public void processCommand(@Nonnull ICommandSender sender, @Nonnull String[] args)
			throws CommandException {

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		boolean isOp;
		if (FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()) {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderName());
		} else {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderName()) || FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner().equalsIgnoreCase(player.getCommandSenderName());
		}

		if (args.length > 0) {

			///name(s) toggle
			if ("toggle".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderName().toLowerCase(), !HideNames.INSTANCE.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()));

				player.addChatMessage(new ChatComponentText("Your name is now: " + (HideNames.INSTANCE.hiddenPlayers.get(player.getCommandSenderName().toLowerCase()) ? EnumChatFormatting.GREEN + "Hidden" : EnumChatFormatting.DARK_RED + "Visible")));

				///name(s) on
			} else if ("on".equalsIgnoreCase(args[0]) || "show".equalsIgnoreCase(args[0]) || "visible".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderName(), false);
				player.addChatMessage(new ChatComponentText("Your name is now: " + EnumChatFormatting.DARK_RED + "Visible"));

				///name(s) off
			} else if ("off".equalsIgnoreCase(args[0]) || "hide".equalsIgnoreCase(args[0]) || "hidden".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderName(), true);
				player.addChatMessage(new ChatComponentText("Your name is now: " + EnumChatFormatting.GREEN + "Hidden"));

				///name(s) all <on|off>
			} else if ("all".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length > 1) {
						if ("on".equalsIgnoreCase(args[1]) || "show".equalsIgnoreCase(args[1])) {

							player.addChatMessage(new ChatComponentText("All names are now: " + EnumChatFormatting.DARK_RED + "Visible"));
							HideNames.INSTANCE.setAll(sender.getCommandSenderName(), false);

						} else if ("off".equalsIgnoreCase(args[1]) || "hide".equalsIgnoreCase(args[1])) {

							player.addChatMessage(new ChatComponentText("All names are now: " + EnumChatFormatting.GREEN + "Hidden"));
							HideNames.INSTANCE.setAll(sender.getCommandSenderName(), true);

						} else {
							throw new WrongUsageException("/name all <on|off>");
						}
					} else {
						throw new WrongUsageException("/name all <on|off>");
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED +
							"You do not have permission to use this command."));
				}

				///name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessage>
			} else if ("option".equalsIgnoreCase(args[0]) || "options".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length > 1) {

						///name(s) option clear
						if ("clear".equalsIgnoreCase(args[1])) {

							HideNames.INSTANCE.clearHiddenPlayers();
							player.addChatMessage(new ChatComponentText("All hidden players have been cleared."));
							player.addChatMessage(new ChatComponentText("Generating new file with all online users."));

							///name(s) option clearOffline
						} else if ("clearOffline".equalsIgnoreCase(args[1])) {

							HideNames.INSTANCE.removeOfflinePlayers();
							player.addChatMessage(new ChatComponentText("All offline players have been removed"));

							///name(s) option default
						} else if ("default".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("on".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
									player.addChatMessage(new ChatComponentText("All new players names will be: " + EnumChatFormatting.DARK_RED + "Visible"));
									HideNames.defaultHiddenStatus = false;
								} else if ("off".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
									player.addChatMessage(new ChatComponentText("All new players names will be: " + EnumChatFormatting.GREEN + "Hidden"));
									HideNames.defaultHiddenStatus = true;
								} else if (args[2] != null) {
									throw new WrongUsageException("/name option default <on|off>");
								}
							} else {
								player.addChatMessage(new ChatComponentText("Default: " + (HideNames.defaultHiddenStatus ? EnumChatFormatting.GREEN + "Hidden" : EnumChatFormatting.DARK_RED + "Visible")));
							}

							///name(s) option showMessageOnLogin
						} else if ("showMessageOnLogin".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("on".equalsIgnoreCase(args[2]) || "true".equalsIgnoreCase(args[2])) {
									HideNames.showHideStatusOnLogin = true;
									player.addChatMessage(new ChatComponentText("Showing HN messages is now: " +
											(HideNames.showHideStatusOnLogin ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.DARK_RED + "Disabled")));
								} else if ("off".equalsIgnoreCase(args[2]) || "false".equalsIgnoreCase(args[2])) {
									HideNames.showHideStatusOnLogin = false;
									player.addChatMessage(new ChatComponentText("Showing HN messages is now: " +
											(HideNames.showHideStatusOnLogin ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.DARK_RED + "Disabled")));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option showMessageOnLogin <true|false>");
								}
							} else {
								player.addChatMessage(new ChatComponentText("showMessageOnLogin: " + HideNames.colorBool(HideNames.showHideStatusOnLogin)));
							}

							///name(s) option saveOfflinePlayers [true|false]
						} else if ("saveOfflinePlayers".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = true;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "SaveOfflinePlayers" + EnumChatFormatting.RESET + " set to: " + EnumChatFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = false;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "SaveOfflinePlayers" + EnumChatFormatting.RESET + " set to: " + EnumChatFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option saveOfflinePlayers [true|false]");
								}
							} else {
								player.addChatMessage(new ChatComponentText("SaveOfflinePlayers: " + HideNames.colorBool(HideNames.saveOfflinePlayers)));
							}

							///name(s) option allowCommand [true|false]
						} else if ("allowCommand".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = true;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "allowCommand" + EnumChatFormatting.RESET + " set to: " + EnumChatFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = false;
									player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "allowCommand" + EnumChatFormatting.RESET + " set to: " + EnumChatFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option allowCommand [true|false]");
								}
							} else {
								player.addChatMessage(new ChatComponentText("allowCommand: " + HideNames.colorBool(HideNames.allowCommand)));
							}
						} else {
							throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessageOnLogin>");
						}
					} else {
						throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessageOnLogin>");
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
				}

				///name(s) set <player> <on|off>
			} else if ("set".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length == 3) {
						EntityPlayerMP targetPlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[1]);
						if ("on".equalsIgnoreCase(args[2]) || "show".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
							HideNames.INSTANCE.updateHiddenPlayers(targetPlayer.getCommandSenderName(), false);
							targetPlayer.addChatMessage(new ChatComponentText(sender.getCommandSenderName() + " set your name to be: " + EnumChatFormatting.DARK_RED + "Visible"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: " + EnumChatFormatting.DARK_RED + "Visible"));
						} else if ("off".equalsIgnoreCase(args[2]) || "hide".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
							HideNames.INSTANCE.updateHiddenPlayers(targetPlayer.getCommandSenderName(), true);
							targetPlayer.addChatMessage(new ChatComponentText(sender.getCommandSenderName() + " set your name to be: " + EnumChatFormatting.GREEN + "Hidden"));
							player.addChatMessage(new ChatComponentText(targetPlayer.getCommandSenderName() + "'s name is now: " + EnumChatFormatting.GREEN + "Hidden"));
						} else {
							throw new WrongUsageException("/name set <player> <on|off>");
						}
					} else {
						throw new WrongUsageException("/name set <player> <on|off>");
					}
				} else {
					player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
				}
			} else {
				throw new WrongUsageException(this.getCommandUsage(sender));
			}
		} else {
			throw new WrongUsageException(this.getCommandUsage(sender));
		}
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
		boolean isOp = TLFUtils.isPlayerOp(sender.getCommandSenderName()) ||
				(!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()
						&& FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isSinglePlayer());

		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, (isOp) ? new String[]{"all", "set", "toggle", "hide", "show", "status", "option"} : new String[]{"toggle", "hide", "show", "status", "default"});
		} else if (args.length == 2 && (isOp) && (args[0].equalsIgnoreCase("option") || args[0].equalsIgnoreCase("options"))) {
			return getListOfStringsMatchingLastWord(args, "default", "clear", "clearOffline", "saveOfflinePlayers", "allowCommand");
		} else if (args.length == 2 && (isOp) && (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("default"))) {
			return getListOfStringsMatchingLastWord(args, "on", "off");
		} else if (args.length == 3 && (isOp) && (args[0].equalsIgnoreCase("option") || args[0].equalsIgnoreCase("options")) && args[1].equalsIgnoreCase("saveOfflinePlayers")) {
			return getListOfStringsMatchingLastWord(args, "true", "false");
		} else if ((args.length == 2 || args.length == 3) && (isOp) && args[0].equalsIgnoreCase("set")) {
			if (args.length == 2) {
				String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getAllUsernames();
				String[] nonSenderUsers = new String[users.length - 1];
				String senderName = sender.getCommandSenderName();
				int p = 0;
				for (String user : users) {
					if (!user.equals(senderName)) {
						nonSenderUsers[p] = user;
						p++;
					}
				}
				return getListOfStringsMatchingLastWord(args, nonSenderUsers);
			} else {
				return getListOfStringsMatchingLastWord(args, "on", "off");
			}
		}
		return null;
	}
}