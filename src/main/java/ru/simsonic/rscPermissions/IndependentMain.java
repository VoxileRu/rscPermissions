package ru.simsonic.rscPermissions;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;
import ru.simsonic.rscPermissions.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Backends.BackendJson;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;
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
		contents.filterServerId("localtest").filterLifetime();
		if(contents.isEmpty())
		{
			System.out.println("Permission database is empty, stopping.");
			return;
		}
		intCache.fill(contents);
		final ResolutionResult result = intCache.resolvePlayer("rscpTester");
		for(Map.Entry<String, Boolean> perm : result.permissions.entrySet())
			System.out.println(perm.getKey() + " = " + perm.getValue());
		if(result.prefix != null)
			System.out.println("Prefix = " + result.prefix);
		if(result.suffix != null)
			System.out.println("Suffix = " + result.suffix);
		System.out.println("Done.");
	}
}
