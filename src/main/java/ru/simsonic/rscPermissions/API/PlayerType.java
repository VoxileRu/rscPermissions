package ru.simsonic.rscPermissions.API;
import ru.simsonic.rscPermissions.Engine.Matchers;

public enum PlayerType
{
	NAME(0),                // 16 chars max
	UUID(1),                // 550e8400-e29b-41d4-a716-446655440000
	DASHLESS_UUID(2),       // 550e8400e29b41d4a716446655440000
	INTERNET_WILDCARD(3),   // 192.168.*.*
	INTERNET_SUBNETMASK(4), // 192.168.0.0/16
	INAPPLICABLE(-1);
	private final int value;
	private PlayerType(int value)
	{
		this.value = value;
	}
	public static PlayerType byValue(int value)
	{
		for(PlayerType constant : PlayerType.values())
			if(constant.value == value)
				return constant;
		return INAPPLICABLE;
	}
	public static PlayerType scanPlayerEntity(String entity)
	{
		if(entity == null || "".equals(entity))
			return NAME;
		if(Matchers.isCorrectNickname(entity))
			return NAME;
		if(Matchers.isCorrectUUID(entity))
			return UUID;
		if(Matchers.isCorrectDashlessUUID(entity))
			return DASHLESS_UUID;
		if(Matchers.isCorrectWildcard(entity))
			return INTERNET_WILDCARD;
		if(Matchers.isCorrectSubnetMask(entity))
			return INTERNET_SUBNETMASK;
		return INAPPLICABLE;
	}
	public boolean isEntityApplicable(String entity, String identifier)
	{
		if(entity == null || "".equals(entity) || identifier == null || "".equals(identifier))
			return false;
		switch(this)
		{
			case NAME:
				return identifier.equals(entity);
			case UUID:
				identifier = identifier.replace("-", "");
			case DASHLESS_UUID:
				return entity.equalsIgnoreCase(identifier);
		}
		return false;
	}
}
