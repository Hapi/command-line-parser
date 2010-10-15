package com.hapiware.utils.cmdline.element;

import java.util.ArrayList;
import java.util.Collections;
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
	private final String _shortDescription;
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
		_shortDescription = command._shortDescription;
		
		// References are ok here.
		_definedOptions = command._definedOptions;
		_definedOptionAlternatives = command._definedOptionAlternatives;
		_definedArguments = command._definedArguments;
	}
	
	public Command(String name, String shortDescription)
	{
		if(name == null || name.trim().length() == 0)
			throw new ConfigurationException("'name' must have a value.");
		
		if(!Util.checkName(name))
			throw
				new ConfigurationException(
					"'name' for the command is incorrect ('" + name + "')."
				);
		
		if(shortDescription == null || shortDescription.trim().length() == 0)
			throw
				new ConfigurationException(
					"'shortDescription' for command '" + name + "' must have a value."
				);

		_command.name(name);
		_commandExecutor = null;
		_shortDescription = shortDescription;
	}
	
	public Command(String name, String shortDescription, CommandExecutor commandExecutor)
	{
		if(name == null || name.trim().length() == 0)
			throw new ConfigurationException("'name' must have a value.");

		if(!Util.checkName(name))
			throw
				new ConfigurationException(
					"'name' for the command is incorrect ('" + name + "')."
				);
		
		if(shortDescription == null || shortDescription.trim().length() == 0)
			throw
				new ConfigurationException(
					"'shortDescription' for command '" + name + "' must have a value."
				);

		if(commandExecutor == null)
			throw
				new ConfigurationException(
					"'commandExecutor' for command '" + name + "' must have a value."
				);
		
		_command.name(name);
		_commandExecutor = commandExecutor;
		_shortDescription = shortDescription;
	}
	
	public Command alternatives(String...alternatives)
	{
		if(alternatives == null || alternatives.length == 0)
			throw
				new ConfigurationException(
					"'alternatives' for command '" + _command.name() + "' must have a value."
				);
		for(int i = 0; i < alternatives.length; i++)
			if(!Util.checkName(alternatives[i]))
				throw
					new ConfigurationException(
						"Alternative name for command '" + _command.name() 
							+ "' is incorrect ('" + alternatives[i] + "')."
					);
		
		_command.alternatives(alternatives);
		return this;
	}
	
	public Command id(String id)
	{
		if(id == null || id.trim().length() == 0)
			throw
				new ConfigurationException(
					"'id' for command '" + _command.name() + "' must have a value."
				);
		if(!Util.checkName(id))
			throw
				new ConfigurationException(
					"'id' for command '" + _command.name() + "' is incorrect ('" + id + "')."
				);
		
		_command.id(id);
		return this;
	}
	
	/**
	 * For further details see {@link Description#description(String)}
	 */
	public Command description(String description)
	{
		if(description == null || description.trim().length() == 0)
			throw
				new ConfigurationException(
					"'description' for command '" + _command.name() + "'  must have a value."
				);
		
		_command.description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#strong(String)}
	 */
	public Command strong(String text)
	{
		_command.strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#paragraph()}
	 */
	public Command paragraph()
	{
		_command.paragraph();
		return this;
	}
	
	/**
	 * For further details see {@link Description#d(String)}
	 */
	public Command d(String description)
	{
		description(description);
		return this;
	}
	
	/**
	 * For further details see {@link Description#b(String)}
	 */
	public Command b(String text)
	{
		strong(text);
		return this;
	}
	
	/**
	 * For further details see {@link Description#p()}
	 */
	public Command p()
	{
		paragraph();
		return this;
	}
	
	public <T> Command add(Class<T> argumentType, Argument argument)
	{
		if(argument == null)
			throw
				new ConfigurationException(
					"'argument' for command '" + _command.name() + "' must have a value."
				);
		
		Argument.Inner<T> inner = new Argument.Inner<T>(argument, argumentType);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw
				new ConfigurationException(
					"'argument' for command '" + _command.name() + "' must have a name."
				);
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException(
					"Argument '" + inner.name() + "' for command '" 
						+ _command.name() + "' must have a description."
				);
		if(_definedArguments.containsKey(inner.name()))
			throw
				new ConfigurationException(
					"Argument name '" + inner.name() + "' for command '" 
						+ _command.name() +"' must be unique."
				);

		if(inner.optional() && !inner.hasDefaultValueForOptional()) {
			String msg =
				"When annotations are used then optional arguments must have a default value "
					+ "(Command '" + _command.name() + "', argument '" + inner.name() + "').";
			throw new ConfigurationException(msg);
		}
		
		_definedArguments.put(inner.name(), inner);
		if(!inner.optional()) {
			_mandatoryArguments = true;
			if(_numOfOptionalArguments >= 2) {
				String msg =
					"If there are more than one optional argument they must be the last arguments. "
						+ "(Command '" + _command.name() + "', argument '" + inner.name() + "'). "
						+ "A single optional argument can have any position.";
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
			throw
				new ConfigurationException(
					"'option' for command '" + _command.name() + "' must have a value."
				);
		
		Option.Inner inner = new Option.Inner(option);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw
				new ConfigurationException(
					"'option' for command '" + _command.name() + "' must have a name."
				);
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException(
					"Option '" + inner.name() + "' for command '" 
						+ _command.name() + "' must have a description."
				);
		if(_definedOptionAlternatives.containsKey(inner.name()))
			throw
				new ConfigurationException(
					"Option name '" + inner.name() + "' for command '" 
						+ _command.name() + "'must be unique."
				);
		
		_definedOptions.put(inner.name(), inner);
		_definedOptionAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			if(_definedOptionAlternatives.put(alternative, inner.name()) != null)
				throw
					new ConfigurationException(
						"Option alternative name '" + alternative + "' for command '" 
							+ _command.name() + "'must be unique."
					);
		
		return this;
	}
	
	public static final class Inner
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
			return Collections.unmodifiableSet(_outer._command.alternatives());
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
			return Collections.unmodifiableList(_outer._cmdLineOptions);
		}
		public List<Argument.Inner<?>> cmdLineArguments()
		{
			return Collections.unmodifiableList(_outer._cmdLineArguments);
		}
		
		public boolean optionExists(String name)
		{
			for(Option.Inner option : _outer._cmdLineOptions)
				if(option.name().equals(_outer._definedOptionAlternatives.get(name)))
					return true;
			
			return false;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T optionValue(String name)
		{
			try {
				Option.Inner option = options(name)[0];
				if(option.argument() != null)
					return (T)option.argument().value();
				else
					return null;
			}
			catch(IndexOutOfBoundsException e) {
				return null;
			}
		}
		
		public Option.Inner[] options(String name)
		{
			List<Option.Inner> options = new ArrayList<Option.Inner>();
			for(Option.Inner option : _outer._cmdLineOptions)
				if(option.name().equals(_outer._definedOptionAlternatives.get(name)))
					options.add(new Option.Inner(option));
			
			return options.toArray(new Option.Inner[0]);
		}
		
		public Argument.Inner<?> argument(String name)
		{
			for(Argument.Inner<?> argument : _outer._cmdLineArguments)
				if(argument.name().equals(name))
					return argument.clone();

			return null;
		}
		
		public Map<String, Option.Inner>definedOptions()
		{
			return Collections.unmodifiableMap(_outer._definedOptions);
		}
		
		public Map<String, Argument.Inner<?>> definedArguments()
		{
			return Collections.unmodifiableMap(_outer._definedArguments);
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
