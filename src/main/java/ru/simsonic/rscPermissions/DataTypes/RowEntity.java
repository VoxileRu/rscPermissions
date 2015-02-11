package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;

public class RowEntity extends AbstractRow
{
	public String entity;
	public EntityType entityType;
	public String prefix;
	public String suffix;
	public Timestamp lifetime;
}
