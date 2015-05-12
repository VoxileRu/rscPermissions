package ru.simsonic.rscPermissions.Engine;

import java.util.Map;
import java.util.Set;
import ru.simsonic.rscPermissions.API.RowEntity;

public class ResolutionParams
{
	public String[] applicableIdentifiers;
	public String[] destRegions;
	public String   destWorld;
	public int      expirience;
	protected transient int depth;
	protected transient RowEntity parentEntity;
	protected transient String    instantiator;
	protected transient Map<String, Boolean> finalPerms;
	protected transient Set<String> groupList;
}
