package ru.simsonic.rscPermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class rscpAPI
{
	private final ru.simsonic.rscPermissions.MainPluginClass rscp;
	protected rscpAPI(MainPluginClass plugin)
	{
		this.rscp = plugin;
	}
	/**
	 * Vault support method.
	 * Checks if method is enabled.
	 *
	 * @return Success or Failure
	 */
	public boolean isEnabled()
	{
		return rscp.isEnabled();
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
		return rscp.settings.getDefaultGroup();
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
		Player onlinePlayer = rscp.getServer().getPlayerExact(player);
		if(onlinePlayer != null)
			return onlinePlayer.hasPermission(permission);
		rscp.cache.calculateBasePermissions(player);
		HashMap<String, Boolean> map = rscp.cache.mapPermissions.get(player);
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
		ArrayList<String> list = rscp.cache.getUserGroups(player);
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
		Set<String> groups = rscp.cache.getAllPossibleGroups();
		return groups.toArray(new String[groups.size()]);
	}
	/**
	 * Vault chat support.
	 * Get player's prefix
	 *
	 * @param world World name
	 * @param player Player name
	 * @return Prefix
	 */
	public String getPlayerPrefix(String world, String player)
	{
		return rscp.cache.userGetPrefix(player);
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
		return rscp.cache.userGetSuffix(player);
	}
	/**
	 * Vault chat support.
	 * Get player's prefix
	 *
	 * @param world World name
	 * @param group Group name
	 * @return Prefix
	 */
	public String getGroupPrefix(String world, String group)
	{
		return rscp.cache.groupGetPrefix(group);
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
		return rscp.cache.groupGetSuffix(group);
	}
	/**
	 * Vault chat support.
	 * Set player's prefix
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
				rscp.connectionList.updateEntityText(player, true, prefix, true);
			}
		}.start();
	}
	/**
	 * Vault chat support.
	 * Set player's prefix
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
				rscp.connectionList.updateEntityText(player, true, suffix, false);
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
				rscp.connectionList.updateEntityText(group, false, prefix, true);
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
				rscp.connectionList.updateEntityText(group, false, suffix, false);
			}
		}.start();
	}

	/**
	 * Checks if a CommandSender has a permission node. This will return the result of bukkits, generic .hasPermission()
	 * method and is identical in all cases. This method will explicitly fail if the registered permission system does
	 * not register permissions in bukkit.
	 *
	 * For easy checking of a commandsender
	 *
	 * @param sender to check permissions on
	 * @param permission to check for
	 * @return true if the sender has the permission
	 */
	public boolean has(CommandSender sender, String permission)
	{
		return sender.hasPermission(permission);
	}

	/**
	 * Checks if player has a permission node. (Short for playerHas(...)
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean has(Player player, String permission)
	{
		return player.hasPermission(permission);
	}

	/**
	 * Checks if player has a permission node. Supports NULL value for World if the permission system registered supports
	 * global permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world String world name
	 * @param player to check
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerHas(String world, OfflinePlayer player, String permission)
	{
		// SHOULD BE UPDATED
		return playerHas(world, player.getName(), permission);
	}

	/**
	 * Checks if player has a permission node. Defaults to world-specific permission check if the permission system
	 * supports it. See {@link #playerHas(String, OfflinePlayer, String)} for explicit global or world checks.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerHas(Player player, String permission)
	{
		return has(player, permission);
	}

	/**
	 * Add permission to a player. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world String world name
	 * @param player to add to
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerAdd(String world, OfflinePlayer player, String permission)
	{
		if(world == null)
			return playerAdd((String)null, player.getName(), permission);
		return playerAdd(world, player.getName(), permission);
	}

	/**
	 * Add permission to a player ONLY for the world the player is currently on. This is a world-specific operation, if
	 * you want to add global permission you must explicitly use NULL for the world. See
	 * {@link #playerAdd(String, OfflinePlayer, String)} for global permission use.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerAdd(Player player, String permission)
	{
		return playerAdd(player.getWorld().getName(), player, permission);
	}

	/**
	 * Add transient permission to a player. This implementation can be used by any subclass which implements a "pure"
	 * superperms plugin, i.e. one that only needs the built-in Bukkit API to add transient permissions to a player.
	 *
	 * @param player to add to
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerAddTransient(OfflinePlayer player, String permission) throws UnsupportedOperationException
	{
		if(player.isOnline())
			return playerAddTransient((Player)player, permission);
		throw new UnsupportedOperationException("rscPermissions does not support offline player transient permissions!");
	}

	/**
	 * Add transient permission to a player. This operation adds a permission onto the player object in bukkit via
	 * Bukkit's permission interface.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerAddTransient(Player player, String permission)
	{
		for(PermissionAttachmentInfo paInfo : player.getEffectivePermissions())
			if(paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(rscp))
			{
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}

		PermissionAttachment attach = player.addAttachment(rscp);
		attach.setPermission(permission, true);
		return true;
	}

	/**
	 * Adds a world specific transient permission to the player, may only work with some permission managers. Defaults to
	 * GLOBAL permissions for any permission system that does not support world-specific transient permissions!
	 *
	 * @param worldName to check on
	 * @param player to add to
	 * @param permission to test
	 * @return Success or Failure
	 */
	public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission)
	{
		return false;
		// return playerAddTransient(worldName, player.getName(), permission);
	}

	/**
	 * Adds a world specific transient permission to the player, may only work with some permission managers. Defaults to
	 * GLOBAL permissions for any permission system that does not support world-specific transient permissions!
	 *
	 * @param worldName to check on
	 * @param player to check
	 * @param permission to check for
	 * @return Success or Failure
	 */
	public boolean playerAddTransient(String worldName, Player player, String permission)
	{
		return playerAddTransient(player, permission);
	}

	/**
	 * Removes a world specific transient permission from the player, may only work with some permission managers.
	 * Defaults to GLOBAL permissions for any permission system that does not support world-specific transient
	 * permissions!
	 *
	 * @param worldName to remove for
	 * @param player to remove for
	 * @param permission to remove
	 * @return Success or Failure
	 */
	public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission)
	{
		return false;
		// return playerRemoveTransient(worldName, player.getName(), permission);
	}

	/**
	 * Removes a world specific transient permission from the player, may only work with some permission managers.
	 * Defaults to GLOBAL permissions for any permission system that does not support world-specific transient
	 * permissions!
	 *
	 * @param worldName to check on
	 * @param player to check
	 * @param permission to check for
	 * @return Success or Failure
	 */
	public boolean playerRemoveTransient(String worldName, Player player, String permission)
	{
		return playerRemoveTransient(worldName, (OfflinePlayer)player, permission);
	}

	/**
	 * Remove permission from a player. Will attempt to remove permission from the player on the player's current world.
	 * This is NOT a global operation.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerRemove(Player player, String permission)
	{
		return false;
		// return playerRemove(player.getWorld().getName(), player, permission);
	}

	/**
	 * Remove transient permission from a player. This implementation can be used by any subclass which implements a
	 * "pure" superperms plugin, i.e. one that only needs the built-in Bukkit API to remove transient permissions from a
	 * player. Any subclass implementing a plugin which provides its own API for this needs to override this method.
	 *
	 * @param player OfflinePlayer
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerRemoveTransient(OfflinePlayer player, String permission)
	{
		if(player.isOnline())
			return playerRemoveTransient((Player)player, permission);
		else
			return false;
	}

	/**
	 * Remove transient permission from a player.
	 *
	 * @param player Player Object
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean playerRemoveTransient(Player player, String permission)
	{
		for(PermissionAttachmentInfo paInfo : player.getEffectivePermissions())
			if(paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(rscp))
			{
				paInfo.getAttachment().unsetPermission(permission);
				return true;
			}
		return false;
	}

	/**
	 * Checks if group has a permission node. Supports NULL value for World if the permission system registered supports
	 * global permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupHas(World world, String group, String permission)
	{
		if(world == null)
			return groupHas((String)null, group, permission);
		return groupHas(world.getName(), group, permission);
	}

	/**
	 * Add permission to a group. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupAdd(World world, String group, String permission)
	{
		if(world == null)
			return groupAdd((String)null, group, permission);
		return groupAdd(world.getName(), group, permission);
	}

	/**
	 * Remove permission from a group. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world World Object
	 * @param group Group name
	 * @param permission Permission node
	 * @return Success or Failure
	 */
	public boolean groupRemove(World world, String group, String permission)
	{
		if(world == null)
			return groupRemove((String)null, group, permission);
		return groupRemove(world.getName(), group, permission);
	}

	/**
	 * Check if player is member of a group. Supports NULL value for World if the permission system registered supports
	 * global permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world World Object
	 * @param player to check
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerInGroup((String)null, player.getName(), group);
		return playerInGroup(world, player.getName(), group);
	}

	/**
	 * Check if player is member of a group. This method will ONLY check groups for which the player is in that are
	 * defined for the current world. This may result in odd return behaviour depending on what permission system has
	 * been registered.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerInGroup(Player player, String group)
	{
		return playerInGroup(player.getWorld().getName(), player, group);
	}

	/**
	 * Add player to a group. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world String world name
	 * @param player to add
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerAddGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerAddGroup((String)null, player.getName(), group);
		return playerAddGroup(world, player.getName(), group);
	}

	/**
	 * Add player to a group. This will add a player to the group on the current World. This may return odd results if
	 * the permission system being used on the server does not support world-specific groups, or if the group being added
	 * to is a global group.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerAddGroup(Player player, String group)
	{
		return playerAddGroup(player.getWorld().getName(), player, group);
	}

	/**
	 * Remove player from a group. Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world World Object
	 * @param player to remove
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerRemoveGroup((String)null, player.getName(), group);
		return playerRemoveGroup(world, player.getName(), group);
	}

	/**
	 * Remove player from a group. This will add a player to the group on the current World. This may return odd results
	 * if the permission system being used on the server does not support world-specific groups, or if the group being
	 * added to is a global group.
	 *
	 * @param player Player Object
	 * @param group Group name
	 * @return Success or Failure
	 */
	public boolean playerRemoveGroup(Player player, String group)
	{
		return playerRemoveGroup(player.getWorld().getName(), player, group);
	}

	/**
	 * Gets the list of groups that this player has Supports NULL value for World if the permission system registered
	 * supports global permissions. But May return odd values if the servers registered permission system does not have a
	 * global permission store.
	 *
	 * @param world String world name
	 * @param player OfflinePlayer
	 * @return Array of groups
	 */
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		return getPlayerGroups(world, player.getName());
	}

	/**
	 * Returns a list of world-specific groups that this player is currently in. May return unexpected results if you are
	 * looking for global groups, or if the registered permission system does not support world-specific groups. See
	 * {@link #getPlayerGroups(String, OfflinePlayer)} for better control of World-specific or global groups.
	 *
	 * @param player Player Object
	 * @return Array of groups
	 */
	public String[] getPlayerGroups(Player player)
	{
		return getPlayerGroups(player.getWorld().getName(), player);
	}

	/**
	 * Gets players primary group Supports NULL value for World if the permission system registered supports global
	 * permissions. But May return odd values if the servers registered permission system does not have a global
	 * permission store.
	 *
	 * @param world String world name
	 * @param player to get from
	 * @return Players primary group
	 */
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		return getPrimaryGroup(world, player.getName());
	}

	/**
	 * Get players primary group. Defaults to the players current world, so may return only world-specific groups. In
	 * most cases {@link #getPrimaryGroup(String, OfflinePlayer)} is preferable.
	 *
	 * @param player Player Object
	 * @return Players primary group
	 */
	public String getPrimaryGroup(Player player)
	{
		return getPrimaryGroup(player.getWorld().getName(), player);
	}
}