package ru.simsonic.rscPermissions.Bukkit;

import java.util.HashSet;
import org.bukkit.entity.Player;
import ru.simsonic.rscCommonsLibrary.RestartableThread;
import ru.simsonic.rscMinecraftLibrary.Bukkit.MovingPlayersCatcher;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class RegionUpdateObserver extends RestartableThread
{
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
			if(granularity < Settings.REGION_UPDATE_GRANULARITY_MIN)
				granularity = Settings.REGION_UPDATE_GRANULARITY_MIN;
			if(granularity > Settings.REGION_UPDATE_GRANULARITY_MAX)
				granularity = Settings.REGION_UPDATE_GRANULARITY_MAX;
			for(; !Thread.interrupted(); Thread.sleep(granularity))
			{
				final HashSet<Player> players = movedPlayers.getMovedPlayersAsync();
				if(players.isEmpty())
					Thread.sleep(Settings.REGION_UPDATE_SLEEP_INTERVAL);
				else
					for(Player player : players)
						if(rscp.regionProviders.isRegionListChanged(player))
							rscp.permissionManager.recalculatePlayer(player);
				
			}
		} catch(InterruptedException ex) {
		}
	}
}
