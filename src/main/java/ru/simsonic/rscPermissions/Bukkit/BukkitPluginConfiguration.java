package ru.simsonic.rscPermissions.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.ConnectionMySQL.ConnectionParams;

public class BukkitPluginConfiguration implements Settings
{
	private final BukkitPluginMain plugin;
	private String strDefaultGroup = "Default";
	private String strMaintenanceMode = "";
	private boolean bAlwaysInheritDefault = false;
	private boolean bTreatAsteriskAsOP = true;
	private boolean bUseMetrics = true;
	private boolean bUseWorldGuard = true;
	private boolean bUseResidence = true;
	private int nAutoReloadDelayTicks = 20 * 900;
	private int nRegionFinderGranularity = 1000;
	public final int CurrentVersion = 3;
	public BukkitPluginConfiguration(final BukkitPluginMain plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void onLoad()
	{
		plugin.saveDefaultConfig();
		final FileConfiguration config = plugin.getConfig();
		switch(plugin.getConfig().getInt("internal.version", CurrentVersion))
		{
			case 1:
				update_v1_to_v2(config);
				BukkitPluginMain.consoleLog.info("[rscp] Configuration updated from v1 to v2.");
			case 2:
				update_v2_to_v3(config);
				BukkitPluginMain.consoleLog.info("[rscp] Configuration updated from v2 to v3.");
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
		config.set("settings.auto-update", null);
		config.set("internal.version", 3);
	}
	@Override
	public void readSettings()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		strDefaultGroup = config.getString("settings.default-group", "Default");
		strMaintenanceMode = config.getString("settings.maintenance-mode", "");
		bAlwaysInheritDefault = config.getBoolean("always-inherit-default-group", false);
		bTreatAsteriskAsOP = config.getBoolean("settings.treat-asterisk-as-op", true);
		bUseMetrics = config.getBoolean("settings.use-metrics", true);
		bUseWorldGuard = config.getBoolean("settings.integration.worldguard", true);
		bUseResidence = config.getBoolean("settings.integration.residence", true);
		nAutoReloadDelayTicks = config.getInt("settings.auto-reload-delay-sec", 900) * 20;
		nRegionFinderGranularity = config.getInt("settings.region-finder-thread-granularity-msec", 1000);
	}
	@Override
	public String getDefaultGroup()
	{
		return strDefaultGroup;
	}
	@Override
	public boolean isInMaintenance()
	{
		return  ! "".equals(strMaintenanceMode);
	}
	@Override
	public String getMaintenanceMode()
	{
		return strMaintenanceMode;
	}
	@Override
	public void setMaintenanceMode(String mode)
	{
		strMaintenanceMode = (mode == null) ? "" : mode;
		plugin.getConfig().set("settings.maintenance-mode", strMaintenanceMode);
		plugin.saveConfig();
	}
	@Override
	public boolean isDefaultForever()
	{
		return bAlwaysInheritDefault;
	}
	@Override
	public boolean isAsteriskOP()
	{
		return bTreatAsteriskAsOP;
	}
	@Override
	public boolean isUseMetrics()
	{
		return bUseMetrics;
	}
	@Override
	public boolean isUseWorldGuard()
	{
		return bUseWorldGuard;
	}
	@Override
	public boolean isUseResidence()
	{
		return bUseResidence;
	}
	@Override
	public int getAutoReloadDelayTicks()
	{
		return nAutoReloadDelayTicks;
	}
	@Override
	public long getRegionFinderGranularity()
	{
		return nRegionFinderGranularity;
	}
	@Override
	public ConnectionParams getConnectionParams()
	{
		final FileConfiguration config = plugin.getConfig();
		final ConnectionParams result = new ConnectionParams();
		result.nodename = "rscp";
		result.database = config.getString("settings.connection.database", "localhost:3306/minecraft");
		result.username = config.getString("settings.connection.username", "user1");
		result.password = config.getString("settings.connection.password", "pass1");
		result.prefixes = config.getString("settings.connection.prefixes", "rscp_");
		return result;
	}
}
