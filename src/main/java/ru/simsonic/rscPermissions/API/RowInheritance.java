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
			final String[] splitted = parent.split(Settings.REGEXP_INSTANCE);
			if(splitted.length > 1)
			{
				parent = GenericChatCodes.glue(Arrays.copyOf(splitted, splitted.length - 1), Settings.INSTANCE_SEP);
				instance = splitted[splitted.length - 1];
				return;
			}
		}
		instance = "";
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
		return Integer.compare(id, other.id);
	}
}
