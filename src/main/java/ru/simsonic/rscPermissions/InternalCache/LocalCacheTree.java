package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.MainPluginClass;
import ru.simsonic.rscPermissions.Settings;
import ru.simsonic.utilities.LanguageUtility;

public class LocalCacheTree extends LocalCacheData
{
	protected static class ResolutionLeaf
	{
		public String group;
		public String instance;
		public RowInheritance row;
	}
	protected LocalCacheTree(MainPluginClass rscp)
	{
		super(rscp);
	}
	public final ConcurrentHashMap<String, ArrayList<ResolutionLeaf>> mapTrees = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<String, HashMap<String, Boolean>> mapPermissions = new ConcurrentHashMap<>();
	protected final RowInheritance defaultInheritance = new RowInheritance();
	public void setDefaultGroup(String defaultGroup)
	{
		defaultInheritance.parent = defaultGroup;
		defaultInheritance.deriveInstance();
	}
	public synchronized void clear()
	{
		mapTrees.clear();
		mapPermissions.clear();
		prefixes_u.clear();
		suffixes_u.clear();
		entities_g.clear();
		entities_u.clear();
		permissions_p2g.clear();
		permissions_p2u.clear();
		inheritance_g2g.clear();
		inheritance_g2u.clear();
		ladders_g.clear();
		ladders_u.clear();
	}
	public synchronized void calculateStartupPermissions()
	{
		final HashSet<String> playerEntities = new HashSet<>();
		// Undefined player
		playerEntities.add(""); // Зачем я его тут добавил?!?
		// Defined players (in any table)
		playerEntities.addAll(entities_u.keySet());
		for(RowPermission row : permissions_p2u)
			playerEntities.add(row.entity);
		for(RowInheritance row : inheritance_g2u)
			playerEntities.add(row.entity);
		for(RowLadder row : ladders_u)
			playerEntities.add(row.climber);
		// Recalculate
		for(String entityNameOrUUID : playerEntities)
			calculateBasePermissions(entityNameOrUUID);
	}
	public synchronized void calculateBasePermissions(String playerName)
	{
		AsyncPlayerInfo p2rc = new AsyncPlayerInfo(playerName);
		HashMap<String, Boolean> list = treeToPermissions(p2rc);
		mapPermissions.put(playerName, list);
	}
	public synchronized void calculateBasePermissions(UUID playerUniqueId)
	{
		AsyncPlayerInfo p2rc = new AsyncPlayerInfo(playerUniqueId);
		HashMap<String, Boolean> list = treeToPermissions(p2rc);
		mapPermissions.put(playerUniqueId.toString().replace("-", "").toLowerCase(), list);
	}
	public void calculatePlayerPermissions(Player player)
	{
		final AsyncPlayerInfo api = new AsyncPlayerInfo(player, plugin.regionListProvider.GetRegionList(player));
		plugin.recalculatingPlayers.offer(api);
	}
	public synchronized HashMap<String, Boolean> treeToPermissions(AsyncPlayerInfo p2rc)
	{
		final HashMap<String, Boolean> permissions = new HashMap<>();
		String prefix = "";
		String suffix = "";
		ArrayList<ResolutionLeaf> tree = buildUserTree(p2rc);
		if(p2rc.name != null)
			mapTrees.put(p2rc.name.toLowerCase(), tree);
		// Group permissions
		for(ResolutionLeaf leaf : tree)
		{
			for(RowPermission row : permissions_p2g)
				if(p2rc.isGroupPermissionApplicable(row, leaf))
				{
					String permission = row.permission;
					// Additional processing
					if(permission.contains(Settings.instantiator) && (leaf.instance != null))
						permission = permission.replace(Settings.instantiator, leaf.instance);
					permissions.put(permission, row.value);
				}
			RowEntity entity = entities_g.get(leaf.group.toLowerCase());
			if(entity != null)
			{
				if(entity.prefix != null && !"".equals(entity.prefix))
					prefix = entity.prefix.replace("%", prefix);
				if(entity.suffix != null && !"".equals(entity.suffix))
					suffix = entity.suffix.replace("%", suffix);
				prefix = prefix.replace(Settings.instantiator, leaf.instance);
				suffix = suffix.replace(Settings.instantiator, leaf.instance);
			}
		}
		// User permissions
		for(RowPermission row : permissions_p2u)
			if(p2rc.isPlayerPermissionApplicable(row))
				permissions.put(row.permission, row.value);
		if(p2rc.name != null)
		{
			RowEntity entity = entities_u.get(p2rc.name.toLowerCase());
			if(entity != null)
			{
				if(entity.prefix != null && !"".equals(entity.prefix))
					prefix = entity.prefix.replace("%", prefix);
				if(entity.suffix != null && !"".equals(entity.suffix))
					suffix = entity.suffix.replace("%", suffix);
			}
			prefixes_u.put(p2rc.name, LanguageUtility.processStringStatic(prefix));
			suffixes_u.put(p2rc.name, LanguageUtility.processStringStatic(suffix));
		}
		return permissions;
	}
	private ArrayList<ResolutionLeaf> buildUserTree(AsyncPlayerInfo p2rc)
	{
		// User's direct inheritance
		ArrayList<RowInheritance> parentRows = new ArrayList<>();
		for(RowInheritance row : inheritance_g2u)
			if(p2rc.isPlayerInheritanceApplicable(row))
				parentRows.add(row);
		Collections.sort(parentRows);
		// Indirect default group
		if(parentRows.isEmpty() || plugin.settings.isDefaultForever())
			parentRows.add(0, defaultInheritance);
		ArrayList<ResolutionLeaf> resultTree = new ArrayList<>();
		// Parent deep inheritances
		for(RowInheritance row : parentRows)
		{
			ResolutionLeaf newleaf = new ResolutionLeaf();
			newleaf.group = row.parent;
			newleaf.instance = row.instance;
			newleaf.row = row;
			buildGroupTree(p2rc, newleaf, resultTree);
		}
		return resultTree;
	}
	private void buildGroupTree(AsyncPlayerInfo p2rc, ResolutionLeaf findAndOpen, ArrayList<ResolutionLeaf> result)
	{
		ArrayList<RowInheritance> parentRows = new ArrayList<>(inheritance_g2g.size() >> 2);
		for(RowInheritance row : inheritance_g2g)
			if(p2rc.isGroupInheritanceApplicable(row, findAndOpen))
				parentRows.add(row);
		Collections.sort(parentRows);
		for(RowInheritance row : parentRows)
		{
			ResolutionLeaf newleaf = new ResolutionLeaf();
			newleaf.group = row.parent;
			newleaf.instance = (row.instance != null) ? row.instance : findAndOpen.instance;
			newleaf.row = row;
			buildGroupTree(p2rc, newleaf, result);
		}
		result.add(findAndOpen);
	}
}