package ru.simsonic.rscPermissions.Bukkit;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.ResolutionParams;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.Bukkit.Tools;
import ru.simsonic.rscUtilityLibrary.RestartableThread;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class BukkitPermissionManager extends RestartableThread
{
	private final BukkitPluginMain rscp;
	public BukkitPermissionManager(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	private final LinkedBlockingQueue<Player>       updateQueue = new LinkedBlockingQueue<>();
	private final Map<String, ResolutionResult>     resolutions = new ConcurrentHashMap<>();
	private final Map<Player, PermissionAttachment> attachments = new HashMap<>();
	private final Map<Player, Map<String, Boolean>> persistent  = new HashMap<>();
	private final Map<Player, Map<String, Boolean>> temporary   = new HashMap<>();
	private final Set<CommandSender> debug = new HashSet<>();
	public void recalculateOnlinePlayers()
	{
		updateQueue.addAll(Tools.getOnlinePlayers());
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
	public ResolutionResult getResult(String playerIdentifier)
	{
		return (resolutions.containsKey(playerIdentifier))
			? resolutions.get(playerIdentifier)
			: resolvePlayerIdentifier(playerIdentifier);
	}
	public ResolutionResult getResult(OfflinePlayer offline)
	{
		final String key = offline.toString();
		return (resolutions.containsKey(key))
			? resolutions.get(key)
			: resolveOfflinePlayer(offline);
	}
	public ResolutionResult getResult(Player player)
	{
		final String key = player.toString();
		return (resolutions.containsKey(key))
			? resolutions.get(key)
			: resolvePlayer(player);
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
		persistent.remove(player);
		temporary.remove(player);
		synchronized(debug)
		{
			debug.remove(player);
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
				persistent.put(current, result.permissions);
				final Player player = current;
				rscp.getServer().getScheduler().runTask(rscp, new Runnable()
				{
					@Override
					public void run()
					{
						// Remove old
						if(attachments.containsKey(player))
							attachments.remove(player).remove();
						// Create new and fill with permissions
						final PermissionAttachment attachment = player.addAttachment(rscp);
						attachments.put(player, attachment);
						final Map<String, Boolean> pp = persistent.get(player);
						if(pp != null && !pp.isEmpty())
							for(Map.Entry<String, Boolean> row : pp.entrySet())
								attachment.setPermission(row.getKey(), row.getValue());
						final Map<String, Boolean> tp = temporary.get(player);
						if(tp != null && !tp.isEmpty())
							for(Map.Entry<String, Boolean> row : tp.entrySet())
								attachment.setPermission(row.getKey(), row.getValue());
						// Give/Reset Server Operator status
						final Boolean asterisk = attachment.getPermissions().get("*");
						if(rscp.settings.isAsteriskOP())
							player.setOp((asterisk != null) ? asterisk : false);
						// Show debugging information
						if(isDebugging(player))
							player.sendMessage(GenericChatCodes.processStringStatic(Settings.chatPrefix
								+ "[DEBUG] Inheritances list: {_LG}" + Arrays.toString(result.getDeorderedGroups())
								+ "{_LS}; you have total {_LG}" + attachment.getPermissions().size()
								+ "{_LS} permissions."));
					}
				});
			}
		} catch(InterruptedException ex) {
		}
		updateQueue.clear();
	}
	public synchronized ResolutionResult resolvePlayerIdentifier(String playerIdentifier)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = new String[] { playerIdentifier };
		final ResolutionResult result = rscp.internalCache.resolvePlayer(params);
		resolutions.put(playerIdentifier, result);
		return result;
	}
	public synchronized ResolutionResult resolveOfflinePlayer(OfflinePlayer offline)
	{
		final ResolutionParams params = new ResolutionParams();
		params.applicableIdentifiers = getOfflinePlayerIdentifiers(offline);
		final ResolutionResult result = rscp.internalCache.resolvePlayer(params);
		for(String id : params.applicableIdentifiers)
			resolutions.put(id, result);
		resolutions.put(offline.toString(), result);
		return result;
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
		final ResolutionResult result = rscp.internalCache.resolvePlayer(params);
		for(String id : params.applicableIdentifiers)
			resolutions.put(id, result);
		resolutions.put(player.toString(), result);
		return result;
	}
	private static String[] getOfflinePlayerIdentifiers(OfflinePlayer offline)
	{
		final ArrayList<String> result = new ArrayList<>();
		try
		{
			result.add(offline.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		try
		{
			result.add(offline.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		return result.toArray(new String[result.size()]);
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
	public Set<CommandSender> getDebuggers()
	{
		synchronized(debug)
		{
			return new HashSet<>(debug);
		}
	}
	public boolean isConsoleDebugging()
	{
		return isDebugging(rscp.getServer().getConsoleSender());
	}
	public boolean isDebugging(CommandSender target)
	{
		synchronized(debug)
		{
			return debug.contains(target);
		}
	}
	public void setDebugging(CommandSender target, boolean value)
	{
		synchronized(debug)
		{
			if(value)
				debug.add(target);
			else
				debug.remove(target);
		}
	}
}
