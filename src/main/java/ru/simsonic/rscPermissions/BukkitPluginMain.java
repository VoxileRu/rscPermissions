package ru.simsonic.rscPermissions;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Backends.BackendJson;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Bukkit.BukkitEventListener;
import ru.simsonic.rscPermissions.Bukkit.BukkitPermissionManager;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.Bukkit.BukkitRegionProviders;
import ru.simsonic.rscPermissions.Bukkit.Commands.BukkitCommands;
import ru.simsonic.rscPermissions.Bukkit.RegionUpdateObserver;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Engine.Phrases;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public final class BukkitPluginMain extends JavaPlugin
{
	public  static final Logger consoleLog = Bukkit.getLogger();
	public  final BukkitPluginConfiguration settings = new BukkitPluginConfiguration(this);
	public  final BridgeForBukkitAPI bridgeForBukkit = new BridgeForBukkitAPI(this);
	public  final BukkitEventListener bukkitListener = new BukkitEventListener(this);
	public  final BackendJson localStorage = new BackendJson(getDataFolder());
	public  final BackendDatabase connection = new BackendDatabase(consoleLog);
	public  final InternalCache internalCache = new InternalCache();
	public  final BukkitPermissionManager permissionManager = new BukkitPermissionManager(this);
	public  final BukkitRegionProviders regionListProvider = new BukkitRegionProviders(this);
	private final RegionUpdateObserver regionUpdateObserver = new RegionUpdateObserver(this);
	public  final BukkitCommands commandHelper = new BukkitCommands(this);
	private MetricsLite metrics;
	@Override
	public void onLoad()
	{
		Phrases.extractTranslations(getDataFolder());
		settings.onLoad();
		consoleLog.log(Level.INFO, "[rscp] This server`s ID is \"{0}\". You can change it in server.properties.", getServer().getServerId());
		consoleLog.log(Level.INFO, "[rscp] rscPermissions has been loaded.");
	}
	@Override
	public void onEnable()
	{
		settings.readSettings();
		internalCache.setDefaultGroup(
			settings.getDefaultGroup(),
			settings.isDefaultForever());
		Phrases.applyTranslation(settings.getTranslationProvider());
		// Restore temporary cached data from json files
		final DatabaseContents contents = localStorage.retrieveContents();
		contents.filterServerId(getServer().getServerId()).filterLifetime();
		internalCache.fill(contents);
		consoleLog.log(Level.INFO,
			"[rscp] Loaded {0} entity, {1} permission and {2} inheritance rows from local cache.", new Integer[]
			{
				contents.entities.length,
				contents.permissions.length,
				contents.inheritance.length,
			});
		// Integrate Metrics
		if(settings.isUseMetrics())
			try
			{
				metrics = new MetricsLite(this);
				metrics.start();
				consoleLog.info(Phrases.PLUGIN_METRICS.toString());
			} catch(IOException ex) {
				consoleLog.log(Level.INFO, "[rscp][Metrics] Exception: {0}", ex);
			}
		// Register event's dispatcher
		getServer().getPluginManager().registerEvents(bukkitListener, this);
		regionUpdateObserver.registerListeners();
		// Integrate Vault
		bridgeForBukkit.setupVault();
		// WorldGuard, Residence and other possible region list providers
		regionListProvider.integrate();
		// Start all needed parallel threads as daemons
		permissionManager.startDeamon();
		regionUpdateObserver.startDeamon();
		// Connect to database and initiate data fetching
		connection.initialize(settings.getConnectionParams());
		commandHelper.threadFetchDatabaseContents.startDeamon();
		// Done
		consoleLog.info(Phrases.PLUGIN_ENABLED.toString());
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
		consoleLog.info(Phrases.PLUGIN_DISABLED.toString());
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
				commandHelper.threadFetchDatabaseContents.startDeamon();
			}
		}, delay);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try
		{
			switch(cmd.getName().toLowerCase())
			{
			case "rscp":
				commandHelper.onCommandHub(sender, args);
				break;
			}
		} catch(CommandAnswerException ex) {
			for(String answer : ex.getMessageArray())
				sender.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix + answer));
		}
		return true;
	}
}
