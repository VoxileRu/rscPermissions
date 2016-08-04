package ru.simsonic.rscPermissions.Bukkit;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.API.RowEntity;
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
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getPrefix();
	}
	@Override
	public String getPlayerPrefix(Player player)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getPrefix();
	}
	@Override
	@Deprecated
	public String getPlayerPrefix(String world, String player)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getPrefix();
	}
	// ***** GET PLAYER SUFFIX *****
	@Override
	public String getPlayerSuffix(String world, OfflinePlayer player)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getSuffix();
	}
	@Override
	public String getPlayerSuffix(Player player)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getSuffix();
	}
	@Override
	@Deprecated
	public String getPlayerSuffix(String world, String player)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getSuffix();
	}
	// ***** SET PLAYER PREFIX *****
	@Override
	public void setPlayerPrefix(String world, OfflinePlayer player, String prefix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerPrefix(Player player, String prefix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerPrefix(String world, String player, String prefix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** SET PLAYER SUFFIX *****
	@Override
	public void setPlayerSuffix(String world, OfflinePlayer player, String suffix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerSuffix(Player player, String suffix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerSuffix(String world, String player, String suffix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	// ***** GET GROUP PREFIX *****
	@Override
	public String getGroupPrefix(String world, String group)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final RowEntity entity = rscp.internalCache.findGroupEntity(group);
		return entity != null ? entity.prefix : null;
	}
	@Override
	public String getGroupPrefix(World world, String group)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final RowEntity entity = rscp.internalCache.findGroupEntity(group);
		return entity != null ? entity.prefix : null;
	}
	// ***** GET GROUP SUFFIX *****
	@Override
	public String getGroupSuffix(String world, String group)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final RowEntity entity = rscp.internalCache.findGroupEntity(group);
		return entity != null ? entity.suffix : null;
	}
	@Override
	public String getGroupSuffix(World world, String group)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		final RowEntity entity = rscp.internalCache.findGroupEntity(group);
		return entity != null ? entity.suffix : null;
	}
	// ***** SET GROUP PREFIX *****
	@Override
	public void setGroupPrefix(World world, String group, String prefix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is still unsupported by rscPermissions.");
	}
	@Override
	public void setGroupPrefix(String world, String group, String prefix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is still unsupported by rscPermissions.");
	}
	// ***** SET GROUP SUFFIX *****
	@Override
	public void setGroupSuffix(String world, String group, String suffix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is still unsupported by rscPermissions.");
	}
	@Override
	public void setGroupSuffix(World world, String group, String suffix)
	{
		rscp.rscpAPIs.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is still unsupported by rscPermissions.");
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
