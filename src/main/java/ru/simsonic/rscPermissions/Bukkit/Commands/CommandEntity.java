package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
	public void onEntityCommandHub(CommandSender sender, TargetType type, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin") == false)
			throw new CommandAnswerException("Not enough permissions.");
		args = Arrays.copyOfRange(args, 1, args.length);
		if(args.length <= 1)
			throw new CommandAnswerException("Read help.");
		RowEntity        entity = null;
		ResolutionResult result = null;
		switch(type)
		{
			case PLAYER:
				final Player player = BukkitUtilities.findOnlinePlayer(args[0]);
				if(player != null)
				{
					args[0] = player.getName();
					result = rscp.permissionManager.getResult(player);
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
			throw new CommandAnswerException("{_LR}I don't know such name!");
		if(args[1] == null)
			args[1] = "";
		switch(args[1].toLowerCase())
		{
			case "":
			case "info":
			case "i":
				// INFO ?
				throw new CommandAnswerException("Subcommand isn't implemented.");
			case "prefix":
			case "p":
				if(result != null)
					viewCalculatedPrefix(result, args[1]);
				else
					viewEntityPrefix(entity);
				break;
			case "suffix":
			case "s":
				if(result != null)
					viewCalculatedSuffix(result, args[1]);
				else
					viewEntitySuffix(entity);
				break;
			case "listpermissions":
			case "permissions":
			case "lp":
				if(result != null)
					listFinalPlayerPermissions(result, args[0]);
				else
					showEntityPermissions(entity);
				break;
			case "listparents":
			case "listgroups":
			case "parents":
			case "groups":
			case "lg":
				if(result != null)
					listUserGroupsTree(result, args[1]);
				else
					showEntityParents(entity);
				break;
			case "addparent":
			case "addgroup":
			case "ap":
			case "ag":
				// TO DO HERE
				addGroup(result, args[1], null, null, null);
				break;
			case "removeparent":
			case "removegroup":
			case "rp":
			case "rg":
				// TO DO HERE
				removeGroup(result, args[1], null);
				break;
			default:
				break;
		}
	}
	public void listGroups(CommandSender sender) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>(16);
		final Set<String>       groups = rscp.internalCache.getKnownGroups();
		answer.add("There are following known groups in database:");
		for(String group : groups)
		{
			if("".equals(group))
				group = "{_LS}<implicit group>";
			answer.add("{_WH}" + group);
		}
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
	private void showEntityPermissions(RowEntity entity)  throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		final String typeName = entity.entityType.name().toLowerCase();
		answer.add("Explicit permissions for " + typeName + " {_YL}" + entity.entity + "{_LS}:");
		for(RowPermission permission : entity.permissions)
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("{_WH}").append(permission.splittedId);
			sb.append(permission.value ? " {_LG}" : " {_LR}").append(permission.permission);
			if(permission.destination != null)
			{
				final String destination = permission.destination.toString();
				if(!"".equals(destination))
					sb.append("{_R} {_LC}{_U}").append(destination);
			}
			if(permission.expirience > 0)
				sb.append("{_R} {_LB}").append(permission.expirience).append(" LVLs");
			if(permission.lifetime != null)
			{
				final String lifetime = permission.lifetime.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", " ");
				sb.append("{_R} {_YL}").append(lifetime);
			}
			answer.add(sb.toString());
		}
		throw new CommandAnswerException(answer);
	}
	private void showEntityParents(RowEntity entity) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Parent groups for " + entity.entityType.name().toLowerCase() + " {_YL}" + entity.entity + "{_LS}:");
		for(RowInheritance parent : entity.inheritance)
			answer.add("{_WH}" + parent.splittedId + " {_LG}" + parent.parent);
		throw new CommandAnswerException(answer);
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
	private void listUserGroupsTree(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Group list for user {_YL}" + user + "{_LS}:");
		for(String group : result.getOrderedGroups())
			answer.add("{_LG}" + group);
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
