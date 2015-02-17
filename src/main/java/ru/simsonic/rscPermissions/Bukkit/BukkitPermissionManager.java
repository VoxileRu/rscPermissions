package ru.simsonic.rscPermissions.Bukkit;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.API.RowPermission;
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
	private final HashMap<Player, PermissionAttachment> attachments = new HashMap<>();
	private final HashMap<Player, RowPermission[]> persistentPermissions = new HashMap<>();
	private final HashMap<Player, RowPermission[]> transientPermissions = new HashMap<>();
	private final HashMap<Player, String> prefixes = new HashMap<>();
	private final HashMap<Player, String> suffixes = new HashMap<>();
	public void recalculateOnlinePlayers()
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
	public Map<String, Boolean> listPlayerPermissions(Player player)
	{
		final PermissionAttachment attachment = rscp.permissionManager.attachments.get(player);
		if(attachment != null)
			return attachment.getPermissions();
		return Collections.EMPTY_MAP;
	}
	public void removePlayer(Player player)
	{
		updateQueue.remove(player);
		attachments.remove(player);
		prefixes.remove(player);
		suffixes.remove(player);
		persistentPermissions.remove(player);
		transientPermissions.remove(player);
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
		params.expirience = player.getLevel();
		return rscp.internalCache.resolvePlayer(params);
	}
	private static String[] getPlayerIdentifiers(Player player)
	{
		final ArrayList<String> result = new ArrayList<>();
		try
		{
			result.add(player.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		try
		{
			result.add(player.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		final InetSocketAddress socketAddress = player.getAddress();
		if(socketAddress != null)
			result.add(socketAddress.getAddress().getHostAddress());
		return result.toArray(new String[result.size()]);
	}
}
