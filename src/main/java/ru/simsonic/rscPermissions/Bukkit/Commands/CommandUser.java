package ru.simsonic.rscPermissions.Bukkit.Commands;

import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscPermissions.Bukkit.BukkitUtilities;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseEditor;
import ru.simsonic.rscPermissions.Engine.Matchers;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class CommandUser
{
	private final BukkitPluginMain rscp;
	CommandUser(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void onUserCommandHub(CommandSender sender, String[] args) throws CommandAnswerException
	{
		/*
			/rscp    - command
			user     - 0
		   <entity> - 1 // -u; --by-uuid / -n (--by-name)
		*/
		if(sender.hasPermission("rscp.admin"))
		{
			if(args.length < 3)
				return;
			final Player player = BukkitUtilities.findOnlinePlayer(args[1]);
			if(player != null)
				args[1] = player.getName();
			final ResolutionResult result = (player != null)
				? rscp.permissionManager.getResult(player)
				: rscp.permissionManager.getResult(args[1]);
			switch(args[2].toLowerCase())
			{
				case "prefix":
				case "p":
					viewPrefix(result, args[1]);
					break;
				case "suffix":
				case "s":
					viewSuffix(result, args[1]);
					break;
				case "listpermissions":
				case "lp":
					listPermissions(result, args[1]);
					break;
				case "listgroups":
				case "lg":
					listGroups(result, args[1]);
					break;
				case "addgroup":
				case "ag":
					// TO DO HERE
					addGroup(result, args[1], null, null, null);
					break;
				case "removegroup":
				case "rg":
					// TO DO HERE
					removeGroup(result, args[1], null);
					break;
				default:
					break;
			}
		} else
			throw new CommandAnswerException("Not enough permissions.");
	}
	private void viewPrefix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated prefix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.prefix + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void viewSuffix(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Calculated suffix for user {_YL}" + user + "{_LS} is:");
		answer.add("{_R}\"" + result.suffix + "{_R}\"");
		throw new CommandAnswerException(answer);
	}
	private void listPermissions(ResolutionResult result, String user) throws CommandAnswerException
	{
		if(Matchers.isCorrectDashlessUUID(user))
			user = Matchers.uuidAddDashes(user);
		final ArrayList<String> answer = new ArrayList<>();
		answer.add("Permission list for user {_YL}" + user + "{_LS}:");
		final ArrayList<String> sorted_keys = new ArrayList<>(result.permissions.keySet());
		Collections.sort(sorted_keys);
		for(String perm : sorted_keys)
			answer.add((result.permissions.get(perm) ? "{_LG}" : "{_LR}") + perm);
		throw new CommandAnswerException(answer);
	}
	private void listGroups(ResolutionResult result, String user) throws CommandAnswerException
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
		final DatabaseEditor databaseTransaction = new DatabaseEditor(rscp);
		databaseTransaction.apply();
		throw new CommandAnswerException(answer);
	}
	private void removeGroup(ResolutionResult result, String user, String parent) throws CommandAnswerException
	{
		final ArrayList<String> answer = new ArrayList<>();
		throw new CommandAnswerException(answer);
	}
}
