package ru.simsonic.rscPermissions.Backends;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import ru.simsonic.rscPermissions.DataTypes.Destination;
import ru.simsonic.rscPermissions.DataTypes.EntityType;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.InternalCache.LocalCacheData;
import ru.simsonic.rscPermissions.MainPluginClass;
import ru.simsonic.rscPermissions.Settings;
import ru.simsonic.utilities.*;

public class BackendMySQL extends ConnectionMySQL implements Backend
{
	protected final MainPluginClass plugin;
	protected static enum WorkMode { read, write, none, }
	protected WorkMode RememberWork;
	public BackendMySQL(MainPluginClass plugin)
	{
		this.plugin = plugin;
	}
	public synchronized void Initialize(String name, String database, String username, String password, String workmode, String prefixes)
	{
		super.Initialize(name, database, username, password, prefixes);
		switch(workmode.toLowerCase())
		{
		case "fullaccess":
			RememberWork = WorkMode.write;
			break;
		case "readonly":
			RememberWork = WorkMode.read;
			break;
		case "none":
		default:
			RememberWork = WorkMode.none;
			break;
		}
	}
	@Override
	public synchronized boolean canRead()
	{
		return (RememberWork != WorkMode.none) ? (isConnected() ? true : Connect()) : false;
	}
	@Override
	public synchronized boolean canWrite()
	{
		return (RememberWork == WorkMode.write) ? (isConnected() ? true : Connect()) : false;
	}
	@Override
	public synchronized boolean Connect()
	{
		if(RememberWork == WorkMode.none)
			return false;
		if(super.Connect())
		{
			executeUpdateT("Initialize_main_v1");
			cleanupTables();
			return true;
		}
		return false;
	}
	@Override
	public synchronized ResultSet executeQuery(String query)
	{
		if(canRead() == false)
			return null;
		return super.executeQuery(query);
	}
	@Override
	public synchronized boolean executeUpdate(String query)
	{
		if(canWrite() == false)
			return false;
		return super.executeUpdate(query);
	}
	private void cleanupTables()
	{
		executeUpdateT("Cleanup_tables");
	}
	@Override
	public synchronized void fetchIntoCache(LocalCacheData cache)
	{
		cleanupTables();
		MainPluginClass.consoleLog.log(Level.INFO,
			"[rscp] Fetched {0}e, {1}p, {2}i, {3}l, from \"{4}\".",
			new Object[]
			{
				Integer.toString(cache.ImportEntities(fetchEntities())),
				Integer.toString(cache.ImportPermissions(fetchPermissions())),
				Integer.toString(cache.ImportInheritance(fetchInheritance())),
				Integer.toString(cache.ImportLadders(fetchLadders())),
				RememberName,
			});
	}
	@Override
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
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2e(): {0}", ex);
		}
		return result.toArray(new RowEntity[result.size()]);
	}
	@Override
	public synchronized RowPermission[] fetchPermissions()
	{
		final ArrayList<RowPermission> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}permissions`;");
		final String serverId = plugin.getServer().getServerId();
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
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2p(): {0}", ex);
		}
		return result.toArray(new RowPermission[result.size()]);
	}
	@Override
	public synchronized RowInheritance[] fetchInheritance()
	{
		final ArrayList<RowInheritance> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}inheritance`;");
		final String serverId = plugin.getServer().getServerId();
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
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2i(): {0}", ex);
		}
		return result.toArray(new RowInheritance[result.size()]);
	}
	@Override
	public synchronized RowLadder[] fetchLadders()
	{
		final ArrayList<RowLadder> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}ladders`;");
		try
		{
			while(rs.next())
			{
				RowLadder row = new RowLadder();
				row.id = rs.getInt("id");
				row.climber = rs.getString("climber");
				if("".equals(row.climber))
					row.climber = null;
				row.climberType = EntityType.byValue(rs.getInt("climber_type"));
				row.ladder = rs.getString("ladder");
				String[] breaked = row.ladder.split(Settings.separatorRegExp);
				if(breaked.length == 2)
				{
					row.ladder = breaked[0];
					row.instance = breaked[1];
				}
				row.rank = rs.getInt("rank");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2l(): {0}", ex);
		}
		return result.toArray(new RowLadder[result.size()]);
	}
	@Override
	public synchronized void insertExampleRows()
	{
		executeUpdateT("Insert_example_rows_v1");
	}
	@Override
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
	@Override
	public synchronized void setUserRank(String user, String ladder, int rank)
	{
		if("".equals(user) || "".equals(ladder))
			return;
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{LADDER}", ladder);
		setupQueryTemplate("{RANK}", Integer.toString(rank));
		executeUpdateT("Set_user_rank");
	}
	@Override
	public synchronized void dropUserFromLadder(String user, String ladder)
	{
		String instance = "";
		String[] breaked = ladder.split(Settings.separatorRegExp);
		if(breaked.length == 2)
		{
			ladder = breaked[0];
			instance = breaked[1];
		}
		if("".equals(user) || "".equals(ladder))
			return;
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{LADDER}", ladder);
		setupQueryTemplate("{INSTANCE}", instance);
		executeUpdateT("Drop_user_from_ladder");
	}
	@Override
	public synchronized void addUserParentGroup(String user, String newGroup)
	{
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{PARENT}", newGroup);
		executeUpdate("INSERT INTO `{DATABASE}`.`{PREFIX}inheritance` (`entity`, `parent`, `inheritance_type`) VALUES ('{USER}', '{PARENT}', b'1');");
	}
}