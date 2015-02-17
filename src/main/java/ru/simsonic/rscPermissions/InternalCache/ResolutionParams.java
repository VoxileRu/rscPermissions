package ru.simsonic.rscPermissions.InternalCache;
import ru.simsonic.rscPermissions.API.RowEntity;

public class ResolutionParams
{
	public String[] applicableIdentifiers;
	public String[] destRegions;
	public String   destWorld;
	public int      expirience;
	protected transient RowEntity parentEntity;
	protected transient String    instantiator;
}
