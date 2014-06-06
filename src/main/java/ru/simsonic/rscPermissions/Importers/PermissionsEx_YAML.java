package ru.simsonic.rscPermissions.Importers;
import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class PermissionsEx_YAML extends BaseImporter
{
	public PermissionsEx_YAML(Plugin plugin, String fileName)
	{
		if(fileName == null || "".equals(fileName))
			fileName = "permissions.yml";
		try
		{
			final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), fileName));
			final ConfigurationSection csGroups = config.getConfigurationSection("groups");
			final ConfigurationSection csUsers = config.getConfigurationSection("users");
			final ConfigurationSection csWorlds = config.getConfigurationSection("worlds");
		} catch(NullPointerException ex) {
		}
	}
}