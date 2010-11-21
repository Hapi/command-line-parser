package com.hapiware.util.cmdlineparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.hapiware.util.cmdlineparser.constraint.Constraint;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;


/**
 * {@code Command} is used to define commands for the command line utility. See
 * <a href="CommandLineParser.html#cmdlineparser-command-line-structure">Command line structure</a>
 * for more information.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class Command
{
	private ElementBase _command = new ElementBase();
	private final String _shortDescription;
	private final CommandExecutor _commandExecutor;

	private Map<String, Option.Internal> _definedOptions = new LinkedHashMap<String, Option.Internal>();
	private Map<String, String> _definedOptionAlternatives = new HashMap<String, String>();
	private Map<String, Argument.Internal<?>> _definedArguments =
		new LinkedHashMap<String, Argument.Internal<?>>();
	private boolean _mandatoryArguments;
	private int _numOfOptionalArguments;
	private boolean _previousWasOptional;
	private List<Option.Internal> _cmdLineOptions = new ArrayList<Option.Internal>(); 
	private List<Argument.Internal<?>> _cmdLineArguments = new ArrayList<Argument.Internal<?>>();
	
	
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
	
	/**
	 * Creates new {@code Command}.
	 * 
	 * @param name
	 * 		Name for the command.
	 *
	 * @param shortDescription
	 * 		A short textual description about the command. This is used by the help system
	 * 		for listing all commands. See {@link CommandLineParser#printCommandsHelp()}. 
	 * 
	 * @throws ConfigurationException
	 * 		If {@code name} is incorrectly formed or {@code shortDescrption} is missing.
	 * 
	 * @see Util#checkName(String)
	 */
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
	
	/**
	 * Creates new {@code Command} with a {@link CommandExecutor} object defined for the command.
	 * {@link CommandExecutor#execute(Command.Data, List)} is run when a parser detects the defined
	 * command on the command line.
	 * 
	 * @param name
	 * 		Name for the command.
	 *
	 * @param shortDescription
	 * 		A short textual description about the command. This is used by the help system
	 * 		for listing all commands. See {@link CommandLineParser#printCommandsHelp()}.
	 * 
	 *  @param commandExecutor
	 *  	The command executor to be called when the respective command is detected.
	 * 
	 * @throws ConfigurationException
	 * 		If {@code name} is incorrectly formed, {@code shortDescrption} is missing or
	 * 		{@link CommandExecutor} is not defined.
	 * 
	 * @see Util#checkName(String)
	 */
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
	
	
	/**
	 * Defines alternative names for the {@link Command}.
	 * 
	 * @param alternatives
	 * 		An array of alternative names.
	 * 
	 * @return
	 * 		The command object for chaining.
	 */
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
		
		String duplicateAlternative = _command.alternatives(alternatives);
		if(duplicateAlternative != null)
			throw
				new ConfigurationException(
					"All the alternative names for command '" + _command.name() + "' must be unique. "
					+ "Conflicting alternative name is '" + duplicateAlternative + "'." 
				);
		return this;
	}

	
	/**
	 * An optional {@code id} for annotation matching. If not defined the name given in
	 * {@link Command#Command(String, String)} or {@link Command#Command(String, String, CommandExecutor)}
	 *  is used as the id. For more information see
	 * <a href="CommandLineParser.html#cmdlineparser-annotations">CommandLineParser, chapter Annotations</a>.
	 * 
	 * @param id
	 * 		An id for the command.
	 * 
	 * @return
	 * 		The command object for chaining.
	 */
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
	
	
	/**
	 * Adds a new argument for {@code Command}.
	 * <p>
	 * Type for the argument is defined twice. Generics are used for compile time type checking
	 * and thus making it easier for programmer to keep type safety. {@code Command} also checks
	 * type in run time and thus the type must be set as a class also.
	 * 
	 * @param <T>
	 * 		A type of the argument.
	 * 
	 * @param argumentType
	 * 		A type of the argument as {@code Class<T>}.
	 * 
	 * @param argument
	 * 		An argument object to be added.
	 * 
	 * @throws ConfigurationException
	 * 		<ul>
	 * 			<li>{@code argument} is {@code null}.</li>
	 * 			<li>{@code argument} does not have a name or it is not unique.</li>
	 * 			<li>{@code argument} description is missing.</li>
	 * 			<li>there is a constraint type mismatch.</li>
	 * 			<li>optional arguments are misplaced</li>
	 * 		</ul>
	 */
	public <T> Command add(Class<T> argumentType, Argument<T> argument)
	{
		if(argument == null)
			throw
				new ConfigurationException(
					"'argument' for command '" + _command.name() + "' must have a value."
				);
		
		Argument.Internal<T> internal = new Argument.Internal<T>(argument, argumentType);
		if(internal.name() == null || internal.name().trim().length() == 0)
			throw
				new ConfigurationException(
					"'argument' for command '" + _command.name() + "' must have a name."
				);
		
		if(internal.description().size() == 0)
			throw
				new ConfigurationException(
					"Argument '" + internal.name() + "' for command '" 
						+ _command.name() + "' must have a description."
				);
		if(_definedArguments.containsKey(internal.name()))
			throw
				new ConfigurationException(
					"Argument name '" + internal.name() + "' for command '" 
						+ _command.name() +"' must be unique."
				);

		for(Constraint<?> constraint : internal.constraints())
			if(!constraint.typeCheck(argumentType)) {
				String msg =
					"Using '" + constraint.getClass().getName() + "' with argument type '"
						+ argumentType + "' creates a type conflict "
						+ "(command '" + _command.name() + "', argument '" + internal.name() + "').";
				throw new ConfigurationException(msg);
			}
		
		_definedArguments.put(internal.name(), internal);

		if(!internal.optional())
			_mandatoryArguments = true;
		else
			_numOfOptionalArguments++;

		if(_numOfOptionalArguments >= 2 && (!internal.optional() || !_previousWasOptional)) {
			String msg =
				"If there is more than one optional argument they must be the last arguments. "
					+ "The first conflicting argument for command '" + _command.name()
					+ "' is '" + internal.name() + "'. "
					+ "A single optional argument can have any position.";
			throw new ConfigurationException(msg);
		}
		_previousWasOptional = internal.optional();
		return this;
	}
	
	
	/**
	 * Adds a new option for {@code Command}.
	 * 
	 * @param option
	 * 		An option object to be added.
	 * 
	 * @throws ConfigurationException
	 * 		<ul>
	 * 			<li>{@code option} is {@code null}.</li>
	 * 			<li>{@code option} does not have a name or it is not unique.</li>
	 * 			<li>any of the alternative names for the {@code option} is not unique.</li>
	 * 			<li>{@code option} description is missing.</li>
	 * 		</ul>
	 */
	public Command add(Option option)
	{
		if(option == null)
			throw
				new ConfigurationException(
					"'option' for command '" + _command.name() + "' must have a value."
				);
		
		Option.Internal internal = new Option.Internal(option);
		if(internal.name() == null || internal.name().trim().length() == 0)
			throw
				new ConfigurationException(
					"'option' for command '" + _command.name() + "' must have a name."
				);
		
		if(internal.description().size() == 0)
			throw
				new ConfigurationException(
					"Option '" + internal.name() + "' for command '" 
						+ _command.name() + "' must have a description."
				);
		if(_definedOptionAlternatives.containsKey(internal.name()))
			throw
				new ConfigurationException(
					"Option name '" + internal.name() + "' for command '" 
						+ _command.name() + "' must be unique."
				);
		
		_definedOptions.put(internal.name(), internal);
		_definedOptionAlternatives.put(internal.name(), internal.name());
		for(String alternative : internal.alternatives())
			if(_definedOptionAlternatives.put(alternative, internal.name()) != null)
				throw
					new ConfigurationException(
						"Option alternative name '" + alternative + "' for command '" 
							+ _command.name() + "' must be unique."
					);
		
		return this;
	}

	
	/**
	 * {@code Command.Data} is a container class for holding information about the given commands.
	 * <p>
	 * This class is immutable <b>only if {@link Argument.Data} is immutable</b>.
	 * 
	 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
	 *
	 */
	public static final class Data
		extends
			DataBase
	{
		private final List<Argument.Data<?>> _arguments;
		private final List<Option.Data> _options;
		
		/**
		 * "Copy" constructs a data object from the internal command object.
		 * 
		 * @param internal
		 * 		The internal command object.
		 */
		Data(Internal internal)
		{
			super(internal.name(), internal.id(), internal.alternatives());
			
			List<Argument.Data<?>> argumentData = new ArrayList<Argument.Data<?>>();
			for(Argument.Internal<?> argument : internal._outer._cmdLineArguments)
				argumentData.add(argument.createDataObject());
			_arguments = Collections.unmodifiableList(argumentData);

			List<Option.Data> optionData = new ArrayList<Option.Data>();
			for(Option.Internal option : internal._outer._cmdLineOptions)
				optionData.add(new Option.Data(option));
			_options  = Collections.unmodifiableList(optionData);
		}
		
		
		public Set<String> getAlternatives()
		{
			return super.getAlternatives();
		}

		
		/**
		 * Returns all the command options found from the command line.
		 * 
		 * @return
		 * 		An array of command option objects.
		 */
		public Option.Data[] getAllOptions()
		{
			return _options.toArray(new Option.Data[0]);
		}
		
		
		/**
		 * Checks if the command option exists among the command line arguments.
		 * 
		 * @param name
		 * 		A name (or alternative name) of the command option.
		 * 
		 * @return
		 * 		{@code true} if the command option exists.
		 */
		public boolean optionExists(String name)
		{
			for(Option.Data option : _options)
				if(option.getName().equals(name))
					return true;
			
			return false;
		}
		
		
		/**
		 * Returns the command option if it exists on the command line.
		 * 
		 * @param name
		 * 		A name (or alternative name) of the command option.
		 * 
		 * @return
		 * 		The command option object if exists on the command line. {@code null} if the command
		 * 		option does not	exist or does not have an argument.
		 */
		public Option.Data getOption(String name)
		{
			try {
				return getOptions(name)[0];
			}
			catch(IndexOutOfBoundsException e) {
				return null;
			}
		}
		
		
		/**
		 * Returns the value of the command option if it exists on the command line.
		 * 
		 * @param <T>
		 * 		A type of the command option argument.
		 * 
		 * @param name
		 * 		A name (or alternative name) of the command option.
		 * 		
		 * @return
		 * 		The command option value if the command option exists on the command line.
		 * 		{@code null} if	the command option does not exist or does not have an argument.
		 */
		@SuppressWarnings("unchecked")
		public <T> T getOptionValue(String name)
		{
			try {
				Option.Data option = getOption(name);
				if(option != null && option.getArgument() != null)
					return (T)option.getArgument().getValue();
				else
					return null;
			}
			catch(IndexOutOfBoundsException e) {
				return null;
			}
		}

		
		/**
		 * Returns an array of command options if exists on the command line. The first command
		 * option is the left-most command option on the command line.
		 * 
		 * @param name
		 * 		A name (or alternative name) of the command option.
		 * 
		 * @return
		 * 		An array of command option objects.
		 */
		public Option.Data[] getOptions(String name)
		{
			List<Option.Data> options = new ArrayList<Option.Data>();
			for(Option.Data option : _options)
				if(option.getName().equals(name))
					options.add(option);
			
			return options.toArray(new Option.Data[0]);
		}
		
		
		/**
		 * Returns a command argument from the command line if exists. Notice that only optional
		 * command arguments can be missing.
		 * 
		 * @param name
		 * 		A name of the command argument.
		 * 
		 * @return
		 * 		An argument object if exist on the command line.
		 */
		public Argument.Data<?> getArgument(String name)
		{
			for(Argument.Data<?> argument : _arguments)
				if(argument.getName().equals(name))
					return argument;

			return null;
		}

		
		/**
		 * Returns the value of the command argument if exists on the command line.
		 * 
		 * @param <T>
		 * 		A type of the command argument.
		 * 
		 * @param name
		 * 		A name of the command argument.
		 * 		
		 * @return
		 * 		The command argument value if the command argument exists on the command line.
		 * 		{@code null} otherwise.
		 */
		@SuppressWarnings("unchecked")
		public <T> T getArgumentValue(String name)
		{
			Argument.Data<?> argument = getArgument(name);
			if(argument != null)
				return (T)argument.getValue();
			else
				return null;
		}
		
		
		/**
		 * Returns all the command arguments found from the command line.
		 * 
		 * @return
		 * 		An array of argument objects.
		 */
		public Argument.Data<?>[] getAllArguments()
		{
			return _arguments.toArray(new Argument.Data[0]);
		}
		
		/**
		 * Returns a {@code String} representation of {@code Option.Data} object. The form is:
		 * <p>
		 * <code>{NAME(ID) : OPTIONS : ARGUMENTS : ALTERNATIVES}</code>
		 * <p>
		 * where:
		 * 	<ul>
		 * 		<li>NAME is the argument name.</li>
		 * 		<li>ID is the id for the argument.</li>
		 * 		<li>MULTI indicates if the option can occur multiple times.</li>
		 * 		<li>VALUE is the argument value.</li>
		 * 		<li>OPTIONAL indicates if the value is optional or not.</li>
		 * 		<li>ALTERNATIVES alternative names for the option</li>
		 * 	</ul>
		 */
		@Override
		public String toString()
		{
			return
				"{" + getName() + "(" + getId()  + ") : "
					+ getAllOptions() + ":"
					+ getAllArguments() + ":"
					+ getAlternatives()
					+ "}";
		}
	}

	
	static final class Internal
		implements
			Parser
	{
		private Command _outer;
		
		public Internal(Internal internal)
		{
			_outer = new Command(internal._outer);
		}
		public Internal(Command outer)
		{
			_outer = outer;
		}
		public String name()
		{
			return _outer._command.name();
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
		public List<Option.Internal> cmdLineOptions()
		{
			return Collections.unmodifiableList(_outer._cmdLineOptions);
		}
		public List<Argument.Internal<?>> cmdLineArguments()
		{
			return Collections.unmodifiableList(_outer._cmdLineArguments);
		}
		public Map<String, Option.Internal>definedOptions()
		{
			return Collections.unmodifiableMap(_outer._definedOptions);
		}
		public Map<String, Argument.Internal<?>> definedArguments()
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
			Set<Option.Internal> nonMultipleOptionCheckSet = new HashSet<Option.Internal>();
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
			
			// There are no command line arguments and all the arguments are optional.
			if(
				_outer._cmdLineArguments.size() == 0
				&& _outer._definedArguments.size() > 0
				&& !_outer._mandatoryArguments)
			{
				Set<Entry<String, Argument.Internal<?>>> entrySet =
					_outer._definedArguments.entrySet();
				for(Iterator<?> it = entrySet.iterator(); it.hasNext();) {
					@SuppressWarnings("unchecked")
					Argument.Internal<?> argument =
						((Entry<String, Argument.Internal<?>>)it.next()).getValue();
					argument.setDefaultValue();
					_outer._cmdLineArguments.add(argument);
				}
			}
			
			
			return true;
		}
		public void execute(List<Option.Data> options)
		{
			if(_outer._commandExecutor != null)
				_outer._commandExecutor.execute(new Data(this), options);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj == this)
				return true;

			if(!(obj instanceof Command.Internal))
				return false;
			Command.Internal internal = (Command.Internal)obj;
			return name().equals(internal.name());
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
			String str = "{" + name() + "(" + id() + ")";
			if(alternatives().size() > 0)
				str += " = " + alternatives();
			str += "}";
			return str;
		}
		
	}
}
