package ru.simsonic.rscPermissions.Importers;
import java.util.ArrayList;
import ru.simsonic.rscPermissions.API.RowEntity;
import ru.simsonic.rscPermissions.API.RowInheritance;
import ru.simsonic.rscPermissions.API.RowPermission;

public abstract class BaseImporter
{
	protected ArrayList<RowEntity> imported_e = new ArrayList<>();
	protected ArrayList<RowPermission> imported_p = new ArrayList<>();
	protected ArrayList<RowInheritance> imported_i = new ArrayList<>();
	public RowEntity[] getEntities()
	{
		return imported_e.toArray(new RowEntity[imported_e.size()]);
	}
	public RowPermission[] getPermissions()
	{
		return imported_p.toArray(new RowPermission[imported_p.size()]);
	}
	public RowInheritance[] getInheritance()
	{
		return imported_i.toArray(new RowInheritance[imported_i.size()]);
	}
}
