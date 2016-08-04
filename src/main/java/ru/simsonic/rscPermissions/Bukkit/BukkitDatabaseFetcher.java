package ru.simsonic.rscPermissions.Bukkit;

import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.simsonic.rscCommonsLibrary.RestartableThread;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class BukkitDatabaseFetcher extends RestartableThread
{
	private final BukkitPluginMain rscp;
	public BukkitDatabaseFetcher(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	@Override
	public void run()
	{
		final long queryStartTime = System.currentTimeMillis();
		Thread.currentThread().setName("rscp:DatabaseFetchingThread");
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		if(rscp.connection.isConnected() == false)
			if(rscp.connection.connect() == false)
			{
				BukkitPluginMain.consoleLog.warning("[rscp] Cannot connect to database! Using local cache only.");
				return;
			}
		final DatabaseContents contents = remoteToLocal();
		rscp.connection.disconnect();
		if(contents != null)
		{
			rscp.internalCache.fill(contents);
			final Runnable syncTask = new Runnable()
			{
				@Override
				public synchronized void run()
				{
					BukkitPluginMain.consoleLog.info(ChatColor.stripColor(
						Phrases.FETCHED_REMOTE_DB.toPlayer()
						.replace("{:E}", String.valueOf(contents.entities.length))
						.replace("{:P}", String.valueOf(contents.permissions.length))
						.replace("{:I}", String.valueOf(contents.inheritance.length))));
					rscp.permissionManager.recalculateOnlinePlayers();
					notify();
				}
			};
			try
			{
				synchronized(syncTask)
				{
					rscp.getServer().getScheduler().runTask(rscp, syncTask);
					syncTask.wait();
				}
			} catch(InterruptedException ex) {
			}
			final long queryTime = System.currentTimeMillis() - queryStartTime;
			final Set<CommandSender> debuggers = rscp.permissionManager.getDebuggers();
			if(!debuggers.isEmpty())
				rscp.getServer().getScheduler().runTask(rscp, new Runnable()
				{
					@Override
					public void run()
					{
						for(CommandSender debugger : debuggers)
							debugger.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX
								+ "Database has been fetched in " + queryTime + " milliseconds."));
					}
				});
		} else
			BukkitPluginMain.consoleLog.warning("[rscp] Cannot load data from database.");
	}
	public synchronized DatabaseContents remoteToLocal()
	{
		final DatabaseContents contents = rscp.connection.retrieveContents();
		if(contents != null)
		{
			rscp.localStorage.cleanup();
			rscp.localStorage.saveContents(contents);
			// contents.filterServerId(rscp.getServer().getServerId());
		}
		return contents;
	}
}
