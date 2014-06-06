package ru.simsonic.rscPermissions.Backends;
import ru.simsonic.utilities.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import ru.simsonic.rscPermissions.DataTypes.Destination;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowEntity.EntityType;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.DataTypes.RowReward;
import ru.simsonic.rscPermissions.DataTypes.RowServer;
import ru.simsonic.rscPermissions.LocalCacheData;
import ru.simsonic.rscPermissions.MainPluginClass;
import ru.simsonic.rscPermissions.Rewards;
import ru.simsonic.rscPermissions.Settings;

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
			createTablesIfNotExist();
			cleanupTables();
			updateServerInfo();
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
	private void createTablesIfNotExist()
	{
		executeUpdate(loadResourceSQLT("Initialize_main_v1"));
		if(plugin.settings.isRewardsEnabled())
			executeUpdate(loadResourceSQLT("Initialize_rewards_v1"));
	}
	private void cleanupTables()
	{
		executeUpdate(loadResourceSQLT("Cleanup_tables"));
	}
	private void updateServerInfo()
	{
		final String mMode = plugin.settings.getMaintenanceMode();
		setupQueryTemplate("{SERVERID}",   plugin.getServer().getServerId());
		setupQueryTemplate("{PLUGIN_VER}", plugin.getDescription().getVersion());
		setupQueryTemplate("{DEFAULT}",    plugin.settings.getDefaultGroup());
		setupQueryTemplate("{OP}",         plugin.settings.isAsteriskOP() ? "1" : "0");
		setupQueryTemplate("{DELAY}",      Integer.toString(plugin.settings.getAutoReloadDelayTicks() / 20));
		setupQueryTemplate("{mMode}",      "".equals(mMode) ? "NULL" : "\"" + mMode + "\"");
		setupQueryTemplate("{USE_R}",      plugin.settings.isRewardsEnabled() ? "1" : "0");
		setupQueryTemplate("{CFG_VER}",    Integer.toString(plugin.settings.CurrentVersion));
		executeUpdate(loadResourceSQLT("Update_server_info"));
	}
	@Override
	public synchronized void fetchIntoCache(LocalCacheData cache)
	{
		cleanupTables();
		MainPluginClass.consoleLog.log(Level.INFO,
			"[rscp] Fetched {0}e, {1}p, {2}i, {3}l, {4}s from \"{5}\".",
			new Object[]
			{
				Integer.toString(cache.ImportEntities(fetchEntities())),
				Integer.toString(cache.ImportPermissions(fetchPermissions())),
				Integer.toString(cache.ImportInheritance(fetchInheritance())),
				Integer.toString(cache.ImportLadders(fetchLadders())),
				Integer.toString(cache.ImportServers(fetchServers())),
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
				row.entity_type = EntityType.byValue(rs.getInt("entity_type"));
				row.prefix = rs.getString("prefix");
				row.suffix = rs.getString("suffix");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2e(): {0}", ex.getLocalizedMessage());
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
				for(Destination destination : Destination.ParseDestinations(rs.getString("destination")))
				{
					if(destination.IsServerIdApplicable(serverId) == false)
						continue;
					RowPermission row = new RowPermission();
					row.id = rs.getInt("id");
					row.entity = rs.getString("entity");
					row.entity_type = EntityType.byValue(rs.getInt("entity_type"));
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
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2p(): {0}", ex.getLocalizedMessage());
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
				for(Destination destination : Destination.ParseDestinations(rs.getString("destination")))
				{
					if(destination.IsServerIdApplicable(serverId) == false)
						continue;
					RowInheritance row = new RowInheritance();
					row.id = rs.getInt("id");
					row.entity = rs.getString("entity");
					row.parent = rs.getString("parent");
					String[] breaked = row.parent.split(Settings.separatorRegExp);
					if(breaked.length == 2)
					{
						row.parent = breaked[0];
						row.instance = breaked[1];
					}
					row.child_type = EntityType.byValue(rs.getInt("inheritance_type"));
					row.priority = rs.getInt("inheritance_priority");
					row.destination = destination;
					row.expirience = rs.getInt("expirience");
					row.lifetime = rs.getTimestamp("lifetime");
					result.add(row);
				}
			}
			rs.close();
		} catch(SQLException ex) {
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2i(): {0}", ex.getLocalizedMessage());
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
				row.climber_type = EntityType.byValue(rs.getInt("climber_type"));
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
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2l(): {0}", ex.getLocalizedMessage());
		}
		return result.toArray(new RowLadder[result.size()]);
	}
	@Override
	public synchronized RowServer[] fetchServers()
	{
		final ArrayList<RowServer> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}servers`;");
		try
		{
			while(rs.next())
			{
				RowServer row = new RowServer();
				row.serverId = rs.getString("serverId");
				// PARSE OTHER COLUMNS HERE
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2s(): {0}", ex.getLocalizedMessage());
		}
		return result.toArray(new RowServer[result.size()]);
	}
	public synchronized void fetchRewards(Rewards rewardHelper)
	{
		final ArrayList<RowReward> result = new ArrayList<>();
		final ResultSet rs = executeQuery("SELECT * FROM `{DATABASE}`.`{PREFIX}rewards`;");
		try
		{
			while(rs.next())
			{
				RowReward row = new RowReward();
				row.id = rs.getInt("id");
				row.user = rs.getString("user").toLowerCase();
				row.code = rs.getString("code");
				row.activated = rs.getBoolean("activated");
				if(row.activated)
					continue;
				row.activated_timestamp = rs.getTimestamp("activated_timestamp");
				row.execute_commands = rs.getString("execute_commands");
				row.command_permissions = rs.getString("command_permissions");
				row.add_group = rs.getString("add_group");
				row.add_group_destination = rs.getString("add_group_destination");
				row.add_group_expirience = rs.getInt("add_group_expirience");
				row.add_group_interval = rs.getString("add_group_interval");
				result.add(row);
			}
			rs.close();
		} catch(SQLException ex) {
			MainPluginClass.consoleLog.log(Level.WARNING, "[rscp] Exception in rs2r(): {0}", ex.getLocalizedMessage());
		}
		MainPluginClass.consoleLog.log(Level.INFO, "[rscp] Fetched {0} unused reward codes.",
			Integer.toString(result.size()));
		rewardHelper.ImportRewards(result.toArray(new RowReward[result.size()]));
	}
	@Override
	public synchronized void insertExampleRows()
	{
		executeUpdate(loadResourceSQLT("Insert_example_rows_v1"));
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
		executeUpdate(loadResourceSQLT("Update_entity_text"));
	}
	@Override
	public synchronized void setUserRank(String user, String ladder, int rank)
	{
		if("".equals(user) || "".equals(ladder))
			return;
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{LADDER}", ladder);
		setupQueryTemplate("{RANK}", Integer.toString(rank));
		executeUpdate(loadResourceSQLT("Set_user_rank"));
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
		executeUpdate(loadResourceSQLT("Drop_user_from_ladder"));
	}
	@Override
	public synchronized void addUserParentGroup(String user, String newGroup)
	{
		setupQueryTemplate("{USER}", user);
		setupQueryTemplate("{PARENT}", newGroup);
		executeUpdate("INSERT INTO `{DATABASE}`.`{PREFIX}inheritance` (`entity`, `parent`, `inheritance_type`) VALUES ('{USER}', '{PARENT}', b'1');");
	}
}