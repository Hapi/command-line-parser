package com.hapiware.util.cmdlineparser;

import java.util.List;


/**
 * {@code CommandExecutor} is an interface for creating an executor for {@link Command}.
 * {@link CommandExecutor#execute(Command.Data, List)} is called if the parsed finds a respective
 * command among the command line arguments.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 * @see Command#Command(String, String, CommandExecutor)
 */
public interface CommandExecutor
{
	/**
	 * {@code execute(Command.Data, List)} is called automatically by the framework whenever
	 * the respective command is found among command line arguments.
	 * 
	 * @param command
	 * 		The defined command attributes for the found command.
	 * 
	 * @param globalOptions
	 * 		Global options found among the command line arguments.
	 * 
	 * @see Command#Command(String, String, CommandExecutor)
	 */
	public void execute(Command.Data command, List<Option.Data> globalOptions);
}
