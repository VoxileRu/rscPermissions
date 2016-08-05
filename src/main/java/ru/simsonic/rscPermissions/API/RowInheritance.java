package ru.simsonic.rscPermissions.API;

import java.util.Arrays;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;

public final class RowInheritance extends ConditionalRow implements Cloneable, Comparable<RowInheritance>
{
	public           String      parent;
	public           String      instance;
	public           int         priority;
	public transient RowEntity   entityChild;
	public transient RowEntity   entityParent;
	public void deriveInstance()
	{
		if(parent != null)
		{
			final String[] splitted = splitIntoNameAndInstance(parent);
			parent   = splitted[0];
			instance = splitted[1];
		}
		instance = "";
	}
	public static String[] splitIntoNameAndInstance(String parent)
	{
		if(parent == null)
			parent = "";
		final String[] result   = new String[2];
		final String[] splitted = parent.split(Settings.REGEXP_INSTANCE);
		if(splitted.length > 1)
		{
			result[0] = GenericChatCodes.glue(Arrays.copyOf(splitted, splitted.length - 1), Settings.INSTANCE_SEP);
			result[1] = splitted[splitted.length - 1];
		} else {
			result[0] = parent;
			result[1] = "";
		}
		return result;
	}
	public static String mergeNameAndInstance(String parent, String instance)
	{
		return parent
			+ (instance != null && !"".equals(instance)
				? Settings.INSTANCE_SEP + instance
				: "");
	}
	public String getParentWithInstance()
	{
		return this.parent + (instance.isEmpty() ? "" : "." + instance);
	}
	@Override
	public RowInheritance clone() throws CloneNotSupportedException
	{
		 return (RowInheritance)super.clone();
	}
	@Override
	public int compareTo(RowInheritance other)
	{
		final int compareByPriority = Integer.compare(priority, other.priority);
		if(compareByPriority != 0)
			return compareByPriority;
		final int compareByParent = parent.toLowerCase().compareTo(other.parent.toLowerCase());
		if(compareByParent != 0)
			return compareByParent;
		if(splittedId != null && other.splittedId != null)
		{
			final int compareBySplittedId = splittedId.compareTo(other.splittedId);
			if(compareBySplittedId != 0)
				return compareBySplittedId;
		}
		return Long.compare(id, other.id);
	}
}
