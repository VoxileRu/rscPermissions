package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import ru.simsonic.rscPermissions.DataTypes.EntityType;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.DataTypes.DatabaseContents;

public class InternalCache
{
	protected final BukkitPluginMain plugin;
	public InternalCache(BukkitPluginMain rscp)
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
	public static class InheritanceLeaf implements Comparable<InheritanceLeaf>
	{
		public RowInheritance node;
		public String instantiator;
		public InheritanceLeaf[] subleafs;
		public String prefix;
		public String suffix;
		@Override
		public int compareTo(InheritanceLeaf other)
		{
			return (other.node != null && node != null) ? other.node.compareTo(node) : 0;
		}
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
			result.prefix = entities_g.get(entityName).prefix;
			result.suffix = entities_g.get(entityName).suffix;
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
	public synchronized ResolutionResult resolvePlayer(String player)
	{
		return resolvePlayer(new String[] { player });
	}
	public synchronized ResolutionResult resolvePlayer(String[] player)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = player;
		params.destRegions = new String[] {};
		// params.destWorld = "";
		params.destServerId = plugin.getServer().getServerId();
		// params.expirience = 0;
		return resolvePlayer(params);
	}
	public synchronized ResolutionResult resolvePlayer(ResolutionParams params)
	{
		final ArrayList<InheritanceLeaf> applicableBranches = new ArrayList<>();
		// Grab all inheritance rows applicable to this player
		for(String identifier : params.applicableIdentifiers)
			for(String tree : entityTrees.keySet())
				if(tree.equals(identifier))
					applicableBranches.add(entityTrees.get(tree));
		Collections.sort(applicableBranches);
		// Begin resolution
		final ArrayList<ResolutionResult> intermediateResults = new ArrayList<>();
		for(InheritanceLeaf branch : applicableBranches)
			if(isInheritanceApplicable(params, branch.node, ""))
				intermediateResults.add(resolveBranch(params, branch, ""));
		final ResolutionResult result = processResultColumn(params, intermediateResults, "");
		intermediateResults.clear();
		return result;
	}
	private ResolutionResult resolveBranch(ResolutionParams params, InheritanceLeaf branch, String instantiator)
	{
		final ArrayList<ResolutionResult> intermediateResults = new ArrayList<>();
		for(InheritanceLeaf subleaf : branch.subleafs)
		{
			final String overloadedInstantiator = (subleaf.instantiator != null && !"".equals(subleaf.instantiator))
				? subleaf.instantiator : instantiator;
			if(isInheritanceApplicable(params, subleaf.node, overloadedInstantiator))
				intermediateResults.add(resolveBranch(params, subleaf, overloadedInstantiator));
		}
		final ResolutionResult result = processResultColumn(params, intermediateResults, branch.instantiator);
		intermediateResults.clear();
		return result;
	}
	private ResolutionResult processResultColumn(ResolutionParams params, ArrayList<ResolutionResult> resultList, String instantiator)
	{
		switch(resultList.size())
		{
			case 0:
				return new ResolutionResult();
			case 1:
				return resultList.get(0);
			default:
				final ResolutionResult result = new ResolutionResult();
				final ArrayList<RowPermission> permissions = new ArrayList<>();
				result.prefix = "";
				result.suffix = "";
				for(ResolutionResult intermediate : resultList)
				{
					// Prefixes & suffixes
					if(intermediate.prefix != null && !"".equals(intermediate.prefix))
						result.prefix = result.prefix.replace("%", result.prefix);
					if(intermediate.suffix != null && !"".equals(intermediate.suffix))
						result.suffix = result.suffix.replace("%", result.suffix);
					result.prefix = result.prefix.replace(Settings.instantiator, instantiator);
					result.suffix = result.suffix.replace(Settings.instantiator, instantiator);
					// Permissions
					for(RowPermission permission : intermediate.permissions)
						if(isPermissionApplicable(params, permission, instantiator))
							permissions.add(permission);
				}
				result.permissions = permissions.toArray(new RowPermission[permissions.size()]);
				return result;
		}
	}
	private boolean isPermissionApplicable(ResolutionParams params, RowPermission row, String instantiator)
	{
		if(params.expirience < row.expirience)
			return false;
		return row.destination.isWorldApplicable(params.destWorld, instantiator)
			? row.destination.isRegionApplicable(params.destRegions, instantiator)
			: false;
	}
	private boolean isInheritanceApplicable(ResolutionParams params, RowInheritance row, String instantiator)
	{
		if(params.expirience < row.expirience)
			return false;
		return row.destination.isWorldApplicable(params.destWorld, instantiator)
			? row.destination.isRegionApplicable(params.destRegions, instantiator)
			: false;
	}
	public synchronized void fill(DatabaseContents contents)
	{
		importEntities(contents.entities);
		importPermissions(contents.permissions);
		importInheritance(contents.inheritance);
	}
	private int importEntities(RowEntity[] rows)
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
	private int importPermissions(RowPermission[] rows)
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
	private int importInheritance(RowInheritance[] rows)
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
	public synchronized void clear()
	{
		entities_g.clear();
		entities_u.clear();
		permissions_p2g.clear();
		permissions_p2u.clear();
		inheritance_g2g.clear();
		inheritance_g2u.clear();
	}
}