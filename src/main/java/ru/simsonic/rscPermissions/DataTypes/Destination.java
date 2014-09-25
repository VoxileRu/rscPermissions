package ru.simsonic.rscPermissions.DataTypes;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.World;
import ru.simsonic.rscPermissions.Settings;
import ru.simsonic.utilities.LanguageUtility;

public class Destination
{
	private final String region;
	private final String world;
	private final String serverId;
	private Destination()
	{
		this.region   = null;
		this.world    = null;
		this.serverId = null;
	}
	private Destination(String region, String world, String serverId)
	{
		this.region   = region;
		this.world    = world;
		this.serverId = serverId;
	}
	public boolean IsServerIdApplicable(String serverId)
	{
		return wildcardTesting(serverId, this.serverId);
	}
	public boolean IsLocationApplicable(Location location, Set<String> regions, String instantiator)
	{
		if(location != null)
		{
			if(location.getWorld() != null)
				if(IsWorldApplicable(location.getWorld(), instantiator))
					return IsRegionApplicable(regions, instantiator);
		} else {
			if(this.world == null)
				return IsRegionApplicable(regions, instantiator);
		}
		return false;
	}
	private boolean IsWorldApplicable(World world, String instantiator)
	{
		if(this.world == null || this.world.isEmpty() || "*".equals(this.world))
			return true;
		final String instantiated = (instantiator != null && !instantiator.isEmpty())
			? this.world.replace(Settings.instantiator, instantiator)
			: this.world;
		return wildcardTesting(world.getName(), instantiated);
	}
	private boolean IsRegionApplicable(Set<String> regions, String instantiator)
	{
		if(this.region == null || "".equals(this.region) || "*".equals(this.region))
			return true;
		final String instantiated = (instantiator != null && !"".equals(instantiator))
			? this.region.replace(Settings.instantiator, instantiator)
			: this.region;
		for(String regionId : regions)
			if(wildcardTesting(regionId, instantiated))
				return true;
		return false;
	}
	private static boolean wildcardTesting(String testing, String pattern)
	{
		if(pattern == null || "".equals(pattern))
			return true;
		if(testing == null || "".equals(testing))
			return false;
		return LanguageUtility.wildcardMatch(
			"<wildcard>" + testing.toLowerCase() + "</wildcard>",
			"<wildcard>" + pattern.toLowerCase() + "</wildcard>");
	}
	private static final String  destinationSplitting = "\\s*[;,\\r\\n]+\\s*";
	public static Destination[] ParseDestinations(String destinations)
	{
		if(destinations == null || destinations.isEmpty())
			return new Destination[] { new Destination() };
		final String[] destinationsList = destinations.split(destinationSplitting);
		final ArrayList<Destination> result = new ArrayList(destinationsList.length);
		for(String inList : destinationsList)
			if(inList != null && !inList.isEmpty())
				result.add(ParseDestination(inList));
		return result.toArray(new Destination[result.size()]);
	}
	private static final Pattern patternDestination = Pattern.compile(
		"^(?:((?:\\w|\\*|\\?)*):)?((?:\\w|\\*|\\?)*)?(?:@((?:\\w|\\*|\\?)*))?$");
	private static Destination ParseDestination(String destination)
	{
		final Matcher match = patternDestination.matcher(destination);
		if(match.find())
		{
			final String groupR = match.group(1);
			final String groupW = match.group(2);
			final String groupS = match.group(3);
			final String region   = (groupR == null || "".equals(groupR)) ? "*" : groupR;
			final String world    = (groupW == null || "".equals(groupW)) ? "*" : groupW;
			final String serverId = (groupS == null || "".equals(groupS)) ? "*" : groupS;
			return new Destination(region, world, serverId);
		}
		return new Destination();
	}
}