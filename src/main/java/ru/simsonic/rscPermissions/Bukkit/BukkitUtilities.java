package ru.simsonic.rscPermissions.Bukkit;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.simsonic.rscMinecraftLibrary.Bukkit.Tools;

public class BukkitUtilities
{
	public static Player findOnlinePlayer(String player)
	{
		for(Player online : Tools.getOnlinePlayers())
			if(online.getName().equals(player))
				return online;
		return null;
	}
	public static OfflinePlayer findOfflinePlayer(String player)
	{
		final String srcUniqueId = player.replace("-", "").toLowerCase();
		OfflinePlayer result = null;
		for(OfflinePlayer offline : Bukkit.getOfflinePlayers())
		{
			try
			{
				// Immediately return if UUID is the same\
				final String dstUniqueId = offline.getUniqueId().toString().replace("-", "");
				if(dstUniqueId.equalsIgnoreCase(srcUniqueId))
				{
					result = offline;
					break;
				}
			} catch(RuntimeException | NoSuchMethodError ex) {
			}
			try
			{
				// Find that player who had this name at last
				final String name = offline.getName();
				if(name != null && name.equalsIgnoreCase(player))
				{
					if(result != null && result.getLastPlayed() > offline.getLastPlayed())
						continue;
					result = offline;
				}
			} catch(RuntimeException | NoSuchMethodError ex) {
			}
		}
		return result;
	}
	public static String[] getOfflinePlayerIdentifiers(OfflinePlayer offline)
	{
		final ArrayList<String> result = new ArrayList<>();
		// SERVERS BEFORE UUIDs
		try
		{
			result.add(offline.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		// SERVERS WITH UUIDs
		try
		{
			result.add(offline.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		return result.toArray(new String[result.size()]);
	}
	public static String[] getPlayerIdentifiers(Player player)
	{
		// SERVERS BEFORE UUIDs
		final ArrayList<String> result = new ArrayList<>();
		try
		{
			result.add(player.getName());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		// SERVERS WITH UUIDs
		try
		{
			result.add(player.getUniqueId().toString().toLowerCase());
		} catch(RuntimeException | NoSuchMethodError ex) {
		}
		// ONLINE IP CONNECTION
		final InetSocketAddress socketAddress = player.getAddress();
		if(socketAddress != null)
			result.add(socketAddress.getAddress().getHostAddress());
		return result.toArray(new String[result.size()]);
	}
}
