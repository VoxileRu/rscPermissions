package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class CommandUnlock
{
	private final BukkitPluginMain rscp;
	CommandUnlock(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender) throws CommandAnswerException
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
