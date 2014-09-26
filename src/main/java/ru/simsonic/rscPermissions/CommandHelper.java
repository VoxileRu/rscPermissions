package ru.simsonic.rscPermissions;
import ru.simsonic.utilities.CommandAnswerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.Importers.PermissionsEx_YAML;

public class CommandHelper
{
	private final MainPluginClass plugin;
	public final Ladders ladderHelper;
	public CommandHelper(final MainPluginClass rscp)
	{
		this.plugin = rscp;
		ladderHelper = new Ladders(rscp);
	}
	public void onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandAnswerException
	{
		switch(cmd.getName().toLowerCase())
		{
		case "rscp":
			onCommandHub(sender, args);
			return;
		case "promote":
			if(args.length >= 1)
			{
				ladderHelper.executePromotion(sender, args[0], (args.length >= 2) ? args[1] : null, true);
				return;
			}
			throw new CommandAnswerException("/promote <user> <ladder[.instance]>");
		case "demote":
			if(args.length >= 1)
			{
				ladderHelper.executePromotion(sender, args[0], (args.length >= 2) ? args[1] : null, false);
				return;
			}
			throw new CommandAnswerException("/demote <user> <ladder[.instance]>");
		}
	}
	private void onCommandHub(CommandSender sender, String[] args) throws CommandAnswerException
	{
		final ArrayList<String> help = new ArrayList<>();
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("/rscp (user|group|ladder) {_LS}-- PermissionsEx-like admin commands");
			help.add("/rscp (promote|demote) {_LS}-- admin promotion/demotion commands");
		}
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
		help.add(0, plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
		help.add(1, "Perfect Superperms manager for multiserver environments");
		if(sender.hasPermission("rscp.admin"))
			help.add(2, "{_DS}Current serverId is \'{_LS}" + plugin.getServer().getServerId() + "{_DS}\' (server.properties)");
		help.add("{_LG}" + plugin.getDescription().getWebsite());
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
			case "ladder":
				onCommandHubLadder(sender, args);
				return;
			case "promote":
				/* rscp promote <user> <ladder> */
				if(args.length < 3)
					throw new CommandAnswerException("/rscp promote <player> <ladder>");
				ladderHelper.executePromotion(sender, args[1], args[2], true);
				return;
			case "demote":
				/* rscp demote <user> <ladder> */
				if(args.length < 3)
					throw new CommandAnswerException("/rscp demote <player> <ladder>");
				ladderHelper.executePromotion(sender, args[1], args[2], false);
				return;
			case "lock":
				/* rscp lock [mMode] */
				if(sender.hasPermission("rscp.lock"))
				{
					final String mMode = (args.length >= 2) ? args[1] : "default";
					String mmon = "Maintenance mode enabled";
					mmon = plugin.getConfig().getString("language.maintenance.locked.default.mmon", mmon);
					mmon = plugin.getConfig().getString("language.maintenance.locked." + mMode + ".mmon", mmon);
					plugin.maintenance.setMaintenanceMode(mMode);
					throw new CommandAnswerException(mmon);
				}
				return;
			case "unlock":
				/* rscp unlock */
				if(sender.hasPermission("rscp.lock"))
				{
					String mmoff = "Maintenance mode disabled";
					mmoff = plugin.getConfig().getString("language.maintenance.unlocked", mmoff);
					plugin.maintenance.setMaintenanceMode(null);
					throw new CommandAnswerException(mmoff);
				}
				break;
			case "examplerows":
				/* rscp examplerows */
				if(sender.hasPermission("rscp.admin"))
				{
					plugin.connectionList.threadInsertExampleRows(sender);
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
								PermissionsEx_YAML importer_pex = new PermissionsEx_YAML(plugin, args[2]);
								plugin.connectionList.threadFetchTablesData();
								throw new CommandAnswerException(new String[]
								{
									"Data has been imported successfully!",
									"Entities: {MAGENTA}" + Integer.toString(importer_pex.getEntities().length),
									"Permissions: {MAGENTA}" + Integer.toString(importer_pex.getPermissions().length),
									"Inheritance: {MAGENTA}" + Integer.toString(importer_pex.getInheritance().length),
									"Ladders: {MAGENTA}" + Integer.toString(importer_pex.getLadders().length),
									"{_DR}{_B}FAKE :p - all this is undone yet!",
								});
							case "pex-sql":
								plugin.connectionList.threadMigrateFromPExSQL(sender);
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
					plugin.connectionList.threadFetchTablesData();
					throw new CommandAnswerException("Tables have been fetched.");
				}
				return;
			case "reload":
				/* rscp reload */
				if(sender.hasPermission("rscp.admin.reload"))
				{
					plugin.getServer().getPluginManager().disablePlugin(plugin);
					plugin.getServer().getPluginManager().enablePlugin(plugin);
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
		final Player player = plugin.getServer().getPlayerExact(args[1]);
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
						final PermissionAttachment pa = plugin.attachments.get(player);
						if(pa == null)
							break;
						final Map<String, Boolean> pv = pa.getPermissions();
						if(pv == null)
							break;
						final ArrayList<String> sorted_keys = new ArrayList<>(pv.keySet());
						Collections.sort(sorted_keys);
						for(String perm : sorted_keys)
							if(pv.containsKey(perm))
								list.add((pv.get(perm) ? "{_LG}" : "{_LR}") + perm);
						throw new CommandAnswerException(list);
					case "groups":
						list.add("{MAGENTA}Group list for {_YL}" + player.getName() + "{MAGENTA}:");
						ArrayList<String> groups = plugin.cache.getUserGroups(player.getName());
						for(String group : groups)
							list.add("{_LG}" + group);
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
					plugin.API.setPlayerPrefix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
				throw new CommandAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					plugin.API.setPlayerSuffix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
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
					plugin.API.setGroupPrefix(null, group, args[3]);
					list.add("{MAGENTA}Prefix for group {_YL}" + group +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.groupGetPrefix(group) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Prefix for group {_YL}" + group +
						" {MAGENTA}is \"{_R}" + plugin.cache.groupGetPrefix(group) + "{MAGENTA}\".");
				throw new CommandAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					plugin.API.setGroupSuffix(null, group, args[3]);
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}is \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
				throw new CommandAnswerException(list);
		}
	}
	private void onCommandHubLadder(CommandSender sender, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		final String[] help = new String[]
		{
			"rscPermissions command hub (ladder section).",
			"{MAGENTA}Usage:",
			// "/rscp ladder <ladder> list groups",
			// "/rscp ladder <ladder> list users",
		};
		if(args.length < 3)
			throw new CommandAnswerException(help);
		final String ladder = args[1];
		throw new CommandAnswerException("dummy :p)");
	}
}