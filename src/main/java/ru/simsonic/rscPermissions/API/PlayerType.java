package ru.simsonic.rscPermissions.API;

import ru.simsonic.rscMinecraftLibrary.Bukkit.GenericChatCodes;
import ru.simsonic.rscPermissions.Engine.Matchers;

public enum PlayerType
{
	NAME               ( 0), // 16 chars [_a-zA-Z0-9] max
	UUID               ( 1), // 550e8400-e29b-41d4-a716-446655440000
	DASHLESS_UUID      ( 2), // 550e8400e29b41d4a716446655440000
	INTERNET_WILDCARD  ( 3), // 192.168.*.*
	INTERNET_SUBNETMASK( 4), // 192.168.0.0/16
	INAPPLICABLE       (-1),
	;
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
	public static String normalize(String entity)
	{
		if(entity == null || "".equals(entity))
			return "";
		if(Matchers.isCorrectDashlessUUID(entity))
			return Matchers.uuidAddDashes(entity);
		return entity;
	}
	public boolean isEntityApplicable(String entity, String identifier)
	{
		if(entity == null || "".equals(entity) || identifier == null || "".equals(identifier))
			return false;
		try
		{
			switch(this)
			{
				case NAME:
					return entity.contains("*")
						? GenericChatCodes.wildcardMatch(identifier, entity)
						: identifier.equals(entity);
				case DASHLESS_UUID:
					if(Matchers.isCorrectUUID(identifier))
						identifier = Matchers.uuidRemoveDashes(identifier);
					if(Matchers.isCorrectDashlessUUID(identifier))
						return entity.equalsIgnoreCase(identifier);
					break;
				case UUID:
					if(Matchers.isCorrectDashlessUUID(identifier))
						identifier = Matchers.uuidAddDashes(identifier);
					if(Matchers.isCorrectUUID(identifier))
						return entity.equalsIgnoreCase(identifier);
					break;
				case INTERNET_WILDCARD:
				case INTERNET_SUBNETMASK:
					// TO DO HERE
					return false;
				case INAPPLICABLE:
				default:
					break;
			}
		} catch(IllegalArgumentException ex) {
		}
		return false;
	}
}
