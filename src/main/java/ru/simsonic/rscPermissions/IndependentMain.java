package ru.simsonic.rscPermissions;

import java.io.File;
import java.util.Map;
import ru.simsonic.rscPermissions.Engine.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Engine.Backends.BackendJson;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class IndependentMain
{
	private static final BackendJson     localJsn = new BackendJson(new File("../"));
	private static final BackendDatabase database = new BackendDatabase();
	private static final InternalCache   intCache = new InternalCache();
	@SuppressWarnings({"DeadBranch", "UnusedAssignment"})
	public static void main(String args[])
	{
		System.out.println("rscPermissions - Bukkit superperms plugin © SimSonic");
		System.out.println("https://github.com/SimSonic/rscPermissions/");
		System.out.println();
		// OK, IT'S SECURE FROM YOU :)
		database.initialize(null,
			"SCOUT:3306/rscp_testing", // DATABASE
			"rscp_testing",            // USERNAME
			"rscp_testing",            // PASSWORD
			"rscp_");
		if(database.connect())
		{
			System.out.println("Retrieving permissions from database into json files.");
			DatabaseContents contents = database.retrieveContents();
			if(contents.isEmpty())
			{
				database.insertExampleRows();
				contents = database.retrieveContents();
			}
			contents.normalize();
			localJsn.cleanup();
			localJsn.saveContents(contents);
		}
		System.out.println("Loading permissions from json files.");
		final DatabaseContents contents = localJsn.retrieveContents();
		System.out.println("Filter and calculating permission tree.");
		contents.filterServerId("Primary").filterLifetime();
		if(contents.isEmpty())
		{
			System.out.println("Permission database is empty, stopping.");
			return;
		}
		intCache.setDefaultGroup("Default", true, true);
		intCache.fill(contents);
		final ResolutionResult result = intCache.resolvePlayer("");
		// Sorted output
		for(Map.Entry<String, Boolean> entry : result.getPermissions().entrySet())
			System.out.println("Permission: " + entry.getKey() + " = " + entry.getValue());
		System.out.println();
		for(String group : result.getOrderedGroups())
			System.out.println("Parent: " + group);
		System.out.println();
		System.out.println("Prefix: " + result.getPrefix());
		System.out.println("Suffix: " + result.getSuffix());
		System.out.println();
		System.out.println("Done.");
	}
}
