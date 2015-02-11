package ru.simsonic.rscPermissions.DataTypes;

public enum EntityType
{
	group(0),
	player(1),
	unknown(-1);
	private final int value;
	private EntityType(int value)
	{
		this.value = value;
	}
	public static EntityType byValue(int value)
	{
		for(EntityType constant : EntityType.values())
			if(constant.value == value)
				return constant;
		return unknown;
	}
}
