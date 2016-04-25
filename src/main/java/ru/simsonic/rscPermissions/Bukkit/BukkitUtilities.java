package ru.simsonic.rscPermissions.Bukkit;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BukkitUtilities
{
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
