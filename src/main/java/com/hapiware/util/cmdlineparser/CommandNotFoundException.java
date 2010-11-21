package com.hapiware.util.cmdlineparser;

/**
 * {@code CommandNotFoundException} is thrown when a command is excepted on command line but it
 * is not found among the command line arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class CommandNotFoundException extends Exception
{
	private static final long serialVersionUID = 4717626597965485599L;

	/**
	 * Constructs {@code CommandNotFoundException} with the specified detail message.
	 * 
	 * @param message
	 * 		The detail message.
	 */
	public CommandNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * Constructs {@code CommandNotFoundException} with the specified detail message and cause.
	 * 
	 * @param message
	 * 		The detail message.
	 * 
	 * @param cause
	 * 		The cause.
	 */
	public CommandNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
