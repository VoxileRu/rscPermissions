package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;
import ru.simsonic.rscPermissions.DataTypes.RowEntity.EntityType;

public class RowInheritance extends AbstractRow implements Comparable<RowInheritance>
{
	public String entity;
	public String parent;
	public String instance;
	public EntityType child_type;
	public int priority;
	public Destination destination;
	public int expirience;
	public Timestamp lifetime;
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