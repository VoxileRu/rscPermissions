package ru.simsonic.rscPermissions.Backends;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
	private final static String entitiesFile    = "entities.json";
	private final static String permissionsFile = "permissions.json";
	private final static String inheritanceFile = "inheritance.json";
	public synchronized DatabaseContents retrieveContents()
	{
		final Gson gson = new Gson();
		final DatabaseContents result = new DatabaseContents();
		// Entities
		try(JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
			new File(workingDir, entitiesFile)), Charset.forName("UTF-8"))))
		{
			result.entities = gson.fromJson(jr, RowEntity[].class);
		} catch(IOException ex) {
		}
		// Permissions
		try(JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
			new File(workingDir, permissionsFile)), Charset.forName("UTF-8"))))
		{
			result.permissions = gson.fromJson(jr, RowPermission[].class);
		} catch(IOException ex) {
		}
		// Inheritance
		try(JsonReader jr = new JsonReader(new InputStreamReader(new FileInputStream(
			new File(workingDir, inheritanceFile)), Charset.forName("UTF-8"))))
		{
			result.inheritance = gson.fromJson(jr, RowInheritance[].class);
		} catch(IOException ex) {
		}
		return result;
	}
	public synchronized void saveContents(DatabaseContents contents)
	{
		final Gson gson = new Gson();
		// Entities
		if(contents.entities == null)
			contents.entities = new RowEntity[] {};
		try(JsonWriter jw = new JsonWriter(new OutputStreamWriter(new FileOutputStream(
			new File(workingDir, entitiesFile)), Charset.forName("UTF-8"))))
		{
			jw.setIndent("\t");
			gson.toJson(contents.entities, RowEntity[].class, jw);
		} catch(IOException ex) {
		}
		// Permissions
		if(contents.permissions == null)
			contents.permissions = new RowPermission[] {};
		try(JsonWriter jw = new JsonWriter(new OutputStreamWriter(new FileOutputStream(
			new File(workingDir, permissionsFile)), Charset.forName("UTF-8"))))
		{
			jw.setIndent("\t");
			gson.toJson(contents.permissions, RowPermission[].class, jw);
		} catch(IOException ex) {
		}
		// Inheritance
		if(contents.inheritance == null)
			contents.inheritance = new RowInheritance[] {};
		try(JsonWriter jw = new JsonWriter(new OutputStreamWriter(new FileOutputStream(
			new File(workingDir, inheritanceFile)), Charset.forName("UTF-8"))))
		{
			jw.setIndent("\t");
			gson.toJson(contents.inheritance, RowInheritance[].class, jw);
		} catch(IOException ex) {
		}
	}
}
