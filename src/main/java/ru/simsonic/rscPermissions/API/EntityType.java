package ru.simsonic.rscPermissions.API;

public enum EntityType
{
	GROUP(0),
	PLAYER(1),
	UNKNOWN(-1);
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
		return UNKNOWN;
	}
}
