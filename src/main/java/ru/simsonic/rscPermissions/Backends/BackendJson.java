package ru.simsonic.rscPermissions.Backends;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import ru.simsonic.rscPermissions.DataTypes.DatabaseContents;
import ru.simsonic.rscPermissions.DataTypes.RowEntity;
import ru.simsonic.rscPermissions.DataTypes.RowInheritance;
import ru.simsonic.rscPermissions.DataTypes.RowPermission;

public class BackendJson
{
	private final File workingDir;
	public BackendJson(File workingDir)
	{
		this.workingDir = workingDir;
	}
	public synchronized DatabaseContents retrieveContents()
	{
		final DatabaseContents result = new DatabaseContents();
		result.entities    = fetchEntities();
		result.permissions = fetchPermissions();
		result.inheritance = fetchInheritance();
		return result;
	}
	private final static String localEntitiesFile    = "entities.json";
	private final static String localPermissionsFile = "permissions.json";
	private final static String localInheritanceFile = "inheritance.json";
	private RowEntity[] fetchEntities()
	{
		final Gson gson = new Gson();
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(workingDir, localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowEntity[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
	private RowPermission[] fetchPermissions()
	{
		final Gson gson = new Gson();
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(workingDir, localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowPermission[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
	private RowInheritance[] fetchInheritance()
	{
		final Gson gson = new Gson();
		try
		{
			JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
				new File(workingDir, localEntitiesFile)), Charset.forName("UTF-8")));
			return gson.fromJson(jr, RowInheritance[].class);
		} catch(FileNotFoundException ex) {
		}
		return null;
	}
}
