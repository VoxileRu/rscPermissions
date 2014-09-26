package ru.simsonic.rscPermissions;
import static java.lang.Thread.MIN_PRIORITY;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.simsonic.utilities.MovingPlayersCatcher;

public class RegionUpdateObserver implements Runnable, Listener
{
	private static final long granularityMin = 20;
	private static final long granularityMax = 10000;
	private final MainPluginClass rscp;
	private final MovingPlayersCatcher movedPlayers = new MovingPlayersCatcher();
	private Thread thread;
	RegionUpdateObserver(MainPluginClass rscp)
	{
		this.rscp = rscp;
	}
	public void registerListeners()
	{
		rscp.getServer().getPluginManager().registerEvents(this, rscp);
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
					MainPluginClass.consoleLog.log(Level.SEVERE, "[rscp] Exception in RegionUpdateObserver: {0}", ex);
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
			Thread.currentThread().setPriority(MIN_PRIORITY);
			long granularity = rscp.settings.getRegionFinderGranularity();
			if(granularity < granularityMin)
				granularity = granularityMin;
			if(granularity > granularityMax)
				granularity = granularityMax;
			for(; !Thread.interrupted(); Thread.sleep(granularity))
				for(Player player : movedPlayers.getMovedPlayersAsync())
					if(rscp.regionListProvider.isRegionListChanged(player))
						rscp.cache.calculatePlayerPermissions(player);
		} catch(InterruptedException ex) {
		}
	}
}
