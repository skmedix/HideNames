package com.tlf.HN.network.packet;

import com.tlf.HN.common.HideNames;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class PacketHideNameChange implements IPacket
{
	private String username;
	private boolean newState;
	
	public PacketHideNameChange() {}
	public PacketHideNameChange(EntityPlayer player, boolean newState)
	{
		this.username = player.getCommandSenderName();
		this.newState = newState;
	}
	public PacketHideNameChange(String username, boolean newState)
	{
		this.username = username;
		this.newState = newState;
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf bytes)
	{
		ByteBufInputStream bbis = new ByteBufInputStream(bytes);
		
		try {
			this.username = bbis.readLine();
			this.newState = Boolean.parseBoolean(bbis.readLine());
			
			bbis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Recieved packet with the boolean newState = " + (this.newState ? "true" : "false") + " for user = " + this.username);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf bytes)
	{
		ByteBufOutputStream bbos = new ByteBufOutputStream(bytes);
		
		try {
			bbos.writeBytes(this.username + "/n");
			bbos.writeBoolean(this.newState);
			
			bbos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleClientSide(EntityPlayer player)
	{
		HideNames.instance.hiddenPlayers.remove(username);
    	HideNames.instance.hiddenPlayers.put(username, this.newState);
	}
	@Override
	public void handleServerSide(EntityPlayer player)
	{
		System.out.println("Packet recieved from user " + username);
		if (this.newState) {
			System.out.println(username + " is now hidden");
		} else {
			System.out.println(username + " is no longer hidden");
		}
	}
	
	/*
	public void handlePacket(Packet250CustomPayload packet) {
		BufferedReader inputStream = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.data)));
        
		String line;
		
        try {
        	line = inputStream.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        String username = "";
        String hidden = "";
        
        int seperator = line.lastIndexOf(":");
		if (seperator > 0) {
			username = line.substring(0, seperator).toLowerCase();
	    	hidden = line.substring(seperator + 1);
		}
	    
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        if (side == side.SERVER) {
        	System.out.println("Packet recieved from user " + username);
        	if (hidden.contentEquals("true")) {
            	System.out.println(username + " is now hidden");
            } else {
            	System.out.println(username + " is no longer hidden");
            }
        } else {
        	HideNames.instance.hiddenPlayers.remove(username);
        	HideNames.instance.hiddenPlayers.put(username, hidden);
        }
	}
	*/
	
	public String username()
	{
		return this.username;
	}
	
	public boolean newState()
	{
		return this.newState;
	}
	
	public EntityPlayer player()
	{
		return Minecraft.getMinecraft().theWorld.getPlayerEntityByName(this.username);
	}
}