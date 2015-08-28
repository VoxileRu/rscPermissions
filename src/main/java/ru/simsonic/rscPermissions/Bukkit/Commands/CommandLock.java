package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;

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
			final String mMode = (args.length >= 2) ? args[1] : "default";
			String mmon = "Maintenance mode enabled";
			mmon = rscp.getConfig().getString("language.maintenance.locked.default.mmon", mmon);
			mmon = rscp.getConfig().getString("language.maintenance.locked." + mMode + ".mmon", mmon);
			rscp.bukkitListener.setMaintenanceMode(mMode);
			throw new CommandAnswerException(mmon);
		}
	}
	public void executeUnlock(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.lock"))
		{
			String mmoff = "Maintenance mode disabled";
			mmoff = rscp.getConfig().getString("language.maintenance.unlocked", mmoff);
			rscp.bukkitListener.setMaintenanceMode(null);
			throw new CommandAnswerException(mmoff);
		}
	}
}
