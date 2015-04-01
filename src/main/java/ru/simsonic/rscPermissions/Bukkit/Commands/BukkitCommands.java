package ru.simsonic.rscPermissions.Bukkit.Commands;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;
import ru.simsonic.rscUtilityLibrary.RestartableThread;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class BukkitCommands
{
	private final BukkitPluginMain rscp;
	private final CommandLock   cmdLock;
	private final CommandUnlock cmdUnlock;
	private final CommandFetch  cmdFetch;
	private final CommandDebug  cmdDebug;
	private final CommandReload cmdReload;
	public BukkitCommands(final BukkitPluginMain plugin)
	{
		this.rscp = plugin;
		cmdLock   = new CommandLock(rscp);
		cmdUnlock = new CommandUnlock(rscp);
		cmdFetch  = new CommandFetch(rscp);
		cmdDebug  = new CommandDebug(rscp);
		cmdReload = new CommandReload(rscp);
	}
	public final RestartableThread threadFetchDatabaseContents = new RestartableThread()
	{
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
						BukkitPluginMain.consoleLog.log(Level.INFO,
							"[rscp] Fetched {0} entities, {1} permissions and {2} inheritances",
							new Integer[]
							{
								contents.entities.length,
								contents.permissions.length,
								contents.inheritance.length,
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
				final Set<Player> debuggers = rscp.permissionManager.getDebuggers();
				if(!debuggers.isEmpty())
					rscp.getServer().getScheduler().runTask(rscp, new Runnable()
					{
						@Override
						public void run()
						{
							for(Player debugger : debuggers)
								debugger.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix
									+ "Database has been fetched in " + queryTime + " milliseconds."));
						}
					});
			} else
				BukkitPluginMain.consoleLog.warning("[rscp] Cannot load data from database.");
		}
	};
	public Thread threadMigrateFromPExSQL(final CommandSender sender)
	{
		final Thread result = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					setName("rscp:MigrateFromPermissionsEx-SQL");
					rscp.connection.executeUpdateT("Migrate_from_PermissionsEx");
					threadFetchDatabaseContents.join();
					rscp.getServer().getScheduler().runTask(rscp, new Runnable()
					{
						@Override
						public void run()
						{
							sender.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix
								+ "Migration from PermissionsEx (MySQL backend) done!"));
							sender.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix
								+ "Check the latest database row for new data."));
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
	public RestartableThread threadInsertExampleRows(final CommandSender sender)
	{
		final RestartableThread threadInsertExampleRows = new RestartableThread()
		{
			@Override
			public void run()
			{
				Thread.currentThread().setName("rscp:InsertExampleRows");
				rscp.connection.insertExampleRows();
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
		threadInsertExampleRows.startDeamon();
		return threadInsertExampleRows;
	}
	public void onCommandHub(CommandSender sender, String[] args) throws CommandAnswerException
	{
		final ArrayList<String> help = new ArrayList<>(64);
		help.add(rscp.getDescription().getName() + " v" + rscp.getDescription().getVersion()
			+ " © " + rscp.getDescription().getAuthors().get(0));
		help.add("{_DS}Perfect permission manager for multiserver environments");
		help.add("{_LB}{_U}" + rscp.getDescription().getWebsite());
		if(args.length == 0)
			throw new CommandAnswerException(help);
		help.add("{_LS}Current serverId is \'{_LG}" + rscp.getServer().getServerId() + "{_LS}\' (server.properties)");
		help.add("Usage of available commands:");
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("{_YL}/rscp user <user> lp {_LS}-- list user's permissions");
			help.add("{_YL}/rscp user <user> lg {_LS}-- list user's groups");
			help.add("{_YL}/rscp user <user> prefix {_LS}-- show user's prefix");
			help.add("{_YL}/rscp user <user> suffix {_LS}-- show user's suffix");
		}
		if(sender.hasPermission("rscp.admin.lock"))
		{
			help.add("{_YL}/rscp lock [mode] {_LS}-- enable specific maintenance mode");
			help.add("{_YL}/rscp unlock {_LS}-- disable maintenance mode");
		}
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("{_YL}/rscp examplerows {_LS}-- insert some fake rows into database");
			help.add("{_YL}/rscp import pex-sql {_LS}-- import data from pex's database (in the same schema)");
			help.add("{_YL}/rscp debug [value] {_LS}-- show/hide some debugging info to you");
			help.add("{_YL}/rscp fetch {_LS}-- reread all permissions from database");
			help.add("{_YL}/rscp reload {_LS}-- reload config and restart the plugin");
		}
		help.add("{_YL}/rscp [help] {_LS}-- show this help page");
		switch(args[0].toLowerCase())
		{
			case "user":
				onCommandHubUser(sender, args);
				return;
			case "lock":
				/* rscp lock [mMode] */
				cmdLock.execute(sender, args);
				return;
			case "unlock":
				/* rscp unlock */
				cmdUnlock.execute(sender);
				return;
			case "examplerows":
				/* rscp examplerows */
				if(sender.hasPermission("rscp.admin"))
				{
					threadInsertExampleRows(sender);
					throw new CommandAnswerException("Example rows have been added into database.");
				}
				return;
			case "import":
				/* rscp import pex <filename.yml> */
				if(sender.hasPermission("rscp.admin"))
				{
					if(args.length > 1)
						switch(args[1].toLowerCase())
						{
							case "pex-sql":
								threadMigrateFromPExSQL(sender);
								throw new CommandAnswerException("Trying to import PEX database into rscPermissions...");
						}
					throw new CommandAnswerException(new String[]
					{
						"Usage: {_YL}/rscp import <importer> [options]",
						"Available importers:",
						"{_LG}pex-sql {_LS}-- (PermissionsEx SQL Backend)",
					});
				}
				return;
			case "fetch":
				/* rscp fetch */
				cmdFetch.execute(sender);
				return;
			case "debug":
				/* rscp debug [<boolean variant>|toggle] */
				cmdDebug.execute(sender, args);
				return;
			case "reload":
				/* rscp reload */
				cmdReload.execute(sender);
				return;
			case "help":
			default:
				break;
		}
		throw new CommandAnswerException(help);
	}
	private void onCommandHubUser(CommandSender sender, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		if(args.length < 3)
			return;
		final Player player = rscp.bridgeForBukkit.findPlayer(args[1]);
		if(player != null)
			args[1] = player.getName();
		final ResolutionResult result = (player != null)
			? rscp.permissionManager.getResult(player)
			: rscp.permissionManager.getResult(args[1]);
		final ArrayList<String> answer = new ArrayList<>();
		switch(args[2].toLowerCase())
		{
			case "lp":
				answer.add("Permission list for user {_YL}" + args[1] + "{_LS}:");
				final ArrayList<String> sorted_keys = new ArrayList<>(result.permissions.keySet());
				Collections.sort(sorted_keys);
				for(String perm : sorted_keys)
					answer.add((result.permissions.get(perm) ? "{_LG}" : "{_LR}") + perm);
				throw new CommandAnswerException(answer);
			case "lg":
				answer.add("Group list for user {_YL}" + args[1] + "{_LS}:");
				for(String group : result.groups)
					answer.add("{_LG}" + group);
				throw new CommandAnswerException(answer);
			case "p":
			case "prefix":
				answer.add("Calculated prefix for user {_YL}" + args[1] + "{_LS} is:");
				answer.add("{_R}\"" + result.prefix + "{_R}\"");
				throw new CommandAnswerException(answer);
			case "s":
			case "suffix":
				answer.add("Calculated suffix for user {_YL}" + args[1] + "{_LS} is:");
				answer.add("{_R}\"" + result.suffix + "{_R}\"");
				throw new CommandAnswerException(answer);
		}
	}
	public static boolean argumentToBoolean(String arg, Boolean prevForToggle) throws IllegalArgumentException
	{
		if(arg == null || "".equals(arg))
			throw new IllegalArgumentException("Argument is null or empty.");
		switch(arg.toLowerCase())
		{
			case "enable":
			case "true":
			case "yes":
			case "on":
				return true;
			case "disable":
			case "false":
			case "no":
			case "off":
				return false;
			case "toggle":
				if(prevForToggle != null)
					return !prevForToggle;
				else
					throw new IllegalArgumentException("Previous value is unknown.");
		}
		throw new IllegalArgumentException("Cannot understand boolean value.");
	}
	public static int argumentToInteger(String arg) throws IllegalArgumentException
	{
		if(arg == null || "".equals(arg))
			throw new IllegalArgumentException("Argument is null or empty.");
		try
		{
			return Integer.parseInt(arg);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}
}
