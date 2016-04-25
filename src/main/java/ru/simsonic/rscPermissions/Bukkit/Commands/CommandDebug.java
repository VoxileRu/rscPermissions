package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.CommandUtilities;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class CommandDebug
{
	private final BukkitPluginMain rscp;
	CommandDebug(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender, String args[]) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin"))
		{
			boolean isDebugging = rscp.permissionManager.isDebugging(sender);
			if(args.length >= 2)
			{
				try
				{
					isDebugging = CommandUtilities.argumentToBoolean(args[1], isDebugging);
				} catch(IllegalArgumentException ex) {
					throw new CommandAnswerException("{_LR}" + ex.getMessage());
				}
			} else
				isDebugging = !isDebugging;
			rscp.permissionManager.setDebugging(sender, isDebugging);
			throw new CommandAnswerException(isDebugging ? Phrases.DEBUG_ON.toString() : Phrases.DEBUG_OFF.toString());
		}
	}
}
