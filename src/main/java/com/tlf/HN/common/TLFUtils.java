package com.tlf.HN.common;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class TLFUtils {
	public static boolean isStringInArray(String[] arr, String match) {
		return isStringInArray(arr, match, false);
	}

	public static boolean isStringInArray(String[] arr, String match, boolean ignoreCase) {
		for (String str : arr) {
			if (ignoreCase && str.equalsIgnoreCase(match)) {
				return true;
			} else if (str.equals(match)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPlayerOp(String username) {
		return isStringInArray(FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getOppedPlayerNames(), username, true);
	}
}