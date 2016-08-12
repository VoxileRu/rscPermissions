package ru.simsonic.rscPermissions.Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.Settings;

public class ResolutionResult extends IntermediateResult
{
	public String getPrefix()
	{
		return prefix;
	}
	public String getSuffix()
	{
		return suffix;
	}
	public Map<String, Boolean> getPermissions()
	{
		return Collections.unmodifiableMap(permissions);
	}
	public boolean hasPermission(String permission)
	{
		for(Map.Entry<String, Boolean> entry : permissions.entrySet())
			if(entry.getKey().equals(permission))
				return entry.getValue();
		return false;
	}
	public boolean hasPermissionWildcard(String permission)
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
	public List<String> getOrderedGroups()
	{
		return Collections.unmodifiableList(groups);
	}
	public String[] getDeorderedGroups()
	{
		final ArrayList<String> list = new ArrayList(groups.size());
		for(String group : groups)
		{
			final String[] splitted = group.split(Settings.REGEXP_GROUP_LVL);
			list.add(splitted[splitted.length - 1]);
		}
		return list.toArray(new String[list.size()]);
	}
	public Set<String> getUniqueGroups()
	{
		final Set<String> result = new TreeSet<>();
		for(String group : groups)
		{
			final String[] splitted = group.split(Settings.REGEXP_GROUP_LVL);
			result.add(splitted[splitted.length - 1]);
		}
		return result;
	}
}
