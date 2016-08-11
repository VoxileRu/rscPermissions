package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.Bukkit.BukkitUtilities;
import ru.simsonic.rscPermissions.Bukkit.Commands.ArgumentUtilities.OptionalParams;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Matchers;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class CommandEntity
{
	private final BukkitPluginMain rscp;
	CommandEntity(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public static enum TargetType
	{
		GROUP,
		USER,
		PLAYER,
	}
	public List<String> getHelp()
	{
		final List<String> help = new ArrayList<>(16);
		// help.add(Phrases.HELP_CMD_USER_LP.toString());
		// help.add(Phrases.HELP_CMD_USER_LG.toString());
		// help.add(Phrases.HELP_CMD_USER_P.toString());
		// help.add(Phrases.HELP_CMD_USER_S.toString());
		help.add("{_YL}/rscp groups {_LS}- show known groups");
		help.addAll(getHelpForType(CommandEntity.TargetType.GROUP));
		help.add("{_YL}/rscp users {_LS}- show known users");
		help.addAll(getHelpForType(CommandEntity.TargetType.USER));
		help.addAll(getHelpForType(CommandEntity.TargetType.PLAYER));
		return help;
	}
	public List<String> getHelpForType(TargetType type)
	{
		final List<String> templt = new ArrayList<>(20);
		switch(type)
		{
			case GROUP:
			case USER:
				templt.add("{_YL}/rscp {:T} [new] <name> listgroups {_LS}- show list of parent groups");
				templt.add("{_YL}/rscp {:T} [new] <name> listpermissions {_LS}- show list of explicit permissions");
				templt.add("{_YL}/rscp {:T} [new] <name> prefix {_LS}- view prefix");
				templt.add("{_YL}/rscp {:T} [new] <name> suffix {_LS}- view suffix");
				templt.add("{_YL}/rscp {:T} [new] <name> addgroup <group> [options]");
				templt.add("{_YL}/rscp {:T} [new] <name> addpermission <[-]perm> [options]");
				templt.add("{_YL}/rscp {:T} [new] <name> removegroup <group|id>");
				templt.add("{_YL}/rscp {:T} [new] <name> removepermission <perm|id>");
				templt.add("{_YL}[options] {_LS}can be:");
				templt.add("{_LS}1. {_WH}destination <destination> {_LS} -- it is destination, yes.");
				templt.add("{_LS}2. {_WH}expirience <levels> {_LS} -- NOT WORKING.");
				templt.add("{_LS}3. {_WH}lifitime <?!?> {_LS} -- NOT READY, will be like '1hours12min30s'.");
				break;
			case PLAYER:
				templt.add("{_YL}/rscp {:T} listgroups {_LS}- show resulting inheritance tree");
				templt.add("{_YL}/rscp {:T} listpermissions {_LS}- show final calculated permissions");
				templt.add("{_YL}/rscp {:T} prefix {_LS}- show {:T}'s prefix");
				templt.add("{_YL}/rscp {:T} suffix {_LS}- show {:T}'s suffix");
				break;
		}
		final List<String> answer = new ArrayList<>(20);
		final String       typeId = type.name().toLowerCase();
		for(String line : templt)
			answer.add(line.replace("{:T}", typeId));
		return answer;
	}
	public void onCommandHub(CommandSender sender, TargetType type, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		args = Arrays.copyOfRange(args, 1, args.length);
		if(args.length == 0)
			throw new CommandAnswerException(getHelpForType(type));
		boolean forceEntityCreation = args.length > 1 && "new".equalsIgnoreCase(args[0]);
		if(forceEntityCreation)
			// Remove this optional argument from the battlefront
			args = Arrays.copyOfRange(args, 1, args.length);
		// Find what is the target for operation
		ResolutionResult result = null;
		RowEntity        entity = null;
		if(type.equals(TargetType.PLAYER))
		{
			// Search for online or offline player object
			String target = args[0];
			final Player        online  = BukkitUtilities.findOnlinePlayer(target);
			final OfflinePlayer offline = online != null
				? online
				: BukkitUtilities.findOfflinePlayer(target);
			// Does command sender require convertion of game Player into database User?
			boolean convertToName = false;
			boolean convertToUUID = false;
			boolean convertToIPv4 = false;
			if(args.length > 1 && args[1] != null && !"".equals(args[1]))
			{
				switch(args[1].toLowerCase())
				{
					case "--by-name":
					case "-n":
						convertToName = true;
						break;
					case "--by-uuid":
					case "-u":
						convertToUUID = true;
						break;
					case "--by-ip":
					case "-i":
						convertToIPv4 = true;
						break;
				}
			}
			// Crop parameter #1 from arguments list
			if(convertToName || convertToUUID || convertToIPv4)
			{
				// Convert player into user with specified parameter
				try
				{
					if(offline != null && convertToName)
					{
						target = offline.getName();
						forceEntityCreation = true;
					}
				} catch(RuntimeException | NoSuchMethodError ex) {
				}
				try
				{
					if(offline != null && convertToUUID)
					{
						target = offline.getUniqueId().toString();
						forceEntityCreation = true;
					}
				} catch(RuntimeException | NoSuchMethodError ex) {
				}
				if(online != null && convertToIPv4)
				{
					target = online.getAddress().getAddress().getHostAddress();
					forceEntityCreation = true;
				}
				args    = Arrays.copyOfRange(args, 1, args.length);
				args[0] = target;
				type    = TargetType.USER;
			} else {
				// Calculate player's permission tree
				if(online != null)
				{
					args[0] = online.getName();
					result = rscp.permissionManager.getResult(online);
				} else if(offline != null) {
					args[0] = offline.getName();
					result = rscp.permissionManager.getResult(offline);
				} else
					result = rscp.permissionManager.getResult(args[0]);
			}
		}
		switch(type)
		{
			case USER:
				entity = rscp.internalCache.findUserEntity(args[0]);
				if(entity == null && forceEntityCreation)
					entity = createEntity(EntityType.PLAYER, args[0]);
				break;
			case GROUP:
				entity = rscp.internalCache.findGroupEntity(args[0]);
				if(entity == null && forceEntityCreation)
					entity = createEntity(EntityType.GROUP, args[0]);
				break;
			case PLAYER:
				// Already dispatched
		}
		if(entity == null && result == null)
			throw new CommandAnswerException(new String[]
			{
				"{_LR}Sorry, I don't know such identifier!",
				"{_LR}Do you want to force it's creation with special keyword {_YL}new{_LR} before name?",
			});
		if(result != null)
			onPlayerCommand(result, args);
		else
			onEntityCommand(entity, type, args);
		throw new CommandAnswerException(getHelpForType(type));
	}
	private void onEntityCommand(RowEntity entity, TargetType type, String[] args) throws CommandAnswerException
	{
		final String subcommand = args.length > 1 && args[1] != null
			? args[1].toLowerCase()
			: "info";
		// Subcommands that doesn't require more arguments
		switch(subcommand)
		{
			case "prefix":
			case "p":
				showEntityPrefix(entity);
				break;
			case "suffix":
			case "s":
				showEntitySuffix(entity);
				break;
			case "listpermissions":
			case "permissions":
			case "lp":
				showEntityPermissions(entity);
				break;
			case "listgroups":
			case "groups":
			case "lg":
				showEntityParents(entity);
				break;
			case "info":
				throw new CommandAnswerException(showEntityDetails(entity));
			case "help":
				throw new CommandAnswerException(getHelpForType(type));
		}
		if(args.length < 3)
			throw new CommandAnswerException("FEW ARGUMENTS");
		final String target = args[2];
		args = Arrays.copyOfRange(args, 3, args.length);
		final OptionalParams optional = ArgumentUtilities.parseCommandParams(args);
		switch(subcommand)
		{
			case "setprefix":
			case "sp":
				// TO DO HERE
				break;
			case "setsuffix":
			case "ss":
				// TO DO HERE
				break;
			case "addgroup":
			case "ag":
				addGroup        (entity, target, optional);
				break;
			case "addpermission":
			case "ap":
				addPermission   (entity, target, optional);
				break;
			case "removegroup":
			case "rg":
				removeGroup     (entity, target);
				break;
			case "removepermission":
			case "rp":
				removePermission(entity, target);
				break;
		}
	}
	private void onPlayerCommand(ResolutionResult result, String[] args) throws CommandAnswerException
	{
		final String targetName = args[0];
		final String subcommand = args.length > 1 && args[1] != null
			? args[1].toLowerCase()
			: "info";
		switch(subcommand)
		{
			case "prefix":
			case "p":
				showPlayerPrefix(result, args[0]);
				break;
			case "suffix":
			case "s":
				showPlayerSuffix(result, targetName);
				break;
			case "listpermissions":
			case "permissions":
			case "lp":
				showPlayerPermissions(result, targetName);
				break;
			case "listgroups":
			case "groups":
			case "lg":
				showPlayerParents(result, targetName);
				break;
			case "info":
				// Should I show some INFO for this result? not now ...
			case "help":
				throw new CommandAnswerException(getHelpForType(TargetType.PLAYER));
		}
	}
	private RowEntity createEntity(EntityType type, String name)
	{
		final RowEntity result = new RowEntity();
		result.entity      = name;
		result.entityType  = type;
		result.permissions = new RowPermission[]  {};
		result.inheritance = new RowInheritance[] {};
		return result;
	}
	public void listGroups(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		final List<String>   answer = new ArrayList<>(16);
		final Set<RowEntity> groups = rscp.internalCache.getKnownGroupObjects();
		answer.add("There are following known groups in database:");
		for(RowEntity group : groups)
		{
			final String details = showEntityDetails(group);
			if(details != null)
				answer.add(details);
		}
		throw new CommandAnswerException(answer);
	}
	public void listUsers(CommandSender sender) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		final List<String>   answer = new LinkedList<>();
		final Set<RowEntity> users  = rscp.internalCache.getKnownUserObjects();
		answer.add("There are following known users in database:");
		for(RowEntity user : users)
		{
			final String details = showEntityDetails(user);
			if(details != null)
				answer.add(details);
		}
		throw new CommandAnswerException(answer);
	}
	private String showEntityDetails(RowEntity entity)
	{
		final String name = entity.entity;
		if("".equals(name))
			return null;
		final StringBuilder sb = new StringBuilder();
		if(entity.splittedId != null)
			sb.append("{_WH}").append(entity.splittedId).append(" ");
		sb.append("{_YL}").append(name);
		final boolean isPrefix = entity.prefix != null && !"".equals(entity.prefix);
		final boolean isSuffix = entity.suffix != null && !"".equals(entity.suffix);
		if(isPrefix || isSuffix)
			sb
				.append("{_R} {_LS}[\'")
				.append(isPrefix ? entity.prefix : "")
				.append("{_LS}\', \'")
				.append(isSuffix ? entity.suffix : "")
				.append("{_LS}\']");
		if(entity.lifetime != null)
		{
			final String lifetime = entity.lifetime
				.toLocalDateTime()
				.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
				.replace("T", " ");
			sb.append("{_R} {_YL}").append(lifetime);
		}
		if(entity.permissions != null && entity.permissions.length > 0)
			sb.append(String.format("{_R} {_LC}%d{_DC}p", entity.permissions.length));
		if(entity.inheritance != null && entity.inheritance.length > 0)
			sb.append(String.format("{_R} {_LC}%d{_DC}i", entity.inheritance.length));
		return sb.toString();
	}
	private void showEntityPermissions(RowEntity entity)  throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		final String typeName = entity.entityType.name().toLowerCase();
		answer.add("Explicit permissions for " + typeName + " {_YL}" + entity.entity + "{_LS}:");
		for(RowPermission permission : entity.permissions)
		{
			final String details = showPermissionDetails(permission);
			if(details != null)
				answer.add(details);
		}
		throw new CommandAnswerException(answer);
	}
	private String showPermissionDetails(RowPermission row)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{_WH}").append(row.splittedId);
		sb.append(row.value ? " {_LG}" : " {_LR}").append(row.permission);
		if(row.destination != null)
		{
			final String destination = row.destination.toString();
			if(!"".equals(destination))
				sb.append("{_R} {_LC}{_U}").append(destination);
		}
		if(row.expirience > 0)
			sb.append("{_R} {_LB}").append(row.expirience).append(" LVLs");
		if(row.lifetime != null)
		{
			final String lifetime = row.lifetime.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
			sb.append("{_R} {_YL}").append(lifetime);
		}
		return sb.toString();
	}
	private void showEntityParents(RowEntity entity) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Explicit parent groups for " + entity.entityType.name().toLowerCase() + " {_YL}" + entity.entity + "{_LS}:");
		for(RowInheritance parent : entity.inheritance)
		{
			final String details = showInheritanceDetails(parent);
			if(details != null)
				answer.add(details);
		}
		throw new CommandAnswerException(answer);
	}
	private String showInheritanceDetails(RowInheritance row)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("{_WH}").append(row.splittedId).append(" {_LG}").append(row.getParentWithInstance());
		sb.append(String.format("{_R} {_DG}({_LG}%d{_DG})", row.priority));
		if(row.destination != null)
		{
			final String destination = row.destination.toString();
			if(!"".equals(destination))
				sb.append("{_R} {_LC}{_U}").append(destination);
		}
		if(row.expirience > 0)
			sb.append("{_R} {_LB}").append(row.expirience).append(" LVLs");
		if(row.lifetime != null)
		{
			final String lifetime = row.lifetime.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
			sb.append("{_R} {_YL}").append(lifetime);
		}
		return sb.toString();
	}
	private void showPlayerPermissions(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Permission list for user {_YL}" + user + "{_LS}:");
		for(Map.Entry<String, Boolean> entry : result.getPermissions().entrySet())
			answer.add((entry.getValue() ? "{_LG}" : "{_LR}") + entry.getKey());
		throw new CommandAnswerException(answer);
	}
	private void showPlayerParents(ResolutionResult result, String player) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(player))
			player = Matchers.uuidAddDashes(player);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("List of parent groups for player {_YL}" + player + "{_LS}:");
		for(String group : result.getOrderedGroups())
			answer.add("{_LG}" + group);
		throw new CommandAnswerException(answer);
	}
	private void showEntityPrefix(RowEntity entity) throws CommandAnswerException
	{
		if(entity.prefix != null)
			throw new CommandAnswerException(String.format(
				"Own prefix for %s {_YL}%s{_LS} is {_R}\"%s{_R}\"",
				entity.entityType.equals(EntityType.GROUP) ? "group" : "user",
				entity.entity,
				entity.prefix));
		throw new CommandAnswerException(String.format(
				"Own prefix for %s {_YL}%s{_LS} is not set (null).",
				entity.entityType.equals(EntityType.GROUP) ? "group" : "user",
				entity.entity));
	}
	private void showEntitySuffix(RowEntity entity) throws CommandAnswerException
	{
		if(entity.suffix != null)
			throw new CommandAnswerException(String.format(
				"Own suffix for %s {_YL}%s{_LS} is {_R}\"%s{_R}\"",
				entity.entityType.equals(EntityType.GROUP) ? "group" : "user",
				entity.entity,
				entity.suffix));
		throw new CommandAnswerException(String.format(
				"Own suffix for %s {_YL}%s{_LS} is not set (null).",
				entity.entityType.equals(EntityType.GROUP) ? "group" : "user",
				entity.entity));
	}
	private void showPlayerPrefix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated prefix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.getPrefix() + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void showPlayerSuffix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated suffix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.getSuffix() + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void addGroup(RowEntity entity, String parent, OptionalParams optional) throws CommandAnswerException
	{
		final String[]  splitted = RowInheritance.splitIntoNameAndInstance(parent);
		final RowEntity existing = rscp.internalCache.findGroupEntity(splitted[0]);
		if(existing != null)
			parent = RowInheritance.mergeNameAndInstance(existing.entity, splitted[1]);
		// final ArrayList<String> answer = new ArrayList<>();
		final RowInheritance row = new RowInheritance();
		row.entity     = entity.entity;
		row.entityType = entity.entityType;
		row.parent     = parent;
		if(row.destination == null)
			row.destination = optional.destination;
		rscp.connection.addInheritance(row);
		rscp.fetchNowAndReschedule();
		throw new CommandAnswerException("{_LG}All is ok? I don't ready to check it myself.");
	}
	private void addPermission(RowEntity entity, String permission, OptionalParams optional) throws CommandAnswerException
	{
		// final ArrayList<String> answer = new ArrayList<>();
		final RowPermission row = new RowPermission();
		boolean negate = permission.startsWith("-");
		if(negate)
			permission = permission.substring(1);
		row.entity     = entity.entity;
		row.entityType = entity.entityType;
		row.permission = permission;
		row.value      = !negate;
		if(row.destination == null)
			row.destination = optional.destination;
		rscp.connection.addPermission(row);
		rscp.fetchNowAndReschedule();
		throw new CommandAnswerException("{_LG}All is ok? I don't ready to check it myself.");
	}
	private void removeGroup(RowEntity entity, String whatToRemove) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		// Find out what does entered identifier mean?
		final LinkedList<RowInheritance> possibleTargets = new LinkedList<>();
		for(RowInheritance row : entity.inheritance)
		{
			if(whatToRemove.equalsIgnoreCase(row.splittedId))
			{
				if(row.hasClonesInRow())
					answer.add("{_LR}Cannot remove inheritance record {_WH}"
						+ row.splittedId + "{_LR} from DB due to too complex initial data.");
				else
					possibleTargets.add(row);
				continue;
			}
			if(whatToRemove.equalsIgnoreCase(row.getParentWithInstance()))
				possibleTargets.add(row);
		}
		if(possibleTargets.isEmpty())
			throw new CommandAnswerException("{_LR}Sorry, I don't understand what I have to remove from DB.");
		if(possibleTargets.size() > 1)
			throw new CommandAnswerException("{_LR}There are several possibilities what to remove. Please use unique white id.");
		final RowInheritance row = possibleTargets.get(0);
		rscp.connection.removeInheritanceById(row.id);
		answer.add("{_LG}Successfully removed inheritance record {_WH}" + row.splittedId + "{_LG}!");
		rscp.fetchNowAndReschedule();
		throw new CommandAnswerException(answer);
	}
	private void removePermission(RowEntity entity, String whatToRemove) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		// Find out what does entered identifier mean?
		final LinkedList<RowPermission> possibleTargets = new LinkedList<>();
		for(RowPermission row : entity.permissions)
		{
			if(whatToRemove.equalsIgnoreCase(row.splittedId))
			{
				if(row.hasClonesInRow())
					answer.add("{_LR}Cannot remove inheritance record {_WH}"
						+ row.splittedId + "{_LR} from DB due to too complex initial data.");
				else
					possibleTargets.add(row);
				continue;
			}
			if(whatToRemove.equalsIgnoreCase(row.permission))
				possibleTargets.add(row);
		}
		if(possibleTargets.isEmpty())
			throw new CommandAnswerException("{_LR}Sorry, I don't understand what I should remove from DB.");
		if(possibleTargets.size() > 1)
			throw new CommandAnswerException("{_LR}There are several possibilities what to remove. Please use unique white id.");
		final RowPermission row = possibleTargets.get(0);
		rscp.connection.removePermissionsById(row.id);
		answer.add("{_LG}Successfully removed inheritance record {_WH}" + row.splittedId + "{_LG}!");
		rscp.fetchNowAndReschedule();
		throw new CommandAnswerException(answer);
	}
}
