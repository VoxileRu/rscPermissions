package ru.simsonic.rscPermissions.Backends;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowEntity[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
	public RowPermission[] fetchPermissions()
	{
		final Gson gson = new Gson();
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowPermission[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
	public RowInheritance[] fetchInheritance()
	{
		final Gson gson = new Gson();
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowInheritance[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
}
