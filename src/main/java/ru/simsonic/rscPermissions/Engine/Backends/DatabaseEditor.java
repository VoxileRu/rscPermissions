package ru.simsonic.rscPermissions.Engine.Backends;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class DatabaseEditor extends BackendDatabase
{
	private final BukkitPluginMain            plugin;
	private final Map<String, RowEntity>      entities    = new HashMap<>();
	private final Map<String, RowPermission>  permissions = new HashMap<>();
	private final Map<String, RowInheritance> inheritance = new HashMap<>();
	public DatabaseEditor(BukkitPluginMain rscp)
	{
		super(rscp.getServer().getLogger());
		this.plugin = rscp;
	}
	@Override
	public DatabaseContents retrieveContents()
	{
		final DatabaseContents result = super.retrieveContents();
		result.normalize();
		storeRowsIDs(result);
		return result;
	}
	private void storeRowsIDs(DatabaseContents contents)
	{
		clear();
		for(RowEntity row : contents.entities)
			entities.put   (row.splittedId, row);
		for(RowPermission row : contents.permissions)
			permissions.put(row.splittedId, row);
		for(RowInheritance row : contents.inheritance)
			inheritance.put(row.splittedId, row);
	}
	private void clear()
	{
		entities.clear();
		permissions.clear();
		inheritance.clear();
	}
	public void removePermission(RowPermission row)
	{
		final RowPermission replacement = restorePermissionsAfterDelete(null, row);
		// delete  row
		super.removePermissionsById(row.id);
		// restore replacement
		if(replacement != null)
		{
			addPermission(replacement);
		}
	}
	public void addPermission(RowPermission row)
	{
		super.insertPermissions(
			row.id          != 0    ? row.id                     : null,
			row.entity,
			row.entityType,
			row.permission,
			row.value,
			row.destination != null ? row.destination.toString() : null,
			row.expirience  != 0    ? row.expirience             : null,
			/* FIX LIFETIME */ null);
	}
	public void addInheritance(RowInheritance row)
	{
		super.insertInheritance(
			row.id          != 0    ? row.id                     : null,
			row.entity,
			row.parent,
			row.entityType,
			row.priority    != 0    ? row.priority               : null,
			row.destination != null ? row.destination.toString() : null,
			row.expirience  != 0    ? row.expirience             : null,
			/* FIX LIFETIME */ null);
	}
	private DatabaseContents prepareChanges()
	{
		// START TRANSACTION AND LOCK TABLE
		plugin.connection.lockTableEntities();
		plugin.connection.lockTablePermissions();
		plugin.connection.lockTableInheritance();
		plugin.connection.transactionStart();
		// SELECT FROM DATABASE INTO LOCAL CACHE TO MAKE IT ACTUAL
		return plugin.fetching.remoteToLocal();
	}
	private void finishChanges(boolean commit)
	{
		// COMMIT OR ROLLBACK ACTIONS
		if(commit)
			plugin.connection.transactionCommit();
		else
			plugin.connection.transactionCommit();
		// CALL PLUGIN TO APPLY ALL THIS CHANGES
		plugin.fetching.run();
	}
	private RowPermission restorePermissionsAfterDelete(DatabaseContents contents, RowPermission remove)
	{
		final LinkedList<RowPermission> sameIDs = new LinkedList<>();
		for(RowPermission row : contents.permissions)
			if(row.id == remove.id)
				sameIDs.add(row);
		if(sameIDs.isEmpty() == false)
		{
			final HashSet<String> names = new HashSet<>();
			final HashSet<String> perms = new HashSet<>();
			final HashSet<String> dests = new HashSet<>();
			for(RowPermission row : sameIDs)
			{
				// assert remove.value == row.value;
				// assert remove.expirience == row.expirience;
				// assert remove.entityType.equals(row.entityType);
				// assert remove.lifetime.equals(row.lifetime);
				names.add(row.entity);
				perms.add(row.permission);
				dests.add(row.destination.toString());
			}
			try
			{
				final RowPermission merged = remove.clone();
				merged.entity            = GenericChatCodes.glue(names.toArray(new String[names.size()]), "; \n");
				merged.permission        = GenericChatCodes.glue(perms.toArray(new String[perms.size()]), "; \n");
				merged.destinationSource = GenericChatCodes.glue(dests.toArray(new String[dests.size()]), "; \n");
				return merged;
			} catch(CloneNotSupportedException ex) {
				// IMPOSSIBLE
			}
		}
		return null;
	}
	private RowInheritance restoreInheritanceAfterDelete(DatabaseContents contents, RowInheritance remove)
	{
		final LinkedList<RowInheritance> sameIDs = new LinkedList<>();
		for(RowInheritance row : contents.inheritance)
			if(row.id == remove.id)
				sameIDs.add(row);
		if(sameIDs.isEmpty() == false)
		{
			final HashSet<String> names = new HashSet<>();
			final HashSet<String> prnts = new HashSet<>();
			final HashSet<String> dests = new HashSet<>();
			for(RowInheritance row : sameIDs)
			{
				// assert remove.value == row.value;
				// assert remove.expirience == row.expirience;
				// assert remove.entityType.equals(row.entityType);
				// assert remove.lifetime.equals(row.lifetime);
				names.add(row.entity);
				prnts.add(row.parent);
				dests.add(row.destination.toString());
			}
			try
			{
				final RowInheritance merged = remove.clone();
				merged.entity            = GenericChatCodes.glue(names.toArray(new String[names.size()]), "; \n");
				merged.parent            = GenericChatCodes.glue(prnts.toArray(new String[prnts.size()]), "; \n");
				merged.destinationSource = GenericChatCodes.glue(dests.toArray(new String[dests.size()]), "; \n");
				return merged;
			} catch(CloneNotSupportedException ex) {
				// IMPOSSIBLE
			}
		}
		return null;
	}
}
