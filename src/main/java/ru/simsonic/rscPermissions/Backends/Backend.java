package ru.simsonic.rscPermissions.Backends;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.InternalCache.AbstractPermissionsCache;

public interface Backend
{
	public abstract boolean canRead();
	public abstract boolean canWrite();

	public abstract void             fetchIntoCache(AbstractPermissionsCache cache);
	public abstract RowEntity[]      fetchEntities();
	public abstract RowPermission[]  fetchPermissions();
	public abstract RowInheritance[] fetchInheritance();

	public abstract void insertExampleRows();
	public abstract void updateEntityText(String entity, boolean entity_type, String text, boolean isPrefix);
	public abstract void addUserParentGroup(String user, String newGroup);
}
