package com.tlf.HN.event;

import com.tlf.HN.common.HideNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HNEventHandlerForge {
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderLiving(RenderLivingEvent.Specials.Pre event) {
		if (event.getEntity() instanceof EntityPlayer) {
			if (event.isCancelable()) {
				Object hidden = HideNames.instance.hiddenPlayers.get(event.getEntity().getCommandSenderEntity().getName().toLowerCase());
				if (hidden != null && (Boolean) hidden) {
					event.setCanceled(true);
				}
			}
		}
	}
}