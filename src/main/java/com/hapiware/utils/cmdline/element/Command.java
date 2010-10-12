package com.hapiware.utils.cmdline.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hapiware.utils.cmdline.Util;
import com.hapiware.utils.cmdline.constraint.ConfigurationException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;

public class Command
{
	private ElementBase _command = new ElementBase();
	private String _shortDescription;
	private final CommandExecutor _commandExecutor;

	private Map<String, Option.Inner> _definedOptions = new LinkedHashMap<String, Option.Inner>();
	private Map<String, String> _definedOptionAlternatives = new HashMap<String, String>();
	private Map<String, Argument.Inner<?>> _definedArguments =
		new LinkedHashMap<String, Argument.Inner<?>>();
	private boolean _mandatoryArguments;
	private int _numOfOptionalArguments;
	private List<Option.Inner> _cmdLineOptions = new ArrayList<Option.Inner>(); 
	private List<Argument.Inner<?>> _cmdLineArguments = new ArrayList<Argument.Inner<?>>(); 
	
	private Command(Command command)
	{
		_command = new ElementBase(command._command);
		_commandExecutor = command._commandExecutor;
		_mandatoryArguments = command._mandatoryArguments;
		_cmdLineOptions.addAll(command._cmdLineOptions);
		_cmdLineArguments.addAll(command._cmdLineArguments);
		_numOfOptionalArguments = command._numOfOptionalArguments;
		
		// References are ok here.
		_definedOptions = command._definedOptions;
		_definedOptionAlternatives = command._definedOptionAlternatives;
		_definedArguments = command._definedArguments;
	}
	
	public Command()
	{
		_commandExecutor = null;
	}
	
	public Command(CommandExecutor commandExecutor)
	{
		if(commandExecutor == null)
			throw new ConfigurationException("'commandExecutor' must have a value.");
		
		_commandExecutor = commandExecutor;
	}
	
	public Command name(String name)
	{
		if(name == null || name.trim().length() == 0)
			throw new ConfigurationException("'name' must have a value.");
		
		_command.name(name);
		return this;
	}
	
	public Command alternatives(String...alternatives)
	{
		if(alternatives == null || alternatives.length == 0)
			throw new ConfigurationException("'alternatives' must have a value.");
		
		_command.alternatives(alternatives);
		return this;
	}
	
	public Command id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw new ConfigurationException("'id' must have a value.");
		
		_command.id(id);
		return this;
	}
	
	public Command shortDescription(String shortDescription)
	{
		if(shortDescription == null || shortDescription.trim().length() == 0)
			throw new ConfigurationException("'shortDescription' must have a value.");

		_shortDescription = shortDescription;
		return this;
	}
	
	public Command description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw new ConfigurationException("'description' must have a value.");
		
		_command.description(description);
		return this;
	}
	public Command p()
	{
		_command.p();
		return this;
	}
	
	public <T> Command add(Class<T> argumentType, Argument argument)
	{
		if(argument == null)
			throw new ConfigurationException("'argument' must have a value.");
		
		Argument.Inner<T> inner = new Argument.Inner<T>(argument, argumentType);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'argument' must have a name.");
		
		_definedArguments.put(inner.name(), inner);
		if(!inner.optional()) {
			_mandatoryArguments = true;
			if(_numOfOptionalArguments >= 2) {
				String msg =
					"If there are more than one optional argument they must be the last arguments."
						+ " A single optional argument can have any position.";
				throw new ConfigurationException(msg);
			}
		}
		else
			_numOfOptionalArguments++;
		return this;
	}
	public Command add(Option option)
	{
		if(option == null)
			throw new ConfigurationException("'option' must have a value.");
		
		if(option == null)
			throw new ConfigurationException("'option' must have a value.");
		
		Option.Inner inner = new Option.Inner(option);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'option' must have a name.");
		
		_definedOptions.put(inner.name(), inner);
		_definedOptionAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedOptionAlternatives.put(alternative, inner.name());
		
		return this;
	}
	
	public static class Inner
		implements
			Parser
	{
		private Command _outer;
		public Inner(Inner inner)
		{
			_outer = new Command(inner._outer);
		}
		public Inner(Command outer)
		{
			_outer = outer;
		}
		public String name()
		{
			return _outer._command.name();
		}
		public boolean checkAlternative(String name)
		{
			return _outer._command.checkAlternative(name);
		}
		public Set<String> alternatives()
		{
			return _outer._command.alternatives();
		}
		public String id()
		{
			return _outer._command.id();
		}
		public List<String> description()
		{
			return _outer._command.description();
		}
		public String shortDescription()
		{
			return _outer._shortDescription;
		}
		public List<Option.Inner> cmdLineOptions()
		{
			return _outer._cmdLineOptions;
		}
		public List<Argument.Inner<?>> cmdLineArguments()
		{
			return _outer._cmdLineArguments;
		}
		public boolean parse(List<String> arguments)
			throws
				ConstraintException,
				IllegalCommandLineArgumentException
		{
			if(arguments.size() == 0)
				return false;
			
			String commandName = arguments.remove(0);
			Set<Option.Inner> nonMultipleOptionCheckSet = new HashSet<Option.Inner>();
			boolean commandArgumentsChecked = false;
			while(arguments.size() > 0) {
				String arg = arguments.get(0);
				if(
					Util.checkOption(
						arg,
						arguments,
						_outer._definedOptions,
						_outer._definedOptionAlternatives,
						nonMultipleOptionCheckSet,
						_outer._cmdLineOptions
					)
				)
					continue;
				else
					if(commandArgumentsChecked)
						break;
				
				if(
					Util.checkArguments(
						commandName,
						arguments,
						_outer._definedArguments,
						_outer._cmdLineArguments
					)
				) {
					commandArgumentsChecked = true;
					continue;
				}
				
				return false;
			}
			
			return true;
		}
		public void execute(List<Option.Inner> options)
		{
			if(_outer._commandExecutor != null)
				_outer._commandExecutor.execute(this, options);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Command.Inner))
				return false;
			Command.Inner inner = (Command.Inner)obj;
			return name().equals(inner.name());
		}

		@Override
		public int hashCode()
		{
			int resultHash = 17;
			resultHash = 31 * resultHash + (name() == null ? 0 : name().hashCode());
			return resultHash;
		}
		
		@Override
		public String toString()
		{
			String str = "[";
			str += "name: " + name() + ", id: " + id();
			if(alternatives().size() > 0) {
				str += ", alt: ";
				int i = 0;
				for(String alternative : alternatives())
					str += alternative + (i++ < alternatives().size() ? ", " : "");
			}
			str += "]";
			return str;
		}
		
	}
}
