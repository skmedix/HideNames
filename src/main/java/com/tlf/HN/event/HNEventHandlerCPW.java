package com.tlf.HN.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.tlf.HN.common.HideNames;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HNEventHandlerCPW {
	private final boolean client;
	private int tickCount = 0;

	public HNEventHandlerCPW() {
		this.client = FMLCommonHandler.instance().getEffectiveSide().isClient();
		System.out.println("HN Event Handler started on side " + FMLCommonHandler.instance().getEffectiveSide());
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!client) {
			HideNames.instance.onClientConnect(event.player);
		}
	}

	@SubscribeEvent
	public void tickEnd(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			onTickInGame();
		}
	}

	private void onTickInGame() {
		if (!HideNames.saveOfflinePlayers) {
			HideNames.instance.removeOfflinePlayers();
		}

		HideNames.instance.checkFile();

		String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getAllUsernames();
		for (String user : users) {
			if (!HideNames.instance.hiddenPlayers.containsKey(user.toLowerCase()) || HideNames.instance.hiddenPlayers.get(user.toLowerCase()) == null) {
				EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerByUsername(user);
				HideNames.instance.updateHiddenPlayers(user, HideNames.defaultHiddenStatus);
				player.addChatMessage(new TextComponentString("Your name is: " + (HideNames.defaultHiddenStatus ? "\u00a7aHidden" : "\u00a74Visible")));
			}
		}

		if (tickCount == 20) {
			tickCount = 0;

			Configuration tempConfig = HideNames.instance.config;

			tempConfig.load();

			if (HideNames.defaultHiddenStatus != tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players").getBoolean(false)) {
				Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players");
				temp.set(HideNames.defaultHiddenStatus);
			}

			if (HideNames.saveOfflinePlayers != tempConfig.get(Configuration.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers").getBoolean(true)) {
				Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers");
				temp.set(HideNames.saveOfflinePlayers);
			}

			if (HideNames.allowCommand != tempConfig.get(Configuration.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command").getBoolean(true)) {
				Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command");
				temp.set(HideNames.allowCommand);
			}

			tempConfig.save();
		} else {
			tickCount++;
		}
	}
}