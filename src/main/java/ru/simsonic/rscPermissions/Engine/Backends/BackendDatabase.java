package ru.simsonic.rscPermissions.Engine.Backends;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import ru.simsonic.rscCommonsLibrary.ConnectionMySQL;
import ru.simsonic.rscPermissions.API.EntityType;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;

public class BackendDatabase extends ConnectionMySQL
{
	private Logger consoleLog;
	public BackendDatabase()
	{
	}
	public void setLogger(Logger logger)
	{
		this.consoleLog = logger;
	}
	@Override
	public synchronized boolean connect()
	{
		try
		{
			return super.connect() && executeUpdateT("Deployment");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
		return false;
	}
	public synchronized void insertExampleRows()
	{
		try
		{
			executeUpdateT("ExampleContents");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized DatabaseContents retrieveContents()
	{
		final DatabaseContents result = new DatabaseContents();
		try
		{
			executeUpdateT("Cleanup");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
			return result;
		}
		result.entities    = fetchEntities();
		result.permissions = fetchPermissions();
		result.inheritance = fetchInheritance();
		result.cached = true;
		return result;
	}
	private RowEntity[] fetchEntities()
	{
		final ArrayList<RowEntity> result = new ArrayList<>();
		try(final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}entities`;"))
		{
			while(rs.next())
			{
				RowEntity row         = new RowEntity();
				row.id                = rs.getLong("id");
				row.entity            = rs.getString("entity");
				row.entityType        = EntityType.byValue(rs.getInt("entity_type"));
				row.prefix            = rs.getString("prefix");
				row.suffix            = rs.getString("suffix");
				row.lifetime          = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
		return result.toArray(new RowEntity[result.size()]);
	}
	private RowPermission[] fetchPermissions()
	{
		final ArrayList<RowPermission> result = new ArrayList<>();
		try(final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}permissions`;"))
		{
			while(rs.next())
			{
				RowPermission row     = new RowPermission();
				row.id                = rs.getLong("id");
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
			consoleLog.warning(ex.toString());
		}
		return result.toArray(new RowPermission[result.size()]);
	}
	private RowInheritance[] fetchInheritance()
	{
		final ArrayList<RowInheritance> result = new ArrayList<>();
		try(final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}inheritance`;"))
		{
			while(rs.next())
			{
				RowInheritance row    = new RowInheritance();
				row.id                = rs.getLong("id");
				row.entity            = rs.getString("entity");
				row.parent            = rs.getString("parent");
				row.entityType        = EntityType.byValue(rs.getInt("inheritance_type"));
				row.priority          = rs.getInt("inheritance_priority");
				row.destinationSource = rs.getString("destination");
				row.expirience        = rs.getInt("expirience");
				row.lifetime          = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
		return result.toArray(new RowInheritance[result.size()]);
	}
	public synchronized void lockTableEntities()
	{
		try
		{
			executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}entities`;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void lockTablePermissions()
	{
		try
		{
			executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}permissions`;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void lockTableInheritance()
	{
		try
		{
			executeUpdate("LOCK TABLES `{DATABASE}`.`{PREFIX}inheritance`;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void unlockAllTables()
	{
		try
		{
			executeUpdate("UNLOCK TABLES;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void transactionStart()
	{
		try
		{
			executeUpdate("START TRANSACTION;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void transactionCommit()
	{
		try
		{
			executeUpdate("COMMIT;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void transactionCancel()
	{
		try
		{
			executeUpdate("ROLLBACK;");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void updateEntityPrefix(String entity, EntityType type, String prefix)
	{
		try
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
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void updateEntitySuffix(String entity, EntityType type, String suffix)
	{
		try
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
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void removeEntityById(long id)
	{
		try
		{
			setupQueryTemplate("{ID}", Long.toString(id));
			executeUpdate("DELETE FROM `{DATABASE}`.`{PREFIX}entities`    WHERE `id` = '{ID}';");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void removePermissionsById(long id)
	{
		try
		{
			setupQueryTemplate("{ID}", Long.toString(id));
			executeUpdate("DELETE FROM `{DATABASE}`.`{PREFIX}permissions` WHERE `id` = '{ID}';");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
	public synchronized void removeInheritanceById(long id)
	{
		try
		{
			setupQueryTemplate("{ID}", Long.toString(id));
			executeUpdate("DELETE FROM `{DATABASE}`.`{PREFIX}inheritance` WHERE `id` = '{ID}';");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
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
		fields.put("entity_type", String.format("b'%d'", type.getValue()));
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
		try
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
			executeUpdate("INSERT INTO `{DATABASE}`.`{PREFIX}{TABLE}` ({FIELDS}) VALUES ({VALUES});");
		} catch(SQLException ex) {
			consoleLog.warning(ex.toString());
		}
	}
}
