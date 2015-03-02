package ru.simsonic.rscPermissions.Bukkit;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscUtilityLibrary.Bukkit.Listeners.MovingPlayersCatcher;
import ru.simsonic.rscUtilityLibrary.RestartableThread;

public class RegionUpdateObserver extends RestartableThread
{
	private static final long granularityMin = 20;
	private static final long granularityMax = 10000;
	private final BukkitPluginMain rscp;
	private final MovingPlayersCatcher movedPlayers = new MovingPlayersCatcher();
	public RegionUpdateObserver(BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	public void registerListeners()
	{
		rscp.getServer().getPluginManager().registerEvents(movedPlayers, rscp);
	}
	@Override
	public void run()
	{
		try
		{
			Thread.currentThread().setName("rscp:" + this.getClass().getSimpleName());
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			long granularity = rscp.settings.getRegionFinderGranularity();
			if(granularity < granularityMin)
				granularity = granularityMin;
			if(granularity > granularityMax)
				granularity = granularityMax;
			for(; !Thread.interrupted(); Thread.sleep(granularity))
				for(Player player : movedPlayers.getMovedPlayersAsync())
					if(rscp.regionListProvider.isRegionListChanged(player))
						rscp.permissionManager.recalculatePlayer(player);
		} catch(InterruptedException ex) {
		}
	}
}
