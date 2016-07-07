package ru.simsonic.rscPermissions.Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.PlayerType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;

class InternalStorage
{
	protected final HashMap<String, RowEntity> entities_g = new HashMap<>();
	protected final HashMap<String, RowEntity> entities_u = new HashMap<>();
	protected final RowInheritance defaultInheritance     = new RowInheritance();
	protected RowEntity implicit_g;
	protected RowEntity implicit_u;
	private   boolean   freshRemoteData;
	public synchronized void fill(DatabaseContents contents)
	{
		clear();
		this.freshRemoteData = contents.cached;
		importEntities   (contents);
		importPermissions(contents.permissions);
		importInheritance(contents.inheritance);
		implicit_g = entities_g.get("");
		implicit_u = entities_u.get("");
	}
	public boolean isFreshData()
	{
		return freshRemoteData;
	}
	private void importEntities(DatabaseContents contents)
	{
		final HashSet<String> names_u = new HashSet<>();
		final HashSet<String> names_g = new HashSet<>();
		for(RowEntity row : contents.entities)
			if(row.entityType == EntityType.GROUP)
			{
				names_g.add(row.entity);
				entities_g.put(row.entity.toLowerCase(), row);
			} else {
				names_u.add(row.entity);
				entities_u.put(row.entity, row);
			}
		for(RowPermission row : contents.permissions)
			if(row.entityType == EntityType.GROUP)
				names_g.add(row.entity);
			else
				names_u.add(row.entity);
		for(RowInheritance row : contents.inheritance)
		{
			names_g.add(row.parent);
			if(row.childType == EntityType.GROUP)
				names_g.add(row.entity);
			else
				names_u.add(row.entity);
		}
		names_g.add(defaultInheritance.parent);
		for(String name : names_g)
		{
			final String groupInternalName = name.toLowerCase();
			if(!entities_g.containsKey(groupInternalName))
			{
				final RowEntity dummy = new RowEntity();
				dummy.entity     = name;
				dummy.entityType = EntityType.GROUP;
				entities_g.put(groupInternalName, dummy);
			}
		}
		for(String name : names_u)
			if(!entities_u.containsKey(name))
			{
				final RowEntity dummy = new RowEntity();
				dummy.entity     = name;
				dummy.entityType = EntityType.PLAYER;
				entities_u.put(name, dummy);
			}
		for(RowEntity row : entities_u.values())
			row.playerType = PlayerType.scanPlayerEntity(row.entity);
	}
	private void importPermissions(RowPermission[] rows)
	{
		final ArrayList<RowPermission> permissions_p2g = new ArrayList<>();
		final ArrayList<RowPermission> permissions_p2u = new ArrayList<>();
		for(RowPermission row : rows)
			if(row.entityType == EntityType.GROUP)
			{
				row.entityObject = entities_g.get(row.entity.toLowerCase());
				permissions_p2g.add(row);
			} else {
				row.entityObject = entities_u.get(row.entity);
				permissions_p2u.add(row);
			}
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
			if(row.childType == EntityType.GROUP)
			{
				row.entityChild  = entities_g.get(row.entity.toLowerCase());
				row.entityParent = entities_g.get(row.parent.toLowerCase());
				inheritance_g2g.add(row);
			} else {
				row.entityChild  = entities_u.get(row.entity);
				row.entityParent = entities_g.get(row.parent.toLowerCase());
				inheritance_g2u.add(row);
			}
		for(Map.Entry<String, RowEntity> entry : entities_g.entrySet())
		{
			final ArrayList<RowInheritance> inheritances = new ArrayList<>();
			final String name = entry.getKey();
			for(RowInheritance row : inheritance_g2g)
				if(row.entity.toLowerCase().equals(name))
					inheritances.add(row);
			Collections.sort(inheritances);
			entry.getValue().inheritance = inheritances.toArray(new RowInheritance[inheritances.size()]);
		}
		for(Map.Entry<String, RowEntity> entry : entities_u.entrySet())
		{
			final ArrayList<RowInheritance> inheritances = new ArrayList<>();
			final String name = entry.getKey();
			for(RowInheritance row : inheritance_g2u)
				if(row.entity.equals(name))
					inheritances.add(row);
			Collections.sort(inheritances);
			entry.getValue().inheritance = inheritances.toArray(new RowInheritance[inheritances.size()]);
		}
		defaultInheritance.childType = EntityType.PLAYER;
		defaultInheritance.entityParent = entities_g.get(defaultInheritance.parent.toLowerCase());
	}
	public synchronized void clear()
	{
		entities_g.clear();
		entities_u.clear();
		implicit_g = null;
		implicit_u = null;
	}
}
