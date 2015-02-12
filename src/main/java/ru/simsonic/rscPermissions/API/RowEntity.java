package ru.simsonic.rscPermissions.API;
import java.sql.Timestamp;

public class RowEntity implements Cloneable
{
	public int        id;
	public String     entity;
	public EntityType entityType;
	public String     prefix;
	public String     suffix;
	public Timestamp  lifetime;
	@Override
	public RowEntity clone() throws CloneNotSupportedException
	{
		 return (RowEntity)super.clone();
	}
}
