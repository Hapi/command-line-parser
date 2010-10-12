package com.hapiware.utils.cmdline.element;

import java.util.List;

public interface Descriptor
{
	public void description(String text);
	public void usage(List<String> usageLines);
	public void options(List<Option> options);
	public void commands(List<Command> commands);
	public void arguments(List<Argument> arguments);
}
