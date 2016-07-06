package ru.simsonic.rscPermissions.API;

import java.util.Map;
import ru.simsonic.rscCommonsLibrary.ConnectionMySQL.ConnectionParams;

public interface Settings
{
	public static final String  UPDATER_URL      = "http://simsonic.github.io/rscPermissions/latest.json";
	public static final String  CHAT_PREFIX      = "{GOLD}[rscp] {_LS}";
	public static final String  SEPARATOR        = ".";
	public static final String  SEPARATOR_REGEXP = "\\.";
	public static final String  INSTANTIATOR     = "?";
	public static final String  TEXT_INHERITER   = "%";
	public static final char    GROUP_LEVEL_TAB  = '┏';
	public static final String  SPLITTED_ID_SEP  = ":";
	public void    onLoad();
	public void    onEnable();
	public String  getDefaultGroup();
	public boolean isDefaultForever();
	public boolean isAsteriskOP();
	public boolean isUsingAncestorPrefixes();
	public boolean isInMaintenance();
	public String  getMaintenanceMode();
	public void    setMaintenanceMode(String mode);
	public String  getMaintenancePingMsg();
	public String  getMaintenanceKickMsg();
	public String  getMaintenanceJoinMsg();
	public boolean isUseResidence();
	public boolean isUseWorldGuard();
	public long    getRegionFinderGranularity();
	public int     getAutoReloadDelayTicks();
	public boolean isUseMetrics();
	public Map<String, Integer> getSlotLimits();
	public TranslationProvider  getTranslationProvider();
	public ConnectionParams     getConnectionParams();
}
