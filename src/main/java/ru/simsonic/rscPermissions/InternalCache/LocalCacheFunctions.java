package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.MainPluginClass;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;

public class LocalCacheFunctions extends LocalCacheTree
{
	public LocalCacheFunctions(MainPluginClass rscp)
	{
		super(rscp);
	}
	public String userGetPrefix(String user)
	{
		return prefixes_u.get(user);
	}
	public String userGetSuffix(String user)
	{
		return suffixes_u.get(user);
	}
	public synchronized String groupGetPrefix(String group)
	{
		if(group == null || "".equals(group))
			return null;
		RowEntity entity = entities_g.get(group.toLowerCase());
		return (entity != null) ? entity.prefix : null;
	}
	public synchronized String groupGetSuffix(String group)
	{
		if(group == null || "".equals(group))
			return null;
		RowEntity entity = entities_g.get(group.toLowerCase());
		return (entity != null) ? entity.suffix : null;
	}
	public synchronized ArrayList<RowLadder> buildLadderTemplate(String ladder)
	{
		final ArrayList<RowLadder> result = new ArrayList<>();
		RowLadder prev = null;
		for(RowLadder row : ladders_g)
			if(row.ladder.equalsIgnoreCase(ladder))
			{
				if(prev != null)
				{
					prev.nextNode = row;
					row.prevNode = prev;
				}
				result.add(row);
				prev = row;
			}
		Collections.sort(result);
		return result;
	}
	public synchronized int getUserRank(String user, String ladder, String instance)
	{
		for(RowLadder row : ladders_u)
			if(row.climber.equalsIgnoreCase(user) && row.ladder.equalsIgnoreCase(ladder))
				if(instance == null || "".equals(instance))
				{
					if(row.instance == null || "".equals(row.instance))
						return row.rank;
				} else
					if(instance.equalsIgnoreCase(row.instance))
						return row.rank;
		return 0;
	}
	public synchronized ArrayList<String> getUserGroups(String player)
	{
		final ArrayList<ResolutionLeaf> tree = mapTrees.get(player.toLowerCase());
		if(tree == null)
			return null;
		final ArrayList<String> result = new ArrayList<>();
		for(ResolutionLeaf leaf : tree)
			result.add(leaf.instance != null ? leaf.group + BukkitPluginConfiguration.separator + leaf.instance : leaf.group);
		return result;
	}
	public synchronized Set<String> getAllPossibleGroups()
	{
		Set<String> result = new HashSet<>();
		for(RowEntity row : entities_g.values())
			result.add(row.entity.toLowerCase());
		for(RowPermission row : permissions_p2g)
			result.add(row.entity.toLowerCase());
		for(RowInheritance row : inheritance_g2g)
		{
			result.add(row.entity.toLowerCase());
			result.add(row.parent.toLowerCase());
		}
		for(RowInheritance row : inheritance_g2u)
			result.add(row.parent.toLowerCase());
		for(RowLadder row : ladders_g)
			if(row.climber != null)
				result.add(row.climber.toLowerCase());
		return result;
	}
}