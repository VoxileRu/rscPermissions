package ru.simsonic.rscPermissions.Engine.Backends;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class DatabaseEditor
{
	private final BukkitPluginMain            plugin;
	private final Map<String, RowEntity>      entities    = new HashMap<>();
	private final Map<String, RowPermission>  permissions = new HashMap<>();
	private final Map<String, RowInheritance> inheritance = new HashMap<>();
	public DatabaseEditor(BukkitPluginMain rscp)
	{
		this.plugin = rscp;
	}
	public void fill(DatabaseContents contents)
	{
		clear();
		for(RowEntity row : contents.entities)
			entities.put(row.splittedId, row);
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
	public void removeEntity(String splittedId)
	{
		// Do I know something about such row?
		final RowEntity row = entities.get(splittedId);
		if(row == null)
			return;
		// Find if it is part of a multidata row
		final List<RowEntity> fromSameRow = new LinkedList<>();
		for(RowEntity test : entities.values())
			if(test != row && test.id == row.id)
				fromSameRow.add(test);
		
		// TO DO : REMOVE ENTITY ROW HERE
		
		// Restore all data that contained in that row
		if(!fromSameRow.isEmpty())
		{
			// DO RESTORE
		}
	}
	public void apply() throws CommandAnswerException
	{
		final DatabaseContents contents = prepareChanges();
		// UNDERSTAND WHAT TO DO
		if(contents != null)
		{
			// OPTIONAL: REMOVE OLD `id`'s WITH POTENTIALLY MULTIPLY DATA
			if(false)
			{
				// OPTIONAL: RESTORE DATA THAT SHOULDN'T BE REMOVED
			}
			// OPTIONAL: INSERT NEW DATA THAT SHOULD BE ADDED
			if(false)
			{
				
			}
			// COMMIT CHANGES AND UNLOCK TABLES
			finishChanges(true);
		} else
			// CANCEL TRANSACTION AND UNLOCK TABLES
			finishChanges(false);
	}
	private void applyAddUserPermission()
	{
	}
	private void applyAddUserInheritance()
	{
	}
	private void applyRemoveUserPermission()
	{
	}
	private void applyRemoveUserInheritance()
	{
	}
	private DatabaseContents prepareChanges()
	{
		// START TRANSACTION AND LOCK TABLE
		plugin.connection.lockTableEntities();
		plugin.connection.lockTablePermissions();
		plugin.connection.lockTableInheritance();
		plugin.connection.transactionStart();
		// SELECT FROM DATABASE INTO LOCAL CACHE TO MAKE IT ACTUAL
		return plugin.commandHelper.threadFetchDatabaseContents.remoteToLocal();
	}
	private void finishChanges(boolean commit)
	{
		// COMMIT OR ROLLBACK ACTIONS
		if(commit)
			plugin.connection.transactionCommit();
		else
			plugin.connection.transactionCommit();
		// CALL PLUGIN TO APPLY ALL THIS CHANGES
		plugin.commandHelper.threadFetchDatabaseContents.run();
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
				merged.permission        = GenericChatCodes.glue(perms.toArray(new String[names.size()]), "; \n");
				merged.destinationSource = GenericChatCodes.glue(dests.toArray(new String[names.size()]), "; \n");
				return merged;
			} catch(CloneNotSupportedException ex) {
				// IMPOSSIBLE
			}
		}
		return null;
	}
	private RowPermission restoreInheritanceAfterDelete(DatabaseContents contents, RowInheritance remove)
	{
		return null;
	}
}
