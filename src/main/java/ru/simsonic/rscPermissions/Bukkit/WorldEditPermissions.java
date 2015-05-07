package ru.simsonic.rscPermissions.Bukkit;

import com.avaje.ebean.EbeanServer;
import com.sk89q.wepif.PermissionsResolver;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import ru.simsonic.rscPermissions.BridgeForBukkitAPI;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public final class WorldEditPermissions implements Plugin, PermissionsResolver
{
	private final BukkitPluginMain   rscp;
	private final BridgeForBukkitAPI bridge;
	private final VaultPermission    permissions;
	public WorldEditPermissions(BridgeForBukkitAPI bridge)
	{
		this.bridge      = bridge;
		this.rscp        = (BukkitPluginMain)bridge.getPlugin();
		this.permissions = (VaultPermission)bridge.getPermission();
	}
	@Override
	public void load()
	{
	}
	@Override
	public String getDetectionMessage()
	{
		return "Using rscp's WEPIF implementation for permissions";
	}
	@Override
	@Deprecated
	public boolean hasPermission(String player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermissionWC(permission);
	}
	@Override
	@Deprecated
	public boolean hasPermission(String worldName, String player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermissionWC(permission);
	}
	@Override
	@Deprecated
	public boolean hasPermission(OfflinePlayer player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermissionWC(permission);
	}
	@Override
	@Deprecated
	public boolean hasPermission(String world, OfflinePlayer player, String permission)
	{
		bridge.printDebugString("Looking for permission: {_LG}" + permission);
		bridge.printDebugStackTrace();
		return rscp.permissionManager.getResult(player).hasPermissionWC(permission);
	}
	@Override
	public boolean inGroup(OfflinePlayer player, String group)
	{
		return permissions.playerInGroup("", player, group);
	}
	@Override
	@Deprecated
	public boolean inGroup(String player, String group)
	{
		return permissions.playerInGroup("", player, group);
	}
	@Override
	public String[] getGroups(OfflinePlayer player)
	{
		return permissions.getPlayerGroups("", player);
	}
	@Override
	@Deprecated
	public String[] getGroups(String player)
	{
		return permissions.getPlayerGroups("", player);
	}
	@Override
	public String getName()
	{
		return rscp.getName();
	}
	@Override
	public PluginDescriptionFile getDescription()
	{
		return rscp.getDescription();
	}
	@Override
	public File getDataFolder()
	{
		return rscp.getDataFolder();
	}
	@Override
	public FileConfiguration getConfig()
	{
		return rscp.getConfig();
	}
	@Override
	public InputStream getResource(String filename)
	{
		return rscp.getResource(filename);
	}
	@Override
	public void saveConfig()
	{
		rscp.saveConfig();
	}
	@Override
	public void saveDefaultConfig()
	{
		rscp.saveDefaultConfig();
	}
	@Override
	public void saveResource(String arg0, boolean arg1)
	{
		rscp.saveResource(arg0, arg1);
	}
	@Override
	public void reloadConfig()
	{
		rscp.reloadConfig();
	}
	@Override
	public PluginLoader getPluginLoader()
	{
		return rscp.getPluginLoader();
	}
	@Override
	public Server getServer()
	{
		return rscp.getServer();
	}
	@Override
	public boolean isEnabled()
	{
		return rscp.isEnabled();
	}
	@Override
	public void onLoad()
	{
		rscp.onLoad();
	}
	@Override
	public void onEnable()
	{
		rscp.onEnable();
	}
	@Override
	public void onDisable()
	{
		rscp.onDisable();
	}
	@Override
	public boolean isNaggable()
	{
		return rscp.isNaggable();
	}
	@Override
	public void setNaggable(boolean canNag)
	{
		rscp.setNaggable(canNag);
	}
	@Override
	public EbeanServer getDatabase()
	{
		return rscp.getDatabase();
	}
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String arg0, String arg1)
	{
		return rscp.getDefaultWorldGenerator(arg0, arg1);
	}
	@Override
	public Logger getLogger()
	{
		return rscp.getLogger();
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
	{
		return rscp.onTabComplete(sender, cmd, label, args);
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return rscp.onCommand(sender, cmd, label, args);
	}
}
