package com.hapiware.utils.cmdline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.hapiware.utils.cmdline.constraint.AnnotatedFieldSetException;
import com.hapiware.utils.cmdline.constraint.CommandNotFoundException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Option;


public class CommandLineParser
{
	private Map<String, Option.Inner> _definedGlobalOptions = new LinkedHashMap<String, Option.Inner>();
	private Map<String, String> _definedOptionGlobalAlternatives = new HashMap<String, String>();
	private Map<String, Command.Inner> _definedCommands = new LinkedHashMap<String, Command.Inner>();
	private Map<String, String> _definedCommandAlternatives = new HashMap<String, String>();
	private Map<String, Argument.Inner<?>> _definedArguments =
		new LinkedHashMap<String, Argument.Inner<?>>();
	private boolean _mandatoryArguments;
	private int _numOfOptionalArguments;
	private List<Option.Inner> _cmdLineOptions = new ArrayList<Option.Inner>(); 
	private Command.Inner _cmdLineCommand;
	private List<Argument.Inner<?>> _cmdLineArguments = new ArrayList<Argument.Inner<?>>(); 

	
	public void add(Option option)
	{
		if(option == null)
			throw new NullPointerException("'option' must have a value.");
		
		Option.Inner inner = new Option.Inner(option);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new NullPointerException("'option' must have a name.");
		_definedGlobalOptions.put(inner.name(), inner);
		_definedOptionGlobalAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedOptionGlobalAlternatives.put(alternative, inner.name());
	}
	
	public void add(Command command)
	{
		if(command == null)
			throw new NullPointerException("'command' must have a value.");
		
		Command.Inner inner = new Command.Inner(command);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new NullPointerException("'command' must have a name.");
		_definedCommands.put(inner.name(), inner);
		_definedCommandAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedCommandAlternatives.put(alternative, inner.name());
	}
	
	public <T> void add(Class<T> argumentType, Argument argument)
	{
		if(argument == null)
			throw new NullPointerException("'argument' must have a value.");
		
		Argument.Inner<T> inner = new Argument.Inner<T>(argument, argumentType);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new NullPointerException("'argument' must have a name.");
		
		_definedArguments.put(inner.name(), inner);
		if(!inner.optional()) {
			_mandatoryArguments = true;
			if(_numOfOptionalArguments >= 2) {
				String msg =
					"If there is more than one optional argument they must be the last arguments.";
				throw new IllegalStateException(msg);
			}
		}
		else
			_numOfOptionalArguments++;
	}

	public boolean optionExists(String name)
	{
		for(Option.Inner option : _cmdLineOptions)
			if(option.name().equals(_definedOptionGlobalAlternatives.get(name)))
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
		for(Option.Inner option : _cmdLineOptions)
			if(option.name().equals(_definedOptionGlobalAlternatives.get(name)))
				options.add(option);
		
		return options.toArray(new Option.Inner[0]);
	}
	
	public boolean commandExists(String name)
	{
		return _definedCommandAlternatives.containsKey(name);
	}
	
	public void parse(String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException
	{
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		try {
			parse(
				null, 
				Class.forName(className),
				args
			);
		}
		catch(ClassNotFoundException e) {
			String msg = "'" + className + "' was not found. Use other parse() method call.";
			throw new RuntimeException(msg, e);
		}
	}
	public void parse(Object callerObject, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException
	{
		if(callerObject == null)
			throw new NullPointerException("'callerObject' must have a value.");
		parse(callerObject, null, args);
	}
	public void parse(Class<?> callerClass, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException
	{
		if(callerClass == null)
			throw new NullPointerException("'callerClass' must have a value.");
		parse(null, callerClass, args);
	}
	private void parse(Object callerObject, Class<?> callerClass, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException
	{
		assert callerObject != null || callerClass != null;

		if(_definedCommands.size() > 0 && _definedArguments.size() > 0)
			throw
				new ConstraintException(
					"Both bare command line arguments and commands are not allowed."
				);
		
		// Adds a space character after a short option if missing.
		List<String> cmdLineArgs = new LinkedList<String>();
		Pattern p = Pattern.compile("^-\\p{Alpha}\\p{Alnum}+");
		for(String arg : args) {
			if(p.matcher(arg).matches()) {
				cmdLineArgs.add(arg.substring(0, 2));
				cmdLineArgs.add(arg.substring(2));
			}
			else
				cmdLineArgs.add(arg);
		}

		Set<Option.Inner> nonMultipleOptionCheckSet = new HashSet<Option.Inner>();
		_cmdLineCommand = null;
		while(cmdLineArgs.size() > 0) {
			String arg = cmdLineArgs.get(0);
			if(
				Util.checkOption(
					arg,
					cmdLineArgs,
					_definedGlobalOptions,
					_definedOptionGlobalAlternatives,
					nonMultipleOptionCheckSet,
					_cmdLineOptions
				)
			)
				continue;
			else
				if(_cmdLineCommand != null)
					throw
						new ConstraintException(
							"'" + arg + "' cannot be interpreted as a proper command line parameter."
						);
					
					
			if(_definedCommands.size() > 0) {
				Command.Inner command = _definedCommands.get(_definedCommandAlternatives.get(arg));
				if(command == null)
					throw
						new CommandNotFoundException(
							"A command was expected but '" + arg + "' cannot be interpreted "
								+ "as a command."
						);
				_cmdLineCommand = new Command.Inner(command);
				if(_cmdLineCommand.parse(cmdLineArgs))
					continue;
			}
			else {
				if(Util.checkArguments(null, cmdLineArgs, _definedArguments, _cmdLineArguments))
					continue;
			}
			
			// If this point is reached then it means that
			// the command line argument is undefined.
			throw new ConstraintException("'" + arg + "' not defined.");
		}
		if(_mandatoryArguments && _cmdLineArguments.size() == 0)
			throw new ConstraintException("A mandatory command line argument is missing.");
		if(_definedCommands.size() > 0 && _cmdLineCommand == null)
			throw new CommandNotFoundException("No command found from the command line.");

		// Global options.
		Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineOptions);
		
		// Command, command options and command arguments.
		Util.setAnnotatedValue(callerObject, callerClass, _cmdLineCommand.name(), _cmdLineCommand.id());
		Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineCommand.cmdLineOptions());
		Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineCommand.cmdLineArguments());
		
		// Global arguments.
		Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineArguments);
		
		if(_cmdLineCommand != null)
			_cmdLineCommand.execute(_cmdLineOptions);
	}
}
