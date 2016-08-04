package ru.simsonic.rscPermissions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscMinecraftLibrary.AutoUpdater.BukkitUpdater;
import ru.simsonic.rscMinecraftLibrary.Bukkit.CommandAnswerException;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Bukkit.BukkitFetching;
import ru.simsonic.rscPermissions.Bukkit.BukkitListener;
import ru.simsonic.rscPermissions.Bukkit.BukkitPermissionManager;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.Bukkit.BukkitRegionProviders;
import ru.simsonic.rscPermissions.Bukkit.Commands.BukkitCommands;
import ru.simsonic.rscPermissions.Bukkit.RegionUpdateObserver;
import ru.simsonic.rscPermissions.Engine.Backends.BackendJson;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseEditor;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Engine.Phrases;

public final class BukkitPluginMain extends JavaPlugin
{
	public  final static Logger           consoleLog        = Bukkit.getLogger();
	public  final Settings                settings          = new BukkitPluginConfiguration(this);
	public  final BukkitUpdater           updating          = new BukkitUpdater(this, Settings.UPDATER_URL, Settings.CHAT_PREFIX, Settings.UPDATE_CMD);
	public  final BridgeForBukkitAPI      rscpAPIs          = new BridgeForBukkitAPI(this);
	public  final BukkitListener          listener          = new BukkitListener(this);
	public  final BukkitCommands          commands          = new BukkitCommands(this);
	public  final BackendJson             localStorage      = new BackendJson(getDataFolder());
	public  final DatabaseEditor          connection        = new DatabaseEditor(this);
	public  final BukkitFetching          fetching          = new BukkitFetching(this);
	public  final InternalCache           internalCache     = new InternalCache();
	public  final BukkitPermissionManager permissionManager = new BukkitPermissionManager(this);
	public  final BukkitRegionProviders   regionProviders   = new BukkitRegionProviders(this);
	private final RegionUpdateObserver    regionObserver    = new RegionUpdateObserver(this);
	private MetricsLite metrics;
	public BukkitPluginMain()
	{
	}
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
		listener.onEnable();
		internalCache.setDefaultGroup(
			settings.getDefaultGroup(),
			settings.isDefaultForever(),
			settings.isUsingAncestorPrefixes());
		internalCache.setCurrentServerId(getServer().getServerId());
		Phrases.applyTranslation(settings.getTranslationProvider());
		// Restore temporary cached data from json files
		final DatabaseContents contents = localStorage.retrieveContents();
		contents.filterServerId(getServer().getServerId()).filterLifetime();
		internalCache.fill(contents);
		final ConsoleCommandSender console = getServer().getConsoleSender();
		console.sendMessage(Phrases.FETCHED_LOCAL_CACHE.toPlayer()
			.replace("{:E}", String.valueOf(contents.entities.length))
			.replace("{:P}", String.valueOf(contents.permissions.length))
			.replace("{:I}", String.valueOf(contents.inheritance.length)));
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
		getServer().getPluginManager().registerEvents(listener, this);
		regionObserver.registerListeners();
		// Integrate Vault and WEPIF
		rscpAPIs.setupVault();
		getServer().getScheduler().runTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				rscpAPIs.setupWEPIF();
			}
		});
		// WorldGuard, Residence and other possible region list providers
		regionProviders.integrate();
		// Start all needed parallel threads as daemons
		permissionManager.startDeamon();
		regionObserver.startDeamon();
		// Connect to database and initiate data fetching
		connection.initialize(settings.getConnectionParams());
		if(settings.getAutoReloadDelayTicks() > 0)
			fetching.startDeamon();
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
		regionObserver.stop();
		permissionManager.stop();
		internalCache.clear();
		connection.disconnect();
		regionProviders.deintegrate();
		if(metrics != null)
			try
			{
				metrics.disable();
			} catch(IOException ex) {
			}
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
						fetching.startDeamon();
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
					commands.onCommandHub(sender, args);
					break;
				}
			} catch(CommandAnswerException ex) {
				for(String answer : ex.getMessageArray())
					sender.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + answer));
			}
		return true;
	}
}
