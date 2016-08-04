package ru.simsonic.rscPermissions.API;

import java.sql.Timestamp;

public class RowEntity implements Cloneable, Comparable<RowEntity>
{
	public int        id;
	public String     entity;
	public EntityType entityType;
	public String     prefix;
	public String     suffix;
	public Timestamp  lifetime;
	public transient String           splittedId;
	public transient PlayerType       playerType;
	public transient RowPermission[]  permissions;
	public transient RowInheritance[] inheritance;
	public boolean isMappedInDB()
	{
		return splittedId != null && !"".equals(splittedId);
	}
	public boolean hasClonesInRow()
	{
		return splittedId != null && splittedId.contains(Settings.SPLITTED_ID_SEP);
	}
	@Override
	public RowEntity clone() throws CloneNotSupportedException
	{
		 return (RowEntity)super.clone();
	}
	@Override
	public int compareTo(RowEntity other)
	{
		if(entityType.equals(EntityType.PLAYER))
		{
			final int compareByPlayerType = playerType.compareTo(other.playerType);
			if(compareByPlayerType != 0)
				return 0 - compareByPlayerType;
		}
		final int compareById = Integer.compare(id, other.id);
		if(compareById != 0)
			return compareById;
		return entity.compareTo(other.entity);
	}
}
