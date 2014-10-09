package ru.simsonic.rscPermissions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscPermissions.Bukkit.BukkitPermissions;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.Bukkit.PlayerEventsListener;
import ru.simsonic.rscPermissions.InternalCache.BrandNewCache;
import ru.simsonic.utilities.CommandAnswerException;
import ru.simsonic.utilities.LanguageUtility;

public final class MainPluginClass extends JavaPlugin
{
	private static final String chatPrefix = "{_YL}[rscp] {GOLD}";
	public  static final Logger consoleLog = Logger.getLogger("Minecraft");
	public  final Settings settings = new BukkitPluginConfiguration(this);
	private final BridgeForBukkitAPI api = new BridgeForBukkitAPI(this);
	public  final PlayerEventsListener listener = new PlayerEventsListener(this);
	public  final BrandNewCache cache2 = new BrandNewCache(this);
	public  final BukkitPermissions permissionManager = new BukkitPermissions(this);
	public  final RegionListProviders regionListProvider = new RegionListProviders(this);
	private final RegionUpdateObserver regionUpdateObserver = new RegionUpdateObserver(this);
	public  final CommandHelper commandHelper = new CommandHelper(this);
	public  final MaintenanceMode maintenance = new MaintenanceMode(this);
	public  ConnectionHelper connectionList;
	private MetricsLite metrics;
	@Override
	public void onLoad()
	{
		settings.onLoad();
		consoleLog.log(Level.INFO, "[rscp] This server\'s ID is \'{0}\'. You can change it in server.properties.", getServer().getServerId());
		consoleLog.log(Level.INFO, "[rscp] rscPermissions has been loaded.");
	}
	@Override
	public void onEnable()
	{
		settings.readSettings();
		connectionList = settings.getConnectionChain();
		if(connectionList == null)
		{
			consoleLog.log(Level.WARNING, "[rscp] No MySQL servers were specified in config.yml, disabling...");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// Register event's dispatcher
		getServer().getPluginManager().registerEvents(maintenance, this);
		regionUpdateObserver.registerListeners();
		// WorldGuard, Residence and other possible region list providers
		regionListProvider.integrate();
		// Start all needed threads
		cache2.setDefaultGroup(settings.getDefaultGroup());
		permissionManager.start();
		regionUpdateObserver.start();
		connectionList.threadFetchTablesData();
		// Metrics
		if(settings.isUseMetrics())
		{
			try
			{
				metrics = new MetricsLite(this);
				metrics.start();
				consoleLog.info("[rscp] Metrics enabled.");
			} catch(IOException ex) {
				consoleLog.log(Level.INFO, "[rscp][Metrics] Exception: {0}", ex);
			}
		}
		consoleLog.info("[rscp] rscPermissions has been successfully enabled.");
	}
	@Override
	public void onDisable()
	{
		getServer().getServicesManager().unregisterAll(this);
		regionUpdateObserver.stop();
		permissionManager.stop();
		// cache.clear();
		connectionList.Disconnect();
		connectionList = null;
		regionListProvider.deintegrate();
		metrics = null;
		consoleLog.info("[rscp] rscPermissions has been disabled.");
	}
	private int nAutoUpdaterTaskId = -1;
	public void scheduleAutoUpdate()
	{
		final BukkitScheduler scheduler = getServer().getScheduler();
		if(nAutoUpdaterTaskId != -1)
			scheduler.cancelTask(nAutoUpdaterTaskId);
		final int delay = settings.getAutoReloadDelayTicks();
		nAutoUpdaterTaskId = scheduler.scheduleSyncDelayedTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				connectionList.threadFetchTablesData();
			}
		}, delay);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			commandHelper.onCommand(sender, cmd, label, args);
		} catch(CommandAnswerException ex) {
			for(String answer : ex.getMessageArray())
				sender.sendMessage(LanguageUtility.processStringStatic(chatPrefix + answer));
		} catch(NullPointerException ex) {
			// These will never occur! I hope...
		}
		return true;
	}
	public void formattedMessage(CommandSender sender, String message)
	{
		if(message == null || "".equals(message))
			return;
		message = LanguageUtility.processStringStatic(chatPrefix + message);
		sender.sendMessage(message);
	}
	@SuppressWarnings({"DeadBranch", "UnusedAssignment"})
	public static void main(String args[])
	{
		System.out.println("rscPermissions - Bukkit superperms plugin © SimSonic");
		System.out.println("http://dev.bukkit.org/bukkit-plugins/rscpermissions/");
		// TEST SECTION STARTS BELOW
		MainPluginClass mpc = new MainPluginClass();
		ConnectionHelper ch = new ConnectionHelper(mpc, null);
		ch.Initialize("Test", "voxile.ru:3306/servers-shared", "server-primary", "zcHzCBFZtTv28JfG", "rscp_");
		ch.Connect();
	}
}
