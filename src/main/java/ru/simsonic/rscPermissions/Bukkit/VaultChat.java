package ru.simsonic.rscPermissions.Bukkit;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.BridgeForBukkitAPI;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public final class VaultChat extends VaultDeprecatedChat
{
	private final BridgeForBukkitAPI bridge;
	private final BukkitPluginMain   rscp;
	private final VaultPermission    permissions;
	public VaultChat(BridgeForBukkitAPI bridge, VaultPermission permissions)
	{
		super(permissions);
		this.bridge = bridge;
		this.rscp = (BukkitPluginMain)bridge.getPlugin();
		this.permissions = permissions;
	}
	@Override
	public String getName()
	{
		return bridge.getName();
	}
	@Override
	public boolean isEnabled()
	{
		return bridge.isEnabled();
	}
	// ***** GET PLAYER PREFIX *****
	@Override
	public String getPlayerPrefix(String world, OfflinePlayer player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.prefix;
	}
	@Override
	public String getPlayerPrefix(Player player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.prefix;
	}
	@Override
	@Deprecated
	public String getPlayerPrefix(String world, String player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.prefix;
	}
	// ***** GET PLAYER SUFFIX *****
	@Override
	public String getPlayerSuffix(String world, OfflinePlayer player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.suffix;
	}
	@Override
	public String getPlayerSuffix(Player player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.suffix;
	}
	@Override
	@Deprecated
	public String getPlayerSuffix(String world, String player)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.suffix;
	}
	// ***** SET PLAYER PREFIX *****
	@Override
	public void setPlayerPrefix(String world, OfflinePlayer player, String prefix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerPrefix(Player player, String prefix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerPrefix(String world, String player, String prefix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** SET PLAYER SUFFIX *****
	@Override
	public void setPlayerSuffix(String world, OfflinePlayer player, String suffix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerSuffix(Player player, String suffix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerSuffix(String world, String player, String suffix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** GET GROUP PREFIX *****
	@Override
	public String getGroupPrefix(String world, String group)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		return rscp.internalCache.findGroupRow(group).prefix;
	}
	@Override
	public String getGroupPrefix(World world, String group)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		return rscp.internalCache.findGroupRow(group).prefix;
	}
	// ***** GET GROUP SUFFIX *****
	@Override
	public String getGroupSuffix(String world, String group)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		return rscp.internalCache.findGroupRow(group).suffix;
	}
	@Override
	public String getGroupSuffix(World world, String group)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		return rscp.internalCache.findGroupRow(group).suffix;
	}
	// ***** SET GROUP PREFIX *****
	@Override
	public void setGroupPrefix(World world, String group, String prefix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupPrefix(String world, String group, String prefix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** SET GROUP SUFFIX *****
	@Override
	public void setGroupSuffix(String world, String group, String suffix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupSuffix(World world, String group, String suffix)
	{
		rscp.bridgeForBukkit.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** DELEGATED TO PERMISSION *****
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		return permissions.playerInGroup(world, player, group);
	}
	@Override
	public boolean playerInGroup(Player player, String group)
	{
		return permissions.playerInGroup(player, group);
	}
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		return permissions.getPlayerGroups(world, player);
	}
	@Override
	public String[] getPlayerGroups(Player player)
	{
		return permissions.getPlayerGroups(player);
	}
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		return permissions.getPrimaryGroup(world, player);
	}
	@Override
	public String getPrimaryGroup(Player player)
	{
		return permissions.getPrimaryGroup(player);
	}
	@Override
	public String[] getGroups()
	{
		return permissions.getGroups();
	}
}
