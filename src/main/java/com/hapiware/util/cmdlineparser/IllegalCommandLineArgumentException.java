package com.hapiware.util.cmdlineparser;


public class IllegalCommandLineArgumentException extends Exception
{
	private static final long serialVersionUID = -2290736635772524372L;

	public IllegalCommandLineArgumentException(String message)
	{
		super(message);
	}
	
	public IllegalCommandLineArgumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
