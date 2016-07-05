package ru.simsonic.rscPermissions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscMinecraftLibrary.AutoUpdater.BukkitUpdater;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Bukkit.BukkitEventListener;
import ru.simsonic.rscPermissions.Bukkit.BukkitPermissionManager;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.Bukkit.BukkitRegionProviders;
import ru.simsonic.rscPermissions.Bukkit.Commands.BukkitCommands;
import ru.simsonic.rscPermissions.Bukkit.RegionUpdateObserver;
import ru.simsonic.rscPermissions.Engine.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Engine.Backends.BackendJson;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Engine.Phrases;

public final class BukkitPluginMain extends JavaPlugin
{
	public  static final Logger consoleLog = Bukkit.getLogger();
	public  final Settings      settings   = new BukkitPluginConfiguration(this);
	public  final BukkitUpdater updating   = new BukkitUpdater(this, Settings.UPDATER_URL, Settings.CHAT_PREFIX);
	public  final BackendJson     localStorage  = new BackendJson(getDataFolder());
	public  final BackendDatabase connection    = new BackendDatabase(consoleLog);
	public  final InternalCache   internalCache = new InternalCache();
	public  final BukkitCommands  commandHelper = new BukkitCommands(this);
	public  final BridgeForBukkitAPI      bridgeForBukkit      = new BridgeForBukkitAPI(this);
	public  final BukkitEventListener     bukkitListener       = new BukkitEventListener(this);
	public  final BukkitPermissionManager permissionManager    = new BukkitPermissionManager(this);
	public  final BukkitRegionProviders   regionListProvider   = new BukkitRegionProviders(this);
	private final RegionUpdateObserver    regionUpdateObserver = new RegionUpdateObserver(this);
	private MetricsLite metrics;
	@Override
	public void onLoad()
	{
		Phrases.extractTranslations(getDataFolder());
		settings.onLoad();
		consoleLog.log(Level.INFO, "[rscp] serverId value is set to \"{0}\". You can change it in server.properties.", getServer().getServerId());
		consoleLog.log(Level.INFO, "[rscp] rscPermissions has been loaded.");
	}
	@Override
	public void onEnable()
	{
		// Read settings and setup components
		settings.onEnable();
		updating.onEnable();
		bukkitListener.onEnable();
		internalCache.setDefaultGroup(
			settings.getDefaultGroup(),
			settings.isDefaultForever(),
			settings.isUsingAncestorPrefixes());
		Phrases.applyTranslation(settings.getTranslationProvider());
		// Restore temporary cached data from json files
		final DatabaseContents contents = localStorage.retrieveContents();
		contents.filterServerId(getServer().getServerId()).filterLifetime();
		internalCache.fill(contents);
		getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(
			(Settings.CHAT_PREFIX + Phrases.FETCHED_LOCAL_CACHE.toString())
			.replace("{:E}", String.valueOf(contents.entities.length))
			.replace("{:P}", String.valueOf(contents.permissions.length))
			.replace("{:I}", String.valueOf(contents.inheritance.length))));
		// Integrate Metrics
		if(settings.isUseMetrics())
			try
			{
				metrics = new MetricsLite(this);
				metrics.start();
				consoleLog.info(Phrases.PLUGIN_METRICS.toString());
			} catch(IOException ex) {
				consoleLog.log(Level.WARNING, "[rscp][Metrics] Exception: {0}", ex);
			}
		// Register event's dispatcher
		getServer().getPluginManager().registerEvents(bukkitListener, this);
		regionUpdateObserver.registerListeners();
		// Integrate Vault and WEPIF
		bridgeForBukkit.setupVault();
		getServer().getScheduler().runTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				bridgeForBukkit.setupWEPIF();
			}
		});
		// WorldGuard, Residence and other possible region list providers
		regionListProvider.integrate();
		// Start all needed parallel threads as daemons
		permissionManager.startDeamon();
		regionUpdateObserver.startDeamon();
		// Connect to database and initiate data fetching
		connection.initialize(settings.getConnectionParams());
		if(settings.getAutoReloadDelayTicks() > 0)
			commandHelper.threadFetchDatabaseContents.startDeamon();
		// Done
		for(Player online : Tools.getOnlinePlayers())
			if(online.hasPermission("rscm.admin"))
				updating.onAdminJoin(online, false);
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
		nAutoUpdaterTaskId = delay > 0
			? scheduler.scheduleSyncDelayedTask(this, new Runnable()
				{
					@Override
					public void run()
					{
						commandHelper.threadFetchDatabaseContents.startDeamon();
					}
				}, delay)
			: -1;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender != null)
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
					sender.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + answer));
			}
		return true;
	}
}
