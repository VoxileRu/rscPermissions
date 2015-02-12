package ru.simsonic.rscPermissions.Bukkit;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class BukkitMaintenance implements Listener
{
	private final BukkitPluginMain rscp;
	public BukkitMaintenance(BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	public void onEnable()
	{
		rscp.getServer().getPluginManager().registerEvents(this, rscp);
	}
	@org.bukkit.event.EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		if(rscp.settings.isInMaintenance())
		{
			String motd = "Server is under maintenance";
			motd = rscp.getConfig().getString("language.maintenance.locked.default.motd", motd);
			motd = rscp.getConfig().getString("language.maintenance.locked." + rscp.settings.getMaintenanceMode() + ".motd", motd);
			motd = GenericChatCodes.processStringStatic(motd);
			if(!"".equals(motd))
				event.setMotd(motd);
		}
	}
	@org.bukkit.event.EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		if("".equals(rscp.settings.getMaintenanceMode()))
		{
			event.allow();
			return;
		}
		final String permissionAll = "rscp.maintenance.*";
		final String permission_mm = "rscp.maintenance." + (rscp.settings.getMaintenanceMode());
		final HashMap<String, Boolean> permissions = new HashMap<>();
		try
		{
			final String name = event.getName();
			rscp.internalCache.resolvePlayer(name, rscp.getServer().getServerId());
			// permissions.putAll(plugin.cache.mapPermissions.get(name));
		} catch(RuntimeException ex) {
		}
		try
		{
			final UUID uuid = event.getUniqueId();
			final String userFriendlyUniqueId = uuid.toString().replace("-", "").toLowerCase();
			rscp.internalCache.resolvePlayer(userFriendlyUniqueId, rscp.getServer().getServerId());
			// permissions.putAll(plugin.cache.mapPermissions.get(userFriendlyUniqueId));
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		for(String permission : permissions.keySet())
			if(permission.equalsIgnoreCase(permission_mm) || permission.equalsIgnoreCase(permissionAll))
			{
				event.allow();
				return;
			}
		String kickMsg = "{_YL}Server is in maintenance mode\nPlease try to connect later...";
		kickMsg = rscp.getConfig().getString("language.maintenance.locked.default.motd", kickMsg);
		kickMsg = rscp.getConfig().getString("language.maintenance.locked." + rscp.settings.getMaintenanceMode() + ".motd", kickMsg);
		kickMsg = GenericChatCodes.processStringStatic(kickMsg);
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMsg);
	}
	public void setMaintenanceMode(String mMode)
	{
		rscp.settings.setMaintenanceMode(mMode);
		if(!rscp.settings.isInMaintenance())
			return;
		rscp.getServer().getScheduler().scheduleSyncDelayedTask(rscp, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getServer().getOnlinePlayers())
					if(player != null)
					{
						if(player.hasPermission("rscp.maintenance.*"))
							continue;
						if(player.hasPermission("rscp.maintenance." + rscp.settings.getMaintenanceMode()))
							continue;
						String kick = "{_YL}Server is going into maintenance mode";
						kick = rscp.getConfig().getString("language.maintenance.locked.default.kick", kick);
						kick = rscp.getConfig().getString("language.maintenance.locked." + rscp.settings.getMaintenanceMode() + ".kick", kick);
						kick = GenericChatCodes.processStringStatic(kick);
						player.kickPlayer(kick);
					}
			}
		});
	}
}
