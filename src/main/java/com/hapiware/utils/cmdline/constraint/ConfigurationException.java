package com.hapiware.utils.cmdline.constraint;


public class ConfigurationException
	extends
		RuntimeException
{
	private static final long serialVersionUID = -5390198059254340352L;

	public ConfigurationException(String message)
	{
		super(message);
	}
	
	public ConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
