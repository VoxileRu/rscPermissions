package ru.simsonic.rscPermissions;
import java.util.List;

public class CommandHelperAnswerException extends Exception
{
	private final String[] message;
	public CommandHelperAnswerException(String message)
	{
		this.message = new String[]{message};
	}
	public CommandHelperAnswerException(String[] messages)
	{
		this.message = messages;
	}
	public CommandHelperAnswerException(List<String> messages)
	{
		this.message = messages.toArray(new String[messages.size()]);
	}
	public String[] getMessageArray()
	{
		return message;
	}
}