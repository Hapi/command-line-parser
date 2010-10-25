package com.hapiware.util.cmdlineparser;

public class ExitException
	extends
		RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public final int exitStatus;
	public ExitException(int exitStatus)
	{
		this.exitStatus = exitStatus;
	}
}
