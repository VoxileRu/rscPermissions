package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.Bukkit.BukkitUtilities;
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
		final List<String> answer = new ArrayList<>(16);
		final String typeName = type.name().toLowerCase();
		switch(type)
		{
			case GROUP:
			case USER:
				answer.add(String.format("{_YL}/rscp %s listgroups {_LS}- show list of parent groups", typeName));
				answer.add(String.format("{_YL}/rscp %s listpermissions {_LS}- show list of explicit permissions", typeName));
				answer.add(String.format("{_YL}/rscp %s prefix [<value>] {_LS}- view or change %s's prefix", typeName, typeName));
				answer.add(String.format("{_YL}/rscp %s suffix [<value>] {_LS}- view or change %s's suffix", typeName, typeName));
				break;
			case PLAYER:
				answer.add(String.format("{_YL}/rscp %s listgroups {_LS}- show resulting inheritance tree", typeName));
				answer.add(String.format("{_YL}/rscp %s listpermissions {_LS}- show final calculated permissions", typeName));
				answer.add(String.format("{_YL}/rscp %s prefix {_LS}- show %s's prefix", typeName, typeName));
				answer.add(String.format("{_YL}/rscp %s suffix {_LS}- show %s's suffix", typeName, typeName));
				break;
		}
		return answer;
	}
	public void onEntityCommandHub(CommandSender sender, TargetType type, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		args = Arrays.copyOfRange(args, 1, args.length);
		if(args.length == 0)
			throw new CommandAnswerException(getHelpForType(type));
		ResolutionResult result = null;
		RowEntity        entity = null;
		switch(type)
		{
			case PLAYER:
				final Player online = BukkitUtilities.findOnlinePlayer(args[0]);
				if(online != null)
				{
					args[0] = online.getName();
					result = rscp.permissionManager.getResult(online);
					break;
				}
				result = rscp.permissionManager.getResult(args[0]);
				break;
			case USER:
				entity = rscp.internalCache.findUserEntity(args[0]);
				break;
			case GROUP:
				entity = rscp.internalCache.findGroupEntity(args[0]);
				break;
		}
		if(entity == null && result == null)
			throw new CommandAnswerException("{_LR}Sorry, I don't know such identifier!");
		final String targetName = args[0];
		final String subcommand = args.length > 1 && args[1] != null
			? args[1].toLowerCase()
			: "info";
		switch(subcommand)
		{
			case "info":
				if(entity != null)
					throw new CommandAnswerException(showEntityDetails(entity));
			case "help":
				throw new CommandAnswerException(getHelpForType(type));
			case "prefix":
			case "p":
				if(result != null)
					viewCalculatedPrefix(result, targetName);
				else
					viewEntityPrefix(entity);
				break;
			case "suffix":
			case "s":
				if(result != null)
					viewCalculatedSuffix(result, targetName);
				else
					viewEntitySuffix(entity);
				break;
			case "listpermissions":
			case "permissions":
			case "lp":
				if(result != null)
					listFinalPlayerPermissions(result, targetName);
				else
					showEntityPermissions(entity);
				break;
			case "listgroups":
			case "groups":
			case "lg":
				if(result != null)
					listUserGroupsTree(result, targetName);
				else
					showEntityParents(entity);
				break;
			case "addpermission":
			case "ap":
				break;
			case "removepermission":
			case "rp":
				break;
			case "addgroup":
			case "ag":
				// TO DO HERE
				addGroup(result, targetName, null, null, null);
				break;
			case "removegroup":
			case "rg":
				// TO DO HERE
				removeGroup(result, targetName, null);
				break;
			default:
				break;
		}
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
	private void listFinalPlayerPermissions(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Permission list for user {_YL}" + user + "{_LS}:");
		for(Map.Entry<String, Boolean> entry : result.getPermissions().entrySet())
			answer.add((entry.getValue() ? "{_LG}" : "{_LR}") + entry.getKey());
		throw new CommandAnswerException(answer);
	}
	private void listUserGroupsTree(ResolutionResult result, String player) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(player))
			player = Matchers.uuidAddDashes(player);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("List of parent groups for player {_YL}" + player + "{_LS}:");
		for(String group : result.getOrderedGroups())
			answer.add("{_LG}" + group);
		throw new CommandAnswerException(answer);
	}
	private void viewEntityPrefix(RowEntity entity) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Own prefix for " + (entity.entityType == EntityType.GROUP ? "group" : "user")
			+ " {_YL}" + entity.entity + "{_LS} is:");
		answer.add("{_R}\"" + (entity.prefix != null ? entity.prefix : "") + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void viewEntitySuffix(RowEntity entity) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Own suffix for " + (entity.entityType == EntityType.GROUP ? "group" : "user")
			+ " {_YL}" + entity.entity + "{_LS} is:");
		answer.add("{_R}\"" + (entity.suffix != null ? entity.suffix : "") + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void viewCalculatedPrefix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated prefix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.getPrefix() + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void viewCalculatedSuffix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated suffix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.getSuffix() + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void addGroup(ResolutionResult result, String user, String parent, String destination, Integer seconds) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		throw new CommandAnswerException(answer);
	}
	private void removeGroup(ResolutionResult result, String user, String parent) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		throw new CommandAnswerException(answer);
	}
}
