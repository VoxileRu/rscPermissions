package ru.simsonic.rscPermissions.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.InternalCache.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class BukkitEventListener implements Listener
{
	private final BukkitPluginMain rscp;
	public BukkitEventListener(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	@EventHandler
	public void onPlayerAsyncPreLogin(AsyncPlayerPreLoginEvent event)
	{
		final ResolutionResult resolution = rscp.internalCache.resolvePlayer(new String[]
		{
			event.getName(),
			event.getUniqueId().toString(),
			event.getAddress().getHostAddress(),
		}, rscp.getServer().getServerId());
		processMaintenanceLogin(event, resolution);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		rscp.permissionManager.recalculatePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerExp(PlayerLevelChangeEvent event)
	{
		rscp.permissionManager.recalculatePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerLevel(PlayerExpChangeEvent event)
	{
		rscp.permissionManager.recalculatePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		final Player player = event.getPlayer();
		rscp.permissionManager.removePlayer(player);
		rscp.regionListProvider.removePlayer(player);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		final Player player = event.getPlayer();
		rscp.permissionManager.removePlayer(player);
		rscp.regionListProvider.removePlayer(player);
	}
	@org.bukkit.event.EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		if(rscp.settings.isInMaintenance())
		{
			final String motd = GenericChatCodes.processStringStatic(
				"Server is under maintenance");
			event.setMotd(motd);
		}
	}
	private void processMaintenanceLogin(AsyncPlayerPreLoginEvent event, ResolutionResult resolution)
	{
		if("".equals(rscp.settings.getMaintenanceMode()))
		{
			event.allow();
			return;
		}
		final String permissionMM  = "rscp.maintenance." + (rscp.settings.getMaintenanceMode());
		final String permissionAll = "rscp.maintenance.*";
		if(resolution.hasPermission(permissionMM) || resolution.hasPermission(permissionAll))
		{
			event.allow();
			return;
		}
		final String kickMsg = GenericChatCodes.processStringStatic(
			"{_YL}Server is in maintenance mode\nPlease try to connect later...");
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMsg);
	}
	public void setMaintenanceMode(String mMode)
	{
		rscp.settings.setMaintenanceMode(mMode);
		if(!rscp.settings.isInMaintenance())
			return;
		rscp.getServer().getScheduler().runTask(rscp, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getServer().getOnlinePlayers())
				{
					if(player.hasPermission("rscp.maintenance.*"))
						continue;
					if(player.hasPermission("rscp.maintenance." + rscp.settings.getMaintenanceMode()))
						continue;
					final String kick = GenericChatCodes.processStringStatic(
						"{_YL}Server is going into maintenance mode. Please connect later.");
					player.kickPlayer(kick);
				}
			}
		});
	}
}
