package ru.simsonic.rscPermissions.Backends;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import ru.simsonic.rscPermissions.DataTypes.Destination;
import ru.simsonic.rscPermissions.DataTypes.EntityType;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.DataTypes.DatabaseContents;
import ru.simsonic.rscUtilityLibrary.ConnectionMySQL;

public class BackendDatabase extends ConnectionMySQL
{
	protected final BukkitPluginMain rscp;
	public BackendDatabase(BukkitPluginMain plugin)
	{
		super(BukkitPluginMain.consoleLog);
		this.rscp = plugin;
	}
	@Override
	public synchronized boolean connect()
	{
		return super.connect()
			&& executeUpdateT("Initialize_main_v1")
			&& executeUpdateT("Cleanup_tables");
	}
	public synchronized DatabaseContents retrieveContents()
	{
		executeUpdateT("Cleanup_tables");
		final DatabaseContents contents = new DatabaseContents();
		contents.entities = fetchEntities();
		contents.permissions = fetchPermissions();
		contents.inheritance = fetchInheritance();
		BukkitPluginMain.consoleLog.log(Level.INFO,
			"[rscp] Fetched {0} entities, {1} permissions and {2} inheritances",
			new Integer[]
			{
				contents.entities.length,
				contents.permissions.length,
				contents.inheritance.length,
			});
		return contents;
	}
	public synchronized RowEntity[] fetchEntities()
	{
		final ArrayList<RowEntity> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}entities`;");
		try
		{
			while(rs.next())
			{
				RowEntity row = new RowEntity();
				row.id = rs.getInt("id");
				row.entity = rs.getString("entity");
				row.entityType = EntityType.byValue(rs.getInt("entity_type"));
				row.prefix = rs.getString("prefix");
				row.suffix = rs.getString("suffix");
				row.lifetime = rs.getTimestamp("lifetime");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			BukkitPluginMain.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2e(): {0}", ex);
		}
		return result.toArray(new RowEntity[result.size()]);
	}
	public synchronized RowPermission[] fetchPermissions()
	{
		final ArrayList<RowPermission> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}permissions`;");
		final String serverId = rscp.getServer().getServerId();
		try
		{
			while(rs.next())
			{
				for(Destination destination : Destination.parseDestinations(rs.getString("destination")))
				{
					if(destination.isServerIdApplicable(serverId) == false)
						continue;
					RowPermission row = new RowPermission();
					row.id = rs.getInt("id");
					row.entity = rs.getString("entity");
					row.entityType = EntityType.byValue(rs.getInt("entity_type"));
					row.permission = rs.getString("permission");
					row.value = rs.getBoolean("value");
					row.destination = destination;
					row.expirience = rs.getInt("expirience");
					row.lifetime = rs.getTimestamp("lifetime");
					result.add(row);
				}
			}
			rs.close();
		} catch(SQLException ex) {
			BukkitPluginMain.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2p(): {0}", ex);
		}
		return result.toArray(new RowPermission[result.size()]);
	}
	public synchronized RowInheritance[] fetchInheritance()
	{
		final ArrayList<RowInheritance> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}inheritance`;");
		final String serverId = rscp.getServer().getServerId();
		try
		{
			while(rs.next())
			{
				for(Destination destination : Destination.parseDestinations(rs.getString("destination")))
				{
					if(destination.isServerIdApplicable(serverId) == false)
						continue;
					RowInheritance row = new RowInheritance();
					row.id = rs.getInt("id");
					row.entity = rs.getString("entity");
					row.parent = rs.getString("parent");
					row.deriveInstance();
					row.childType = EntityType.byValue(rs.getInt("inheritance_type"));
					row.priority = rs.getInt("inheritance_priority");
					row.destination = destination;
					row.expirience = rs.getInt("expirience");
					row.lifetime = rs.getTimestamp("lifetime");
					result.add(row);
				}
			}
			rs.close();
		} catch(SQLException ex) {
			BukkitPluginMain.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2i(): {0}", ex);
		}
		return result.toArray(new RowInheritance[result.size()]);
	}
	public synchronized void insertExampleRows()
	{
		executeUpdateT("Insert_example_rows_v1");
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
	public synchronized void addUserParentGroup(String user, String newGroup)
	{
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{PARENT}", newGroup);
		executeUpdate("INSERT INTO `{DATABASE}`.`{PREFIX}inheritance` (`entity`, `parent`, `inheritance_type`) VALUES ('{USER}', '{PARENT}', b'1');");
	}
}
