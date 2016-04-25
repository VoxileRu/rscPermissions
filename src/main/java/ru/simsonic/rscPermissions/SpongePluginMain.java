package ru.simsonic.rscPermissions;

import com.google.inject.Inject;
import java.io.File;
import org.slf4j.Logger;
/*
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.sstate.ServerAboutToStartEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
*/
import org.spongepowered.api.plugin.Plugin;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscPermissions.Engine.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Engine.Backends.BackendJson;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Sponge.SpongePermissionManager;
import ru.simsonic.rscPermissions.Sponge.SpongePluginConfiguration;

// Documentation for Sponge: https://docs.spongepowered.org/ru/index.html

@Plugin(id = "rscPermissions", name = "rscPermissions", version = "0.10.0b")
public class SpongePluginMain
{
	/*
	private final Logger logger;
	*/
	private final Settings settings = new SpongePluginConfiguration(this);
	private final SpongePermissionManager permissionManager = new SpongePermissionManager(this);
	public  final BackendJson localStorage = new BackendJson(new File(""));
	public  final BackendDatabase connection = new BackendDatabase(null);
	public  final InternalCache internalCache = new InternalCache();
	/*
	@Inject
	private Game game;
	@Inject
	public SpongePluginMain(Logger logger)
	{
		this.logger = logger;
		logger.info("API VERSION IS " + game.getApiVersion());
	}
	public Logger getLogger()
	{
		return logger;
	}
	@Subscribe
	public void onServerStart(ServerAboutToStartEvent event)
	{
		logger.info("onServerStart");
	}
	@Subscribe
	public void onServerStarting(ServerStartingEvent event)
	{
		logger.info("onServerStarting");
	}
	@Subscribe
	public void onServerStarted(ServerStartedEvent event)
	{
		logger.info("onServerStarted");
	}
	@Subscribe
	public void onServerStopping(ServerStoppingEvent event)
	{
		logger.info("onServerStopping");
	}
	@Subscribe
	public void onServerStopped(ServerStoppedEvent event)
	{
		logger.info("onServerStopped");
	}
	*/
}
