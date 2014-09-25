package ru.simsonic.rscPermissions.InternalCache;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowLadder;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;

public interface AbstractPermissionsCache
{
	public int ImportEntities(RowEntity[] rows);
	public int ImportPermissions(RowPermission[] rows);
	public int ImportInheritance(RowInheritance[] rows);
	public int ImportLadders(RowLadder[] rows);
}
