package ru.simsonic.rscPermissions;
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
	public final Rewards rewardHelper;
	public final Ladders ladderHelper;
	public CommandHelper(final MainPluginClass rscp)
	{
		this.plugin = rscp;
		rewardHelper = new Rewards(rscp);
		ladderHelper = new Ladders(rscp);
	}
	public void onCommand(CommandSender sender, Command cmd, String label, String[] args) throws CommandHelperAnswerException
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
			throw new CommandHelperAnswerException("/promote <user> <ladder[.instance]>");
		case "demote":
			if(args.length >= 1)
			{
				ladderHelper.executePromotion(sender, args[0], (args.length >= 2) ? args[1] : null, false);
				return;
			}
			throw new CommandHelperAnswerException("/demote <user> <ladder[.instance]>");
		case "reward":
			if(sender instanceof Player)
			{
				String reward = (args.length >= 1) ? args[0] : null;
				rewardHelper.executeReward((Player)sender, reward);
				return;
			}
			throw new CommandHelperAnswerException("This command cannot be run from console.");
		}
	}
	private void onCommandHub(CommandSender sender, String[] args) throws CommandHelperAnswerException
	{
		final ArrayList<String> help = new ArrayList<>();
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("/rscp (user|group|ladder) -- PermissionsEx-like admin commands");
			help.add("/rscp (promote|demote) -- admin promotion/demotion commands");
		}
		if(sender.hasPermission("rscp.admin.lock"))
			help.add("/rscp (lock|unlock) -- maintenance mode control");
		if(sender.hasPermission("rscp.admin"))
		{
			help.add("/rscp (examplerows|import) -- possible useful things");
			help.add("/rscp (debug|fetch|reload) -- admin stuff");
		}
		help.add("/rscp (help) -- show these notes");
		if(help.size() > 0)
			help.add(0, "{MAGENTA}Usage:");
		help.add(0, plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion());
		help.add(1, "Perfect Superperms manager for multiserver environments");
		if(sender.hasPermission("rscp.admin"))
			help.add(2, "{_DS}Current serverId is \'{_LS}" + plugin.getServer().getServerId() + "{_DS}\' (server.properties)");
		help.add("{_LG}" + plugin.getDescription().getWebsite());
		if(args.length == 0)
			throw new CommandHelperAnswerException(help);
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
					throw new CommandHelperAnswerException("/rscp promote <player> <ladder>");
				ladderHelper.executePromotion(sender, args[1], args[2], true);
				return;
			case "demote":
				/* rscp demote <user> <ladder> */
				if(args.length < 3)
					throw new CommandHelperAnswerException("/rscp demote <player> <ladder>");
				ladderHelper.executePromotion(sender, args[1], args[2], false);
				return;
			case "lock":
				/* rscp lock [mMode] */
				if(sender.hasPermission("rscp.lock"))
				{
					final String mMode = (args.length >= 2) ? args[1] : "default";
					String mmon = "Maintenance mode enabled";
					mmon = plugin.getConfig().getString("language.mModes.locked.default.mmon", mmon);
					mmon = plugin.getConfig().getString("language.mModes.locked." + mMode + ".mmon", mmon);
					plugin.maintenance.setMaintenanceMode(mMode);
					throw new CommandHelperAnswerException(mmon);
				}
				return;
			case "unlock":
				/* rscp unlock */
				if(sender.hasPermission("rscp.lock"))
				{
					String mmoff = "Maintenance mode disabled";
					mmoff = plugin.getConfig().getString("language.mModes.unlocked", mmoff);
					plugin.maintenance.setMaintenanceMode(null);
					throw new CommandHelperAnswerException(mmoff);
				}
				break;
			case "examplerows":
				/* rscp examplerows */
				if(sender.hasPermission("rscp.admin"))
				{
					plugin.connectionList.threadInsertExampleRows(sender);
					throw new CommandHelperAnswerException("Example rows have been added into database.");
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
								throw new CommandHelperAnswerException(new String[]
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
								throw new CommandHelperAnswerException("Trying to import PEX database into rscPermissions...");
						}
					throw new CommandHelperAnswerException(new String[]
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
					throw new CommandHelperAnswerException("Tables have been fetched.");
				}
				return;
			case "reload":
				/* rscp reload */
				if(sender.hasPermission("rscp.admin.reload"))
				{
					plugin.getServer().getPluginManager().disablePlugin(plugin);
					plugin.getServer().getPluginManager().enablePlugin(plugin);
					throw new CommandHelperAnswerException("Plugin has been reloaded.");
				}
				return;
			case "update":
				/* rscp update */
				if(sender.hasPermission("rscp.admin"))
					throw new CommandHelperAnswerException(plugin.doUpdate(sender));
				return;
			case "debug":
				/* rscp debug [yes|on|no|off|toggle] */
				if(sender.hasPermission("rscp.admin"))
					throw new CommandHelperAnswerException("Not implemented yet.");
				return;
			case "help":
			default:
				throw new CommandHelperAnswerException(help);
		}
	}
	private void onCommandHubUser(CommandSender sender, String[] args) throws CommandHelperAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandHelperAnswerException("Not enough permissions.");
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
			throw new CommandHelperAnswerException(help);
		final Player player = plugin.getServer().getPlayerExact(args[1]);
		if(player == null)
			throw new CommandHelperAnswerException("Player should be online");
		final ArrayList<String> list = new ArrayList<>();
		switch(args[2].toLowerCase())
		{
			case "list":
				if(args.length < 4)
					throw new CommandHelperAnswerException(help);
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
						throw new CommandHelperAnswerException(list);
					case "groups":
						list.add("{MAGENTA}Group list for {_YL}" + player.getName() + "{MAGENTA}:");
						ArrayList<String> groups = plugin.cache.getUserGroups(player.getName());
						for(String group : groups)
							list.add("{_LG}" + group);
						throw new CommandHelperAnswerException(list);
					/*
					case "ranks":
						list.add("{MAGENTA}Ranks of player {_YL}" + player.getName() + "{MAGENTA}:");
						throw new CommandHelperAnswerException(list);
					*/
				}
				throw new CommandHelperAnswerException(list);
			case "prefix":
				if(args.length > 3)
				{
					plugin.API.setPlayerPrefix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Prefix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetPrefix(player.getName()) + "{MAGENTA}\".");
				throw new CommandHelperAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					plugin.API.setPlayerSuffix(null, player.getName(), args[3]);
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Suffix for user {_YL}" + player.getName() +
						" {MAGENTA}is \"{_R}" + plugin.cache.userGetSuffix(player.getName()) + "{MAGENTA}\".");
				throw new CommandHelperAnswerException(list);
		}
	}
	private void onCommandHubGroup(CommandSender sender, String[] args) throws CommandHelperAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandHelperAnswerException("Not enough permissions.");
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
			throw new CommandHelperAnswerException(help);
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
				throw new CommandHelperAnswerException(list);
			case "suffix":
				if(args.length > 3)
				{
					plugin.API.setGroupSuffix(null, group, args[3]);
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}has been set to \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
				} else
					list.add("{MAGENTA}Suffix for group {_YL}" + group +
						" {MAGENTA}is \"{_R}" + plugin.cache.groupGetSuffix(group) + "{MAGENTA}\".");
				throw new CommandHelperAnswerException(list);
		}
	}
	private void onCommandHubLadder(CommandSender sender, String[] args) throws CommandHelperAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandHelperAnswerException("Not enough permissions.");
		final String[] help = new String[]
		{
			"rscPermissions command hub (ladder section).",
			"{MAGENTA}Usage:",
			// "/rscp ladder <ladder> list groups",
			// "/rscp ladder <ladder> list users",
		};
		if(args.length < 3)
			throw new CommandHelperAnswerException(help);
		final String ladder = args[1];
		throw new CommandHelperAnswerException("dummy :p)");
	}
}