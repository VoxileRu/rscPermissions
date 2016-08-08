package ru.simsonic.rscPermissions;

import com.sk89q.wepif.PermissionsResolverManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
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

public final class BridgeForBukkitAPI
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
		instance             = BridgeForBukkitAPI.this;
		this.rscp            = plugin;
		this.vaultPermission = new VaultPermission(this);
		this.vaultChat       = new VaultChat(this, vaultPermission);
	}
	public void onEnable()
	{
		setupVault();
		rscp.getServer().getScheduler().runTask(rscp, new Runnable()
		{
			@Override
			public void run()
			{
				setupWEPIF();
			}
		});
	}
	public JavaPlugin getPlugin()
	{
		return this.rscp;
	}
	public String getName()
	{
		return rscp.getDescription().getName();
	}
	public boolean isEnabled()
	{
		return rscp.isEnabled();
	}
	public Permission getPermission()
	{
		return this.vaultPermission;
	}
	public Chat getChat()
	{
		return this.vaultChat;
	}
	public void sendConsoleMessage(String message)
	{
		final ConsoleCommandSender console = rscp.getServer().getConsoleSender();
		if(console == null)
		{
			// Decolorized chat prefix
			final String dcp = ChatColor.stripColor(Settings.CHAT_PREFIX);
			// Decolorized messages
			message = ChatColor.stripColor(message);
			// Strip prefix
			if(message.startsWith(dcp))
				message = message.substring(dcp.length());
			rscp.getLogger().info(message);
		} else
			console.sendMessage(message);
	}
	private void setupVault()
	{
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
			sendConsoleMessage(Phrases.INTEGRATION_V_Y.toPlayer());
		} else
			sendConsoleMessage(Phrases.INTEGRATION_V_N.toPlayer());
	}
	private void setupWEPIF()
	{
		final Plugin plugin = rscp.getServer().getPluginManager().getPlugin("WorldEdit");
		if(plugin != null)
		{
			final WorldEditPermissions wepif = new WorldEditPermissions(this);
			final PermissionsResolverManager prm = PermissionsResolverManager.getInstance();
			if(prm != null)
				prm.setPluginPermissionsResolver(wepif);
			else
				PermissionsResolverManager.initialize(wepif);
			sendConsoleMessage(Phrases.INTEGRATION_WE_Y.toPlayer());
		} else
			sendConsoleMessage(Phrases.INTEGRATION_WE_N.toPlayer());
	}
	public void printDebugString(String info)
	{
		if(rscp.permissionManager.isConsoleDebugging())
		{
			final StringBuilder sb = new StringBuilder(Settings.DEBUG_PREFIX)
				.append(info);
			rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(sb.toString()));
		}
	}
	public void printDebugStackTrace()
	{
		if(rscp.permissionManager.isConsoleDebugging())
		{
			final StringBuilder sb = new StringBuilder(Settings.DEBUG_PREFIX)
				.append("An API method was invoked from the path:")
				.append(System.lineSeparator());
			for(StackTraceElement element : Thread.currentThread().getStackTrace())
			{
				final String className = element.getClassName();
				if(!className.equals(BridgeForBukkitAPI.class.getName())
					&& !className.equals(Thread.class.getName())
					)
					sb.append(Settings.DEBUG_PREFIX)
						.append(className.startsWith(BukkitPluginMain.class.getPackage().getName()) ? "{_LG}" : "{_LS}")
						.append(element.toString())
						.append(System.lineSeparator());
			}
			rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(sb.toString()));
		}
	}
}
