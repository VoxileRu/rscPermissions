package ru.simsonic.rscPermissions.Frontends;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import ru.simsonic.rscPermissions.MainPluginClass;

public class VaultPermission extends net.milkbowl.vault.permission.Permission
{
	private final MainPluginClass rscp;
	public VaultPermission(MainPluginClass plugin)
	{
		this.rscp = plugin;
	}
	@Override
	public String getName()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean isEnabled()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean hasSuperPermsCompat()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerHas(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerAdd(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerRemove(String world, String player, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean groupHas(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean groupAdd(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean groupRemove(String world, String group, String permission)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerInGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerAddGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean playerRemoveGroup(String world, String player, String group)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String[] getPlayerGroups(String world, String player)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getPrimaryGroup(String world, String player)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String[] getGroups()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean hasGroupSupport()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
		if(world == null)
			return has((String)null, player.getName(), permission);
		return has(world, player.getName(), permission);
	}
	@Override
	public boolean playerHas(Player player, String permission)
	{
		return has(player, permission);
	}
	@Override
	public boolean playerAdd(String world, OfflinePlayer player, String permission)
	{
		if(world == null)
			return playerAdd((String)null, player.getName(), permission);
		return playerAdd(world, player.getName(), permission);
	}
	@Override
	public boolean playerAdd(Player player, String permission)
	{
		return playerAdd(player.getWorld().getName(), player, permission);
	}
	@Override
	public boolean playerAddTransient(OfflinePlayer player, String permission) throws UnsupportedOperationException
	{
		if(player.isOnline())
			return playerAddTransient((Player)player, permission);
		throw new UnsupportedOperationException(getName() + " does not support offline player transient permissions!");
	}
	@Override
	public boolean playerAddTransient(Player player, String permission)
	{
		for(PermissionAttachmentInfo paInfo : player.getEffectivePermissions())
			if(paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin))
			{
				paInfo.getAttachment().setPermission(permission, true);
				return true;
			}

		PermissionAttachment attach = player.addAttachment(plugin);
		attach.setPermission(permission, true);

		return true;
	}
	@Override
	public boolean playerAddTransient(String worldName, OfflinePlayer player, String permission)
	{
		return playerAddTransient(worldName, player.getName(), permission);
	}
	@Override
	public boolean playerAddTransient(String worldName, Player player, String permission)
	{
		return playerAddTransient(player, permission);
	}
	@Override
	public boolean playerRemoveTransient(String worldName, OfflinePlayer player, String permission)
	{
		return playerRemoveTransient(worldName, player.getName(), permission);
	}
	@Override
	public boolean playerRemoveTransient(String worldName, Player player, String permission)
	{
		return playerRemoveTransient(worldName, (OfflinePlayer)player, permission);
	}
	@Override
	public boolean playerRemove(String world, OfflinePlayer player, String permission)
	{
		if(world == null)
			return playerRemove((String)null, player.getName(), permission);
		return playerRemove(world, player.getName(), permission);
	}
	@Deprecated
	@Override
	public boolean playerRemove(World world, String player, String permission)
	{
		if(world == null)
			return playerRemove((String)null, player, permission);
		return playerRemove(world.getName(), player, permission);
	}
	@Override
	public boolean playerRemove(Player player, String permission)
	{
		return playerRemove(player.getWorld().getName(), player, permission);
	}
	@Override
	public boolean playerRemoveTransient(OfflinePlayer player, String permission)
	{
		if(player.isOnline())
			return playerRemoveTransient((Player)player, permission);
		else
			return false;
	}
	@Override
	public boolean playerRemoveTransient(Player player, String permission)
	{
		for(PermissionAttachmentInfo paInfo : player.getEffectivePermissions())
			if(paInfo.getAttachment() != null && paInfo.getAttachment().getPlugin().equals(plugin))
			{
				paInfo.getAttachment().unsetPermission(permission);
				return true;
			}
		return false;
	}
	@Override
	public boolean groupHas(World world, String group, String permission)
	{
		if(world == null)
			return groupHas((String)null, group, permission);
		return groupHas(world.getName(), group, permission);
	}
	@Override
	public boolean groupAdd(World world, String group, String permission)
	{
		if(world == null)
			return groupAdd((String)null, group, permission);
		return groupAdd(world.getName(), group, permission);
	}
	@Override
	public boolean groupRemove(World world, String group, String permission)
	{
		if(world == null)
			return groupRemove((String)null, group, permission);
		return groupRemove(world.getName(), group, permission);
	}
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerInGroup((String)null, player.getName(), group);
		return playerInGroup(world, player.getName(), group);
	}
	@Override
	public boolean playerInGroup(Player player, String group)
	{
		return playerInGroup(player.getWorld().getName(), player, group);
	}
	@Override
	public boolean playerAddGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerAddGroup((String)null, player.getName(), group);
		return playerAddGroup(world, player.getName(), group);
	}
	@Override
	public boolean playerAddGroup(Player player, String group)
	{
		return playerAddGroup(player.getWorld().getName(), player, group);
	}
	@Override
	public boolean playerRemoveGroup(String world, OfflinePlayer player, String group)
	{
		if(world == null)
			return playerRemoveGroup((String)null, player.getName(), group);
		return playerRemoveGroup(world, player.getName(), group);
	}
	@Override
	public boolean playerRemoveGroup(Player player, String group)
	{
		return playerRemoveGroup(player.getWorld().getName(), player, group);
	}
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		return getPlayerGroups(world, player.getName());
	}
	@Override
	public String[] getPlayerGroups(Player player)
	{
		return getPlayerGroups(player.getWorld().getName(), player);
	}
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		return getPrimaryGroup(world, player.getName());
	}
	@Override
	public String getPrimaryGroup(Player player)
	{
		return getPrimaryGroup(player.getWorld().getName(), player);
	}
}