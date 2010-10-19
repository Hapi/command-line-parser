package com.hapiware.util.cmdlineparser;

import java.util.List;

import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

public interface Parser
{
	public boolean parse(List<String> arguments)
		throws
			ConstraintException,
			IllegalCommandLineArgumentException;
}
