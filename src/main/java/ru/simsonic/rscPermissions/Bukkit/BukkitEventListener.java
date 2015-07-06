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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;
import ru.simsonic.rscPermissions.BukkitPluginMain;
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
	private void processLimitedSlotsLogin(AsyncPlayerPreLoginEvent event, ResolutionResult resolution)
	{
		boolean allowed = true;
		int freeSlots = rscp.getServer().getMaxPlayers() - Tools.getOnlinePlayers().size();
		for(Map.Entry<String, Integer> limit : slotLimits.entrySet())
		{
			boolean permission = resolution.hasPermission("rscp.limits." + limit.getKey());
			if(permission)
			{
				// Если есть разрешение
				allowed = true;
				// Если лимит "более жёсткий", то он позволяет вход в текущем состоянии
				if(freeSlots > limit.getValue())
					break;
			} else {
				// Если разрешения нет
				if(freeSlots < limit.getValue())
					allowed = false;
			}
		}
		if(allowed)
		{
			event.allow();
			return;
		}
		final String kickMsg = GenericChatCodes.processStringStatic(
			"{_LR}Server is too full to allow you enter.\n{_YL}Please try to connect later...");
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
				for(Player player : Tools.getOnlinePlayers())
				{
					if(player.hasPermission("rscp.maintenance.*"))
						continue;
					if(player.hasPermission("rscp.maintenance." + rscp.settings.getMaintenanceMode()))
						continue;
					final String kick = GenericChatCodes.processStringStatic(
						"{_LR}Server is going into maintenance mode.\n{_YL}Please try to connect later...");
					player.kickPlayer(kick);
				}
			}
		});
	}
}
