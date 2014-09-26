package ru.simsonic.rscPermissions;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.rscPermissions.InternalCache.LocalCacheFunctions;
import ru.simsonic.rscPermissions.InternalCache.AsyncPlayerInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mcstats.MetricsLite;
import ru.simsonic.rscPermissions.InternalCache.BrandNewCache;
import ru.simsonic.utilities.CommandAnswerException;
import ru.simsonic.utilities.LanguageUtility;
import ru.simsonic.utilities.MovingPlayersCatcher;

public final class MainPluginClass extends JavaPlugin implements Listener
{
	private static final String chatPrefix = "{_YL}[rscp] {GOLD}";
	public  static final Logger consoleLog = Logger.getLogger("Minecraft");
	public  final Settings settings = new BukkitPluginConfiguration(this);
	public  final LocalCacheFunctions cache = new LocalCacheFunctions(this);
	public  final CommandHelper commandHelper = new CommandHelper(this);
	public  final MaintenanceMode maintenance = new MaintenanceMode(this);
	public  final RegionListProviders regionListProvider = new RegionListProviders(this);
	private final MovingPlayersCatcher movedPlayers = new MovingPlayersCatcher();
	public  ConnectionHelper connectionList;
	public  Thread threadPermissions;
	private MetricsLite metrics;
	public final HashMap<Player, PermissionAttachment> attachments = new HashMap<>();
	public final LinkedBlockingQueue<AsyncPlayerInfo> recalculatingPlayers = new LinkedBlockingQueue<>();
	// private final HashSet<String> verbosePlayers = new HashSet<>();
	public  final rscpAPI API = new rscpAPI(this);
	private final BrandNewCache newCache = new BrandNewCache(this);
	private final BridgeForBukkitAPI api = new BridgeForBukkitAPI(this);
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
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(maintenance, this);
		getServer().getPluginManager().registerEvents(movedPlayers, this);
		// WorldGuard, Residence and other possible region list providers
		regionListProvider.integrate();
		// Start all needed threads
		cache.setDefaultGroup(settings.getDefaultGroup());
		StartRecalcThread();
		RegionFinderThreadStart();
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
				consoleLog.log(Level.INFO, "[rscp][Metrics] Exception: {0}", ex.getLocalizedMessage());
			}
		}
		consoleLog.info("[rscp] rscPermissions has been successfully enabled.");
	}
	@Override
	public void onDisable()
	{
		getServer().getServicesManager().unregisterAll(this);
		RegionFinderThreadStop();
		StopRecalcThread();
		cache.clear();
		connectionList.Disconnect();
		connectionList = null;
		regionListProvider.deintegrate();
		metrics = null;
		consoleLog.info("[rscp] rscPermissions has been disabled.");
	}
	private Thread hThreadRegionFinder = null;
	private void RegionFinderThreadStart()
	{
		RegionFinderThreadStop();
		hThreadRegionFinder = new Thread()
		{
			@Override
			public void run()
			{
				this.setName("rscp:RegionFinder");
				this.setPriority(MIN_PRIORITY);
				long granularity = settings.getRegionFinderGranularity();
				if(granularity < 20)
					granularity = 20;
				if(granularity > 10000)
					granularity = 10000;
				try
				{
					for(; !Thread.interrupted(); Thread.sleep(granularity))
						for(Player player : movedPlayers.getMovedPlayersAsync())
							if(regionListProvider.isRegionListChanged(player))
								cache.calculatePlayerPermissions(player);
				} catch(InterruptedException ex) {
				}
			}
		};
		hThreadRegionFinder.start();
	}
	public void RegionFinderThreadStop()
	{
		if(hThreadRegionFinder == null)
			return;
		try
		{
			hThreadRegionFinder.interrupt();
			hThreadRegionFinder.join();
			hThreadRegionFinder = null;
		} catch(InterruptedException ex) {
			consoleLog.log(Level.SEVERE, "[rscp] Exception in RegionFinderThread(): {0}", ex.getLocalizedMessage());
		}
	}
	public void StopRecalcThread()
	{
		if(threadPermissions != null)
			try
			{
				threadPermissions.interrupt();
				threadPermissions.join();
				threadPermissions = null;
			} catch(InterruptedException ex) {
				consoleLog.log(Level.WARNING, "[rscp] Exception in StopRecalcThread: {0}", ex.getLocalizedMessage());
			}
	}
	public void StartRecalcThread()
	{
		StopRecalcThread();
		final MainPluginClass plugin = this;
		threadPermissions = new Thread()
		{
			@Override
			public void run()
			{
				setName("rscp:PermCalculator");
				setPriority(Thread.MIN_PRIORITY);
				try
				{
					AsyncPlayerInfo p2rc;
					while((p2rc = recalculatingPlayers.take()) != null)
					{
						// Build inheritance tree and calculate permissions
						final HashMap<String, Boolean> permissions = cache.treeToPermissions(p2rc);
						// Schedule attachment update
						final Player player = p2rc.player;
						getServer().getScheduler().runTask(plugin, new Runnable()
						{
							@Override
							public void run()
							{
								PermissionAttachment attachment = attachments.get(player);
								if(attachment != null)
									attachment.remove();
								attachment = player.addAttachment(plugin);
								attachments.put(player, attachment);
								for(String permission : permissions.keySet())
									attachment.setPermission(permission, permissions.get(permission));
								if(settings.isAsteriskOP())
								{
									final Boolean asteriskValue = permissions.get("*");
									player.setOp(asteriskValue != null ? asteriskValue : false);
								}
							}
						});
					}
				} catch(InterruptedException ex) {
				}
				recalculatingPlayers.clear();
			}
		};
		threadPermissions.start();
	}
	public void recalculateOnlinePlayers()
	{
		for(Player player : Bukkit.getServer().getOnlinePlayers())
			if(player != null)
				cache.calculatePlayerPermissions(player);
		rescheduleAutoUpdate();
	}
	private int  nAutoUpdaterTaskId = -1;
	private void rescheduleAutoUpdate()
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
	@org.bukkit.event.EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		final Player player = event.getPlayer();
		String name = event.getPlayer().getName();
		final HashMap<String, Boolean> pending = cache.mapPermissions.get(name);
		if(pending != null)
		{
			final PermissionAttachment attachment = player.addAttachment(this);
			for(String permission : pending.keySet())
				attachment.setPermission(permission, pending.get(permission));
			attachments.put(player, attachment);
		}
		cache.calculatePlayerPermissions(player);
	}
	@org.bukkit.event.EventHandler
	public void onPlayerExp(PlayerLevelChangeEvent event)
	{
		cache.calculatePlayerPermissions(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	public void onPlayerLevel(PlayerExpChangeEvent event)
	{
		cache.calculatePlayerPermissions(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	public void onPlayerKick(PlayerQuitEvent event)
	{
		attachments.remove(event.getPlayer());
		regionListProvider.removePlayer(event.getPlayer());
	}
	@org.bukkit.event.EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		attachments.remove(event.getPlayer());
		regionListProvider.removePlayer(event.getPlayer());
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
	}
}