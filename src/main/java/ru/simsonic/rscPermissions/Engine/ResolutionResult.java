package ru.simsonic.rscPermissions.Engine;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import ru.simsonic.rscPermissions.API.Settings;

public class ResolutionResult
{
	public String prefix;
	public String suffix;
	public Map<String, Boolean> permissions;
	public Set<String> groups;
	public boolean hasPermission(String permission)
	{
		for(Map.Entry<String, Boolean> entry : permissions.entrySet())
			if(entry.getKey().equals(permission))
				return entry.getValue();
		return false;
	}
	public String[] getDeorderedGroups()
	{
		final ArrayList<String> list = new ArrayList(groups.size());
		final String separator = new String(new char[] { Settings.groupLevelTab });
		for(String group : groups)
		{
			String[] splitted = group.split(separator);
			list.add(splitted[splitted.length - 1]);
		}
		return list.toArray(new String[list.size()]);
	}
}
