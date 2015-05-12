package ru.simsonic.rscPermissions.Engine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Matchers
{
	private static final String genericSplitter = "\\s*[;,\\r\\n\\s]+\\s*";
	public static String[] genericParse(String multiobject)
	{
		if(multiobject == null)
			multiobject = "";
		return multiobject.split(genericSplitter);
	}
	private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$");
	public static boolean isCorrectNickname(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		return NICKNAME_PATTERN.matcher(entityName).matches();
	}
	private static final Pattern UUID_PATTERN = Pattern.compile("^(?:[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})$");
	private static final Pattern DASHLESS_UUID_PATTERN = Pattern.compile("^([A-Fa-f0-9]{8})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{4})([A-Fa-f0-9]{12})$");
	public static boolean isCorrectUUID(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		entityName = entityName.toLowerCase();
		return UUID_PATTERN.matcher(entityName).matches();
	}
	public static boolean isCorrectDashlessUUID(String entityName)
	{
		if(entityName == null || "".equals(entityName))
			return false;
		entityName = entityName.toLowerCase();
		return DASHLESS_UUID_PATTERN.matcher(entityName).matches();
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
		final Matcher matcher = DASHLESS_UUID_PATTERN.matcher(uuidRemoveDashes(uuid));
		return matcher.replaceAll("$1-$2-$3-$4-$5").toLowerCase();
	}
	private static final Pattern WILDCARD_PATTERN = Pattern.compile("^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*)$");
	private static final Pattern SUBNETMASK_PATTERN = Pattern.compile("^"
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\."
		+ "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])"
		+ "(?:/([0-9]|[1-2][0-9]|3[0-2]))$");
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
