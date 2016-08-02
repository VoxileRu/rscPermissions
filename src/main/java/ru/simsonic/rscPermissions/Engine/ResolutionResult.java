package ru.simsonic.rscPermissions.Engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.API.RowEntity;
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
		final String separator = Pattern.quote(new String(new char[] { Settings.SHOW_GROUP_LEVEL }));
		for(String group : groups)
		{
			final String[] splitted = group.split(separator);
			list.add(splitted[splitted.length - 1]);
		}
		return list.toArray(new String[list.size()]);
	}
	public RowEntity[] getApplicableRows()
	{
		return null;
	}
}
