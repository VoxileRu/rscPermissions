package ru.simsonic.rscPermissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Bukkit.VaultChat;
import ru.simsonic.rscPermissions.Bukkit.VaultPermission;
import ru.simsonic.rscPermissions.Engine.Phrases;
import ru.simsonic.rscUtilityLibrary.Bukkit.Tools;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

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
	public org.bukkit.plugin.java.JavaPlugin getPlugin()
	{
		return this.rscp;
	}
	public net.milkbowl.vault.permission.Permission getPermission()
	{
		return this.vaultPermission;
	}
	public net.milkbowl.vault.chat.Chat getChat()
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
	public Player findPlayer(String player)
	{
		for(Player online : Tools.getOnlinePlayers())
			if(online.getName().equals(player))
				return online;
		return null;
	}
	protected void setupVault()
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
			BukkitPluginMain.consoleLog.info(GenericChatCodes.processStringStatic("[rscp] " + Phrases.INTEGRATION_V_Y.toString()));
		} else
			BukkitPluginMain.consoleLog.warning(GenericChatCodes.processStringStatic("[rscp] " + Phrases.INTEGRATION_V_N.toString()));
	}
	public void printDebugStackTrace()
	{
		if(rscp.permissionManager.isConsoleDebugging())
		{
			final StringBuilder sb = new StringBuilder(Settings.chatPrefix);
			sb.append("[DEBUG] An API method was invoked from the path:").append(System.lineSeparator());
			for(StackTraceElement ste : Thread.currentThread().getStackTrace())
			{
				final String className = ste.getClassName();
				if(!className.equals(BridgeForBukkitAPI.class.getName())
					&& !className.equals(Thread.class.getName())
					)
					sb.append(Settings.chatPrefix).append("[DEBUG] ")
						.append(className.startsWith(BukkitPluginMain.class.getPackage().getName()) ? "{_LG}" : "{_LS}")
						.append(ste.toString())
						.append(System.lineSeparator());
			}
			rscp.getServer().getConsoleSender().sendMessage(GenericChatCodes.processStringStatic(sb.toString()));
		}
	}
}
