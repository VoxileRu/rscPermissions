package ru.simsonic.rscPermissions.Bukkit;

import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.BridgeForBukkitAPI;
import ru.simsonic.rscPermissions.BukkitPluginMain;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public final class VaultPermission extends net.milkbowl.vault.permission.Permission
{
	private final BridgeForBukkitAPI bridge;
	private final BukkitPluginMain   rscp;
	public VaultPermission(BridgeForBukkitAPI bridge)
	{
		this.bridge = bridge;
		this.rscp = (BukkitPluginMain)bridge.getPlugin();
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
	@Override
	public boolean hasSuperPermsCompat()
	{
		return true;
	}
	@Override
	public boolean hasGroupSupport()
	{
		return true;
	}
	@Override
	@Deprecated
	public boolean playerAdd(String world, String player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean playerRemove(String world, String player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupHas(String world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupAdd(String world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupRemove(String world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean playerAddGroup(String world, String player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean has(CommandSender sender, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return sender.hasPermission(permission);
	}
	@Override
	public boolean has(Player player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermission(permission);
	}
	@Override
	@Deprecated
	public boolean playerHas(String world, String player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermission(permission);
	}
	@Override
	public boolean playerHas(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermission(permission);
	}
	@Override
	public boolean playerHas(Player player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermission(permission);
	}
	@Override
	public boolean playerAdd(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAdd(Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(String world, Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(String world, Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean playerRemove(World world, String player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(OfflinePlayer player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(Player player, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupHas(World world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupAdd(World world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupRemove(World world, String group, String permission)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddGroup(String world, OfflinePlayer player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddGroup(Player player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveGroup(Player player, String group)
	{
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean playerInGroup(String world, String player, String group)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		for(String parent : result.getDeorderedGroups())
			if(parent.equalsIgnoreCase(group))
				return true;
		return false;
	}
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		for(String parent : result.getDeorderedGroups())
			if(parent.equalsIgnoreCase(group))
				return true;
		return false;
	}
	@Override
	public boolean playerInGroup(Player player, String group)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		for(String parent : result.getDeorderedGroups())
			if(parent.equalsIgnoreCase(group))
				return true;
		return false;
	}
	@Override
	@Deprecated
	public String[] getPlayerGroups(String world, String player)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getDeorderedGroups();
	}
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getDeorderedGroups();
	}
	@Override
	public String[] getPlayerGroups(Player player)
	{
		bridge.printDebugStackTrace();
		final ResolutionResult result = rscp.permissionManager.getResult(player);
		return result.getDeorderedGroups();
	}
	@Override
	@Deprecated
	public String getPrimaryGroup(String world, String player)
	{
		bridge.printDebugStackTrace();
		final String[] groups = getPlayerGroups(world, player);
		return groups.length > 0 ? groups[groups.length - 1] : rscp.settings.getDefaultGroup();
	}
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		bridge.printDebugStackTrace();
		final String[] groups = getPlayerGroups(world, player);
		return groups.length > 0 ? groups[groups.length - 1] : rscp.settings.getDefaultGroup();
	}
	@Override
	public String getPrimaryGroup(Player player)
	{
		bridge.printDebugStackTrace();
		final String[] groups = getPlayerGroups(player);
		return groups.length > 0 ? groups[groups.length - 1] : rscp.settings.getDefaultGroup();
	}
	@Override
	public String[] getGroups()
	{
		bridge.printDebugStackTrace();
		final Set<String> groups = rscp.internalCache.getKnownGroups();
		return groups.toArray(new String[groups.size()]);
	}
}
