package ru.simsonic.rscPermissions.InternalCache;
import java.util.regex.Pattern;

public class Matchers
{
	private static final String genericSplitter = "\\s*[;,\\r\\n\\s]+\\s*";
	public static String[] genericParse(String multiobject)
	{
		if(multiobject == null)
			multiobject = "";
		return multiobject.split(genericSplitter);
	}
	private static final Pattern patternUUID = Pattern.compile(
		"<uuid>" + "(?:[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})" + "</uuid>");
	private static boolean isCorrectUUID(String entityName)
	{
		if(entityName == null)
			return false;
		return patternUUID.matcher("<uuid>" + entityName.toLowerCase() + "</uuid>").find();
	}
}
