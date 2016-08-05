package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class CommandReload
{
	private final BukkitPluginMain rscp;
	CommandReload(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void executeFetch(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin.reload"))
		{
			rscp.fetchNowAndReschedule();
			throw new CommandAnswerException(Phrases.FETCHED_ANSWER.toString());
		}
	}
	public void executeReload(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin.reload"))
		{
			rscp.getServer().getPluginManager().disablePlugin(rscp);
			rscp.getServer().getPluginManager().enablePlugin(rscp);
			throw new CommandAnswerException(Phrases.PLUGIN_RELOADED.toString());
		}
	}
	public void executeUpdate(CommandSender sender, String args[]) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin"))
		{
			if(args.length > 1 && "do".equals(args[1]))
			{
				rscp.updating.doUpdate(sender instanceof Player ? (Player)sender : null);
			} else {
				rscp.updating.checkUpdate(sender instanceof Player ? (Player)sender : null);
			}
		}
	}
}
