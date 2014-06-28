package com.tlf.HN.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.tlf.HN.commands.CommandName;
import com.tlf.HN.event.HNEventHandlerCPW;
import com.tlf.HN.event.HNEventHandlerForge;
import com.tlf.HN.network.packet.PacketHNChange;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = HideNames.MODID, name = HideNames.NAME, version = HideNames.VERSION)
public class HideNames
{	
	public static final String MODID = "hidenames";
	public static final String NAME = "Hide Names";
	public static final String VERSION = "1.1.5";
	
	/** The public instance */
	@Instance(HideNames.MODID)
	public static HideNames instance;
	
	/** The {@link Configuration} for Hide Names */
	public Configuration config;
	
	/** The channel that Hide Names uses for custom packets */
	public String channel;
	public SimpleNetworkWrapper network;
	
	/** All players currently in the file {@link #fileHiddenPlayers hidden.txt} */
	public Map<String, Boolean> hiddenPlayers = new HashMap<String, Boolean>();
	public Logger logger = Logger.getLogger("Minecraft");
	
	public static final int commandPermissionLevel = 0;
	public static final String commandName1 = "name";
	public static final String commandName2 = "names";
	
	/** The path to the file hidden.txt */
	public String fileHiddenPlayers;
	public final String fileName = "hidden.txt";
	public String serverFilePath;
	public String clientFilePath;
	
	public static boolean defaultHiddenStatus;
	public static boolean saveOfflinePlayers;
	public static boolean allowCommand;
	
	private ModMetadata metadata;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		metadata = event.getModMetadata();
		channel = metadata.modId;
		
		config = new Configuration(new File(event.getModConfigurationDirectory(), "/tlf/HideNames.cfg"));
		
		config.load();
		
		defaultHiddenStatus = config.get(Configuration.CATEGORY_GENERAL, "defaultHiddenStatus", false, "Default state for new players").getBoolean(false);
		saveOfflinePlayers = config.get(Configuration.CATEGORY_GENERAL, "saveOfflinePlayers", true, "Whether or not to keep players in 'hidden.txt' if they are offline - useful for big servers").getBoolean(true);		
		allowCommand = config.get(Configuration.CATEGORY_GENERAL, "allowCommand", true, "Whether or not non-ops can use the /name command").getBoolean(true);
		serverFilePath = config.get(Configuration.CATEGORY_GENERAL, "serverFilePath", "", "Where the file 'hidden.txt' should be on a dedicated server - NOTE: all directories are located within the server folder").getString();
		clientFilePath = config.get(Configuration.CATEGORY_GENERAL, "clientFilePath", "/config/tlf", "Where the file 'hidden.txt' should be on a client/LAN server - NOTE: all directories are located within the '.minecraft' folder").getString();
		
		config.save();
		
