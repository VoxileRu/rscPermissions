package ru.simsonic.rscPermissions.DataTypes;

public class RowPermission extends ConditionalRow
{
	public String entity;
	public EntityType entityType;
	public String permission;
	public boolean value;
	@Override
	public Table getTable()
	{
		return Table.permissions;
	}
}