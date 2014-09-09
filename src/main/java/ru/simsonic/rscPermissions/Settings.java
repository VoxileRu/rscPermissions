package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings
{
	private final MainPluginClass plugin;
	private String strDefaultGroup = "Default";
	private String strMaintenanceMode = "";
	private boolean bAlwaysInheritDefault = false;
	private boolean bTreatAsteriskAsOP = true;
	private boolean bUseMetrics = true;
	private boolean bUseUpdater = true;
	private boolean bUseWorldGuard = true;
	private boolean bUseResidence = true;
	private int nAutoReloadDelayTicks = 20 * 900;
	private int nRegionFinderGranularity = 1000;
	public final int CurrentVersion = 3;
	public static final String separator = ".";
	public static final String separatorRegExp = "\\.";
	public static final String instantiator = "?";
	public static final String instantiatorRegExp = "\\?";
	public Settings(final MainPluginClass plugin)
	{
		this.plugin = plugin;
	}
	public void onLoad()
	{
		plugin.saveDefaultConfig();
		final FileConfiguration config = plugin.getConfig();
		switch(plugin.getConfig().getInt("internal.version", CurrentVersion))
		{
			case 1:
				update_v1_to_v2(config);
				MainPluginClass.consoleLog.info("[rscp] Configuration updated from v1 to v2.");
			case 2:
				update_v2_to_v3(config);
				MainPluginClass.consoleLog.info("[rscp] Configuration updated from v2 to v3.");
			case CurrentVersion: // Current version
				plugin.saveConfig();
				break;
		}
	}
	private void update_v1_to_v2(FileConfiguration config)
	{
		config.set("settings.enable-bans", null);
		config.set("settings.integration.worldguard", true);
		config.set("settings.integration.residence", true);
		config.set("internal.version", 2);
	}
	private void update_v2_to_v3(FileConfiguration config)
	{
		config.set("settings.enable-rewards", null);
		config.set("internal.version", 3);
	}
	public void readSettings()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		strDefaultGroup          = config.getString ("settings.default-group", "Default");
		strMaintenanceMode       = config.getString ("settings.maintenance-mode", "");
		bAlwaysInheritDefault    = config.getBoolean("always-inherit-default-group", false);
		bTreatAsteriskAsOP       = config.getBoolean("settings.treat-asterisk-as-op", true);
		bUseMetrics              = config.getBoolean("settings.use-metrics", true);
		bUseUpdater              = config.getBoolean("settings.auto-update", true);
		bUseWorldGuard           = config.getBoolean("settings.integration.worldguard", true);
		bUseResidence            = config.getBoolean("settings.integration.residence", true);
		nAutoReloadDelayTicks    = config.getInt    ("settings.auto-reload-delay-sec", 900) * 20;
		nRegionFinderGranularity = config.getInt    ("settings.region-finder-thread-granularity-msec", 1000);
	}
	public String getDefaultGroup()
	{
		return strDefaultGroup;
	}
	public boolean isInMaintenance()
	{
		return !"".equals(strMaintenanceMode);
	}
	public String getMaintenanceMode()
	{
		return strMaintenanceMode;
	}
	public void setMaintenanceMode(String mMode)
	{
		strMaintenanceMode = (mMode == null) ? "" : mMode;
		plugin.getConfig().set("settings.maintenance-mode", strMaintenanceMode);
		plugin.saveConfig();
	}
	public boolean isDefaultForever()
	{
		return bAlwaysInheritDefault;
	}
	public boolean isAsteriskOP()
	{
		return bTreatAsteriskAsOP;
	}
	public boolean isUseMetrics()
	{
		return bUseMetrics;
	}
	public boolean isUpdatable()
	{
		return bUseUpdater;
	}
	public boolean isUseWorldGuard()
	{
		return bUseWorldGuard;
	}
	public boolean isUseResidence()
	{
		return bUseResidence;
	}
	public int getAutoReloadDelayTicks()
	{
		return nAutoReloadDelayTicks;
	}
	public long getRegionFinderGranularity()
	{
		return nRegionFinderGranularity;
	}
	public ConnectionHelper getConnectionChain()
	{
		List<Map<?, ?>> configServers = plugin.getConfig().getMapList("servers");
		List<HashMap<String, String>> serverlist = new ArrayList<>();
		for(Iterator<Map<?, ?>> it = configServers.iterator(); it.hasNext();)
		{
			Map<String, String> server = (Map<String, String>) it.next();
			HashMap<String, String> nodeinfo = new HashMap<>();
			String nodename = (String) server.get("nodename");
			String database = (String) server.get("database");
			String username = (String) server.get("username");
			String password = (String) server.get("password");
			String prefixes = (String) server.get("prefixes");
			String workmode = (String) server.get("workmode");
			if(nodename != null && !"".equals(nodename))
			{
				nodeinfo.put("nodename", nodename);
				nodeinfo.put("database", (database != null) ? database : "localhost:3306/minecraft");
				nodeinfo.put("username", (username != null) ? username : "user");
				nodeinfo.put("password", (password != null) ? password : "pass");
				nodeinfo.put("prefixes", (prefixes != null) ? prefixes : "rscp_");
				nodeinfo.put("workmode", (workmode != null) ? workmode : "none");
				serverlist.add(nodeinfo);
			}
		}
		Collections.reverse(serverlist);
		ConnectionHelper connPrev = null;
		for(HashMap<String, String> server : serverlist)
		{
			ConnectionHelper conn = new ConnectionHelper(plugin, connPrev);
			conn.Initialize(
				server.get("nodename"), server.get("database"),
				server.get("username"), server.get("password"),
				server.get("workmode"), server.get("prefixes"));
			connPrev = conn;
		}
		return connPrev;
	}
}