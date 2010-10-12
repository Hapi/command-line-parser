package com.hapiware.utils.cmdline.constraint;

public class CommandNotFoundException extends Exception
{
	private static final long serialVersionUID = 4717626597965485599L;

	public CommandNotFoundException(String message)
	{
		super(message);
	}
	
	public CommandNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
