package ru.simsonic.rscPermissions.API;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum PlayerType
{
	NAME(0),                // 16 chars max
	HYPHENATED_UUID(1),     // 550e8400-e29b-41d4-a716-446655440000
	DEHYPHENATED_UUID(2),   // 550e8400e29b41d4a716446655440000
	INTERNET_WILDCARD(3),   // 192.168.*.*
	INTERNET_SUBNETMASK(4), // 192.168.1.0/16
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
	private static final Pattern nicknameRegExp     = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$");
	private static final Pattern hyphenatedRegExp   = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
	private static final Pattern dehyphenatedRegExp = Pattern.compile("^[0-9a-f]{32}$");
	private static final Pattern ipWildcardRegExp   = Pattern.compile("^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)$");
	private static final Pattern ipSubnetMaskRegExp = Pattern.compile("^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])"
		+ "(?:/([0-9]|[1-2][0-9]|3[0-2]))$");
	public static PlayerType scanPlayerEntity(String entity)
	{
		if(entity == null || "".equals(entity))
			return NAME;
		if(nicknameRegExp.matcher(entity).matches())
			return NAME;
		if(hyphenatedRegExp.matcher(entity.toLowerCase()).matches())
			return HYPHENATED_UUID;
		if(dehyphenatedRegExp.matcher(entity.toLowerCase()).matches())
			return DEHYPHENATED_UUID;
		/*
		final Matcher mIP1 = ipWildcardRegExp.matcher(entity);
		if(mIP1.matches())
		{
			final String a1 = mIP1.group(1);
			final String a2 = mIP1.group(2);
			final String a3 = mIP1.group(3);
			final String a4 = mIP1.group(4);
			// TO DO
			long address = 0, mask = 0;
			return INTERNET_WILDCARD;
		}
		final Matcher mIP2 = ipSubnetMaskRegExp.matcher(entity);
		if(mIP2.matches())
		{
			final String a1 = mIP1.group(1);
			final String a2 = mIP1.group(2);
			final String a3 = mIP1.group(3);
			final String a4 = mIP1.group(4);
			final String sn = mIP1.group(5);
			// TO DO
			long address = 0, mask = 0;
			return INTERNET_SUBNETMASK;
		}
		*/
		return INAPPLICABLE;
	}
	/*
	public static void getAddressDetails(String entity, RowPermission row)
	{
		final Matcher mIP1 = ipWildcardRegExp.matcher(entity);
		if(mIP1.matches())
		{
			final String a1 = mIP1.group(1);
			final String a2 = mIP1.group(2);
			final String a3 = mIP1.group(3);
			final String a4 = mIP1.group(4);
			// TO DO
			if("*".equals(a1))
			{
			} else {
			}
			long address = 0, mask = 0;
		}
		final Matcher mIP2 = ipSubnetMaskRegExp.matcher(entity);
		if(mIP2.matches())
		{
			final String a1 = mIP1.group(1);
			final String a2 = mIP1.group(2);
			final String a3 = mIP1.group(3);
			final String a4 = mIP1.group(4);
			final String sn = mIP1.group(5);
			// TO DO
			long address = 0, mask = 0;
		}
	}
	*/
	public boolean isEntityApplicable(String entity, String identifier)
	{
		if(entity == null || "".equals(entity) || identifier == null || "".equals(identifier))
			return false;
		switch(this)
		{
			case NAME:
				return identifier.equals(entity);
			case HYPHENATED_UUID:
				identifier = identifier.replace("-", "");
			case DEHYPHENATED_UUID:
				return entity.equalsIgnoreCase(identifier);
		}
		return false;
	}
}
