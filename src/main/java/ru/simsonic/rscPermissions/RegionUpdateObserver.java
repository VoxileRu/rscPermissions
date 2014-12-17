package ru.simsonic.rscPermissions;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import ru.simsonic.rscUtilityLibrary.BukkitListeners.MovingPlayersCatcher;

public class RegionUpdateObserver implements Runnable
{
	private static final long granularityMin = 20;
	private static final long granularityMax = 10000;
	private final BukkitPluginMain rscp;
	private final MovingPlayersCatcher movedPlayers = new MovingPlayersCatcher();
	private Thread thread;
	RegionUpdateObserver(BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	public void registerListeners()
	{
		rscp.getServer().getPluginManager().registerEvents(movedPlayers, rscp);
	}
	public void start()
	{
		stop();
		thread = new Thread(this);
		thread.start();
	}
	public void stop()
	{
		if(thread != null)
		{
			if(thread.isAlive())
			{
				try
				{
					thread.interrupt();
					thread.join();
				} catch(InterruptedException ex) {
					BukkitPluginMain.consoleLog.log(Level.SEVERE, "[rscp] Exception in RegionUpdateObserver: {0}", ex);
				}
			}
			thread = null;
		}
	}
	@Override
	public void run()
	{
		try
		{
			Thread.currentThread().setName("rscp:RegionUpdateObserver");
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			long granularity = rscp.settings.getRegionFinderGranularity();
			if(granularity < granularityMin)
				granularity = granularityMin;
			if(granularity > granularityMax)
				granularity = granularityMax;
			for(; !Thread.interrupted(); Thread.sleep(granularity))
				for(Player player : movedPlayers.getMovedPlayersAsync())
					if(rscp.regionListProvider.isRegionListChanged(player))
						rscp.cache2.resolvePlayer(player);
		} catch(InterruptedException ex) {
		}
	}
}
