package ru.simsonic.rscPermissions.Engine.Backends;

import java.util.LinkedList;
import java.util.List;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
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
		// START TRANSACTION AND LOCK TABLE
		rscp.connection.lockTableEntities();
		rscp.connection.lockTablePermissions();
		rscp.connection.lockTableInheritance();
		rscp.connection.transactionStart();
		
		// SELECT FROM DATABASE INTO LOCAL CACHE TO MAKE IT ACTUAL
		final DatabaseContents contents = rscp.commandHelper.threadFetchDatabaseContents.remoteToLocal();
		
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
		}
		
		// COMMIT CHANGES AND UNLOCK TABLE
		rscp.connection.transactionCommit();
		rscp.connection.unlockAllTables();
		
		// CALL PLUGIN TO APPLY ALL THIS CHANGES
		rscp.commandHelper.threadFetchDatabaseContents.run();
	}
}
