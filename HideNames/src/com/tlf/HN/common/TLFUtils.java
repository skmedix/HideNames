package com.tlf.HN.common;

import net.minecraft.server.MinecraftServer;

public class TLFUtils
{
	public static boolean isStringInArray(String[] arr, String match) {
		return isStringInArray(arr, match, false);
	}
	public static boolean isStringInArray(String[] arr, String match, boolean ignoreCase)
	{
		for (String str : arr) {
			if (ignoreCase && str.equalsIgnoreCase(match)) {
				return true;
			} else if (str.equals(match)) {
				return true;
			}
		}
		
		return false;
	}
	public static boolean isPlayerOp(String username)
	{
		return isStringInArray(MinecraftServer.getServer().getConfigurationManager().func_152606_n(), username, true);
	}
}