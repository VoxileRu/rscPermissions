package ru.simsonic.rscPermissions.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class PlayerEventsListener implements Listener
{
	private final BukkitPluginMain rscp;
	public PlayerEventsListener(BukkitPluginMain plugin)
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
		rscp.cache2.resolvePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerExp(PlayerLevelChangeEvent event)
	{
		rscp.cache2.resolvePlayer(event.getPlayer());
	}
	@EventHandler
	public void onPlayerLevel(PlayerExpChangeEvent event)
	{
		rscp.cache2.resolvePlayer(event.getPlayer());
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
