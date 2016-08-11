package ru.simsonic.rscPermissions.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.simsonic.rscCommonsLibrary.ConnectionMySQL.ConnectionParams;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.API.TranslationProvider;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class BukkitSettings implements Settings
{
	private final static int CURRENT_CONFIG_VERSION = 5;
	private final BukkitPluginMain plugin;
	private String  strDefaultGroup          = "Default";
	private String  strMaintenanceMode       = "";
	private String  strMaintenancePingMsg    = GenericChatCodes.processStringStatic(DEFAULT_MMODE_MSG_PING);
	private String  strMaintenanceKickMsg    = GenericChatCodes.processStringStatic(DEFAULT_MMODE_MSG_KICK);
	private String  strMaintenanceJoinMsg    = GenericChatCodes.processStringStatic(DEFAULT_MMODE_MSG_JOIN);
	private String  language                 = "english";
	private boolean bAlwaysInheritDefault    = false;
	private boolean bTreatAsteriskAsOP       = true;
	private boolean bDisableDatabaseEdits    = true;
	private boolean bUsingAncestorPrefixes   = true;
	private boolean bUseMetrics              = true;
	private boolean bUseWorldGuard           = true;
	private int     nAutoReloadDelayTicks    = 20 * 900;
	private int     nRegionFinderGranularity = 1000;
	public BukkitSettings(final BukkitPluginMain plugin)
	{
		this.plugin = plugin;
	}
	@Override
	public void onLoad()
	{
		plugin.saveDefaultConfig();
		final FileConfiguration config = plugin.getConfig();
		switch(plugin.getConfig().getInt("internal.version", CURRENT_CONFIG_VERSION))
		{
			case 1:
				update_v1_to_v2(config);
				BukkitPluginMain.consoleLog.info(Settings.CHAT_PREFIX + "Configuration updated from v1 to v2.");
			case 2:
				update_v2_to_v3(config);
				BukkitPluginMain.consoleLog.info(Settings.CHAT_PREFIX + "Configuration updated from v2 to v3.");
			case 3:
				update_v3_to_v4(config);
				BukkitPluginMain.consoleLog.info(Settings.CHAT_PREFIX + "Configuration updated from v3 to v4.");
			case 4:
				update_v4_to_v5(config);
				BukkitPluginMain.consoleLog.info(Settings.CHAT_PREFIX + "Configuration updated from v3 to v4.");
				// Keep it here to not rewrite config everyday
				config.set("internal.version", CURRENT_CONFIG_VERSION);
				plugin.saveConfig();
			case CURRENT_CONFIG_VERSION:
				break;
		}
	}
	private void update_v1_to_v2(FileConfiguration config)
	{
		config.set("settings.enable-bans", null);
		config.set("settings.integration.worldguard", true);
		config.set("settings.integration.residence", true);
	}
	private void update_v2_to_v3(FileConfiguration config)
	{
		config.set("settings.enable-rewards", null);
		config.set("settings.auto-update", null);
		config.set("settings.language", "english");
	}
	private void update_v3_to_v4(FileConfiguration config)
	{
		if(!config.contains("settings.slot-limits"))
		{
			config.set("settings.slot-limits.administrators", 5);
			config.set("settings.slot-limits.premium",        25);
		}
		config.set("settings.groups-inherit-parent-prefixes", true);
		config.set("settings.maintenances.default.ping-motd",   DEFAULT_MMODE_MSG_PING);
		config.set("settings.maintenances.default.kick-online", DEFAULT_MMODE_MSG_KICK);
		config.set("settings.maintenances.default.block-join",  DEFAULT_MMODE_MSG_JOIN);
	}
	private void update_v4_to_v5(FileConfiguration config)
	{
		config.set("settings.integration.residence", null);
		config.set("settings.disable-insecure-commands", true);
	}
	@Override
	public void onEnable()
	{
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();
		language                 = config.getString("settings.language", "english");
		strDefaultGroup          = config.getString("settings.default-group", "Default");
		strMaintenanceMode       = config.getString("settings.maintenance-mode", "");
		bAlwaysInheritDefault    = config.getBoolean("settings.always-inherit-default-group", false);
		bTreatAsteriskAsOP       = config.getBoolean("settings.treat-asterisk-as-op", true);
		bDisableDatabaseEdits    = config.getBoolean("settings.disable-insecure-commands", true);
		bUsingAncestorPrefixes   = config.getBoolean("settings.groups-inherit-parent-prefixes", true);
		bUseWorldGuard           = config.getBoolean("settings.integration.worldguard", true);
		bUseMetrics              = config.getBoolean("settings.use-metrics", true);
		nAutoReloadDelayTicks    = config.getInt("settings.auto-reload-delay-sec", 900) * 20;
		nRegionFinderGranularity = config.getInt("settings.region-finder-thread-granularity-msec", 1000);
		if(nAutoReloadDelayTicks <= 0)
			nAutoReloadDelayTicks = -1;
		getMaintenanceStrings();
	}
	@Override
	public String getDefaultGroup()
	{
		return strDefaultGroup;
	}
	@Override
	public boolean isInMaintenance()
	{
		return !"".equals(strMaintenanceMode);
	}
	@Override
	public String getMaintenanceMode()
	{
		return strMaintenanceMode;
	}
	@Override
	public void setMaintenanceMode(String mode)
	{
		strMaintenanceMode = (mode != null) ? mode : "";
		plugin.getConfig().set("settings.maintenance-mode", strMaintenanceMode);
		plugin.saveConfig();
		getMaintenanceStrings();
	}
	private void getMaintenanceStrings()
	{
		if(!"".equals(strMaintenanceMode))
		{
			final String configPath = "settings.maintenances." + strMaintenanceMode.toLowerCase();
			strMaintenancePingMsg = GenericChatCodes.processStringStatic(
				plugin.getConfig().getString(configPath + ".ping-motd",   DEFAULT_MMODE_MSG_PING)
				.replace("{MMODE}", strMaintenanceMode));
			strMaintenanceKickMsg = GenericChatCodes.processStringStatic(
				plugin.getConfig().getString(configPath + ".kick-online", DEFAULT_MMODE_MSG_KICK)
				.replace("{MMODE}", strMaintenanceMode));
			strMaintenanceJoinMsg = GenericChatCodes.processStringStatic(
				plugin.getConfig().getString(configPath + ".block-join",  DEFAULT_MMODE_MSG_JOIN)
				.replace("{MMODE}", strMaintenanceMode));
		}
	}
	@Override
	public String  getMaintenancePingMsg()
	{
		return isInMaintenance() ? strMaintenancePingMsg : "";
	}
	@Override
	public String  getMaintenanceKickMsg()
	{
		return isInMaintenance() ? strMaintenanceKickMsg : "";
	}
	@Override
	public String  getMaintenanceJoinMsg()
	{
		return isInMaintenance() ? strMaintenanceJoinMsg : "";
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
	public boolean isUsingAncestorPrefixes()
	{
		return bUsingAncestorPrefixes;
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
	public boolean areInsecureCommandsDisabled()
	{
		return bDisableDatabaseEdits;
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
	public TranslationProvider getTranslationProvider()
	{
		final File langFile = new File(plugin.getDataFolder(), language + ".yml");
		final YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
		return new TranslationProvider()
		{
			@Override
			public String getString(String path)
			{
				return langConfig.getString(path, path);
			}
		};
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
	@Override
	public Map<String, Integer> getSlotLimits()
	{
		final FileConfiguration config = plugin.getConfig();
		final ConfigurationSection limits = config.getConfigurationSection("settings.slot-limits");
		final Map<String, Integer> result = new HashMap<>();
		for(String limit : limits.getKeys(false))
			result.put(limit, limits.getInt(limit));
		return result;
	}
}
