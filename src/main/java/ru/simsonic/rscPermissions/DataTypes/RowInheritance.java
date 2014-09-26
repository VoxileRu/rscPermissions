package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;
import java.util.Arrays;
import ru.simsonic.rscPermissions.Bukkit.BukkitPluginConfiguration;
import ru.simsonic.utilities.LanguageUtility;

public class RowInheritance extends AbstractRow implements Comparable<RowInheritance>
{
	public String entity;
	public String parent;
	public String instance;
	public EntityType childType;
	public int priority;
	public Destination destination;
	public int expirience;
	public Timestamp lifetime;
	public void deriveInstance()
	{
		if(parent != null)
		{
			final String[] splitted = parent.split(BukkitPluginConfiguration.separatorRegExp);
			if(splitted.length > 1)
			{
				parent = LanguageUtility.glue(Arrays.copyOf(splitted, splitted.length - 1), BukkitPluginConfiguration.separator);
				instance = splitted[splitted.length - 1];
				return;
			}
		}
		instance = null;
	}
	@Override
	public int compareTo(RowInheritance t)
	{
		return (priority != t.priority) ? priority - t.priority : parent.compareTo(t.parent);
	}
	@Override
	public Table getTable()
	{
		return Table.inheritance;
	}
}