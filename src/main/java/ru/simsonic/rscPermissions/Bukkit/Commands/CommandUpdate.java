package ru.simsonic.rscPermissions.Bukkit.Commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class CommandUpdate
{
	private final BukkitPluginMain rscp;
	CommandUpdate(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void execute(CommandSender sender, String args[]) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin"))
		{
			if(args.length > 0 && "do".equals(args[0]))
			{
				rscp.updating.doUpdate(sender instanceof Player ? (Player)sender : null);
			} else {
				rscp.updating.checkUpdate(sender instanceof Player ? (Player)sender : null);
			}
		}
	}
}
