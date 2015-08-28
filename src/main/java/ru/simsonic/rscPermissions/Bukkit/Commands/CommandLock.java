package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class CommandLock
{
	private final BukkitPluginMain rscp;
	CommandLock(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void executeLock(CommandSender sender, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.lock"))
		{
			final String mode = (args.length >= 2) ? args[1] : "default";
			rscp.bukkitListener.setMaintenanceMode(mode);
			throw new CommandAnswerException(Phrases.MAINTENANCE_ON.toString());
		}
	}
	public void executeUnlock(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.lock"))
		{
			rscp.bukkitListener.setMaintenanceMode(null);
			throw new CommandAnswerException(Phrases.MAINTENANCE_OFF.toString());
		}
	}
}
