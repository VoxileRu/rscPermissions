package ru.simsonic.rscPermissions.Bukkit.Commands;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Bukkit.PermissionsEx_YAML;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.CommandProcessing.CommandAnswerException;
import ru.simsonic.rscUtilityLibrary.RestartableThread;

public class BukkitCommands
{
	private final BukkitPluginMain rscp;
	public BukkitCommands(final BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	public final RestartableThread threadFetchDatabaseContents = new RestartableThread()
	{
		@Override
		public void run()
		{
			if(rscp.connection.isConnected() == false)
				if(rscp.connection.connect() == false)
				{
					BukkitPluginMain.consoleLog.warning("[rscp] Cannot connect to database! Using local cache only.");
					return;
				}
			final DatabaseContents contents = rscp.connection.retrieveContents();
			if(contents != null)
			{
				contents.normalize();
				rscp.fileCache.cleanup();
				rscp.fileCache.saveContents(contents);
				contents.filterServerId(rscp.getServer().getServerId());
				rscp.internalCache.fill(contents);
				final Runnable syncTask = new Runnable()
				{
					@Override
					public synchronized void run()
					{
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
					setName("rscp:MigrateFromPExSQL");
					rscp.connection.executeUpdateT("Migrate_from_PermissionsEx");
					threadFetchDatabaseContents.join();
					rscp.getServer().getScheduler().runTask(rscp, new Runnable()
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
	public void onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandAnswerException
	{
		switch(cmd.getName().toLowerCase())
		{
		case "rscp":
			onCommandHub(sender, args);
			break;
		}
	}
	private void onCommandHub(CommandSender sender, String[] args) throws CommandAnswerException
	{
		final ArrayList<String> help = new ArrayList<>();
		if(sender.hasPermission("rscp.admin"))
			help.add("/rscp (user|group) {_LS}-- PermissionsEx-like admin commands");
		if(sender.hasPermission("rscp.admin.lock"))
			help.add("/rscp (lock|unlock) {_LS}-- maintenance mode control");
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("/rscp (examplerows|import) {_LS}-- possible useful things");
			help.add("/rscp (debug|fetch|reload) {_LS}-- admin stuff");
		}
		help.add("/rscp (help) {_LS}-- show these notes");
		if(help.size() > 0)
			help.add(0, "{MAGENTA}Usage:");
		help.add(0, rscp.getDescription().getName() + " v" + rscp.getDescription().getVersion());
		help.add(1, "Perfect Superperms manager for multiserver environments");
		if(sender.hasPermission("rscp.admin"))
			help.add(2, "{_DS}Current serverId is \'{_LS}" + rscp.getServer().getServerId() + "{_DS}\' (server.properties)");
		help.add("{_LG}" + rscp.getDescription().getWebsite());
		if(args.length == 0)
			throw new CommandAnswerException(help);
		switch(args[0].toLowerCase())
		{
			case "user":
				onCommandHubUser(sender, args);
				return;
			case "group":
				onCommandHubGroup(sender, args);
				return;
			case "lock":
				/* rscp lock [mMode] */
				if(sender.hasPermission("rscp.lock"))
				{
					final String mMode = (args.length >= 2) ? args[1] : "default";
					String mmon = "Maintenance mode enabled";
					mmon = rscp.getConfig().getString("language.maintenance.locked.default.mmon", mmon);
					mmon = rscp.getConfig().getString("language.maintenance.locked." + mMode + ".mmon", mmon);
					rscp.bukkitListener.setMaintenanceMode(mMode);
					throw new CommandAnswerException(mmon);
				}
				return;
			case "unlock":
				/* rscp unlock */
				if(sender.hasPermission("rscp.lock"))
				{
					String mmoff = "Maintenance mode disabled";
					mmoff = rscp.getConfig().getString("language.maintenance.unlocked", mmoff);
					rscp.bukkitListener.setMaintenanceMode(null);
					throw new CommandAnswerException(mmoff);
				}
				break;
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
							case "pex-yaml":
								if(args.length == 2)
									break;
								// TO DO HERE
								PermissionsEx_YAML importer_pex = new PermissionsEx_YAML(
									new File(rscp.getDataFolder(), args[2]));
								threadFetchDatabaseContents.startDeamon();
								throw new CommandAnswerException(new String[]
								{
									"Data has been imported successfully!",
									/*
									"Entities: {MAGENTA}"    + Integer.toString(importer_pex.getEntities().length),
									"Permissions: {MAGENTA}" + Integer.toString(importer_pex.getPermissions().length),
									"Inheritance: {MAGENTA}" + Integer.toString(importer_pex.getInheritance().length),
									*/
									"{_DR}{_B}IT IS FAKE :p - all this is undone yet!",
								});
							case "pex-sql":
								threadMigrateFromPExSQL(sender);
								throw new CommandAnswerException("Trying to import PEX database into rscPermissions...");
						}
					throw new CommandAnswerException(new String[]
					{
						"Usage: {GOLD}/rscp import <importer> [options]",
						"Available importers:",
						"{_LR}pex-yaml{_LS} (PermissionsEx)",
						"{_LG}pex-sql{_LS} (PermissionsEx)",
					});
				}
				return;
			case "fetch":
				/* rscp fetch */
				if(sender.hasPermission("rscp.admin.reload"))
				{
					threadFetchDatabaseContents.startDeamon();
					throw new CommandAnswerException("Tables have been fetched.");
				}
				return;
			case "reload":
				/* rscp reload */
				if(sender.hasPermission("rscp.admin.reload"))
				{
					rscp.getServer().getPluginManager().disablePlugin(rscp);
					rscp.getServer().getPluginManager().enablePlugin(rscp);
					throw new CommandAnswerException("Plugin has been reloaded.");
				}
				return;
			case "debug":
				/* rscp debug [yes|on|no|off|toggle] */
				if(sender.hasPermission("rscp.admin"))
					throw new CommandAnswerException("Not implemented yet.");
				return;
			case "help":
			default:
				throw new CommandAnswerException(help);
		}
	}
	private void onCommandHubUser(CommandSender sender, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		final String[] help = new String[]
		{
			"rscPermissions command hub (user section).",
			"{MAGENTA}Usage:",
			"/rscp user <user> list permissions",
			"/rscp user <user> list groups",
			// "/rscp user <user> list ranks",
			"/rscp user <user> prefix [prefix]",
			"/rscp user <user> suffix [suffix]",
		};
		if(args.length < 3)
			throw new CommandAnswerException(help);
		final Player player = rscp.getServer().getPlayerExact(args[1]);
		if(player == null)
			throw new CommandAnswerException("Player should be online");
		final ArrayList<String> list = new ArrayList<>();
		switch(args[2].toLowerCase())
		{
			case "list":
				if(args.length < 4)
					throw new CommandAnswerException(help);
				switch(args[3].toLowerCase())
				{
					case "permissions":
						list.add("{MAGENTA}Permission list for {_YL}" + player.getName());
						final Map<String, Boolean> pv = rscp.permissionManager.listPlayerPermissions(player);
						final ArrayList<String> sorted_keys = new ArrayList<>(pv.keySet());
						Collections.sort(sorted_keys);
						for(String perm : sorted_keys)
							if(pv.containsKey(perm))
								list.add((pv.get(perm) ? "{_LG}" : "{_LR}") + perm);
						throw new CommandAnswerException(list);
					case "groups":
						list.add("{MAGENTA}Group list for {_YL}" + player.getName() + "{MAGENTA}:");
						/*
						ArrayList<String> groups = plugin.cache.getUserGroups(player.getName());
						for(String group : groups)
							list.add("{_LG}" + group);
						*/
						throw new CommandAnswerException(list);
					/*
					case "ranks":
						list.add("{MAGENTA}Ranks of player {_YL}" + player.getName() + "{MAGENTA}:");
						throw new CommandAnswerException(list);
					*/
				}
				throw new CommandAnswerException(list);
			case "prefix":
				if(args.length > 3)
				{
					/*
					plugin.API.setPlayerPrefix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
					*/
				} else {
					/*
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
					*/
				}
				throw new CommandAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					/*
					plugin.API.setPlayerSuffix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
					*/
				} else {
					/*
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
					*/
				}
				throw new CommandAnswerException(list);
		}
	}
	private void onCommandHubGroup(CommandSender sender, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		final String[] help = new String[]
		{
			"rscPermissions command hub (group section).",
			"{MAGENTA}Usage:",
			// "/rscp group <group> list permissions",
			// "/rscp group <group> list ranks",
			"/rscp group <group> prefix [prefix]",
			"/rscp group <group> suffix [suffix]",
		};
		if(args.length < 3)
			throw new CommandAnswerException(help);
		final String group = args[1];
		final ArrayList<String> list = new ArrayList<>();
		switch(args[2].toLowerCase())
		{
			case "prefix":
				if(args.length > 3)
				{
					/*
					plugin.API.setGroupPrefix(null, group, args[3]);
					list.add("{MAGENTA}Prefix for group {_YL}" + group +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.groupGetPrefix(group) + "{MAGENTA}\".");
					*/
				} else {
					/*
					list.add("{MAGENTA}Prefix for group {_YL}" + group +
						" {MAGENTA}is \"{_R}" + plugin.cache.groupGetPrefix(group) + "{MAGENTA}\".");
					*/
				}
				throw new CommandAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					/*
					plugin.API.setGroupSuffix(null, group, args[3]);
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
					*/
				} else {
					/*
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}is \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
					*/
				}
				throw new CommandAnswerException(list);
		}
	}
}
