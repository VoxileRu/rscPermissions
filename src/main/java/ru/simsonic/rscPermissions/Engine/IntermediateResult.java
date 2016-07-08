package ru.simsonic.rscPermissions.Engine;

import java.util.List;
import java.util.Map;

class IntermediateResult
{
	ResolutionParams     params;
	String               prefix = "";
	String               suffix = "";
	List<String>         groups;
	Map<String, Boolean> permissions;
	/*
	void sortPermissions()
	{
		final Map<String, Boolean> result = new TreeMap<>();
		final ArrayList<String>    sorted = new ArrayList<>(permissions.keySet());
		Collections.sort(sorted);
		for(String key : sorted)
			result.put(key, permissions.get(key));
		this.permissions = result;
	}
	*/
}
