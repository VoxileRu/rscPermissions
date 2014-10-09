package ru.simsonic.rscPermissions;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import ru.simsonic.utilities.LanguageUtility;

public class MaintenanceMode implements Listener
{
	private final MainPluginClass plugin;
	public MaintenanceMode(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	@org.bukkit.event.EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		if(plugin.settings.isInMaintenance())
		{
			String motd = "Server is under maintenance";
			motd = plugin.getConfig().getString("language.maintenance.locked.default.motd", motd);
			motd = plugin.getConfig().getString("language.maintenance.locked." + plugin.settings.getMaintenanceMode() + ".motd", motd);
			motd = LanguageUtility.processStringStatic(motd);
			if(!"".equals(motd))
				event.setMotd(motd);
		}
	}
	@org.bukkit.event.EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event)
	{
		if("".equals(plugin.settings.getMaintenanceMode()))
		{
			event.allow();
			return;
		}
		final String permissionAll = "rscp.maintenance.*";
		final String permission_mm = "rscp.maintenance." + (plugin.settings.getMaintenanceMode());
		final HashMap<String, Boolean> permissions = new HashMap<>();
		try
		{
			final String name = event.getName();
			plugin.cache2.resolvePlayer(name);
			// permissions.putAll(plugin.cache.mapPermissions.get(name));
		} catch(RuntimeException ex) {
		}
		try
		{
			final UUID uuid = event.getUniqueId();
			final String userFriendlyUniqueId = uuid.toString().replace("-", "").toLowerCase();
			plugin.cache2.resolvePlayer(userFriendlyUniqueId);
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
		kickMsg = plugin.getConfig().getString("language.maintenance.locked.default.motd", kickMsg);
		kickMsg = plugin.getConfig().getString("language.maintenance.locked." + plugin.settings.getMaintenanceMode() + ".motd", kickMsg);
		kickMsg = LanguageUtility.processStringStatic(kickMsg);
		event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMsg);
	}
	public void setMaintenanceMode(String mMode)
	{
		plugin.settings.setMaintenanceMode(mMode);
		if(!plugin.settings.isInMaintenance())
			return;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				for(Player player : Bukkit.getServer().getOnlinePlayers())
					if(player != null)
					{
						if(player.hasPermission("rscp.maintenance.*"))
							continue;
						if(player.hasPermission("rscp.maintenance." + plugin.settings.getMaintenanceMode()))
							continue;
						String kick = "{_YL}Server is going into maintenance mode";
						kick = plugin.getConfig().getString("language.maintenance.locked.default.kick", kick);
						kick = plugin.getConfig().getString("language.maintenance.locked." + plugin.settings.getMaintenanceMode() + ".kick", kick);
						kick = LanguageUtility.processStringStatic(kick);
						player.kickPlayer(kick);
					}
			}
		});
	}
}
