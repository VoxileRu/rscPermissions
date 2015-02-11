package ru.simsonic.rscPermissions;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import ru.simsonic.rscPermissions.Backends.BackendMySQL;
import ru.simsonic.rscUtilityLibrary.ConnectionMySQL;

public class ConnectionHelper extends BackendMySQL
{
	private ConnectionHelper nextInChain = null;
	public ConnectionHelper(BukkitPluginMain rscp, ConnectionHelper nextInChain)
	{
		super(rscp);
		this.nextInChain = nextInChain;
	}
	protected synchronized BackendMySQL findConnectedNode()
	{
		for(ConnectionHelper result = this; result != null; result = result.nextInChain)
			if(result.isConnected())
				return (BackendMySQL)result;
		return null;
	}
	@Override
	public void Disconnect()
	{
		if(nextInChain != null)
		{
			nextInChain.Disconnect();
			nextInChain = null;
		}
		super.Disconnect();
	}
	public Thread threadFetchTablesData()
	{
		final Thread result = new Thread()
		{
			@Override
			public void run()
			{
				// Fetch tables
				final ConnectionMySQL connection = findConnectedNode();
				if(connection == null)
					return;
				fetchIntoCache(rscp.internalCache);
				// Update permissions for online players
				try
				{
					Runnable syncTask = new Runnable()
					{
						@Override
						public synchronized void run()
						{
							rscp.permissionManager.recalculateOnlinePlayers();
							notify();
						}
					};
					synchronized(syncTask)
					{
						rscp.getServer().getScheduler().runTask(rscp, syncTask);
						syncTask.wait();
					}
				} catch(InterruptedException ex) {
					BukkitPluginMain.consoleLog.log(Level.SEVERE, "[rscp] Exception in FetchTables(): {0}", ex);
				}
				// rscp.cache.calculateStartupPermissions();
			}
		};
		result.start();
		return result;
	}
	public Thread threadInsertExampleRows(final CommandSender sender)
	{
		final Thread result = new Thread()
		{
			@Override
			public void run()
			{
				setName("InsertExampleRows");
				final BackendMySQL backend = findConnectedNode();
				if(backend == null)
					return;
				backend.insertExampleRows();
				rscp.getServer().getScheduler().runTask(rscp, new Runnable()
				{
					@Override
					public void run()
					{
						sender.sendMessage("Database tables were filled with example rows.");
					}
				});
			}
		};
		result.start();
		return result;
	}
	public Thread threadMigrateFromPExSQL(final CommandSender sender)
	{
		final Thread result = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					setName("MigrateFromPExSQL");
					final BackendMySQL backend = findConnectedNode();
					if(backend == null)
						return;
					backend.executeUpdate(loadResourceSQLT("Migrate_from_PermissionsEx"));
					threadFetchTablesData().join();
					rscp.getServer().getScheduler().runTask(rscp, new BukkitRunnable()
					{
						@Override
						public void run()
						{
							rscp.formattedMessage(sender, "Migration from PermissionsEx (MySQL backend) done!");
							rscp.formattedMessage(sender, "Check the latest database row for new data.");
						}
					});
				} catch(InterruptedException ex) {
					BukkitPluginMain.consoleLog.log(Level.SEVERE, "[rscp] Exception in MigrateFromPExSQL(): {0}", ex);
				}
			}
		};
		result.start();
		return result;
	}
}
