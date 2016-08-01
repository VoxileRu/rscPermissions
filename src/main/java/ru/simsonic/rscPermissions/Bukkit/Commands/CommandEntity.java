package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.Bukkit.BukkitUtilities;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseEditor;
import ru.simsonic.rscPermissions.Engine.Matchers;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class CommandEntity
{
	private final BukkitPluginMain rscp;
	CommandEntity(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void onEntityCommandHub(CommandSender sender, boolean type, String[] args) throws CommandAnswerException
	{
		if(sender.hasPermission("rscp.admin"))
			throw new CommandAnswerException("Not enough permissions.");
		args = Arrays.copyOfRange(args, 1, args.length);
		if(args.length == 0)
			throw new CommandAnswerException("Read help.");
		RowEntity entity = null;
		Player    player = null;
		if(type)
		{
			player = BukkitUtilities.findOnlinePlayer(args[0]);
			if(player != null)
				args[1] = player.getName();
		} else
			entity = rscp.internalCache.findGroupEntity(args[0]);
		final ResolutionResult result = (player != null)
			? rscp.permissionManager.getResult(player)
			: rscp.permissionManager.getResult(args[0]);
		if(player == null && entity == null)
			throw new CommandAnswerException("I don't know him.");
		switch(args[1].toLowerCase())
		{
			case "prefix":
			case "p":
				if(player != null)
					viewCalculatedPrefix(result, args[1]);
				else
					viewEntityPrefix(entity);
				break;
			case "suffix":
			case "s":
				if(player != null)
					viewCalculatedSuffix(result, args[1]);
				else
					viewEntitySuffix(entity);
				break;
			case "listpermissions":
			case "lp":
				listFinalPlayerPermissions(result, args[1]);
				break;
			case "listgroups":
			case "lg":
				listUserGroupsTree(result, args[1]);
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
