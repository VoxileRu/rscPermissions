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
		if(this.world == null || "".equals(this.world) || "*".equals(this.world))
			return true;
		final String instantiated = (instantiator != null && !"".equals(instantiator)) ?
			this.world.replaceAll(Settings.instantiatorRegExp, instantiator) :
			this.world;
		return wildcardTesting(world.getName(), instantiated);
	}
	private boolean IsRegionApplicable(Set<String> regions, String instantiator)
	{
		if(this.region == null || "".equals(this.region) || "*".equals(this.region))
			return true;
		final String instantiated = (instantiator != null && !"".equals(instantiator)) ?
			this.region.replaceAll(Settings.instantiatorRegExp, instantiator) :
			this.region;
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
	public static Destination[] ParseDestinations(String destinations)
	{
		if(destinations == null)
			return new Destination[] { new Destination() };
		if(destinations.isEmpty())
			return new Destination[] { new Destination() };
		final String[] destinationsList = destinations.split("\\s*(?:;|,|\\r|\\n)+\\s*");
		final ArrayList<Destination> result = new ArrayList(destinationsList.length);
		for(int nDestination = 0; nDestination < destinationsList.length; nDestination += 1)
			if(destinationsList[nDestination].isEmpty() == false)
				result.add(ParseDestination(destinationsList[nDestination]));
		return result.toArray(new Destination[result.size()]);
	}
	private static final Pattern patternDestination = Pattern.compile(
		"<destination>" + "(?:((?:\\w|\\*|\\?)*):)?((?:\\w|\\*|\\?)*)?(?:@((?:\\w|\\*|\\?)*))?" + "</destination>");
	private static Destination ParseDestination(String destination)
	{
		Matcher match = patternDestination.matcher("<destination>" + destination + "</destination>");
		if(match.find())
		{
			final String group1 = match.group(1);
			final String group2 = match.group(2);
			final String group3 = match.group(3);
			final String region   = (group1 == null || "".equals(group1)) ? "*" : group1;
			final String world    = (group2 == null || "".equals(group2)) ? "*" : group2;
			final String serverId = (group3 == null || "".equals(group3)) ? "*" : group3;
			return new Destination(region, world, serverId);
		}
		return new Destination();
	}
}