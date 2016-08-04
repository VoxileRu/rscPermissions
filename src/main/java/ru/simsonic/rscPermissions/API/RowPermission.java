package ru.simsonic.rscPermissions.API;

import java.sql.Timestamp;

public class RowPermission implements Cloneable, Comparable<RowPermission>
{
	public int         id;
	public String      entity;
	public EntityType  entityType;
	public String      permission;
	public boolean     value;
	public Destination destination;
	public int         expirience;
	public Timestamp   lifetime;
	public transient String     splittedId;
	public transient PlayerType playerType;
	public transient String     destinationSource;
	public transient RowEntity  entityObject;
	@Override
	public RowPermission clone() throws CloneNotSupportedException
	{
		 return (RowPermission)super.clone();
	}
	@Override
	public int compareTo(RowPermission other)
	{
		final int compareByPermission = permission.toLowerCase().compareTo(other.permission.toLowerCase());
		if(compareByPermission != 0)
			return compareByPermission;
		if(splittedId != null && other.splittedId != null)
		{
			final int compareBySplittedId = splittedId.compareTo(other.splittedId);
			if(compareBySplittedId != 0)
				return compareBySplittedId;
		}
		return Integer.compare(id, other.id);
	}
}
