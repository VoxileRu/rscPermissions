package ru.simsonic.rscPermissions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Phrases
{
	PLUGIN_ENABLED    ("generic.enabled"),
	PLUGIN_DISABLED   ("generic.disabled"),
	PLUGIN_METRICS    ("generic.metrics"),
	PLUGIN_RELOADED   ("generic.reloaded"),
	FETCHED           ("generic.fetched");
	private final String node;
	private String phrase;
	private Phrases(String node)
	{
		this.node = node;
	}
	@Override
	public String toString()
	{
		return phrase;
	}
	public static void fill(BukkitPluginMain plugin, String langName)
	{
		final File langFile = new File(plugin.getDataFolder(), langName + ".yml");
		final YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
		for(Phrases value : Phrases.values())
			value.phrase = langConfig.getString(value.node, value.node);
	}
	public static void extract(BukkitPluginMain plugin, String langName)
	{
		try
		{
			final File langFile = new File(plugin.getDataFolder(), langName + ".yml");
			if(!langFile.isFile())
			{
				final YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
				final InputStream langStream = BukkitPluginMain.class.getResourceAsStream("/languages/" + langName + ".yml");
				YamlConfiguration langSource = YamlConfiguration.loadConfiguration(new InputStreamReader(langStream));
				langConfig.setDefaults(langSource);
				langConfig.save(langFile);
			}
		} catch(IOException ex) {
			BukkitPluginMain.consoleLog.log(Level.WARNING, "Cannot extract language: {0}", langName);
		}
	}
}
