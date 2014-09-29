package ru.simsonic.rscPermissions.Bukkit;
import java.util.HashMap;
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
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.MainPluginClass;

public class PlayerEventsListener implements Listener
{
	private final MainPluginClass rscp;
	public PlayerEventsListener(MainPluginClass plugin)
	{
		this.rscp = plugin;
	}
	@EventHandler
	public void onPlayerAsyncPreLogin(AsyncPlayerPreLoginEvent event)
	{
		rscp.cache2.resolvePlayer(new String[]
		{
			event.getName(),
			event.getUniqueId().toString(),
			event.getAddress().getHostAddress(),
		});
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		final Player player = event.getPlayer();
		String name = event.getPlayer().getName();
		final HashMap<String, Boolean> pending = rscp.cache.mapPermissions.get(name);
		if(pending != null)
		{
			final PermissionAttachment attachment = player.addAttachment(rscp);
			for(String permission : pending.keySet())
				attachment.setPermission(permission, pending.get(permission));
			rscp.permissionManager.attachments.put(player, attachment);
		}
		rscp.cache.calculatePlayerPermissions(player);
	}
	@EventHandler
	public void onPlayerExp(PlayerLevelChangeEvent event)
	{
		rscp.cache.calculatePlayerPermissions(event.getPlayer());
	}
	@EventHandler
	public void onPlayerLevel(PlayerExpChangeEvent event)
	{
		rscp.cache.calculatePlayerPermissions(event.getPlayer());
	}
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		rscp.permissionManager.attachments.remove(event.getPlayer());
		rscp.regionListProvider.removePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		rscp.permissionManager.attachments.remove(event.getPlayer());
		rscp.regionListProvider.removePlayer(event.getPlayer());
	}
}
