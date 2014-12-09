package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.API.Settings;

public class LocalCacheFunctions extends LocalCacheTree
{
	public LocalCacheFunctions(BukkitPluginMain rscp)
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
	public synchronized ArrayList<String> getUserGroups(String player)
	{
		final ArrayList<ResolutionLeaf> tree = mapTrees.get(player.toLowerCase());
		if(tree == null)
			return null;
		final ArrayList<String> result = new ArrayList<>();
		for(ResolutionLeaf leaf : tree)
			result.add(leaf.instance != null ? leaf.group + Settings.separator + leaf.instance : leaf.group);
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
		return result;
	}
}
