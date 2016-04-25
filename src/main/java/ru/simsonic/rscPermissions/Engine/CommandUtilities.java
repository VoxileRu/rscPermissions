package ru.simsonic.rscPermissions.Engine;

public class CommandUtilities
{
	public static boolean argumentToBoolean(String arg, Boolean prevForToggle) throws IllegalArgumentException
	{
		if(arg == null || "".equals(arg))
			throw new IllegalArgumentException("Argument is null or empty.");
		switch(arg.toLowerCase())
		{
			case "enable":
			case "true":
			case "yes":
			case "on":
			case "1":
				return true;
			case "disable":
			case "false":
			case "no":
			case "off":
			case "0":
				return false;
			case "toggle":
				if(prevForToggle != null)
					return !prevForToggle;
				else
					throw new IllegalArgumentException("Previous value is unknown.");
		}
		throw new IllegalArgumentException("Cannot understand boolean value.");
	}
	public static int argumentToInteger(String arg) throws IllegalArgumentException
	{
		if(arg == null || "".equals(arg))
			throw new IllegalArgumentException("Argument is null or empty.");
		try
		{
			return Integer.parseInt(arg);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException(ex.getMessage());
		}
	}
}
