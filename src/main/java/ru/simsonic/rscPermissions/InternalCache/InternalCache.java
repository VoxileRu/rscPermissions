package ru.simsonic.rscPermissions.InternalCache;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.PlayerType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;

public class InternalCache
{
	private final RowInheritance defaultInheritance = new RowInheritance();
	public void setDefaultGroup(String defaultGroup)
	{
		defaultInheritance.parent = defaultGroup;
		defaultInheritance.deriveInstance();
	}
	private final HashMap<String, RowEntity> entities_g = new HashMap<>();
	private final HashMap<String, RowEntity> entities_u = new HashMap<>();/*
	private final ArrayList<RowPermission>   permissions_p2g = new ArrayList<>();
	private final ArrayList<RowPermission>   permissions_p2u = new ArrayList<>();
	private final ArrayList<RowInheritance>  inheritance_g2g = new ArrayList<>();
	private final ArrayList<RowInheritance>  inheritance_g2u = new ArrayList<>();*/
	public synchronized void fill(DatabaseContents contents)
	{
		clear();
		// Import data
		importEntities(contents);
		importPermissions(contents.permissions);
		importInheritance(contents.inheritance);
		// Parse PlayerType's
		for(RowEntity row : entities_u.values())
			row.playerType = PlayerType.scanPlayerEntity(row.entity);
	}
	private void importEntities(DatabaseContents contents)
	{
		final HashSet<String> names_u = new HashSet<>();
		final HashSet<String> names_g = new HashSet<>();
		for(RowEntity row : contents.entities)
			if(row.entityType == EntityType.group)
			{
				entities_g.put(row.entity.toLowerCase(), row);
				names_g.add(row.entity.toLowerCase());
			} else {
				entities_u.put(row.entity, row);
				names_u.add(row.entity);
			}
		for(RowPermission row : contents.permissions)
			if(row.entityType == EntityType.group)
				names_g.add(row.entity.toLowerCase());
			else
				names_u.add(row.entity);
		for(RowInheritance row : contents.inheritance)
		{
			names_g.add(row.parent.toLowerCase());
			if(row.childType == EntityType.group)
				names_g.add(row.entity.toLowerCase());
			else
				names_u.add(row.entity);
		}
		for(String name : names_g)
			if(!entities_g.containsKey(name))
				entities_g.put(name, new RowEntity());
		for(String name : names_u)
			if(!entities_u.containsKey(name))
				entities_u.put(name, new RowEntity());
	}
	private void importPermissions(RowPermission[] rows)
	{
		final ArrayList<RowPermission> permissions_p2g = new ArrayList<>();
		final ArrayList<RowPermission> permissions_p2u = new ArrayList<>();
		for(RowPermission row : rows)
			if(row.entityType == EntityType.group)
				permissions_p2g.add(row);
			else
				permissions_p2u.add(row);
		for(String entry : entities_g.keySet())
		{
			final ArrayList<RowPermission> permissions = new ArrayList<>();
			for(RowPermission row : permissions_p2g)
				if(row.entity.toLowerCase().equals(entry))
					permissions.add(row);
			entities_g.get(entry).permissions = permissions.toArray(new RowPermission[permissions.size()]);
		}
		for(String entry : entities_u.keySet())
		{
			final ArrayList<RowPermission> permissions = new ArrayList<>();
			for(RowPermission row : permissions_p2u)
				if(row.entity.equals(entry))
					permissions.add(row);
			entities_u.get(entry).permissions = permissions.toArray(new RowPermission[permissions.size()]);
		}
	}
	private void importInheritance(RowInheritance[] rows)
	{
		final ArrayList<RowInheritance> inheritance_g2g = new ArrayList<>();
		final ArrayList<RowInheritance> inheritance_g2u = new ArrayList<>();
		for(RowInheritance row : rows)
			if(row.childType == EntityType.group)
				inheritance_g2g.add(row);
			else
				inheritance_g2u.add(row);
		for(Entry<String, RowEntity> entry : entities_g.entrySet())
		{
			final ArrayList<RowInheritance> inheritances = new ArrayList<>();
			final String name = entry.getKey();
			for(RowInheritance row : inheritance_g2g)
				if(row.entity.toLowerCase().equals(name))
					inheritances.add(row);
			entry.getValue().inheritance = inheritances.toArray(new RowInheritance[inheritances.size()]);
		}
		for(Entry<String, RowEntity> entry : entities_u.entrySet())
		{
			final ArrayList<RowInheritance> inheritance = new ArrayList<>();
			final String name = entry.getKey();
			for(RowInheritance row : inheritance_g2u)
				if(row.entity.equals(name))
					inheritance.add(row);
			entry.getValue().inheritance = inheritance.toArray(new RowInheritance[inheritance.size()]);
		}
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
		// params.expirience = 0;
		return resolvePlayer(params);
	}
	public synchronized ResolutionResult resolvePlayer(ResolutionParams params)
	{
		final ArrayList<ResolutionResult> intermediate = new ArrayList<>();
		if(entities_g.containsKey(""))
		{
			params.parentEntity = entities_g.get("");
			params.instantiator = "";
			intermediate.add(resolveParent(params));
		}
		for(RowEntity row : entities_u.values())
			for(String identifier : params.applicableIdentifiers)
				if(row.playerType.isEntityApplicable(row.entity, identifier))
				{
					params.parentEntity = row;
					params.instantiator = "";
					intermediate.add(resolveParent(params));
					break;
				}
		final ResolutionResult result = processResultColumn(params, intermediate);
		
		parents.addAll(Arrays.asList(implicitInheritance_u));
		parents.add(defaultInheritance);
		for(Entry<String, RowInheritance[]> entity : inheritanceTrees_u.entrySet())
		{
			for(RowInheritance row : entity.getValue())
				if(PlayerType.isEntityApplicable(entity, , entity))
				};
		final ResolutionResult result = new ResolutionResult();
		intermediate.addAll(Arrays.asList(implicitPermissions_u));
		final ArrayList<RowPermission> inheritance = new ArrayList<>();
		return result;
	}
	public synchronized ResolutionResult resolveParent(ResolutionParams params)
	{
		return null;
	}
	/*
	public synchronized ResolutionResult resolvePlayerOld(ResolutionParams params)
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
	// FROM HERE I SHOULD MAKE IT WORKING
	private void buildInheritanceForest()
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
			if(row.parentEntity.equalsIgnoreCase(source.entity))
				parents.add(row);
		Collections.sort(parents);
		final ArrayList<InheritanceLeaf> subleafs = new ArrayList<>();
		for(RowInheritance row : parents)
			subleafs.add(buildBranch(row));
		result.subleafs = subleafs.toArray(new InheritanceLeaf[subleafs.size()]);
		return result;
	}
	private ResolutionResult resolveBranch(ResolutionParams params, RowInheritance[] rows, String instantiator)
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
	*/
	private ResolutionResult processResultColumn(ResolutionParams params, ArrayList<ResolutionResult> intermediate)
	{
		switch(intermediate.size())
		{
			case 0:
				return new ResolutionResult();
			case 1:
				return intermediate.get(0);
			default:
				final ResolutionResult result = new ResolutionResult();
				final ArrayList<RowPermission> permissions = new ArrayList<>();
				result.prefix = "";
				result.suffix = "";
				for(ResolutionResult oneOf : intermediate)
				{
					// Prefixes & suffixes
					if(oneOf.prefix != null && !"".equals(oneOf.prefix))
						result.prefix = result.prefix.replace("%", oneOf.prefix);
					if(oneOf.suffix != null && !"".equals(oneOf.suffix))
						result.suffix = result.suffix.replace("%", oneOf.suffix);
					result.prefix = result.prefix.replace(Settings.instantiator, params.instantiator);
					result.suffix = result.suffix.replace(Settings.instantiator, params.instantiator);
					// Permissions
					for(RowPermission permission : oneOf.permissions)
						if(isPermissionApplicable(params, permission, params.instantiator))
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
	public synchronized void clear()
	{
		entities_g.clear();
		entities_u.clear();
		/*
		permissions_p2g.clear();
		permissions_p2u.clear();
		inheritance_g2g.clear();
		inheritance_g2u.clear();
		*/
	}
}
