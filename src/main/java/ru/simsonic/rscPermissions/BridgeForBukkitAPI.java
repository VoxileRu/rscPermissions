package ru.simsonic.rscPermissions;

import com.sk89q.wepif.PermissionsResolverManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Bukkit.VaultChat;
import ru.simsonic.rscPermissions.Bukkit.VaultPermission;
import ru.simsonic.rscPermissions.Bukkit.WorldEditPermissions;
import ru.simsonic.rscPermissions.Engine.Phrases;

public class BridgeForBukkitAPI
{
	private static BridgeForBukkitAPI instance;
	public  static BridgeForBukkitAPI getInstance()
	{
		return instance;
	}
	private final BukkitPluginMain rscp;
	private final VaultPermission  vaultPermission;
	private final VaultChat        vaultChat;
	protected BridgeForBukkitAPI(BukkitPluginMain plugin)
	{
		BridgeForBukkitAPI.instance = BridgeForBukkitAPI.this;
		this.rscp = plugin;
		this.vaultPermission = new VaultPermission(this);
		this.vaultChat       = new VaultChat(this, vaultPermission);
	}
	public JavaPlugin getPlugin()
	{
		return this.rscp;
	}
	public Permission getPermission()
	{
		return this.vaultPermission;
	}
	public Chat getChat()
	{
		return this.vaultChat;
	}
	public String getName()
	{
		return rscp.getDescription().getName();
	}
	public boolean isEnabled()
	{
		return rscp.isEnabled();
	}
	protected void setupVault()
	{
		final ConsoleCommandSender console = rscp.getServer().getConsoleSender();
		final Plugin plugin = rscp.getServer().getPluginManager().getPlugin("Vault");
		if(plugin != null)
		{
			// Register Chat
			rscp.getServer().getServicesManager().register(
				net.milkbowl.vault.chat.Chat.class, vaultChat,
				rscp, ServicePriority.Highest);
			// Register Permission
			rscp.getServer().getServicesManager().register(
				net.milkbowl.vault.permission.Permission.class, vaultPermission,
				rscp, ServicePriority.Highest);
			console.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + Phrases.INTEGRATION_V_Y.toString()));
		} else
			console.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + Phrases.INTEGRATION_V_N.toString()));
	}
	protected void setupWEPIF()
	{
		final ConsoleCommandSender console = rscp.getServer().getConsoleSender();
		final Plugin plugin = rscp.getServer().getPluginManager().getPlugin("WorldEdit");
		if(plugin != null)
		{
			final WorldEditPermissions wepif = new WorldEditPermissions(this);
			final PermissionsResolverManager prm = PermissionsResolverManager.getInstance();
			if(prm != null)
				prm.setPluginPermissionsResolver(wepif);
			else
				PermissionsResolverManager.initialize(wepif);
			console.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + Phrases.INTEGRATION_WE_Y.toString()));
		} else
			console.sendMessage(GenericChatCodes.processStringStatic(Settings.CHAT_PREFIX + Phrases.INTEGRATION_WE_N.toString()));
	}
	public void printDebugString(String info)
	{
		if(rscp.permissionManager.isConsoleDebugging())
		{
			final StringBuilder sb = new StringBuilder(Settings.CHAT_PREFIX).append("[DEBUG] {_LS}").append(info);
			rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(sb.toString()));
		}
	}
	public void printDebugStackTrace()
	{
		if(rscp.permissionManager.isConsoleDebugging())
		{
			final StringBuilder sb = new StringBuilder(Settings.CHAT_PREFIX);
			sb.append("[DEBUG] An API method was invoked from the path:").append(System.lineSeparator());
			for(StackTraceElement ste : Thread.currentThread().getStackTrace())
			{
				final String className = ste.getClassName();
				if(!className.equals(BridgeForBukkitAPI.class.getName())
					&& !className.equals(Thread.class.getName())
					)
					sb.append(Settings.CHAT_PREFIX).append("[DEBUG] ")
						.append(className.startsWith(BukkitPluginMain.class.getPackage().getName()) ? "{_LG}" : "{_LS}")
						.append(ste.toString())
						.append(System.lineSeparator());
			}
			rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(sb.toString()));
		}
	}
}
