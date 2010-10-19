package com.hapiware.util.cmdlineparser;

public class AnnotatedFieldSetException extends Exception
{
	private static final long serialVersionUID = 4324294856410113789L;

	public AnnotatedFieldSetException(String message)
	{
		super(message);
	}
	
	public AnnotatedFieldSetException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
