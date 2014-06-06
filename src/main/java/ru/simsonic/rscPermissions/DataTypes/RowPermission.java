package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;
import ru.simsonic.rscPermissions.DataTypes.RowEntity.EntityType;

public class RowPermission extends AbstractRow
{
	public String entity;
	public EntityType entity_type;
	public String permission;
	public boolean value;
	public Destination destination;
	public int expirience;
	public Timestamp lifetime;
	@Override
	public Table getTable()
	{
		return Table.permissions;
	}
}