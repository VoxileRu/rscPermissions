package ru.simsonic.rscPermissions.API;

import java.util.Map;
import ru.simsonic.rscCommonsLibrary.ConnectionMySQL.ConnectionParams;

public interface Settings
{
	public static final String  UPDATER_URL      = "http://simsonic.github.io/rscPermissions/latest.json";
	public static final String  CHAT_PREFIX      = "{GOLD}[rscp] {_LS}";
	public static final String  UPDATE_CMD       = "/rscp update do";
	
	public static final String  TEXT_INHERITER   = "%";
	public static final char    SHOW_GROUP_LEVEL = '┏';
	public static final String  INSTANTIATOR     = "?";
	public static final String  INSTANCE_SEP     = ".";
	public static final String  REGEXP_SEPARATOR = "\\.";
	public static final String  REGEXP_ROW_SPLIT = "\\s*[;,\\r\\n\\s]+\\s*";
	public static final String  REGEXP_NICKNAME  = "^[\\*a-zA-Z0-9_-]{3,16}$";
	public static final String  REGEXP_UUID_DASH = "^(?:[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})$";
	public static final String  REGEXP_UUID      = "^([A-Fa-f0-9]{8})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{12})$";
	public static final String  REGEXP_IPADDR    = "^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)$";
	public static final String  REGEXP_SUBNET    = "^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])"
		+ "(?:/([0-9]|[1-2][0-9]|3[0-2]))$";
	
	public static final String DEFAULT_MMODE_MSG_PING = "{_LR}Maintenance mode";
	public static final String DEFAULT_MMODE_MSG_KICK = "{_LR}Sorry! Server is going into maintenance mode.";
	public static final String DEFAULT_MMODE_MSG_JOIN = "{_LR}You are not allowed to enter when maintenance is on.";
	
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
