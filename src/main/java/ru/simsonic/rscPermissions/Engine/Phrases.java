package ru.simsonic.rscPermissions.Engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import ru.simsonic.rscPermissions.API.TranslationProvider;
import ru.simsonic.rscPermissions.BukkitPluginMain;

public enum Phrases
{
	PLUGIN_ENABLED     ("generic.enabled"),
	PLUGIN_DISABLED    ("generic.disabled"),
	PLUGIN_METRICS     ("generic.metrics"),
	PLUGIN_RELOADED    ("generic.reloaded"),
	PLUGIN_PLAYER_ONLY ("generic.player-only"),
	PLUGIN_CONSOLE_ONLY("generic.console-only"),
	SERVER_IS_FULL     ("generic.server-is-full"),
	MAINTENANCE_ON     ("generic.maintenance-on"),
	MAINTENANCE_OFF    ("generic.maintenance-off"),
	INTEGRATION_V_Y    ("integration.vault-yes"),
	INTEGRATION_V_N    ("integration.vault-no"),
	INTEGRATION_WE_Y   ("integration.worldedit-yes"),
	INTEGRATION_WE_N   ("integration.worldedit-no"),
	INTEGRATION_WG_Y   ("integration.worldguard-yes"),
	INTEGRATION_WG_N   ("integration.worldguard-no"),
	INTEGRATION_WG_OLD ("integration.worldguard-old"),
	INTEGRATION_R_Y    ("integration.residence-yes"),
	INTEGRATION_R_N    ("integration.residence-no"),
	DEBUG_ON           ("debug.enable"),
	DEBUG_OFF          ("debug.disable"),
	FETCHED_ANSWER     ("database.command-answer"),
	FETCHED_LOCAL_CACHE("database.fetched-local"),
	FETCHED_REMOTE_DB  ("database.fetched-remote"),
	HELP_HEADER_1      ("help.header-1"),
	HELP_HEADER_2      ("help.header-2"),
	HELP_USAGE         ("help.usage"),
	HELP_CMD_USER_LP   ("help.cmd-user-lp"),
	HELP_CMD_USER_LG   ("help.cmd-user-lg"),
	HELP_CMD_USER_P    ("help.cmd-user-p"),
	HELP_CMD_USER_S    ("help.cmd-user-s"),
	HELP_CMD_LOCK      ("help.cmd-lock"),
	HELP_CMD_UNLOCK    ("help.cmd-unlock"),
	HELP_CMD_DEBUG     ("help.cmd-debug"),
	HELP_CMD_FETCH     ("help.cmd-fetch"),
	HELP_CMD_RELOAD    ("help.cmd-reload"),
	HELP_CMD_HELP      ("help.cmd-help"),
	;
	public final static String defaultMaintenancePingMsg = "{_LR}Maintenance mode";
	public final static String defaultMaintenanceKickMsg = "{_LR}Sorry! Server is going into maintenance mode.";
	public final static String defaultMaintenanceJoinMsg = "{_LR}You are not allowed to enter when maintenance is on.";
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
	public static void applyTranslation(TranslationProvider provider)
	{
		for(Phrases value : Phrases.values())
			value.phrase = provider.getString(value.node);
	}
	public static void extractTranslations(File workingDir)
	{
		extractTranslation(workingDir, "english");
		extractTranslation(workingDir, "russian");
	}
	private static void extractTranslation(File workingDir, String langName)
	{
		try
		{
			final File langFile = new File(workingDir, langName + ".yml");
			if(langFile.isFile())
				langFile.delete();
			final FileChannel fileChannel = new FileOutputStream(langFile).getChannel();
			fileChannel.force(true);
			final InputStream langStream = BukkitPluginMain.class.getResourceAsStream("/languages/" + langName + ".yml");
			fileChannel.transferFrom(Channels.newChannel(langStream), 0, Long.MAX_VALUE);
		} catch(IOException ex) {
		}
	}
}
