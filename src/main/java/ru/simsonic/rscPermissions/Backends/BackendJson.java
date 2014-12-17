package ru.simsonic.rscPermissions.Backends;
import com.google.gson.Gson;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;

public class BackendJson
{
	private final static String localEntitiesFile    = "entities.json";
	private final static String localPermissionsFile = "permissions.json";
	private final static String localInheritanceFile = "inheritance.json";
	public RowEntity[] fetchEntities()
	{
		final Gson gson = new Gson();
		return null;
	}
	public RowPermission[] fetchPermissions()
	{
		final Gson gson = new Gson();
		return null;
	}
	public RowInheritance[] fetchInheritance()
	{
		final Gson gson = new Gson();
		return null;
	}
}
