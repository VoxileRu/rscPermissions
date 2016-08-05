package ru.simsonic.rscPermissions.API;

public final class RowEntity extends GenericRow implements Cloneable, Comparable<RowEntity>
{
	public           String           prefix;
	public           String           suffix;
	public transient RowPermission[]  permissions;
	public transient RowInheritance[] inheritance;
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
