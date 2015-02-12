package ru.simsonic.rscPermissions.Bukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.t00thpick1.residence.Residence;
import net.t00thpick1.residence.api.ResidenceAPI;
import net.t00thpick1.residence.api.ResidenceManager;
import net.t00thpick1.residence.api.areas.ResidenceArea;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public final class BukkitRegionProviders
{
	private final BukkitPluginMain plugin;
	private Plugin worldguard;
	private Plugin residence;
	private final Map<Player, Set<String>> regionsByPlayer = new HashMap<>();
	private final Map<Player, Integer> playerRegionHashes = new HashMap<>();
	private final Map<Player, World> playerLastWorld = new HashMap<>();
	public BukkitRegionProviders(BukkitPluginMain rscp)
	{
		this.plugin = rscp;
	}
	public synchronized void integrate()
	{
		// WorldGuard
		if(plugin.settings.isUseWorldGuard())
		{
			final Plugin pluginWG = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
			if((pluginWG != null) && (pluginWG instanceof WorldGuardPlugin))
			{
				this.worldguard = pluginWG;
				BukkitPluginMain.consoleLog.info("[rscp] WorldGuard was found and integrated.");
			} else {
				this.worldguard = null;
				BukkitPluginMain.consoleLog.info("[rscp] WorldGuard was not found.");
			}
		} else
			this.worldguard = null;
		// Residence
		if(plugin.settings.isUseResidence())
		{
			final Plugin pluginR = plugin.getServer().getPluginManager().getPlugin("Residence");
			if((pluginR != null) && (pluginR instanceof Residence))
			{
				this.residence = pluginR;
				BukkitPluginMain.consoleLog.info("[rscp] Residence was found and integrated.");
			} else {
				this.residence = null;
				BukkitPluginMain.consoleLog.info("[rscp] Residence was not found.");
			}
		} else
			this.residence = null;
	}
	public synchronized void deintegrate()
	{
		this.worldguard = null;
		this.residence = null;
	}
	public synchronized boolean isRegionListChanged(Player player)
	{
		final Location location = player.getLocation();
		final World world = location.getWorld();
		final Set<String> playerRegions = new HashSet<>();
		// WorldGuard
		if(worldguard != null && worldguard.isEnabled())
			try
			{
				final WorldGuardPlugin pluginWG = (WorldGuardPlugin)worldguard;
				final RegionManager rman = pluginWG.getRegionManager(world);
				if(rman == null)
					return false;
				// Get list
				final ApplicableRegionSet appregs = rman.getApplicableRegions(location);
				for(ProtectedRegion region : appregs)
					playerRegions.add(region.getId());
			} catch(RuntimeException ex) {
			}
		// Residence
		if(residence != null && residence.isEnabled())
			try
			{
				// Get list
				final ResidenceManager residenceManager = ResidenceAPI.getResidenceManager();
				if(residenceManager != null)
				{
					final ResidenceArea residenceArea = residenceManager.getByLocation(location);
					if(residenceArea != null)
						playerRegions.add(residenceArea.getFullName());
				}
			} catch(RuntimeException ex) {
			}
		// Is it changed?
		int hashcode = playerRegions.hashCode();
		if(playerLastWorld.containsKey(player))
			if(playerLastWorld.get(player).equals(world))
				if(hashcode == playerRegionHashes.get(player))
					return false;
		// Update
		playerRegionHashes.put(player, hashcode);
		regionsByPlayer.put(player, playerRegions);
		playerLastWorld.put(player, world);
		return true;
	}
	public synchronized Set<String> getPlayerRegions(Player player)
	{
		Set<String> result = regionsByPlayer.get(player);
		return (result != null) ? result : Collections.<String>emptySet();
	}
	public synchronized void removePlayer(Player player)
	{
		playerRegionHashes.remove(player);
		regionsByPlayer.remove(player);
		playerLastWorld.remove(player);
	}
}
