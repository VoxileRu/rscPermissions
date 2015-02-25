package ru.simsonic.rscPermissions.Engine;
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
		"^(?:[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})$");
	private static boolean isCorrectUUID(String entityName)
	{
		if(entityName == null)
			return false;
		return patternUUID.matcher(entityName).find();
	}
}
