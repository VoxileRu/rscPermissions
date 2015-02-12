package ru.simsonic.rscPermissions.InternalCache;
import ru.simsonic.rscPermissions.API.RowPermission;

public class ResolutionResult
{
	public String prefix;
	public String suffix;
	public RowPermission[] permissions;
	public boolean hasPermission(String permission)
	{
		for(RowPermission row : permissions)
			if(permission.equals(row.permission))
				return row.value;
		return false;
	}
}
