package ru.simsonic.rscPermissions.Bukkit;
import java.io.File;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.simsonic.rscUtilityLibrary.Bukkit.Commands.CommandAnswerException;

public final class PermissionsEx_YAML
{
	public PermissionsEx_YAML(File permissionsYml) throws CommandAnswerException
	{
		try
		{
			final FileConfiguration config = YamlConfiguration.loadConfiguration(permissionsYml);
			final ConfigurationSection csGroups = config.getConfigurationSection("groups");
			final ConfigurationSection csUsers  = config.getConfigurationSection("users");
			final ConfigurationSection csWorlds = config.getConfigurationSection("worlds");
		} catch(NullPointerException ex) {
		}
	}
}
