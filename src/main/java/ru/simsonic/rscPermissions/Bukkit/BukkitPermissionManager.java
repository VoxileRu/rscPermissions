package ru.simsonic.rscPermissions.Bukkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.InternalCache.ResolutionParams;
import ru.simsonic.rscPermissions.InternalCache.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.RestartableThread;

public class BukkitPermissionManager extends RestartableThread
{
	private final BukkitPluginMain rscp;
	public BukkitPermissionManager(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	private final LinkedBlockingQueue<Player> updateQueue = new LinkedBlockingQueue<>();
	private final HashMap<Player, String> prefixes = new HashMap<>();
	private final HashMap<Player, String> suffixes = new HashMap<>();
	private final HashMap<Player, RowPermission[]> persistentPermissions = new HashMap<>();
	private final HashMap<Player, RowPermission[]> transientPermissions = new HashMap<>();
	public  final HashMap<Player, PermissionAttachment> attachments = new HashMap<>();
	public void recalculateOnlinePlayersSync()
	{
		try
		{
			Runnable syncTask = new Runnable()
			{
				@Override
				public synchronized void run()
				{
					rscp.permissionManager.recalculateOnlinePlayersAsync();
					notify();
				}
			};
			synchronized(syncTask)
			{
				rscp.getServer().getScheduler().runTask(rscp, syncTask);
				syncTask.wait();
			}
		} catch(InterruptedException ex) {
		}
	}
	public void recalculateOnlinePlayersAsync()
	{
		updateQueue.addAll(rscp.getServer().getOnlinePlayers());
		rscp.scheduleAutoUpdate();
	}
	public void recalculatePlayer(Player player)
	{
		try
		{
			updateQueue.put(player);
		} catch(InterruptedException ex) {
		}
	}
	@Override
	public void run()
	{
		Thread.currentThread().setName("rscp:" + this.getClass().getSimpleName());
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		try
		{
			for(Player current = updateQueue.take(); current != null; current = updateQueue.take())
			{
				final ResolutionResult result = rscp.permissionManager.resolvePlayer(current);
				prefixes.put(current, result.prefix);
				suffixes.put(current, result.suffix);
				persistentPermissions.put(current, result.permissions);
				final Player player = current;
				rscp.getServer().getScheduler().runTask(rscp, new Runnable()
				{
					@Override
					public void run()
					{
						// Remove old
						final PermissionAttachment previous = attachments.get(player);
						if(previous != null)
						{
							player.removeAttachment(previous);
							attachments.remove(player);
						}
						// Create new
						final RowPermission[] pp = persistentPermissions.get(player);
						final RowPermission[] tp = transientPermissions.get(player);
						if(pp == null && tp == null)
							return;
						final PermissionAttachment attachment = player.addAttachment(rscp);
						attachments.put(player, attachment);
						if(pp != null)
							for(RowPermission row : pp)
								attachment.setPermission(row.permission, row.value);
						if(tp != null)
							for(RowPermission row : tp)
								attachment.setPermission(row.permission, row.value);
						// Server operator
						final Boolean asterisk = attachment.getPermissions().get("*");
						if(rscp.settings.isAsteriskOP())
							player.setOp((asterisk != null) ? asterisk : false);
					}
				});
			}
		} catch(InterruptedException ex) {
		}
		updateQueue.clear();
	}
	public synchronized ResolutionResult resolvePlayer(Player player)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = getPlayerIdentifiers(player);
		if(rscp.regionListProvider != null)
		{
			Set<String> regionSet = rscp.regionListProvider.getPlayerRegions(player);
			params.destRegions = regionSet.toArray(new String[regionSet.size()]);
		} else
			params.destRegions = new String[] {};
		params.destWorld = player.getLocation().getWorld().getName();
		params.destServerId = rscp.getServer().getServerId();
		params.expirience = player.getLevel();
		return rscp.internalCache.resolvePlayer(params);
	}
	private static String[] getPlayerIdentifiers(Player player)
	{
		final ArrayList<String> result = new ArrayList<>();
		// For old servers Player's name can be used as entity name
		try
		{
			// minecraft <= 1.7.x
			result.add(player.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
			// minecraft >= 1.8
		}
		// For newest servers Player's UUID is used as entity name
		try
		{
			// minecraft >= 1.8
			result.add(player.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
			// minecraft <= 1.7.x
		}
		// IP address of a Player can be used as entity name too
		result.add(player.getAddress().getAddress().getHostAddress());
		return result.toArray(new String[result.size()]);
	}
}