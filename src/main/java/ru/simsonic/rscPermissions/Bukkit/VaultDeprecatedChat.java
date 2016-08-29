package ru.simsonic.rscPermissions.Bukkit;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.simsonic.rscPermissions.BridgeForBukkitAPI;

public abstract class VaultDeprecatedChat extends net.milkbowl.vault.chat.Chat
{
	protected final BridgeForBukkitAPI bridge;
	public VaultDeprecatedChat(BridgeForBukkitAPI bridge, Permission permissions)
	{
		super(permissions);
		this.bridge = bridge;
	}
	@Override
	@Deprecated
	public int getPlayerInfoInteger(String world, String player, String node, int defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerInfoInteger(String world, String player, String node, int value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public double getPlayerInfoDouble(String world, String player, String node, double defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerInfoDouble(String world, String player, String node, double value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public String getPlayerInfoString(String world, String player, String node, String defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	@Deprecated
	public void setPlayerInfoString(String world, String player, String node, String value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getGroupInfoString(String world, String group, String node, String defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoString(String world, String group, String node, String value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public int getPlayerInfoInteger(String world, OfflinePlayer player, String node, int defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public int getPlayerInfoInteger(Player player, String node, int defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|)");
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoInteger(String world, OfflinePlayer player, String node, int value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoInteger(Player player, String node, int value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public int getGroupInfoInteger(World world, String group, String node, int defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoInteger(World world, String group, String node, int value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public double getPlayerInfoDouble(String world, OfflinePlayer player, String node, double defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public double getPlayerInfoDouble(Player player, String node, double defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|)");
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoDouble(String world, OfflinePlayer player, String node, double value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoDouble(Player player, String node, double value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public double getGroupInfoDouble(World world, String group, String node, double defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoDouble(World world, String group, String node, double value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean getPlayerInfoBoolean(String world, OfflinePlayer player, String node, boolean defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean getPlayerInfoBoolean(Player player, String node, boolean defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|)");
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoBoolean(String world, OfflinePlayer player, String node, boolean value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoBoolean(Player player, String node, boolean value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public boolean getGroupInfoBoolean(World world, String group, String node, boolean defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoBoolean(World world, String group, String node, boolean value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getPlayerInfoString(String world, OfflinePlayer player, String node, String defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getPlayerInfoString(Player player, String node, String defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_WH}" + player + "{_LS} ({_LB}" + defaultValue + "{_LS|)");
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoString(String world, OfflinePlayer player, String node, String value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setPlayerInfoString(Player player, String node, String value)
	{
		bridge.printDebugString("Set " + node + " for " + player + " = " + value);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public String getGroupInfoString(World world, String group, String node, String defaultValue)
	{
		bridge.printDebugString("{_LS}Get {_LG}" + node + "{_LS} of {_YL}" + group + "{_LS} ({_LB}" + defaultValue + "{_LS|) @ " + world);
		bridge.printDebugStackTrace();
		return defaultValue;
		// throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
	@Override
	public void setGroupInfoString(World world, String group, String node, String value)
	{
		bridge.printDebugString("Set " + node + " for " + group + " = " + value + " @ " + world);
		bridge.printDebugStackTrace();
		throw new UnsupportedOperationException("This method is unsupported by rscPermissions.");
	}
}
