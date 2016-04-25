package ru.simsonic.rscPermissions.Engine.Backends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.simsonic.rscCommonsLibrary.ConnectionMySQL;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;

public class BackendDatabase extends ConnectionMySQL
{
	public BackendDatabase(Logger logger)
	{
		super(logger);
	}
	@Override
	public synchronized boolean connect()
	{
		return super.connect() && executeUpdateT("Deployment");
	}
	public synchronized void insertExampleRows()
	{
		executeUpdateT("ExampleContents");
	}
	public synchronized DatabaseContents retrieveContents()
	{
		executeUpdateT("Cleanup");
		final DatabaseContents contents = new DatabaseContents();
		contents.entities    = fetchEntities();
		contents.permissions = fetchPermissions();
		contents.inheritance = fetchInheritance();
		return contents;
	}
	private RowEntity[] fetchEntities()
	{
		final ArrayList<RowEntity> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}entities`;");
		try
		{
			while(rs.next())
			{
				RowEntity row  = new RowEntity();
				row.id         = rs.getInt("id");
				row.entity     = rs.getString("entity");
				row.entityType = EntityType.byValue(rs.getInt("entity_type"));
				row.prefix     = rs.getString("prefix");
				row.suffix     = rs.getString("suffix");
				row.lifetime   = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			logger.log(Level.WARNING, "[rscp] Exception in rs2e(): {0}", ex);
		}
		return result.toArray(new RowEntity[result.size()]);
	}
	private RowPermission[] fetchPermissions()
	{
		final ArrayList<RowPermission> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}permissions`;");
		try
		{
			while(rs.next())
			{
				RowPermission row     = new RowPermission();
				row.id                = rs.getInt("id");
				row.entity            = rs.getString("entity");
				row.entityType        = EntityType.byValue(rs.getInt("entity_type"));
				row.permission        = rs.getString("permission");
				row.value             = rs.getBoolean("value");
				row.destinationSource = rs.getString("destination");
				row.expirience        = rs.getInt("expirience");
				row.lifetime          = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			logger.log(Level.WARNING, "[rscp] Exception in rs2p(): {0}", ex);
		}
		return result.toArray(new RowPermission[result.size()]);
	}
	private RowInheritance[] fetchInheritance()
	{
		final ArrayList<RowInheritance> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}inheritance`;");
		try
		{
			while(rs.next())
			{
				RowInheritance row    = new RowInheritance();
				row.id                = rs.getInt("id");
				row.entity            = rs.getString("entity");
				row.parent            = rs.getString("parent");
				row.childType         = EntityType.byValue(rs.getInt("inheritance_type"));
				row.priority          = rs.getInt("inheritance_priority");
				row.destinationSource = rs.getString("destination");
				row.expirience        = rs.getInt("expirience");
				row.lifetime          = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			logger.log(Level.WARNING, "[rscp] Exception in rs2i(): {0}", ex);
		}
		return result.toArray(new RowInheritance[result.size()]);
	}
	public synchronized void updateEntityText(String entity, boolean entity_type, String text, boolean isPrefix)
	{
		if("".equals(entity))
			return;
		if("".equals(text) || "\"\"".equals(text))
			text = null;
		setupQueryTemplate("{ENTITY}", entity);
		setupQueryTemplate("{ENTITY_TYPE}", entity_type ? "1" : "0");
		setupQueryTemplate("{TEXT_TYPE}", isPrefix ? "prefix" : "suffix");
		setupQueryTemplate("{TEXT}", (text != null) ? "'" + text + "'" : "NULL");
		executeUpdateT("Update_entity_text");
	}
	public synchronized void lockTableEntities()
	{
		executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}entities`;");
	}
	public synchronized void lockTablePermissions()
	{
		executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}permissions`;");
	}
	public synchronized void lockTableInheritance()
	{
		executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}inheritance`;");
	}
	public synchronized void unlockAllTables()
	{
		executeUpdate("UNLOCK TABLES;");
	}
	public synchronized void transactionStart()
	{
		executeUpdate("START TRANSACTION;");
	}
	public synchronized void transactionCommit()
	{
		executeUpdate("COMMIT;");
	}
	public synchronized void transactionCancel()
	{
		executeUpdate("ROLLBACK;");
	}
	public synchronized void modifyDatabase()
	{
		lockTableEntities();
		// FETCH ALL DATA
		transactionStart();
		// MAKE MODIFICATIONS
		transactionCommit();
		unlockAllTables();
		// FETCH ALL DATA AGAIN
	}
}
