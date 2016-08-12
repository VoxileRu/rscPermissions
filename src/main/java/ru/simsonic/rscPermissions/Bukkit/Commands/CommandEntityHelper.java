package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class CommandEntityHelper
{
	protected final BukkitPluginMain rscp;
	CommandEntityHelper(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	protected void showEntityPrefix(RowEntity entity) throws CommandAnswerException
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
	protected void showEntitySuffix(RowEntity entity) throws CommandAnswerException
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
	protected void showEntityPermissions(RowEntity entity)  throws CommandAnswerException
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
	protected void showEntityParents(RowEntity entity) throws CommandAnswerException
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
	protected String showEntityDetails(RowEntity entity)
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
	protected String showPermissionDetails(RowPermission row)
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
	protected String showInheritanceDetails(RowInheritance row)
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
	protected void addGroup(RowEntity entity, String parent, ArgumentUtilities.OptionalParams optional) throws CommandAnswerException
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
	protected void addPermission(RowEntity entity, String permission, ArgumentUtilities.OptionalParams optional) throws CommandAnswerException
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
	protected void removeGroup(RowEntity entity, String whatToRemove) throws CommandAnswerException
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
	protected void removePermission(RowEntity entity, String whatToRemove) throws CommandAnswerException
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
