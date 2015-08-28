package ru.simsonic.rscPermissions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import ru.simsonic.rscPermissions.Engine.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Engine.Backends.BackendJson;
import ru.simsonic.rscPermissions.Engine.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.Engine.InternalCache;
import ru.simsonic.rscPermissions.Engine.ResolutionResult;

public class IndependentMain
{
	private static final BackendJson     localJsn = new BackendJson(new File("../"));
	private static final BackendDatabase remoteDb = new BackendDatabase(Logger.getGlobal());
	private static final InternalCache   intCache = new InternalCache();
	@SuppressWarnings({"DeadBranch", "UnusedAssignment"})
	public static void main(String args[])
	{
		System.out.println("rscPermissions - Bukkit superperms plugin © SimSonic");
		System.out.println("https://github.com/SimSonic/rscPermissions/");
		// TESTING HERE
		remoteDb.initialize(null,
			"", // DATABASE
			"", // USERNAME
			"", // PASSWORD
			"rscp_");
		if(remoteDb.connect())
		{
			System.out.println("Retrieving permissions from database into json files.");
			final DatabaseContents contents = remoteDb.retrieveContents();
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
		final ResolutionResult result = intCache.resolvePlayer("87f946d8212440539d685eab07f8e266");
		// Sorted output
		ArrayList<String> perms = new ArrayList<>(result.permissions.keySet());
		Collections.sort(perms);
		for(String key : perms)
			System.out.println("Permission: " + key + " = " + result.permissions.get(key));
		for(String group : result.getOrderedGroups())
			System.out.println("Parent: " + group);
		System.out.println("Prefix: " + result.prefix);
		System.out.println("Suffix: " + result.suffix);
		System.out.println("Done.");
	}
}
