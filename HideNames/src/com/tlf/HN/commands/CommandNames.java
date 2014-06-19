package com.tlf.HN.commands;

import com.tlf.HN.common.HideNames;

import net.minecraft.command.ICommandSender;

public class CommandNames extends CommandName
{
	@Override
	public String getCommandName()
	{
		return HideNames.instance.commandName2;
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
        return HideNames.instance.commandPermissionLevel;
    }
	
	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return super.getCommandUsage(par1ICommandSender);
    }
	
	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
	{
		super.processCommand(par1ICommandSender, par2ArrayOfStr);
	}
}
