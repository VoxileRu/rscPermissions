package ru.simsonic.rscPermissions.DataTypes;

public class RowEntity extends AbstractRow
{
	public String entity;
	public EntityType entityType;
	public String prefix;
	public String suffix;
	@Override
	public Table getTable()
	{
		return Table.entities;
	}
}