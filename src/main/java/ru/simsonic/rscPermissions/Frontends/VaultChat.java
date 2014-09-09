package ru.simsonic.rscPermissions.Frontends;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.MainPluginClass;

public final class VaultChat extends net.milkbowl.vault.chat.Chat
{
	private final MainPluginClass rscp;
	public VaultChat(MainPluginClass plugin, Permission perms)
	{
		super(perms);
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
	public String getPlayerPrefix(String world, String player)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerPrefix(String world, String player, String prefix)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getPlayerSuffix(String world, String player)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerSuffix(String world, String player, String suffix)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getGroupPrefix(String world, String group)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupPrefix(String world, String group, String prefix)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getGroupSuffix(String world, String group)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupSuffix(String world, String group, String suffix)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getPlayerInfoString(String world, String player, String node, String defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setPlayerInfoString(String world, String player, String node, String value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public void setGroupInfoString(String world, String group, String node, String value)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	@Override
	public String getPlayerPrefix(String world, OfflinePlayer player)
	{
		return getPlayerPrefix(world, player.getName());
	}
	@Override
	public String getPlayerPrefix(Player player)
	{
		return getPlayerPrefix(player.getWorld().getName(), player);
	}
	@Override
	public void setPlayerPrefix(String world, OfflinePlayer player, String prefix)
	{
		setPlayerPrefix(world, player.getName(), prefix);
	}
	@Override
	public void setPlayerPrefix(Player player, String prefix)
	{
		setPlayerPrefix(player.getWorld().getName(), player, prefix);
	}
	@Override
	public String getPlayerSuffix(String world, OfflinePlayer player)
	{
		return getPlayerSuffix(world, player.getName());
	}
	@Override
	public String getPlayerSuffix(Player player)
	{
		return getPlayerSuffix(player.getWorld().getName(), player);
	}
	@Override
	public void setPlayerSuffix(String world, OfflinePlayer player, String suffix)
	{
		setPlayerSuffix(world, player.getName(), suffix);
	}
	@Override
	public void setPlayerSuffix(Player player, String suffix)
	{
		setPlayerSuffix(player.getWorld().getName(), player, suffix);
	}
	@Override
	public String getGroupPrefix(World world, String group)
	{
		return getGroupPrefix(world.getName(), group);
	}
	@Override
	public void setGroupPrefix(World world, String group, String prefix)
	{
		setGroupPrefix(world.getName(), group, prefix);
	}
	@Override
	public String getGroupSuffix(World world, String group)
	{
		return getGroupSuffix(world.getName(), group);
	}
	@Override
	public void setGroupSuffix(World world, String group, String suffix)
	{
		setGroupSuffix(world.getName(), group, suffix);
	}
	@Override
	public int getPlayerInfoInteger(String world, OfflinePlayer player, String node, int defaultValue)
	{
		return getPlayerInfoInteger(world, player.getName(), node, defaultValue);
	}
	@Override
	public int getPlayerInfoInteger(Player player, String node, int defaultValue)
	{
		return getPlayerInfoInteger(player.getWorld().getName(), player, node, defaultValue);
	}
	@Override
	public void setPlayerInfoInteger(String world, OfflinePlayer player, String node, int value)
	{
		setPlayerInfoInteger(world, player.getName(), node, value);
	}
	@Override
	public void setPlayerInfoInteger(Player player, String node, int value)
	{
		setPlayerInfoInteger(player.getWorld().getName(), player, node, value);
	}
	@Override
	public int getGroupInfoInteger(World world, String group, String node, int defaultValue)
	{
		return getGroupInfoInteger(world.getName(), group, node, defaultValue);
	}
	@Override
	public void setGroupInfoInteger(World world, String group, String node, int value)
	{
		setGroupInfoInteger(world.getName(), group, node, value);
	}
	@Override
	public double getPlayerInfoDouble(String world, OfflinePlayer player, String node, double defaultValue)
	{
		return getPlayerInfoDouble(world, player.getName(), node, defaultValue);
	}
	@Override
	public double getPlayerInfoDouble(Player player, String node, double defaultValue)
	{
		return getPlayerInfoDouble(player.getWorld().getName(), player, node, defaultValue);
	}
	@Override
	public void setPlayerInfoDouble(String world, OfflinePlayer player, String node, double value)
	{
		setPlayerInfoDouble(world, player.getName(), node, value);
	}
	@Override
	public void setPlayerInfoDouble(Player player, String node, double value)
	{
		setPlayerInfoDouble(player.getWorld().getName(), player, node, value);
	}
	@Override
	public double getGroupInfoDouble(World world, String group, String node, double defaultValue)
	{
		return getGroupInfoDouble(world.getName(), group, node, defaultValue);
	}
	@Override
	public void setGroupInfoDouble(World world, String group, String node, double value)
	{
		setGroupInfoDouble(world.getName(), group, node, value);
	}
	@Override
	public boolean getPlayerInfoBoolean(String world, OfflinePlayer player, String node, boolean defaultValue)
	{
		return getPlayerInfoBoolean(world, player.getName(), node, defaultValue);
	}
	@Override
	public boolean getPlayerInfoBoolean(Player player, String node, boolean defaultValue)
	{
		return getPlayerInfoBoolean(player.getWorld().getName(), player, node, defaultValue);
	}
	@Override
	public void setPlayerInfoBoolean(String world, OfflinePlayer player, String node, boolean value)
	{
		setPlayerInfoBoolean(world, player.getName(), node, value);
	}
	@Override
	public void setPlayerInfoBoolean(Player player, String node, boolean value)
	{
		setPlayerInfoBoolean(player.getWorld().getName(), player, node, value);
	}
	@Override
	public boolean getGroupInfoBoolean(World world, String group, String node, boolean defaultValue)
	{
		return getGroupInfoBoolean(world.getName(), group, node, defaultValue);
	}
	@Override
	public void setGroupInfoBoolean(World world, String group, String node, boolean value)
	{
		setGroupInfoBoolean(world.getName(), group, node, value);
	}
	@Override
	public String getPlayerInfoString(String world, OfflinePlayer player, String node, String defaultValue)
	{
		return getPlayerInfoString(world, player.getName(), node, defaultValue);
	}
	@Override
	public String getPlayerInfoString(Player player, String node, String defaultValue)
	{
		return getPlayerInfoString(player.getWorld().getName(), player, node, defaultValue);
	}
	@Override
	public void setPlayerInfoString(String world, OfflinePlayer player, String node, String value)
	{
		setPlayerInfoString(world, player.getName(), node, value);
	}
	@Override
	public void setPlayerInfoString(Player player, String node, String value)
	{
		setPlayerInfoString(player.getWorld().getName(), player, node, value);
	}
	@Override
	public String getGroupInfoString(World world, String group, String node, String defaultValue)
	{
		return getGroupInfoString(world.getName(), group, node, defaultValue);
	}
	@Override
	public void setGroupInfoString(World world, String group, String node, String value)
	{
		setGroupInfoString(world.getName(), group, node, value);
	}
	@Override
	public boolean playerInGroup(String world, OfflinePlayer player, String group)
	{
		// return perms.playerInGroup(world, player, group);
		return false;
	}
	@Override
	public boolean playerInGroup(Player player, String group)
	{
		// return playerInGroup(player.getWorld().getName(), player, group);
		return false;
	}
	@Override
	public String[] getPlayerGroups(String world, OfflinePlayer player)
	{
		// return perms.getPlayerGroups(world, player);
		return null;
	}
	@Override
	public String[] getPlayerGroups(Player player)
	{
		// return getPlayerGroups(player.getWorld().getName(), player);
		return null;
	}
	@Override
	public String getPrimaryGroup(String world, OfflinePlayer player)
	{
		// return perms.getPrimaryGroup(world, player);
		return "";
	}
	@Override
	public String getPrimaryGroup(Player player)
	{
		return getPrimaryGroup(player.getWorld().getName(), player);
	}
	@Override
	public String[] getGroups()
	{
		// return perms.getGroups();
		return null;
	}
}