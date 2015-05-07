package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;

public class CommandFetch
{
	private final BukkitPluginMain rscp;
	CommandFetch(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin.reload"))
		{
			rscp.commandHelper.threadFetchDatabaseContents.startDeamon();
			throw new CommandAnswerException(Phrases.MYSQL_FETCHED.toString());
		}
	}
}
