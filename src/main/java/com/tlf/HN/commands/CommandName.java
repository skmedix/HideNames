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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandName extends CommandBase {
	@Override
	public String getName() {
		return HideNames.commandName1;
	}

	@Override
	public int getRequiredPermissionLevel() {
		return HideNames.commandPermissionLevel;
	}

	@Override
	public List<String> getAliases() {
		List<String> list = new ArrayList<>();
		list.add(HideNames.commandName1);
		list.add(HideNames.commandName2);
		return list;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		if (TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName()) ||
				(!FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()
						&& FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isSinglePlayer())) {
			return "/name(s) <all|set|toggle|hide|show|option>";
		} else {
			return "/name(s) <toggle|hide|show>";
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName().toLowerCase()) || HideNames.allowCommand;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
			throws CommandException {

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		boolean isOp;
		if (FMLCommonHandler.instance().getMinecraftServerInstance().getServer().isDedicatedServer()) {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName());
		} else {
			isOp = TLFUtils.isPlayerOp(player.getCommandSenderEntity().getName()) || FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner().equalsIgnoreCase(player.getCommandSenderEntity().getName());
		}

		if (args.length > 0) {

			///name(s) toggle
			if ("toggle".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderEntity().getName().toLowerCase(), !HideNames.INSTANCE.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()));

				player.sendMessage(new TextComponentString("Your name is now: " + (HideNames.INSTANCE.hiddenPlayers.get(player.getCommandSenderEntity().getName().toLowerCase()) ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));

			///name(s) on
			} else if ("on".equalsIgnoreCase(args[0]) || "show".equalsIgnoreCase(args[0]) || "visible".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderEntity().getName(), false);
				player.sendMessage(new TextComponentString("Your name is now: " + TextFormatting.DARK_RED + "Visible"));

			///name(s) off
			} else if ("off".equalsIgnoreCase(args[0]) || "hide".equalsIgnoreCase(args[0]) || "hidden".equalsIgnoreCase(args[0])) {

				HideNames.INSTANCE.updateHiddenPlayers(player.getCommandSenderEntity().getName(), true);
				player.sendMessage(new TextComponentString("Your name is now: " + TextFormatting.GREEN + "Hidden"));

			///name(s) all <on|off>
			} else if ("all".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length > 1) {
						if ("on".equalsIgnoreCase(args[1]) || "show".equalsIgnoreCase(args[1])) {

							player.sendMessage(new TextComponentString("All names are now: " + TextFormatting.DARK_RED + "Visible"));
							HideNames.INSTANCE.setAll(sender.getCommandSenderEntity().getName(), false);

						} else if ("off".equalsIgnoreCase(args[1]) || "hide".equalsIgnoreCase(args[1])) {

							player.sendMessage(new TextComponentString("All names are now: " + TextFormatting.GREEN + "Hidden"));
							HideNames.INSTANCE.setAll(sender.getCommandSenderEntity().getName(), true);

						} else {
							throw new WrongUsageException("/name all <on|off>");
						}
					} else {
						throw new WrongUsageException("/name all <on|off>");
					}
				} else {
					player.sendMessage(new TextComponentString(TextFormatting.RED +
							"You do not have permission to use this command."));
				}

			///name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessage>
			} else if ("option".equalsIgnoreCase(args[0]) || "options".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length > 1) {

						///name(s) option clear
						if ("clear".equalsIgnoreCase(args[1])) {

							HideNames.INSTANCE.clearHiddenPlayers();
							player.sendMessage(new TextComponentString("All hidden players have been cleared."));
							player.sendMessage(new TextComponentString("Generating new file with all online users."));

						///name(s) option clearOffline
						} else if ("clearOffline".equalsIgnoreCase(args[1])) {

							HideNames.INSTANCE.removeOfflinePlayers();
							player.sendMessage(new TextComponentString("All offline players have been removed"));

						///name(s) option default
						} else if ("default".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("on".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
									player.sendMessage(new TextComponentString("All new players names will be: " + TextFormatting.DARK_RED + "Visible"));
									HideNames.defaultHiddenStatus = false;
								} else if ("off".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
									player.sendMessage(new TextComponentString("All new players names will be: " + TextFormatting.GREEN + "Hidden"));
									HideNames.defaultHiddenStatus = true;
								} else if (args[2] != null) {
									throw new WrongUsageException("/name option default <on|off>");
								}
							} else {
								player.sendMessage(new TextComponentString("Default: " + (HideNames.defaultHiddenStatus ? TextFormatting.GREEN + "Hidden" : TextFormatting.DARK_RED + "Visible")));
							}

						///name(s) option showMessageOnLogin
						} else if ("showMessageOnLogin".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("on".equalsIgnoreCase(args[2]) || "true".equalsIgnoreCase(args[2])) {
									HideNames.showHideStatusOnLogin = true;
									player.sendMessage(new TextComponentString("Showing HN messages is now: " +
											(HideNames.showHideStatusOnLogin ? TextFormatting.GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled")));
								} else if ("off".equalsIgnoreCase(args[2]) || "false".equalsIgnoreCase(args[2])) {
									HideNames.showHideStatusOnLogin = false;
									player.sendMessage(new TextComponentString("Showing HN messages is now: " +
											(HideNames.showHideStatusOnLogin ? TextFormatting.GREEN + "Enabled" : TextFormatting.DARK_RED + "Disabled")));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option showMessageOnLogin <true|false>");
								}
							} else {
								player.sendMessage(new TextComponentString("showMessageOnLogin: " + HideNames.colorBool(HideNames.showHideStatusOnLogin)));
							}

						///name(s) option saveOfflinePlayers [true|false]
						} else if ("saveOfflinePlayers".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = true;
									player.sendMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.saveOfflinePlayers = false;
									player.sendMessage(new TextComponentString(TextFormatting.AQUA + "SaveOfflinePlayers" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option saveOfflinePlayers [true|false]");
								}
							} else {
								player.sendMessage(new TextComponentString("SaveOfflinePlayers: " + HideNames.colorBool(HideNames.saveOfflinePlayers)));
							}

						///name(s) option allowCommand [true|false]
						} else if ("allowCommand".equalsIgnoreCase(args[1])) {
							if (args.length == 3) {
								if ("true".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = true;
									player.sendMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.GREEN + "true"));
								} else if ("false".equalsIgnoreCase(args[2])) {
									HideNames.allowCommand = false;
									player.sendMessage(new TextComponentString(TextFormatting.AQUA + "allowCommand" + TextFormatting.RESET + " set to: " + TextFormatting.DARK_RED + "false"));
								} else if (args[2] != null) {
									throw new WrongUsageException("/name(s) option allowCommand [true|false]");
								}
							} else {
								player.sendMessage(new TextComponentString("allowCommand: " + HideNames.colorBool(HideNames.allowCommand)));
							}
						} else {
							throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessageOnLogin>");
						}
					} else {
						throw new WrongUsageException("/name(s) option <default|clear|clearOffline|saveOfflinePlayers|allowCommand|showMessageOnLogin>");
					}
				} else {
					player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}

			///name(s) set <player> <on|off>
			} else if ("set".equalsIgnoreCase(args[0])) {
				if (isOp) {
					if (args.length == 3) {
						EntityPlayerMP targetPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(args[1]);
						if ("on".equalsIgnoreCase(args[2]) || "show".equalsIgnoreCase(args[2]) || "visible".equalsIgnoreCase(args[2])) {
							HideNames.INSTANCE.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), false);
							targetPlayer.sendMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.DARK_RED + "Visible"));
							player.sendMessage(new TextComponentString(targetPlayer.getCommandSenderEntity().getName() + "'s name is now: " + TextFormatting.DARK_RED + "Visible"));
						} else if ("off".equalsIgnoreCase(args[2]) || "hide".equalsIgnoreCase(args[2]) || "hidden".equalsIgnoreCase(args[2])) {
							HideNames.INSTANCE.updateHiddenPlayers(targetPlayer.getCommandSenderEntity().getName(), true);
							targetPlayer.sendMessage(new TextComponentString(sender.getCommandSenderEntity().getName() + " set your name to be: " + TextFormatting.GREEN + "Hidden"));
							player.sendMessage(new TextComponentString(targetPlayer.getCommandSenderEntity().getName() + "'s name is now: " + TextFormatting.GREEN + "Hidden"));
						} else {
							throw new WrongUsageException("/name set <player> <on|off>");
						}
					} else {
						throw new WrongUsageException("/name set <player> <on|off>");
					}
				} else {
					player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
				}
			} else {
				throw new WrongUsageException(this.getUsage(sender));
			}
		} else {
			throw new WrongUsageException(this.getUsage(sender));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
		boolean isOp = TLFUtils.isPlayerOp(sender.getCommandSenderEntity().getName()) ||
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
				String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getOnlinePlayerNames();
				String[] nonSenderUsers = new String[users.length - 1];
				String senderName = sender.getCommandSenderEntity().getName();
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