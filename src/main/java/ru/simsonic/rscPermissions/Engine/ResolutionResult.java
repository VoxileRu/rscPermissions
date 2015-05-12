package ru.simsonic.rscPermissions.Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import ru.simsonic.rscPermissions.API.Settings;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class ResolutionResult
{
	public String prefix;
	public String suffix;
	public Map<String, Boolean> permissions;
	protected Set<String> groups;
	public boolean hasPermission(String permission)
	{
		for(Map.Entry<String, Boolean> entry : permissions.entrySet())
			if(entry.getKey().equals(permission))
				return entry.getValue();
		return false;
	}
	public boolean hasPermissionWC(String permission)
	{
		for(Map.Entry<String, Boolean> entry : permissions.entrySet())
		{
			final String key = entry.getKey();
			if(key.equals(permission))
				return entry.getValue();
			if(key.contains("*") && GenericChatCodes.wildcardMatch(permission, key))
				return entry.getValue();
		}
		return false;
	}
	public Set<String> getOrderedGroups()
	{
		return Collections.unmodifiableSet(groups);
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
