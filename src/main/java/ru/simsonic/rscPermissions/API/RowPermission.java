package ru.simsonic.rscPermissions.API;

public final class RowPermission extends ConditionalRow implements Cloneable, Comparable<RowPermission>
{
	public           String      permission;
	public           boolean     value;
	public transient RowEntity   entityObject;
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
		return Long.compare(id, other.id);
	}
}
