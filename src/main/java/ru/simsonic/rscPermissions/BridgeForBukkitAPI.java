package ru.simsonic.rscPermissions;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import ru.simsonic.rscPermissions.Bukkit.VaultChat;
import ru.simsonic.rscPermissions.Bukkit.VaultPermission;

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
	public BridgeForBukkitAPI(BukkitPluginMain plugin)
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
			BukkitPluginMain.consoleLog.info("[rscp] Vault found and integrated.");
		}
	}
}
