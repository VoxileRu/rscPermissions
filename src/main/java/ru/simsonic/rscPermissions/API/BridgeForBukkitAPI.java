package ru.simsonic.rscPermissions.API;
import ru.simsonic.rscPermissions.Bukkit.VaultChat;
import ru.simsonic.rscPermissions.Bukkit.VaultPermission;
import ru.simsonic.rscPermissions.BukkitPluginMain;

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
}
