package com.hapiware.util.cmdlineparser;

/**
 * {@code IllegalCommandLineArgumentException} is thrown whenever a command line argument cannot
 * be interpreted as a configured option, command option, argument or command argument.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class IllegalCommandLineArgumentException extends Exception
{
	private static final long serialVersionUID = -2290736635772524372L;

	/**
	 * Constructs {@code IllegalCommandLineArgumentException} with the specified detail message.
	 * 
	 * @param message
	 * 		The detail message.
	 */
	public IllegalCommandLineArgumentException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs {@code IllegalCommandLineArgumentException} with the specified detail message and
	 * cause.
	 * 
	 * @param message
	 * 		The detail message.
	 * 
	 * @param cause
	 * 		The cause.
	 */
	public IllegalCommandLineArgumentException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
