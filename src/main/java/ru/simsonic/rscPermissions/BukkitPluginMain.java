package ru.simsonic.rscPermissions;
import ru.simsonic.rscPermissions.Bukkit.BukkitRegionProviders;
import ru.simsonic.rscPermissions.Bukkit.RegionUpdateObserver;
import ru.simsonic.rscPermissions.Bukkit.BukkitMaintenance;
import ru.simsonic.rscPermissions.API.Settings;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscPermissions.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Backends.BackendJson;
import ru.simsonic.rscPermissions.Bukkit.BukkitPermissionManager;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.Bukkit.PlayerEventsListener;
import ru.simsonic.rscPermissions.InternalCache.InternalCache;
import ru.simsonic.rscUtilityLibrary.CommandProcessing.CommandAnswerException;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public final class BukkitPluginMain extends JavaPlugin
{
	private static final String chatPrefix = "{YELLOW}[rscp] {GOLD}";
	public  static final Logger consoleLog = Bukkit.getLogger();
	public  final Settings settings = new BukkitPluginConfiguration(this);
	private final BridgeForBukkitAPI bridgeForBukkit = new BridgeForBukkitAPI(this);
	public  final PlayerEventsListener bukkitListener = new PlayerEventsListener(this);
	public  final BackendJson fileCache = new BackendJson(getDataFolder());
	public  final BackendDatabase connection = new BackendDatabase(this);
	public  final InternalCache internalCache = new InternalCache(this);
	public  final BukkitPermissionManager permissionManager = new BukkitPermissionManager(this);
	public  final BukkitRegionProviders regionListProvider = new BukkitRegionProviders(this);
	private final RegionUpdateObserver regionUpdateObserver = new RegionUpdateObserver(this);
	public  final CommandHelper commandHelper = new CommandHelper(this);
	public  final BukkitMaintenance maintenance = new BukkitMaintenance(this);
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
		connection.initialize(settings.getConnectionParams());
		// Register event's dispatcher
		getServer().getPluginManager().registerEvents(maintenance, this);
		regionUpdateObserver.registerListeners();
		// WorldGuard, Residence and other possible region list providers
		regionListProvider.integrate();
		// Start all needed threads
		internalCache.setDefaultGroup(settings.getDefaultGroup());
		permissionManager.start();
		regionUpdateObserver.start();
		commandHelper.threadFetchTablesData();
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
		internalCache.clear();
		connection.disconnect();
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
				commandHelper.threadFetchTablesData.start();
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
				sender.sendMessage(GenericChatCodes.processStringStatic(chatPrefix + answer));
		} catch(NullPointerException ex) {
			// These will never occur! I hope...
		}
		return true;
	}
	public void formattedMessage(CommandSender sender, String message)
	{
		if(message == null || "".equals(message))
			return;
		message = GenericChatCodes.processStringStatic(chatPrefix + message);
		sender.sendMessage(message);
	}
	@SuppressWarnings({"DeadBranch", "UnusedAssignment"})
	public static void main(String args[])
	{
		System.out.println("rscPermissions - Bukkit superperms plugin © SimSonic");
		System.out.println("https://github.com/SimSonic/rscPermissions/");
	}
}