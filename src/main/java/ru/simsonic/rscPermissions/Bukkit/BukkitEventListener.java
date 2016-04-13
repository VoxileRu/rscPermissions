package ru.simsonic.rscPermissions.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class BukkitEventListener implements Listener
{
	private final BukkitPluginMain rscp;
	private Map<String, Integer> slotLimits = Collections.emptyMap();
	public BukkitEventListener(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	public void onEnable()
	{
		slotLimits = rscp.settings.getSlotLimits();
	}
	@EventHandler
	public void onPlayerAsyncPreLogin(AsyncPlayerPreLoginEvent event)
	{
		final ArrayList<String> identifiers = new ArrayList<>();
		try
		{
			identifiers.add(event.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		try
		{
			identifiers.add(event.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		identifiers.add(event.getAddress().getHostAddress());
		// Resolution
		final ResolutionResult resolution = rscp.internalCache.resolvePlayer(identifiers.toArray(new String[identifiers.size()]));
		// Maintenance mode limits
		if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED)
			processMaintenanceLogin(event, resolution);
		// Empty slots limits
		if(event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED)
			processLimitedSlotsLogin(event, resolution);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		rscp.permissionManager.recalculatePlayer(event.getPlayer());
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		// Inform admins about updates
		if(player.hasPermission("rscp.admin"))
			rscp.updating.onAdminJoin(player, true);
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
			event.setMotd(rscp.settings.getMaintenancePingMsg());
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
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, rscp.settings.getMaintenanceJoinMsg());
	}
	private void processLimitedSlotsLogin(AsyncPlayerPreLoginEvent event, ResolutionResult resolution)
	{
		boolean allowed = true;
		int freeSlots = rscp.getServer().getMaxPlayers() - Tools.getOnlinePlayers().size();
		for(Map.Entry<String, Integer> limit : slotLimits.entrySet())
		{
			// Ignore non-positive values
			if(limit.getValue() <= 0)
				continue;
			boolean permission = resolution.hasPermission("rscp.limits." + limit.getKey());
			if(permission)
			{
				allowed = true;
				// "Harder" limit allows to skip "lighter" checks
				if(freeSlots > limit.getValue())
					break;
			} else {
				// Block otherwise
				if(freeSlots < limit.getValue())
					allowed = false;
			}
		}
		if(allowed)
		{
			event.allow();
			return;
		}
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Phrases.SERVER_IS_FULL.toString());
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
				final String kick = rscp.settings.getMaintenanceKickMsg();
				for(Player player : Tools.getOnlinePlayers())
				{
					if(player.hasPermission("rscp.maintenance.*"))
						continue;
					if(player.hasPermission("rscp.maintenance." + rscp.settings.getMaintenanceMode()))
						continue;
					player.kickPlayer(kick);
				}
			}
		});
	}
}
