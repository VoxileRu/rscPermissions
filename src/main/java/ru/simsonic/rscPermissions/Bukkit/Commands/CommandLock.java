package ru.simsonic.rscPermissions.Bukkit.Commands;
import org.bukkit.command.CommandSender;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;

public class CommandLock
{
	private final BukkitPluginMain rscp;
	CommandLock(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender, String args[]) throws CommandAnswerException
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
}
