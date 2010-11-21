package com.hapiware.util.cmdlineparser;


/**
 * {@code AnnotatedFieldSetException} is thrown if an argument or option value cannot be set
 * to the annotated field.
 *  
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class AnnotatedFieldSetException extends Exception
{
	private static final long serialVersionUID = 4324294856410113789L;

	/**
	 * Constructs {@code AnnotatedFieldSetException} with the specified detail message.
	 * 
	 * @param message
	 * 		The detail message.
	 */
	public AnnotatedFieldSetException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructs {@code AnnotatedFieldSetException} with the specified detail message and cause.
	 * 
	 * @param message
	 * 		The detail message.
	 * 
	 * @param cause
	 * 		The cause.
	 */
	public AnnotatedFieldSetException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
