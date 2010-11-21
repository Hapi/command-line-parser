package com.hapiware.util.cmdlineparser;

import java.util.List;

import com.hapiware.util.cmdlineparser.constraint.ConstraintException;

/**
 * A parser interface for internal classes.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
interface Parser
{
	/**
	 * Parses given arguments considering the class implementing this interface.
	 * 
	 * @param arguments
	 * 		Arguments to be parsed.
	 * 
	 * @return
	 * 		{@code true} if parsing was successful. Notice that successful parsing does not
	 * 		necessarily mean that the implementing object gets anything from {@code arguments}
	 * 		belonging to the object. 
	 * 
	 * @throws ConstraintException
	 * 		When a constraint violation has been detected.
	 * 
	 * @throws IllegalCommandLineArgumentException
	 * 		If argument(s) cannot be interpreted any of the defined command line element type.
	 */
	public boolean parse(List<String> arguments)
		throws
			ConstraintException,
			IllegalCommandLineArgumentException;
}
