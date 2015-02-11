package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;

public class RowPermission extends AbstractRow
{
	public String entity;
	public EntityType entityType;
	public String permission;
	public boolean value;
	public Destination destination;
	public int expirience;
	public Timestamp lifetime;
}
