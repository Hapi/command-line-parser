package com.hapiware.utils.cmdline.element;

import java.util.List;

import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;

public interface Parser
{
	public boolean parse(List<String> arguments)
		throws
			ConstraintException,
			IllegalCommandLineArgumentException;
}
