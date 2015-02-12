package ru.simsonic.rscPermissions;
import java.io.File;
import java.util.logging.Logger;
import ru.simsonic.rscPermissions.Backends.BackendDatabase;
import ru.simsonic.rscPermissions.Backends.BackendJson;
import ru.simsonic.rscPermissions.Backends.DatabaseContents;
import ru.simsonic.rscPermissions.InternalCache.InternalCache;

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
			final DatabaseContents contents = remoteDb.retrieveContents();
			contents.normalize();
			localJsn.cleanup();
			localJsn.saveContents(contents);
		}
		intCache.fill(localJsn.retrieveContents());
	}
}
