package com.hapiware.util.cmdlineparser;


/**
 * {@code ConfigurationException} is thrown when {@link CommandLineParser} detects any problems
 * during the configuration.
 * 
 * Notice that {@code ConfigurationException} is a runtime exception.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class ConfigurationException
	extends
		RuntimeException
{
	private static final long serialVersionUID = -5390198059254340352L;

	/**
	 * Constructs {@code ConfigurationException} with the specified detail message.
	 * 
	 * @param message
	 * 		The detail message.
	 */
	public ConfigurationException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs {@code ConfigurationException} with the specified detail message and cause.
	 * 
	 * @param message
	 * 		The detail message.
	 * 
	 * @param cause
	 * 		The cause.
	 */
	public ConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
