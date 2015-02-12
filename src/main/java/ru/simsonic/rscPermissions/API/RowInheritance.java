package ru.simsonic.rscPermissions.API;
import java.sql.Timestamp;
import java.util.Arrays;
import ru.simsonic.rscUtilityLibrary.TextProcessing.GenericChatCodes;

public class RowInheritance implements Cloneable, Comparable<RowInheritance>
{
	public int         id;
	public String      entity;
	public String      parent;
	public String      instance;
	public EntityType  childType;
	public int         priority;
	public Destination destination;
	public int         expirience;
	public Timestamp   lifetime;
	public transient String destinationSource;
	public void deriveInstance()
	{
		if(parent != null)
		{
			final String[] splitted = parent.split(Settings.separatorRegExp);
			if(splitted.length > 1)
			{
				parent = GenericChatCodes.glue(Arrays.copyOf(splitted, splitted.length - 1), Settings.separator);
				instance = splitted[splitted.length - 1];
				return;
			}
		}
		instance = null;
	}
	@Override
	public RowInheritance clone() throws CloneNotSupportedException
	{
		 return (RowInheritance)super.clone();
	}
	@Override
	public int compareTo(RowInheritance t)
	{
		return (priority != t.priority) ? priority - t.priority : parent.compareTo(t.parent);
	}
}
