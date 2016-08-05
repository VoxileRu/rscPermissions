package ru.simsonic.rscPermissions.API;

import java.sql.Timestamp;

public abstract class GenericRow
{
	public           int        id;
	public transient String     splittedId;
	public           String     entity;
	public           EntityType entityType;
	public transient PlayerType playerType;
	public           Timestamp  lifetime;
	public boolean isMappedInDB()
	{
		return splittedId != null && !"".equals(splittedId);
	}
	public boolean hasClonesInRow()
	{
		return splittedId != null && splittedId.contains(Settings.SPLITTED_ID_SEP);
	}
}
