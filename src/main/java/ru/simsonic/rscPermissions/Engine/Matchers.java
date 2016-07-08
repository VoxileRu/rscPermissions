package ru.simsonic.rscPermissions.Engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.simsonic.rscPermissions.API.Settings;

public final class Matchers
{
	private static final Pattern PATTERN_FOR_NICKNAME      = Pattern.compile(Settings.REGEXP_NICKNAME);
	private static final Pattern PATTERN_FOR_UUID          = Pattern.compile(Settings.REGEXP_UUID_DASH);
	private static final Pattern PATTERN_FOR_UUID_DASHLESS = Pattern.compile(Settings.REGEXP_UUID);
	private static final Pattern PATTERN_FOR_IPADDR        = Pattern.compile(Settings.REGEXP_IPADDR);
	private static final Pattern PATTERN_FOR_SUBNETMASK    = Pattern.compile(Settings.REGEXP_SUBNET);
	public static boolean isCorrectNickname(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		return PATTERN_FOR_NICKNAME.matcher(entityName).matches();
	}
	public static boolean isCorrectUUID(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		entityName = entityName.toLowerCase();
		return PATTERN_FOR_UUID.matcher(entityName).matches();
	}
	public static boolean isCorrectDashlessUUID(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		entityName = entityName.toLowerCase();
		return PATTERN_FOR_UUID_DASHLESS.matcher(entityName).matches();
	}
	public static String uuidRemoveDashes(String uuid) throws IllegalArgumentException
	{
		if(!isCorrectUUID(uuid) && !isCorrectDashlessUUID(uuid))
			throw new IllegalArgumentException("Invalid UUID format");
		return uuid.replace("-", "").toLowerCase();
		
	}
	public static String uuidAddDashes(String uuid) throws IllegalArgumentException
	{
		if(!isCorrectUUID(uuid) && !isCorrectDashlessUUID(uuid))
			throw new IllegalArgumentException("Invalid UUID format");
		final Matcher matcher = PATTERN_FOR_UUID_DASHLESS.matcher(uuidRemoveDashes(uuid));
		return matcher.replaceAll("$1-$2-$3-$4-$5").toLowerCase();
	}
	
	public static boolean isCorrectWildcard(String wildcard)
	{
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
		*/
		return false;
	}
	public static boolean isCorrectSubnetMask(String wildcard)
	{
		/*
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
		return false;
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
}
