package com.tlf.HN.event;

import com.tlf.HN.common.HideNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventHandler {
	private final boolean client;
	private int tickCount = 0;

	public EventHandler() {
		this.client = FMLCommonHandler.instance().getEffectiveSide().isClient();
		System.out.println("HN Event Handler started on side " + FMLCommonHandler.instance().getEffectiveSide());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
		if (event.getEntity() instanceof EntityPlayer) {
			if (event.isCancelable()) {
				Object hidden = HideNames.INSTANCE.hiddenPlayers.get(
						event.getEntity().getCommandSenderEntity().getName().toLowerCase());
				if (hidden != null && (Boolean) hidden) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (!client) {
			HideNames.INSTANCE.onClientConnect(event.player);
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
			HideNames.INSTANCE.removeOfflinePlayers();
		}

		HideNames.INSTANCE.checkFile();

		String[] users = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOnlinePlayerNames();
		for (String user : users) {
			if (!HideNames.INSTANCE.hiddenPlayers.containsKey(user.toLowerCase())
					|| HideNames.INSTANCE.hiddenPlayers.get(user.toLowerCase()) == null) {

				EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance()
						.getServer().getPlayerList().getPlayerByUsername(user);
				HideNames.INSTANCE.updateHiddenPlayers(user, HideNames.defaultHiddenStatus);
				player.sendMessage(new TextComponentString(
						"Your name is: " + (HideNames.defaultHiddenStatus ? "\u00a7a Hidden" : "\u00a74 Visible")));
			}
		}

		if (tickCount == 20) {
			tickCount = 0;

			Configuration tempConfig = HideNames.INSTANCE.config;
			tempConfig.load();

			if (HideNames.defaultHiddenStatus != tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players").getBoolean(false)) {
				Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players");
				temp.set(HideNames.defaultHiddenStatus);
			}

			if (HideNames.showHideStatusOnLogin != tempConfig.get(Configuration.CATEGORY_GENERAL, "showHideStatusOnLogin",
					true, "Showing information about hide status after enter the game").getBoolean(true)) {
				Property temp = tempConfig.get(Configuration.CATEGORY_GENERAL, "showHideStatusOnLogin", true, "Showing information about hide status after enter the game");
				temp.set(HideNames.showHideStatusOnLogin);
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