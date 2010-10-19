package com.hapiware.util.cmdlineparser;

import java.util.List;

public interface CommandExecutor
{
	public void execute(Command.Data command, List<Option.Data> globalOptions);
}
