package com.tlf.HN.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;

import com.tlf.HN.common.HideNames;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HNEventHandlerForge
{
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderLiving(RenderLivingEvent.Specials.Pre event)
	{
		if (event.entity instanceof EntityPlayer)
		{
			if (event.isCancelable())
			{
				Object hidden = HideNames.instance.hiddenPlayers.get(event.entity.getCommandSenderName().toLowerCase());
				if (hidden != null && (Boolean)hidden) {
					event.setCanceled(true);
				}
			}
		}
	}
}