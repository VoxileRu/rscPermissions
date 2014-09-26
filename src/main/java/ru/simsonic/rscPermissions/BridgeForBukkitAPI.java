package ru.simsonic.rscPermissions;
import ru.simsonic.rscPermissions.Frontends.VaultChat;
import ru.simsonic.rscPermissions.Frontends.VaultPermission;

public class BridgeForBukkitAPI
{
	private final MainPluginClass rscp;
	private final VaultPermission vaultPermission;
	private final VaultChat vaultChat;
	protected BridgeForBukkitAPI(MainPluginClass plugin)
	{
		this.rscp = plugin;
		this.vaultPermission = new VaultPermission(this);
		this.vaultChat = new VaultChat(this, vaultPermission);
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
}