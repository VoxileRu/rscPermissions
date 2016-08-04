package ru.simsonic.rscPermissions.Engine.Backends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
		final DatabaseContents result = new DatabaseContents();
		result.entities    = fetchEntities();
		result.permissions = fetchPermissions();
		result.inheritance = fetchInheritance();
		result.cached = true;
		return result;
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
	public synchronized void updateEntityPrefix(String entity, EntityType type, String prefix)
	{
		if("".equals(entity))
			return;
		if("".equals(prefix) || "\"\"".equals(prefix))
			prefix = null;
		setupQueryTemplate("{ENTITY}", entity);
		setupQueryTemplate("{ENTITY_TYPE}", String.valueOf(type.getValue()));
		setupQueryTemplate("{TEXT_TYPE}", "prefix");
		setupQueryTemplate("{TEXT}", (prefix != null) ? "'" + prefix + "'" : "NULL");
		executeUpdateT("UpdateEntity");
	}
	public synchronized void updateEntitySuffix(String entity, EntityType type, String suffix)
	{
		if("".equals(entity))
			return;
		if("".equals(suffix) || "\"\"".equals(suffix))
			suffix = null;
		setupQueryTemplate("{ENTITY}", entity);
		setupQueryTemplate("{ENTITY_TYPE}", String.valueOf(type.getValue()));
		setupQueryTemplate("{TEXT_TYPE}", "suffix");
		setupQueryTemplate("{TEXT}", (suffix != null) ? "'" + suffix + "'" : "NULL");
		executeUpdateT("UpdateEntity");
	}
	public synchronized void removeEntityById(long id)
	{
		setupQueryTemplate("{ID}", Long.toString(id));
		executeUpdateT("DELETE FROM `{DATABASE}`.`{PREFIX}entities`    WHERE `id` = '{ID}';");
	}
	public synchronized void removePermissionsById(long id)
	{
		setupQueryTemplate("{ID}", Long.toString(id));
		executeUpdateT("DELETE FROM `{DATABASE}`.`{PREFIX}permissions` WHERE `id` = '{ID}';");
	}
	public synchronized void removeInheritanceById(long id)
	{
		setupQueryTemplate("{ID}", Long.toString(id));
		executeUpdateT("DELETE FROM `{DATABASE}`.`{PREFIX}inheritance` WHERE `id` = '{ID}';");
	}
	public synchronized void insertEntity(
		Long id,
		String entity, EntityType type,
		String prefix, String     suffix, Integer lifetime)
	{
		setupQueryTemplate("{TABLE}", "entities");
		final Map<String, String> fields = new HashMap<>();
		// Required fields
		fields.put("entity",      quoteValue(entity));
		fields.put("entity_type", type.equals(EntityType.PLAYER) ? "b'1'" : "b'0'");
		// Optional fields
		if(id          != null)
			fields.put("id",       Long.toString(id));
		if(prefix      != null)
			fields.put("prefix",   quoteValue(prefix));
		if(suffix      != null)
			fields.put("suffix",   quoteValue(suffix));
		if(lifetime    != null)
			fields.put("lifetime", lifetimeToValue(lifetime));
		insertRow(fields);
	}
	public synchronized void insertPermissions(
		Long   id,
		String entity,      EntityType type,
		String permission,  boolean    value,
		String destination, Integer    expirience, Integer lifetime)
	{
		setupQueryTemplate("{TABLE}", "permissions");
		final Map<String, String> fields = new HashMap<>();
		// Required fields
		fields.put("entity",      quoteValue(entity));
		fields.put("entity_type", type.equals(EntityType.PLAYER) ? "b'1'" : "b'0'");
		fields.put("permission",  quoteValue(permission));
		fields.put("value",       value ? "b'1'" : "b'0'");
		// Optional fields
		if(id          != null)
			fields.put("id",          Long.toString(id));
		if(destination != null)
			fields.put("destination", quoteValue(destination));
		if(expirience  != null)
			fields.put("expirience",  Integer.toString(expirience));
		if(lifetime    != null)
			fields.put("lifetime",    lifetimeToValue(lifetime));
		insertRow(fields);
	}
	public synchronized void insertInheritance(
		Long   id,
		String entity,      String  parent,     EntityType type,     Integer priority,
		String destination, Integer expirience, Integer    lifetime)
	{
		setupQueryTemplate("{TABLE}", "inheritance");
		final Map<String, String> fields = new HashMap<>();
		// Required fields
		fields.put("entity",           quoteValue(entity));
		fields.put("parent",           quoteValue(parent));
		fields.put("inheritance_type", type.equals(EntityType.PLAYER) ? "b'1'" : "b'0'");
		// Optional fields
		if(id          != null)
			fields.put("id",                   Long.toString(id));
		if(priority    != null)
			fields.put("inheritance_priority", Integer.toString(priority));
		if(destination != null)
			fields.put("destination",          quoteValue(destination));
		if(expirience  != null)
			fields.put("expirience",           Integer.toString(expirience));
		if(lifetime    != null)
			fields.put("lifetime",             lifetimeToValue(lifetime));
		insertRow(fields);
	}
	private String quoteValue(String value)
	{
		return new StringBuilder("'").append(value).append("'").toString();
	}
	private String lifetimeToValue(int lifetime)
	{
		if(lifetime < 0)
			return "NULL";
		return new StringBuilder("NOW() + INTERVAL ").append(lifetime).append(" SECOND").toString();
	}
	private void insertRow(Map<String, String> fields)
	{
		if(fields.isEmpty())
			return;
		final StringBuilder sbf = new StringBuilder();
		final StringBuilder sbv = new StringBuilder();
		final String        sep = ", ";
		for(Map.Entry<String, String> entry : fields.entrySet())
		{
			sbf.append("`").append(entry.getKey()).append("`").append(sep);
			sbv.append(entry.getValue()).append(sep);
		}
		sbf.setLength(sbf.length() - sep.length());
		sbv.setLength(sbv.length() - sep.length());
		setupQueryTemplate("{FIELDS}", sbf.toString());
		setupQueryTemplate("{VALUES}", sbv.toString());
		executeUpdateT("INSERT INTO `{DATABASE}`.`{PREFIX}{TABLE}` ({FIELDS}) VALUES ({VALUES});");
	}
}
