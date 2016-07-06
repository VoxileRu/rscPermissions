package ru.simsonic.rscPermissions.API;

import java.sql.Timestamp;
import java.util.Arrays;
import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;

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
	public transient String     splittedId;
	public transient PlayerType playerType;
	public transient String     destinationSource;
	public transient RowEntity  entityChild;
	public transient RowEntity  entityParent;
	public void deriveInstance()
	{
		if(parent != null)
		{
			final String[] splitted = parent.split(Settings.SEPARATOR_REGEXP);
			if(splitted.length > 1)
			{
				parent = GenericChatCodes.glue(Arrays.copyOf(splitted, splitted.length - 1), Settings.SEPARATOR);
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
	public int compareTo(RowInheritance other)
	{
		return (priority != other.priority)
			? priority - other.priority
			: parent.compareTo(other.parent);
	}
}
