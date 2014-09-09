package ru.simsonic.rscPermissions.Backends;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;
import ru.simsonic.rscPermissions.InternalCache.LocalCacheData;

public interface Backend
{
	public abstract boolean canRead();
	public abstract boolean canWrite();

	public abstract void             fetchIntoCache(LocalCacheData cache);
	public abstract RowEntity[]      fetchEntities();
	public abstract RowPermission[]  fetchPermissions();
	public abstract RowInheritance[] fetchInheritance();
	public abstract RowLadder[]      fetchLadders();

	public abstract void insertExampleRows();
	public abstract void updateEntityText(String entity, boolean entity_type, String text, boolean isPrefix);
	public abstract void setUserRank(String user, String ladder, int rank);
	public abstract void dropUserFromLadder(String user, String ladder);
	public abstract void addUserParentGroup(String user, String newGroup);
}