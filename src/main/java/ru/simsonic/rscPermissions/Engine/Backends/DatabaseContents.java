package ru.simsonic.rscPermissions.Engine.Backends;

import java.util.ArrayList;
import java.util.Date;
import ru.simsonic.rscPermissions.API.Destination;
import ru.simsonic.rscPermissions.API.PlayerType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Engine.Matchers;

public class DatabaseContents
{
	public boolean        cached;
	public RowEntity      entities[];
	public RowPermission  permissions[];
	public RowInheritance inheritance[];
	public DatabaseContents normalize()
	{
		if(entities == null)
			entities = new RowEntity[] {};
		if(permissions == null)
			permissions = new RowPermission[] {};
		if(inheritance == null)
			inheritance = new RowInheritance[] {};
		final ArrayList<RowEntity>      le = new ArrayList<>();
		final ArrayList<RowPermission>  lp = new ArrayList<>();
		final ArrayList<RowInheritance> li = new ArrayList<>();
		try
		{
			long subRowEntry;
			// Entities
			for(RowEntity row : entities)
			{
				subRowEntry = 0;
				final String[] splittedByEntity = Matchers.splitDatabaseRows(row.entity);
				for(String oneEntity : splittedByEntity)
				{
					final RowEntity clone = row.clone();
					clone.splittedId = String.format("e%d:%d", row.id, subRowEntry);
					clone.entity     = PlayerType.normalize(oneEntity);
					le.add(clone);
					subRowEntry += 1;
				}
			}
			// Permissions
			for(RowPermission row : permissions)
			{
				subRowEntry = 0;
				final String[] splittedByEntity      = Matchers.splitDatabaseRows(row.entity);
				final String[] splittedByPermission  = Matchers.splitDatabaseRows(row.permission);
				final String[] splittedByDestination = Matchers.splitDatabaseRows(row.destinationSource);
				row.destinationSource = null;
				for(String oneDestination : splittedByDestination)
				{
					final Destination destination = Destination.parseDestination(oneDestination);
					for(String permission : splittedByPermission)
						for(String entity : splittedByEntity)
						{
							final RowPermission clone = row.clone();
							clone.splittedId  = String.format("p%d:%d", row.id, subRowEntry);
							clone.entity      = PlayerType.normalize(entity);
							clone.permission  = permission;
							clone.destination = destination;
							lp.add(clone);
							subRowEntry += 1;
						}
				}
			}
			// Inheritance
			for(RowInheritance row : inheritance)
			{
				subRowEntry = 0;
				final String[] splittedByEntity      = Matchers.splitDatabaseRows(row.entity);
				final String[] splittedByParent      = Matchers.splitDatabaseRows(row.parent);
				final String[] splittedByDestination = Matchers.splitDatabaseRows(row.destinationSource);
				row.destinationSource = null;
				for(String oneDestination : splittedByDestination)
				{
					final Destination destination = Destination.parseDestination(oneDestination);
					for(String parent : splittedByParent)
						for(String entity : splittedByEntity)
						{
							final RowInheritance clone = row.clone();
							clone.splittedId  = String.format("i%d:%d", row.id, subRowEntry);
							clone.entity      = PlayerType.normalize(entity);
							clone.parent      = parent;
							clone.deriveInstance();
							clone.destination = destination;
							li.add(clone);
							subRowEntry += 1;
						}
				}
			}
		} catch(CloneNotSupportedException ex) {
		}
		entities    = le.toArray(new RowEntity[le.size()]);
		permissions = lp.toArray(new RowPermission[lp.size()]);
		inheritance = li.toArray(new RowInheritance[li.size()]);
		return this;
	}
	public DatabaseContents filterLifetime()
	{
		if(entities == null)
			entities = new RowEntity[] {};
		if(permissions == null)
			permissions = new RowPermission[] {};
		if(inheritance == null)
			inheritance = new RowInheritance[] {};
		final Date date = new Date();
		final ArrayList<RowEntity>      le = new ArrayList<>();
		final ArrayList<RowPermission>  lp = new ArrayList<>();
		final ArrayList<RowInheritance> li = new ArrayList<>();
		for(RowEntity row : entities)
			if(!(row.lifetime != null && row.lifetime.after(date)))
				le.add(row);
		for(RowPermission row : permissions)
			if(!(row.lifetime != null && row.lifetime.after(date)))
				lp.add(row);
		for(RowInheritance row : inheritance)
			if(!(row.lifetime != null && row.lifetime.after(date)))
				li.add(row);
		entities    = le.toArray(new RowEntity[le.size()]);
		permissions = lp.toArray(new RowPermission[lp.size()]);
		inheritance = li.toArray(new RowInheritance[li.size()]);
		return this;
	}
	public DatabaseContents filterServerId(String serverId)
	{
		if(permissions == null)
			permissions = new RowPermission[] {};
		if(inheritance == null)
			inheritance = new RowInheritance[] {};
		final ArrayList<RowPermission> lp = new ArrayList<>();
		final ArrayList<RowInheritance> li = new ArrayList<>();
		// Permissions
		for(RowPermission row : permissions)
			if(row.destination.isServerIdApplicable(serverId))
				lp.add(row);
		for(RowInheritance row : inheritance)
			if(row.destination.isServerIdApplicable(serverId))
				li.add(row);
		permissions = lp.toArray(new RowPermission[lp.size()]);
		inheritance = li.toArray(new RowInheritance[li.size()]);
		return this;
	}
	public boolean isEmpty()
	{
		return !((entities != null && entities.length > 0)
			&& (permissions != null && permissions.length > 0)
			&& (inheritance != null && inheritance.length > 0));
	}
}
