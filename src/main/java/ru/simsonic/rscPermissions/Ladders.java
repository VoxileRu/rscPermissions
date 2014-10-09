package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.Backends.BackendMySQL;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;

public class Ladders
{
	private final MainPluginClass plugin;
	public Ladders(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	public String[] executePromotion(CommandSender sender, String user, String ladder, boolean bPromote)
	{
		if(plugin.connectionList == null)
			return null;
		final BackendMySQL connection = plugin.connectionList.findConnectedNode();
		if(connection == null || !connection.canWrite())
			return null;
		if("".equals(user) || (user == null))
			return new String[] { (bPromote ? "/promote" : "/demote") + " <user> <ladder>" };
		if("".equals(ladder) || (ladder == null))
			return new String[] { "You should specify ladder to promote on." };
		final Player player = plugin.getServer().getPlayerExact(user);
		if(player == null)
			return new String[] { "Player must be online." };
		String template = ladder;
		String instance = "";
		String[] breaked = ladder.split(Settings.separatorRegExp);
		if(breaked.length == 2)
		{
			template = breaked[0].toLowerCase();
			instance = breaked[1];
		}
		final String perm_onself = bPromote ? "rscp.promote-self." : "rscp.demote-self.";
		final String perm_sender = bPromote ? "rscp.promote." : "rscp.demote.";
		final String perm_target = bPromote ? "rscp.promotable." : "rscp.demotable.";
		boolean bOS = sender.hasPermission(perm_onself + "*");
		boolean bSP = sender.hasPermission(perm_sender + "*");
		boolean bUP = player.hasPermission(perm_target + "*");
		if(!"".equals(instance))
		{
			bOS = bOS || sender.hasPermission(perm_onself + template + ".*");
			bOS = bOS || sender.hasPermission(perm_onself + template + "." + instance.toLowerCase());
			bSP = bSP || sender.hasPermission(perm_sender + template + ".*");
			bSP = bSP || sender.hasPermission(perm_sender + template + "." + instance.toLowerCase());
			bUP = bUP || player.hasPermission(perm_target + template + ".*");
			bUP = bUP || player.hasPermission(perm_target + template + "." + instance.toLowerCase());
		} else {
			bOS = bOS || sender.hasPermission(perm_onself + template);
			bSP = bSP || sender.hasPermission(perm_sender + template);
			bUP = bUP || player.hasPermission(perm_target + template);
		}
		if(sender instanceof Player)
			if(player != (Player)sender)
				bOS = false;
		boolean bPromotionAllowed = bOS || (bSP && bUP) || sender.hasPermission("rscp.admin.promote");
		if(bPromotionAllowed == false)
		{
			if(bSP == false)
				return new String[] { "You are not allowed to promote/demote on this ladder." };
			if(bUP == false)
				return new String[] { "Player is not promotable on this ladder." };
		}
		/*
		int rank = plugin.cache2.getUserRank(user, template, instance);
		final ArrayList<RowLadder> ladderArray = plugin.cache.buildLadderTemplate(template);
		if(ladderArray.isEmpty())
			return new String[] { "There is no such ladder." };
		RowLadder position = ladderArray.get(0).getActualNode(rank);
		if(bPromote)
		{
			if(position.nextNode != null)
				position = position.nextNode;
		} else
			if(position.prevNode != null)
				position = position.prevNode;
		if(position.instance != null)
			if(Settings.instantiator.equals(position.instance))
			{
				if("".equals(instance))
					return new String[] { "Operation requires ladder instance (<template>.<instance>)." };
			} else
				instance = ("".equals(position.instance) ? null : position.instance);
		else
			instance = null;
		ladder = position.ladder + ((instance != null) ? "." + instance : "");
		connection.dropUserFromLadder(user, ladder);
		connection.setUserRank(user, ladder, position.rank);
		final String parent = position.climber + ((instance != null) ? "." + instance : "");
		if(position.climber != null)
			connection.addUserParentGroup(user, parent);
		connection.fetchIntoCache(plugin.cache);
		plugin.cache.calculatePlayerPermissions(player);
		final String resultP = "{GOLD}You have been " + 
			((position.climber != null) ?
			(
				(bPromote ? "promoted" : "demoted") + " to the group {_LS}" +
				position.climber + ((instance != null) ? " {_DS}(" + instance + ") " : "") +
				"{GOLD}on"
			) : "removed from"
			) + " the ladder {_LG}" + position.ladder + ((instance != null) ? "{_DS}(" + instance + ")" : "") + "{GOLD} by " +
			(
				(sender instanceof Player) ?
					((player == (Player)sender) ? "yourself" : "{_YL}" + sender.getName())
				: "server"
			) + "{GOLD}.";
		final String resultS = "{GOLD}User {_YL}" + player.getName() + " {GOLD}has been " +
			((position.climber != null) ?
			(
				(bPromote ? "promoted" : "demoted") + " to the group {_LS}" +
				position.climber + ((instance != null) ? "{_DS}(" + instance + ") " : "") +
				"{GOLD}on"
			) : "removed from"
			) + " the ladder {_LG}" + position.ladder + ((instance != null) ? "{_DS}(" + instance + ")" : "") + "{GOLD}.";
		plugin.formattedMessage(player, resultP);
		return new String [] { resultS };
		*/
		return new String[] { "Function is temporary unavailable." };
	}
}
