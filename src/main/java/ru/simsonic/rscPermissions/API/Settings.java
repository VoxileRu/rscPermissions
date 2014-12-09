package ru.simsonic.rscPermissions.API;
import ru.simsonic.rscPermissions.ConnectionHelper;

public interface Settings
{
	public static final String instantiator = "?";
	public static final String separator = ".";
	public static final String separatorRegExp = "\\.";
	public void    onLoad();
	public void    readSettings();
	public String  getDefaultGroup();
	public boolean isDefaultForever();
	public boolean isAsteriskOP();
	public boolean isInMaintenance();
	public String  getMaintenanceMode();
	public void    setMaintenanceMode(String mode);
	public boolean isUseResidence();
	public boolean isUseWorldGuard();
	public long    getRegionFinderGranularity();
	public ConnectionHelper getConnectionChain();
	public int     getAutoReloadDelayTicks();
	public boolean isUseMetrics();
}
