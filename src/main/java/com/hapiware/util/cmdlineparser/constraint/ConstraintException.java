package com.hapiware.util.cmdlineparser.constraint;


/**
 * {@code ConstraintException} is thrown when a {@link Constraint#evaluate(String, Object)}
 * detects a constraint violation.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class ConstraintException extends Exception
{
	private static final long serialVersionUID = 7210442487487693741L;

	/**
	 * Constructs {@code ConstraintException} with the specified detail message.
	 * 
	 * @param message
	 * 		The detail message.
	 */
	public ConstraintException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs {@code ConstraintException} with the specified detail message and cause.
	 * 
	 * @param message
	 * 		The detail message.
	 * 
	 * @param cause
	 * 		The cause.
	 */
	public ConstraintException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
