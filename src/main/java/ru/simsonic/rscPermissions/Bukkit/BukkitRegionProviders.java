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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.Phrases;

public final class BukkitRegionProviders
{
	private final BukkitPluginMain rscp;
	private Plugin worldguard;
	private Plugin residence;
	private final Map<Player, Set<String>> regionsByPlayer = new HashMap<>();
	private final Map<Player, Integer> playerRegionHashes = new HashMap<>();
	private final Map<Player, World> playerLastWorld = new HashMap<>();
	public BukkitRegionProviders(BukkitPluginMain rscp)
	{
		this.rscp = rscp;
	}
	public synchronized void integrate()
	{
		final ConsoleCommandSender console = rscp.getServer().getConsoleSender();
		// WorldGuard
		if(rscp.settings.isUseWorldGuard())
		{
			final Plugin plugin = rscp.getServer().getPluginManager().getPlugin("WorldGuard");
			if(plugin != null && plugin instanceof WorldGuardPlugin)
			{
				this.worldguard = plugin;
				console.sendMessage(Phrases.INTEGRATION_WG_Y.toPlayer());
			} else {
				this.worldguard = null;
				console.sendMessage(Phrases.INTEGRATION_WG_N.toPlayer());
			}
		} else
			this.worldguard = null;
	}
	public synchronized void deintegrate()
	{
		this.worldguard = null;
		this.residence = null;
		regionsByPlayer.clear();
		playerRegionHashes.clear();
		playerLastWorld.clear();
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
				final WorldGuardPlugin plugin = (WorldGuardPlugin)worldguard;
				final RegionManager rman = plugin.getRegionManager(world);
				if(rman == null)
					return false;
				// Get list
				final ApplicableRegionSet appregs = rman.getApplicableRegions(location);
				for(ProtectedRegion region : appregs)
					playerRegions.add(region.getId());
			} catch(RuntimeException | IncompatibleClassChangeError ex) {
				worldguard = null;
				rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic("[rscp] " + Phrases.INTEGRATION_WG_OLD.toString()));
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
		return result != null ? result : Collections.<String>emptySet();
	}
	public synchronized void removePlayer(Player player)
	{
		playerRegionHashes.remove(player);
		regionsByPlayer.remove(player);
		playerLastWorld.remove(player);
	}
}
