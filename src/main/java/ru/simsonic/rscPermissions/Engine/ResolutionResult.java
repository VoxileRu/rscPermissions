package ru.simsonic.rscPermissions.Engine;
import java.util.Map;
import java.util.Set;

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
}
