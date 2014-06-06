package ru.simsonic.rscPermissions.DataTypes;
import java.util.regex.Pattern;

public abstract class AbstractRow
{
	public int id = 0;
	public static enum Table
	{
		entities, permissions, inheritance, ladders, unknown;
	}
	public abstract Table getTable();
	private static final Pattern patternUUID = Pattern.compile(
		"<uuid>" + "(?:[a-f\\d]{8}(?:-[a-f\\d]{4}){3}-[a-f\\d]{12})" + "</uuid>");
	private static boolean isCorrectUUID(String entityName)
	{
		if(entityName == null)
			return false;
		return patternUUID.matcher("<uuid>" + entityName.toLowerCase() + "</uuid>").find();
	}
}