package ru.simsonic.rscPermissions.Sponge;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;
import org.spongepowered.api.service.permission.SubjectData;
import ru.simsonic.rscPermissions.SpongePluginMain;

public class SpongePermissionManager implements PermissionService
{
	private final SpongePluginMain rscp;
	// private final Logger logger;
	public SpongePermissionManager(SpongePluginMain plugin)
	{
		this.rscp = plugin;
		// this.logger = rscp.getLogger();
	}
	@Override
	public SubjectCollection getUserSubjects()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public SubjectCollection getGroupSubjects()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public SubjectData getDefaultData()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public SubjectCollection getSubjects(String identifier)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Map<String, SubjectCollection> getKnownSubjects()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public void registerContextCalculator(ContextCalculator<Subject> cc)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Optional<PermissionDescription.Builder> newDescriptionBuilder(Object o)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Optional<PermissionDescription> getDescription(String string)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	@Override
	public Collection<PermissionDescription> getDescriptions()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
