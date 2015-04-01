package ru.simsonic.rscPermissions.Bukkit.Commands;
import org.bukkit.command.CommandSender;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;

public class CommandReload
{
	private final BukkitPluginMain rscp;
	CommandReload(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin.reload"))
		{
			rscp.getServer().getPluginManager().disablePlugin(rscp);
			rscp.getServer().getPluginManager().enablePlugin(rscp);
			throw new CommandAnswerException(Phrases.PLUGIN_RELOADED.toString());
		}
	}
}
