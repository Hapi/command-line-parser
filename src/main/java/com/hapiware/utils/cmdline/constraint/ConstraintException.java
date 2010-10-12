package com.hapiware.utils.cmdline.constraint;

public class ConstraintException extends Exception
{
	private static final long serialVersionUID = 7210442487487693741L;

	public ConstraintException(String message)
	{
		super(message);
	}
	
	public ConstraintException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
