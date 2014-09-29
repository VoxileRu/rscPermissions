package ru.simsonic.rscPermissions.DataTypes;
import java.sql.Timestamp;

public abstract class ConditionalRow extends AbstractRow
{
	public Destination destination;
	public int expirience;
	public Timestamp lifetime;
}
