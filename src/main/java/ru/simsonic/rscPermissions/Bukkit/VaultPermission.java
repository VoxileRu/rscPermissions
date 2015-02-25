package ru.simsonic.rscPermissions.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.BridgeForBukkitAPI;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public class VaultPermission extends net.milkbowl.vault.permission.Permission
{
	private final BridgeForBukkitAPI bridge;
	private final BukkitPluginMain rscp;
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
	public boolean playerHas(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAdd(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupHas(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupAdd(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupRemove(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerInGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String[] getPlayerGroups(String world, String player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getPrimaryGroup(String world, String player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String[] getGroups()
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean hasGroupSupport()
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean has(CommandSender sender, String permission)
	{
		return sender.hasPermission(permission);
	}
	@Override
	public boolean has(Player player, String permission)
	{
		return player.hasPermission(permission);
	}
	@Override
	public boolean playerHas(String world, OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerHas(Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAdd(String world, OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAdd(Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(OfflinePlayer player, String permission) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddTransient(String worldName, Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(String worldName, Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(String world, OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(World world, String player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemove(Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(OfflinePlayer player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveTransient(Player player, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupHas(World world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupAdd(World world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean groupRemove(World world, String group, String permission)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerInGroup(Player player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddGroup(String world, OfflinePlayer player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerAddGroup(Player player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean playerRemoveGroup(Player player, String group)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String[] getPlayerGroups(Player player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getPrimaryGroup(Player player)
	{
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
}
