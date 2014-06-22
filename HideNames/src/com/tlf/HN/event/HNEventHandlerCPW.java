package com.tlf.HN.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.tlf.HN.common.HideNames;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class HNEventHandlerCPW
{
	private final boolean client;
	private int tickCount = 0;
	
	public HNEventHandlerCPW()
	{
		this.client = FMLCommonHandler.instance().getEffectiveSide().isClient();
		System.out.println("HN Event Handler started on side " + FMLCommonHandler.instance().getEffectiveSide());
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		if (!client) {
			HideNames.instance.onClientConnect(event.player);
		}
	}
	
	@SubscribeEvent
	public void tickEnd(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END) {
			onTickInGame();
		}
	}
	
	private void onTickInGame() 
	{	
		if (!HideNames.instance.saveOfflinePlayers)
		{
			HideNames.instance.removeOfflinePlayers();
		}
		
		HideNames.instance.checkFile();
		
		String[] users = MinecraftServer.getServer().getAllUsernames();
		for (int i = 0; i < users.length; i++) {
			if (!HideNames.instance.hiddenPlayers.containsKey(users[i].toLowerCase()) || HideNames.instance.hiddenPlayers.get(users[i].toLowerCase()) == null)
			{
				EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(users[i]);
				HideNames.instance.updateHiddenPlayers(users[i], HideNames.instance.defaultHiddenStatus);
				player.addChatMessage(new ChatComponentText("Your name is: " + (HideNames.instance.defaultHiddenStatus ? "\u00a7aHidden" : "\u00a74Visible")));
			}
		}
		
		if (tickCount == 20)
		{
			tickCount = 0;
			
			Configuration tempConfig = HideNames.instance.config;
			
			tempConfig.load();
			
			if (HideNames.instance.defaultHiddenStatus != tempConfig.get(tempConfig.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players").getBoolean(false))
			{
				Property temp = tempConfig.get(tempConfig.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players");
				temp.set(HideNames.instance.defaultHiddenStatus);
			}
			
			if (HideNames.instance.saveOfflinePlayers != tempConfig.get(tempConfig.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers").getBoolean(true))
			{
				Property temp = tempConfig.get(tempConfig.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers");
				temp.set(HideNames.instance.saveOfflinePlayers);
			}
			
			if (HideNames.instance.allowCommand != tempConfig.get(tempConfig.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command").getBoolean(true))
			{
				Property temp = tempConfig.get(tempConfig.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command");
				temp.set(HideNames.instance.allowCommand);
			}
			
			tempConfig.save();
		} else {
			tickCount++;
		}
	}
}