package ru.simsonic.rscPermissions.Bukkit;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.ResolutionParams;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.RestartableThread;

public class BukkitPermissionManager extends RestartableThread
{
	private final BukkitPluginMain rscp;
	public BukkitPermissionManager(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	private final LinkedBlockingQueue<Player>       updateQueue = new LinkedBlockingQueue<>();
	private final Map<Player, PermissionAttachment> attachments = new HashMap<>();
	private final Map<Player, Map<String, Boolean>> persistentPermissions = new HashMap<>();
	private final Map<Player, Map<String, Boolean>> transientPermissions = new HashMap<>();
	private final Map<Player, Set<String>> groups   = new ConcurrentHashMap<>();
	private final Map<Player, String>      prefixes = new ConcurrentHashMap<>();
	private final Map<Player, String>      suffixes = new ConcurrentHashMap<>();
	private final Set<Player>              debug    = new HashSet<>();
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
	public String getPlayerPrefix(Player player)
	{
		final String prefix = prefixes.get(player);
		return prefix != null ? prefix : "";
	}
	public String getPlayerSuffix(Player player)
	{
		final String suffix = suffixes.get(player);
		return suffix != null ? suffix : "";
	}
	public Set<String> getPlayerGroups(Player player)
	{
		final Set<String> result = groups.get(player);
		return result != null ? result : Collections.EMPTY_SET;
	}
	public void removePlayer(Player player)
	{
		updateQueue.remove(player);
		attachments.remove(player);
		prefixes.remove(player);
		suffixes.remove(player);
		persistentPermissions.remove(player);
		transientPermissions.remove(player);
		debug.remove(player);
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
				groups.put(current, result.groups);
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
						if(attachments.containsKey(player))
							attachments.remove(player).remove();
						// Create new
						final Map<String, Boolean> pp = persistentPermissions.get(player);
						final Map<String, Boolean> tp = transientPermissions.get(player);
						if(pp == null && tp == null)
							return;
						final PermissionAttachment attachment = player.addAttachment(rscp);
						attachments.put(player, attachment);
						if(pp != null)
							for(Map.Entry<String, Boolean> row : pp.entrySet())
								attachment.setPermission(row.getKey(), row.getValue());
						if(tp != null)
							for(Map.Entry<String, Boolean> row : tp.entrySet())
								attachment.setPermission(row.getKey(), row.getValue());
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
	public boolean isDebugging(Player target)
	{
		return debug.contains(target);
	}
	public void setDebugging(Player target, boolean value)
	{
		if(value)
			debug.add(target);
		else
			debug.remove(target);
	}
}
