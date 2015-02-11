package ru.simsonic.rscPermissions.Bukkit;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.InternalCache.BrandNewCache;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.InternalCache.ResolutionResult;
import ru.simsonic.rscUtilityLibrary.RestartableThread;

public class BukkitPermissions extends RestartableThread
{
	private final BukkitPluginMain rscp;
	public BukkitPermissions(BukkitPluginMain plugin)
	{
		this.rscp = plugin;
	}
	private final LinkedBlockingQueue<Player> updateQueue = new LinkedBlockingQueue<>();
	private final HashMap<Player, String> prefixes = new HashMap<>();
	private final HashMap<Player, String> suffixes = new HashMap<>();
	private final HashMap<Player, RowPermission[]> persistentPermissions = new HashMap<>();
	private final HashMap<Player, RowPermission[]> transientPermissions = new HashMap<>();
	public final HashMap<Player, PermissionAttachment> attachments = new HashMap<>();
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
	@Override
	public void run()
	{
		Thread.currentThread().setName("rscp:" + this.getClass().getSimpleName());
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		try
		{
			for(Player current = updateQueue.take(); current != null; current = updateQueue.take())
			{
				final ResolutionResult result = rscp.cache2.resolvePlayer(current);
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
}
