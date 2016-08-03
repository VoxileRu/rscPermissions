package ru.simsonic.rscPermissions.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.API.Settings;

public class InternalCache extends InternalStorage
{
	private boolean alwaysInheritDefaultGroup   = false;
	private boolean groupsInheritParentPrefixes = true;
	public void setDefaultGroup(String defaultGroup, boolean alwaysInheritDefaultGroup, boolean groupsInheritParentPrefixes)
	{
		super.defaultInheritance.parent = defaultGroup;
		super.defaultInheritance.deriveInstance();
		this.alwaysInheritDefaultGroup   = alwaysInheritDefaultGroup;
		this.groupsInheritParentPrefixes = groupsInheritParentPrefixes;
	}
	public synchronized Set<String> getKnownGroups()
	{
		final Set<String> result = new TreeSet<>();
		for(RowEntity row : entities_g.values())
			result.add(row.entity);
		return result;
	}
	public synchronized Set<String> getKnownUsers()
	{
		final Set<String> result = new TreeSet<>();
		for(RowEntity row : entities_u.values())
			result.add(row.entity);
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
		return resolvePlayer(params);
	}
	public synchronized ResolutionResult resolvePlayer(ResolutionParams params)
	{
		params.groupList    = new LinkedList<>();
		params.finalPerms   = new TreeMap<>();
		params.instantiator = "";
		params.depth        = 0;
		if(implicit_u != null && implicit_u.permissions != null)
		{
			processPermissions(params, Arrays.asList(implicit_u.permissions));
		}
		final ArrayList<RowEntity>      applicableEntities    = new ArrayList<>();
		final ArrayList<RowPermission>  applicablePermissions = new ArrayList<>();
		final ArrayList<RowInheritance> applicableInheritance = new ArrayList<>();
		for(RowEntity row : entities_u.values())
			for(String identifier : params.applicableIdentifiers)
				if(row.playerType.isEntityApplicable(row.entity, identifier))
				{
					// Apply direct inheritance
					if(row.inheritance != null && row.inheritance.length > 0)
						for(RowInheritance inheritance : row.inheritance)
							if(isInheritanceApplicable(params, inheritance))
								applicableInheritance.add(inheritance);
					// Apply direct permissions
					applicablePermissions.addAll(Arrays.asList(row.permissions));
					// Apply direct prefixes and suffixes
					applicableEntities.add(row);
				}
		Collections.sort(applicableEntities);
		final ArrayList<ResolutionResult> intermediateResults = new ArrayList<>();
		Collections.sort(applicableInheritance);
		// Mix into default inheritance
		if(applicableInheritance.isEmpty() || alwaysInheritDefaultGroup)
			applicableInheritance.add(0, defaultInheritance);
		for(RowInheritance row : applicableInheritance)
		{
			params.instantiator = "";
			params.parentEntity = row.entityParent;
			intermediateResults.add(resolveParent(params));
		}
		// Process all applicable prefixes using only entity id order
		ResolutionResult result = processPrefixesAndSuffixes(params, intermediateResults);
		for(RowEntity row : applicableEntities)
		{
			params.instantiator = "";
			params.parentEntity = row;
			result = processPrefixesAndSuffixes(params, Arrays.asList(new ResolutionResult[] { result }));
		}
		if(implicit_u != null)
			applicableEntities.add(0, implicit_u);
		result.entities    = applicableEntities;
		result.prefix      = GenericChatCodes.processStringStatic(result.prefix);
		result.suffix      = GenericChatCodes.processStringStatic(result.suffix);
		processPermissions(params, applicablePermissions);
		result.permissions = params.finalPerms;
		result.groups      = params.groupList;
		result.params      = params;
		return result;
	}
	private ResolutionResult resolveParent(ResolutionParams params)
	{
		if(implicit_g != null && implicit_g.permissions != null)
			processPermissions(params, Arrays.asList(implicit_g.permissions));
		final RowEntity currentParent = params.parentEntity;
		final String instantiator = params.instantiator;
		final ArrayList<ResolutionResult> intermediateResults = new ArrayList<>();
		params.depth += 1;
		for(RowInheritance row : params.parentEntity.inheritance)
			if(isInheritanceApplicable(params, row))
			{
				params.parentEntity = row.entityParent;
				params.instantiator = row.instance.isEmpty()
					? instantiator
					: row.instance;
				intermediateResults.add(resolveParent(params));
			}
		params.depth -= 1;
		params.groupList.add(depthPrefix(params.depth) + currentParent.entity
			+ ("".equals(instantiator) ? "" : Settings.INSTANCE_SEP + instantiator));
		// Prefixes and suffixes
		params.parentEntity = currentParent;
		params.instantiator = instantiator;
		final ResolutionResult result = processPrefixesAndSuffixes(params, intermediateResults);
		intermediateResults.clear();
		// Permissions
 		if(currentParent.permissions != null)
  			processPermissions(params, Arrays.asList(currentParent.permissions));
		return result;
	}
	private String depthPrefix(int depth)
	{
		if(depth > 0)
		{
			final char[] levelParent = new char[depth];
			levelParent[depth - 1] = Settings.SHOW_GROUP_LEVEL;
			return new String(levelParent).replace('\0', ' ');
		}
		return "";
	}
	private ResolutionResult processPrefixesAndSuffixes(ResolutionParams params, List<ResolutionResult> intermediate)
	{
		final ResolutionResult result = new ResolutionResult();
		final boolean gipp = groupsInheritParentPrefixes || params.parentEntity.entityType.equals(EntityType.PLAYER);
		result.prefix = params.parentEntity.prefix;
		result.suffix = params.parentEntity.suffix;
		if(result.prefix == null || "".equals(result.prefix))
			result.prefix = (gipp ? Settings.TEXT_INHERITER : "");
		if(result.suffix == null || "".equals(result.suffix))
			result.suffix = (gipp ? Settings.TEXT_INHERITER : "");
		final StringBuilder sbp = new StringBuilder();
		final StringBuilder sbs = new StringBuilder();
		for(ResolutionResult inherited : intermediate)
		{
			if(inherited.prefix != null)
				sbp.append(inherited.prefix);
			if(inherited.suffix != null)
				sbs.append(inherited.suffix);
		}
		result.prefix = result.prefix.replace(Settings.TEXT_INHERITER, sbp.toString());
		result.suffix = result.suffix.replace(Settings.TEXT_INHERITER, sbs.toString());
		result.prefix = result.prefix.replace(Settings.INSTANTIATOR, params.instantiator);
		result.suffix = result.suffix.replace(Settings.INSTANTIATOR, params.instantiator);
		return result;
	}
	private void processPermissions(ResolutionParams params, List<RowPermission> permissions)
	{
		for(RowPermission row : permissions)
			if(isPermissionApplicable(params, row))
				params.finalPerms.put(
					row.permission.replace(Settings.INSTANTIATOR, params.instantiator),
					row.value);
	}
	private boolean isPermissionApplicable(ResolutionParams params, RowPermission row)
	{
		if(params.expirience < row.expirience)
			return false;
		return row.destination.isWorldApplicable(params.destWorld, params.instantiator)
			? row.destination.isRegionApplicable(params.destRegions, params.instantiator)
			: false;
	}
	private boolean isInheritanceApplicable(ResolutionParams params, RowInheritance row)
	{
		if(params.expirience < row.expirience)
			return false;
		return row.destination.isWorldApplicable(params.destWorld, params.instantiator)
			? row.destination.isRegionApplicable(params.destRegions, params.instantiator)
			: false;
	}
}
