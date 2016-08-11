package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import ru.simsonic.rscCommonsLibrary.RestartableThread;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class BukkitCommands
{
	private final BukkitPluginMain rscp;
	private final CommandEntity    cmdEntity;
	private final CommandLock      cmdLock;
	private final CommandDebug     cmdDebug;
	private final CommandReload    cmdReload;
	public BukkitCommands(final BukkitPluginMain plugin)
	{
		this.rscp = plugin;
		cmdEntity = new CommandEntity(rscp);
		cmdLock   = new CommandLock  (rscp);
		cmdDebug  = new CommandDebug (rscp);
		cmdReload = new CommandReload(rscp);
	}
	@Deprecated
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
					try
					{
						rscp.connection.executeUpdateT("Migrate_from_PermissionsEx");
					} catch(SQLException ex) {
						BukkitPluginMain.consoleLog.warning(ex.toString());
						return;
					}
					rscp.fetching.join();
					rscp.getServer().getScheduler().runTask(rscp, new Runnable()
					{
						@Override
						public void run()
						{
							sender.sendMessage(GenericChatCodes.processStringStatic(
								Settings.CHAT_PREFIX + "Migration from PermissionsEx (MySQL backend) done!"));
							sender.sendMessage(GenericChatCodes.processStringStatic(
								Settings.CHAT_PREFIX + "Check the latest database rows for new data."));
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
	@Deprecated
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
		final boolean isAdmin  = sender.hasPermission("rscp.admin");
		final boolean isLocker = sender.hasPermission("rscp.admin.lock");
		final boolean isPlayer = !(sender instanceof ConsoleCommandSender);
		help.addAll(Tools.getPluginWelcome(rscp, Phrases.HELP_HEADER_1.toString()));
		if(isLocker)
			help.add(Phrases.HELP_HEADER_2.toString().replace("{:SERVERID}", rscp.getServer().getServerId()));
		final String mmode = rscp.settings.getMaintenanceMode();
		if(isLocker && !"".equals(mmode))
			help.add(Phrases.HELP_HEADER_3.toString().replace("{:MMODE}", mmode));
		final String subcommand = args.length > 0
			? args[0].toLowerCase()
			: (isPlayer ? "" : "help");
		if("".equals(subcommand))
			throw new CommandAnswerException(help);
		// Generating full help page
		help.add(Phrases.HELP_USAGE.toString());
		if(isAdmin)
			help.addAll(cmdEntity.getHelp());
		if(isLocker)
		{
			help.add(Phrases.HELP_CMD_LOCK.toString());
			help.add(Phrases.HELP_CMD_UNLOCK.toString());
		}
		if(isAdmin)
		{
			help.add("{_YL}/rscp {_LR}examplerows {_LS}- insert some fake rows into database");
			help.add("{_YL}/rscp {_LR}import pex-sql {_LS}- import data from pex's database (in the same schema)");
			help.add(Phrases.HELP_CMD_DEBUG.toString());
			help.add(Phrases.HELP_CMD_FETCH.toString());
			help.add(Phrases.HELP_CMD_RELOAD.toString());
		}
		help.add(Phrases.HELP_CMD_HELP.toString());
		switch(subcommand)
		{
			case "listgroups":
			case "groups":
			case "lg":
			case "gs":
				cmdEntity.listGroups(sender);
				return;
			case "listusers":
			case "users":
			case "lu":
			case "us":
				cmdEntity.listUsers(sender);
				return;
			case "group":
			case "g":
				cmdEntity.onCommandHub(sender, CommandEntity.TargetType.GROUP,  args);
				return;
			case "user":
			case "u":
				cmdEntity.onCommandHub(sender, CommandEntity.TargetType.USER,   args);
				return;
			case "player":
			case "p":
				cmdEntity.onCommandHub(sender, CommandEntity.TargetType.PLAYER, args);
				return;
			case "lock":
				cmdLock.executeLock(sender, args);
				return;
			case "unlock":
				cmdLock.executeUnlock(sender);
				return;
			case "fetch":
				cmdReload.executeFetch(sender);
				return;
			case "reload":
				cmdReload.executeReload(sender);
				return;
			case "update":
				cmdReload.executeUpdate(sender, args);
				return;
			case "debug":
				cmdDebug.execute(sender, args);
				return;
			case "examplerows":
				/* DEPRECATED: rscp examplerows */
				if(sender.hasPermission("rscp.admin"))
				{
					threadInsertExampleRows(sender);
					throw new CommandAnswerException("Example rows have been added into database.");
				}
				break;
			case "import":
				/* DEPRECATED: rscp import pex-sql*/
				if(sender.hasPermission("rscp.admin"))
				{
					if(args.length <= 1)
						throw new CommandAnswerException(new String[]
						{
							"Usage: {_YL}/rscp import <importer> [options]",
							"Available importers:",
							"{_LG}pex-sql {_LS}- (PermissionsEx's SQL backend)",
						});
					switch(args[1].toLowerCase())
					{
						case "pex-sql":
							threadMigrateFromPExSQL(sender);
							throw new CommandAnswerException("Trying to import PEX database into rscPermissions...");
						default:
							break;
					}
				}
				return;
			case "help":
			default:
				break;
		}
		throw new CommandAnswerException(help);
	}
}
