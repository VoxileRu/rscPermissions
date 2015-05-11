package ru.simsonic.rscPermissions.Bukkit;

import java.util.Set;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.RestartableThread;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

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
		final DatabaseContents contents = rscp.connection.retrieveContents();
		rscp.connection.disconnect();
		if(contents != null)
		{
			contents.normalize();
			rscp.localStorage.cleanup();
			rscp.localStorage.saveContents(contents);
			contents.filterServerId(rscp.getServer().getServerId());
			rscp.internalCache.fill(contents);
			final Runnable syncTask = new Runnable()
			{
				@Override
				public synchronized void run()
				{
					BukkitPluginMain.consoleLog.log(Level.INFO, "[rscp] Fetched {0} entities, {1} permissions and {2} inheritances",
						new Integer[]
						{
							contents.entities.length,
							contents.permissions.length,
							contents.inheritance.length
						});
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
							debugger.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix
								+ "Database has been fetched in " + queryTime + " milliseconds."));
					}
				});
		}
		else
			BukkitPluginMain.consoleLog.warning("[rscp] Cannot load data from database.");
	}
}