		this.network = NetworkRegistry.INSTANCE.newSimpleChannel(this.channel);
		this.network.registerMessage(PacketHNChange.Handler.class, PacketHNChange.class, 0, Side.CLIENT);
	}
	
	@EventHandler
	public void onModInit(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new HNEventHandlerForge());
		FMLCommonHandler.instance().bus().register(new HNEventHandlerCPW());
	}
	
	@EventHandler
	public void onModLoad(FMLPostInitializationEvent event)
	{		
		System.out.println(metadata.name + " " + metadata.version + " loaded!");
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandName());
		getFilePath();
		getHiddenPlayers();
	}
	
	/**
	 * Checks to see if the server is a dedicated server. If it is, it sets {@link #fileHiddenPlayers} to {@link #serverFilePath} + {@link #fileName}.
	 * Otherwise, its sets {@link #fileHiddenPlayers} to {@link #clientFilePath} + {@link #fileName}.
	 */
	public void getFilePath()
	{
		if (MinecraftServer.getServer().isDedicatedServer()) {
			if (!serverFilePath.endsWith("/")) {
				if (!fileName.startsWith("/")) {
					fileHiddenPlayers = serverFilePath + "/" + fileName;
				} else {
					fileHiddenPlayers = serverFilePath + fileName;
				}
			} else {
				if (!fileName.startsWith("/")) {
					fileHiddenPlayers = serverFilePath + fileName;
				} else {
					fileHiddenPlayers = serverFilePath.substring(0, serverFilePath.length() - 1) + fileName;
				}
			}
		} else {
			if (!clientFilePath.endsWith("/")) {
				if (!fileName.startsWith("/")) {
					fileHiddenPlayers = clientFilePath + "/" + fileName;
				} else {
					fileHiddenPlayers = clientFilePath + fileName;
				}
			} else {
				if (!fileName.startsWith("/")) {
					fileHiddenPlayers = clientFilePath + fileName;
				} else {
					fileHiddenPlayers = clientFilePath.substring(0, clientFilePath.length() - 1) + fileName;
				}
			}
		}
		
		if (fileHiddenPlayers.startsWith("/"))
		{
			fileHiddenPlayers = fileHiddenPlayers.substring(1);
		}
	}
	
	
	/**
	 * Removes all players from the file {@link #fileHiddenPlayers} and {@link #hiddenPlayers}
	 */
	public void clearHiddenPlayers()
	{
		hiddenPlayers.clear();
		new File(fileHiddenPlayers).delete();
	}
	
	/**
	 * Refreshes the {@link java.util.HashMap HashMap} {@link #hiddenPlayers hiddenPlayers}
	 */
	public void getHiddenPlayers()
	{
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileHiddenPlayers));
			String line;
			hiddenPlayers.clear();
			while ((line = br.readLine()) != null) {
				if (line.length() > 0 && !line.substring(0, 1).contentEquals("#")) {
					int seperator = line.lastIndexOf(":");
					String username = line.substring(0, seperator).toLowerCase();
					String hidden = line.substring(seperator + 1);
					updateHiddenPlayers(username, (hidden.equalsIgnoreCase("true") ? true : false));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			logger.log(Level.FINE, "Error: File " + fileHiddenPlayers + " not found.");
			logger.log(Level.FINE, "Creating file " + fileHiddenPlayers);
			createFile(fileHiddenPlayers);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Error: " + e.getMessage());
		}
	}
	
	/**
	 * Called every time a user connects. If that user is not in {@link #fileHiddenPlayers hidden.txt}, then they are placed in {@link #fileHiddenPlayers hidden.txt} with the hidden status of whatever
	 * {@link #defaultHiddenStatus defaultHiddenStatus} is set to. If they are in {@link #fileHiddenPlayers hidden.txt}, then their hidden status is whatever is
	 * said in {@link #fileHiddenPlayers hidden.txt}
	 * @param username 
	 */
	public void onClientConnect(EntityPlayer player)
	{
		Iterator<String> iterator = hiddenPlayers.keySet().iterator();
		while (iterator.hasNext()) {
			String user = iterator.next();
			this.network.sendTo(new PacketHNChange(user, hiddenPlayers.get(user)), (EntityPlayerMP)player);
			System.out.println("Send data to " + player.getCommandSenderName());
		}
		
		String username = player.getCommandSenderName().toLowerCase();
		
		if (hiddenPlayers.get(username) == null) {
			updateHiddenPlayers(username, defaultHiddenStatus);
		} else {
			updateHiddenPlayers(username, hiddenPlayers.get(username));
		}
		
		player.addChatMessage(new ChatComponentText("Your name is: " + (hiddenPlayers.get(username) ? "\u00a7aHidden" : "\u00a74Visible")));
	}
	
	/**
	 * Creates a file at the location 'file'
	 * @param file The location to create the file
	 */
	public void createFile(String file)
	{
		try
		{
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("#List of Hidden Players");
			
			Iterator iter = hiddenPlayers.entrySet().iterator();
			
			while (iter.hasNext())
			{
				Map.Entry mEntry = (Map.Entry) iter.next();
				out.write("\n" + mEntry.getKey() + ":" + ((Boolean)mEntry.getValue() ? "true" : "false"));
			}
			
			out.close();
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, "Error: " + e.getMessage());
		}
	}
	
	/**
	 * Sets all players hidden status to the supplied boolean
	 * @param hidden The state to set all players to
	 */
	public void setAll(String sender, boolean hidden)
	{
		List<String> users = new ArrayList<String>();
		for (Map.Entry<String, Boolean> entry : hiddenPlayers.entrySet())
		{
			String key = entry.getKey();
			users.add(key);
		}
		
		for (int i = 0; i < users.size(); i++)
		{
			String username = users.get(i);
			updateHiddenPlayers(username, hidden);
			
			if (!username.equalsIgnoreCase(sender)) {
				playerForName(users.get(i)).addChatMessage(new ChatComponentText(sender+" set your name to be: " + (hiddenPlayers.get(username) ? EnumChatFormatting.GREEN+"Hidden" : EnumChatFormatting.DARK_RED+"Visible")));
			}
		}
	}
	
	/**
	 * Changes the state of the player 'username' to the state 'hidden'
	 * @param username The player to change
	 * @param hidden The state to change them to
	 */
	public void updateHiddenPlayers(String username, boolean hidden)
	{
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		
		if (side == Side.SERVER) {
			username = username.toLowerCase();
			
			hiddenPlayers.remove(username);
			hiddenPlayers.put(username, hidden);
			refreshFile(fileHiddenPlayers);
			
			this.network.sendToAll(new PacketHNChange(username, hidden));
		}
	}
	
	public void removeOfflinePlayers()
	{
		String[] users = MinecraftServer.getServer().getAllUsernames();
		Object[] keySet = hiddenPlayers.keySet().toArray();
		Boolean[] keepUsers = new Boolean[keySet.length];
		Boolean foundUser = false;
		Boolean foundDifferent = false;
		
		for (int i = 0; i < keySet.length; i++)
		{
			for (int j = 0; j < users.length; j++)
			{
				if (keySet[i].toString().equalsIgnoreCase(users[j]))
				{
					foundUser = true;
				}
			}
			
			if (!foundUser) { foundDifferent = true; }
			
			keepUsers[i] = foundUser;
			foundUser = false;
		}
		
		for (int i = 0; i < keepUsers.length; i++)
		{
			if (!keepUsers[i])
			{
				hiddenPlayers.remove(keySet[i]);
			}
		}
		
		if (foundDifferent)
		{
			refreshFile(fileHiddenPlayers);
		}
	}
	
	public void refreshFile(String fileName)
	{
		new File(fileName).delete();
		createFile(fileName);
	}
	
	public void checkFile()
	{
		File file = new File(fileHiddenPlayers);
		
		if (!file.exists())
		{
			createFile(fileHiddenPlayers);
		}
	}
	
	public static EntityPlayerMP playerForName(String username) {
		return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);
	}
	
	public static String colorBool(boolean bool, boolean capatalize)
	{
		return capatalize ? (bool ? EnumChatFormatting.GREEN+"True" : EnumChatFormatting.DARK_RED+"False") : (bool ? EnumChatFormatting.GREEN+"true" : EnumChatFormatting.DARK_RED+"false");
	}
	
	@Optional.Method(modid = "forgeupdater")
	public String curseID() { return "hide-names"; /** CurseID. Get it from curse.com/mc-mods/minecraft/[curseID]*/ }
	@Optional.Method(modid = "forgeupdater")
	public String[] fileFormats() { return new String[]{"Hide_Names-$mc-$v.jar", "HideNames_v$v_MC_$mc.jar"}; /** $mc = minecraft version; $v = mod version */ }
	@Optional.Method(modid = "forgeupdater")
	public int minType() { return 2; /** 0 = alpha; 1 = beta; 2 = release */ }
}