package ru.simsonic.rscPermissions.Engine.Backends;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class DatabaseTransaction
{
	private final BukkitPluginMain     rscp;
	private final List<DatabaseAction> actions = new LinkedList<>();
	public DatabaseTransaction(BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	/*
		What can happen?
		<user> add    permission [destination] [lifetime]
		<user> remove permission [destination]
		<user> add    group      [destination] [lifitime]
		<user> remove group      [destination]
	*/
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
		rscp.connection.lockTableEntities();
		rscp.connection.lockTablePermissions();
		rscp.connection.lockTableInheritance();
		rscp.connection.transactionStart();
		// SELECT FROM DATABASE INTO LOCAL CACHE TO MAKE IT ACTUAL
		return rscp.commandHelper.threadFetchDatabaseContents.remoteToLocal();
	}
	private void finishChanges(boolean commit)
	{
		// COMMIT OR ROLLBACK ACTIONS
		if(commit)
			rscp.connection.transactionCommit();
		else
			rscp.connection.transactionCommit();
		// CALL PLUGIN TO APPLY ALL THIS CHANGES
		rscp.commandHelper.threadFetchDatabaseContents.run();
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
