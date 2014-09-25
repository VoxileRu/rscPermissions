package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.DataTypes.EntityType;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.MainPluginClass;

public class BrandNewCache implements AbstractPermissionsCache
{
	protected final MainPluginClass plugin;
	protected BrandNewCache(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	protected final RowInheritance defaultInheritance = new RowInheritance();
	public void setDefaultGroup(String defaultGroup)
	{
		defaultInheritance.parent = defaultGroup;
		defaultInheritance.deriveInstance();
	}
	protected final HashMap<String, RowEntity> entities_g = new HashMap<>();
	protected final HashMap<String, RowEntity> entities_u = new HashMap<>();
	protected final ArrayList<RowPermission> permissions_p2g = new ArrayList<>();
	protected final ArrayList<RowPermission> permissions_p2u = new ArrayList<>();
	protected final ArrayList<RowInheritance> inheritance_g2g = new ArrayList<>();
	protected final ArrayList<RowInheritance> inheritance_g2u = new ArrayList<>();
	protected final ArrayList<RowLadder> ladders_g = new ArrayList<>();
	protected final ArrayList<RowLadder> ladders_u = new ArrayList<>();
	public static class InheritanceLeaf implements Comparable<InheritanceLeaf>
	{
		public RowInheritance node;
		public String instantiator;
		public InheritanceLeaf[] subleafs;
		public String resolvedPrefix;
		public String resolvedSuffix;
		@Override
		public int compareTo(InheritanceLeaf other)
		{
			return (other.node != null && node != null) ? other.node.compareTo(node) : 0;
		}
	}
	public static class ResolutionParams
	{
		public String[] applicableIdentifiers;
		public String[] destRegions;
		public String   destWorld;
		public String   destServerId;
	}
	public static class ResolutionResult
	{
		public String prefix;
		public String suffix;
		public RowPermission[] permissions;
	}
	final HashMap<String, InheritanceLeaf> entityTrees = new HashMap<>();
	// Права по сущностям
	final HashMap<String, RowPermission[]> groupPermissions = new HashMap<>();
	final HashMap<String, RowPermission[]> playerPermissions = new HashMap<>();
	private void buildEntityTree()
	{
		final HashSet<String> entitiesWhichInherits = new HashSet<>();
		for(RowInheritance row : inheritance_g2u)
			entitiesWhichInherits.add(row.entity);
		for(String inheritingEntity : entitiesWhichInherits)
		{
			final ArrayList<RowInheritance> entityDirectParents = new ArrayList<>();
			for(RowInheritance row : inheritance_g2u)
				if(row.entity.equalsIgnoreCase(inheritingEntity))
					entityDirectParents.add(row);
			Collections.sort(entityDirectParents);
			for(RowInheritance row : entityDirectParents)
				this.entityTrees.put(inheritingEntity, buildBranch(row));
		}
	}
	private InheritanceLeaf buildBranch(RowInheritance source)
	{
		final InheritanceLeaf result = new InheritanceLeaf();
		result.node = source;
		result.instantiator = source.instance;
		final String entityName = source.entity.toLowerCase();
		if(entities_g.containsKey(entityName))
		{
			result.resolvedPrefix = entities_g.get(entityName).prefix;
			result.resolvedSuffix = entities_g.get(entityName).suffix;
		}
		final ArrayList<RowInheritance> parents = new ArrayList<>();
		for(RowInheritance row : inheritance_g2g)
			if(row.parent.equalsIgnoreCase(source.entity))
				parents.add(row);
		Collections.sort(parents);
		final ArrayList<InheritanceLeaf> subleafs = new ArrayList<>();
		for(RowInheritance row : parents)
			subleafs.add(buildBranch(row));
		result.subleafs = subleafs.toArray(new InheritanceLeaf[subleafs.size()]);
		return result;
	}
	private static String[] getPlayerIdentifiers(Player player)
	{
		final ArrayList<String> result = new ArrayList<>();
		// For old servers Player's name can be used as entity name
		try
		{
			// minecraft <= 1.7.x
			result.add(player.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
			// minecraft >= 1.8
		}
		// For newest servers Player's UUID is used as entity name
		try
		{
			// minecraft >= 1.8
			result.add(player.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
			// minecraft <= 1.7.x
		}
		// IP address of a Player can be used as entity name too
		result.add(player.getAddress().getAddress().getHostAddress());
		return result.toArray(new String[result.size()]);
	}
	public void resolvePlayer(Player player)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = getPlayerIdentifiers(player);
		if(plugin.regionListProvider != null)
		{
			Set<String> regionSet = plugin.regionListProvider.GetRegionList(player);
			params.destRegions = regionSet.toArray(new String[regionSet.size()]);
		} else
			params.destRegions = new String[] {};
		params.destWorld = player.getLocation().getWorld().getName();
		params.destServerId = plugin.getServer().getServerId();
		resolvePlayer(params);
	}
	public void resolvePlayer(String player)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = new String[] { player };
		params.destRegions = new String[] {};
		params.destWorld = "";
		params.destServerId = plugin.getServer().getServerId();
		resolvePlayer(params);
	}
	private void resolvePlayer(ResolutionParams params)
	{
		final ArrayList<InheritanceLeaf> applicableBranches = new ArrayList<>();
		for(String identifier : params.applicableIdentifiers)
			for(String tree : entityTrees.keySet())
				if(tree.equals(identifier))
					applicableBranches.add(entityTrees.get(tree));
		Collections.sort(applicableBranches);
		final InheritanceLeaf root = new InheritanceLeaf();
	}
	public void recoursion(ResolutionParams params, InheritanceLeaf branch)
	{
		for(InheritanceLeaf leaf : branch.subleafs)
		{
		}
	}
	@Override
	public synchronized int ImportEntities(RowEntity[] rows)
	{
		entities_g.clear();
		entities_u.clear();
		if(rows == null)
			return 0;
		for(RowEntity row : rows)
		{
			if(row.entityType == EntityType.group)
				entities_g.put(row.entity.toLowerCase(), row);
			else
				entities_u.put(row.entity.toLowerCase(), row);
		}
		return entities_g.size() + entities_u.size();
	}
	@Override
	public synchronized int ImportPermissions(RowPermission[] rows)
	{
		permissions_p2g.clear();
		permissions_p2u.clear();
		if(rows == null)
			return 0;
		for(RowPermission row : rows)
		{
			if(row.entityType == EntityType.group)
				permissions_p2g.add(row);
			else
				permissions_p2u.add(row);
		}
		return permissions_p2g.size() + permissions_p2u.size();
	}
	@Override
	public synchronized int ImportInheritance(RowInheritance[] rows)
	{
		inheritance_g2g.clear();
		inheritance_g2u.clear();
		if(rows == null)
			return 0;
		for(RowInheritance row : rows)
		{
			if(row.childType == EntityType.group)
				inheritance_g2g.add(row);
			else
				inheritance_g2u.add(row);
		}
		return inheritance_g2g.size() + inheritance_g2u.size();
	}
	@Override
	public synchronized int ImportLadders(RowLadder[] rows)
	{
		ladders_g.clear();
		ladders_u.clear();
		if(rows == null)
			return 0;
		for(RowLadder row : rows)
		{
			if(row.climberType == EntityType.group)
				ladders_g.add(row);
			else
				ladders_u.add(row);	
		}
		return ladders_g.size() + ladders_u.size();
	}
}