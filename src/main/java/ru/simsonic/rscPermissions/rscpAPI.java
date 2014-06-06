package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.entity.Player;

public class rscpAPI
{
	private final ru.simsonic.rscPermissions.MainPluginClass plugin;
	protected rscpAPI(MainPluginClass rscp)
	{
		this.plugin = rscp;
	}
	/**
	 * Vault support method.
	 * Gets name of permission method
	 *
	 * @return Name of Permission Method
	 */
	public String getName()
	{
		return plugin.getName();
	}
	/**
	 * Vault support method.
	 * Checks if method is enabled.
	 *
	 * @return Success or Failure
	 */
	public boolean isEnabled()
	{
		return plugin.isEnabled();
	}
	/**
	 * Vault support method.
	 * Returns if the permission system is or attempts to be compatible with super-perms.
	 *
	 * @return True if this permission implementation works with super-perms
	 */
	public boolean hasSuperPermsCompat()
	{
		return true;
	}
	/**
	 * Vault permission support method.
	 * Returns true if the given implementation supports groups.
	 *
	 * @return true if the implementation supports groups
	 */
	public boolean hasGroupSupport()
	{
		return true;
	}
	/**
	 * Vault permission support method.
	 * Gets players primary group
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Players primary group
	 */
	public String getPrimaryGroup(String world, String player)
	{
		return plugin.settings.getDefaultGroup();
	}
	/**
	 * Vault permission support method.
	 * Check if player is member of a group.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * This method is known to return unexpected results depending on what permission system is being used. Different
	 * permission systems
	 * will store the player groups differently, It is HIGHLY suggested you test your code out first.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerInGroup(String world, String player, String group)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Add player to a group.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerAddGroup(String world, String player, String group)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Remove player from a group.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Checks if player has a permission node.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But may return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerHas(String world, String player, String permission)
	{
		Player onlinePlayer = plugin.getServer().getPlayerExact(player);
		if(onlinePlayer != null)
			return onlinePlayer.hasPermission(permission);
		plugin.cache.calculateBasePermissions(player);
		HashMap<String, Boolean> map = plugin.cache.mapPermissions.get(player);
		if(map != null)
		{
			Boolean value = map.get(permission);
			if(value != null)
				return value;
		}
		return false;
	}
	/**
	 * Vault permission support method.
	 * Add permission to a player.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerAdd(String world, String player, String permission)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Remove permission from a player.
	 *
	 * @param world World name
	 * @param player Name of Player
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerRemove(String world, String player, String permission)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Checks if group has a permission node.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupHas(String world, String group, String permission)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Add permission to a group.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupAdd(String world, String group, String permission)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Remove permission from a group.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupRemove(String world, String group, String permission)
	{
		// TO DO
		return false;
	}
	/**
	 * Vault permission support method.
	 * Gets the list of groups that this player has.
	 * Supports NULL value for World if the permission system registered supports global permissions.
	 * But May return odd values if the servers registered permission system does not have a global permission store.
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Array of groups
	 */
	public String[] getPlayerGroups(String world, String player)
	{
		ArrayList<String> list = plugin.cache.getUserGroups(player);
		return list.toArray(new String[list.size()]);
	}
	/**
	 * Vault permission support method.
	 * Returns a list of all known groups
	 *
	 * @return an Array of String of all groups
	 */
	public String[] getGroups()
	{
		Set<String> groups = plugin.cache.getAllPossibleGroups();
		return groups.toArray(new String[groups.size()]);
	}
	/**
	 * Vault chat support.
	 * Get players prefix
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Prefix
	 */
	public String getPlayerPrefix(String world, String player)
	{
		return plugin.cache.userGetPrefix(player);
	}
	/**
	 * Vault chat support.
	 * Get players prefix
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Prefix
	 */
	public String getPlayerSuffix(String world, String player)
	{
		return plugin.cache.userGetSuffix(player);
	}
	/**
	 * Vault chat support.
	 * Get players prefix
	 *
	 * @param world World name
	 * @param group Group name
	 * @return Prefix
	 */
	public String getGroupPrefix(String world, String group)
	{
		return plugin.cache.groupGetPrefix(group);
	}
	/**
	 * Vault chat support.
	 * Get players prefix
	 *
	 * @param world World name
	 * @param group Group name
	 * @return Prefix
	 */
	public String getGroupSuffix(String world, String group)
	{
		return plugin.cache.groupGetSuffix(group);
	}
	/**
	 * Vault chat support.
	 * Set players prefix
	 *
	 * @param world World name
	 * @param player Player name
	 * @param prefix Prefix
	 */
	public void setPlayerPrefix(String world, final String player, final String prefix)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				plugin.connectionList.updateEntityText(player, true, prefix, true);
			}
		}.start();
	}
	/**
	 * Vault chat support.
	 * Set players prefix
	 *
	 * @param world World name
	 * @param player Player name
	 * @param suffix Suffix
	 */
	public void setPlayerSuffix(String world, final String player, final String suffix)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				plugin.connectionList.updateEntityText(player, true, suffix, false);
			}
		}.start();
	}
	/**
	 * Vault chat support.
	 * Set group prefix
	 *
	 * @param world World name
	 * @param group Group name
	 * @param prefix Prefix
	 */
	public void setGroupPrefix(String world, final String group, final String prefix)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				plugin.connectionList.updateEntityText(group, false, prefix, true);
			}
		}.start();
	}
	/**
	 * Vault chat support.
	 * Set group prefix
	 *
	 * @param world World name
	 * @param group Group name
	 * @param suffix Suffix
	 */
	public void setGroupSuffix(String world, final String group, final String suffix)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				plugin.connectionList.updateEntityText(group, false, suffix, false);
			}
		}.start();
	}
}