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
import com.hapiware.utils.cmdline.constraint.ConfigurationException;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
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
			throw new ConfigurationException("'option' must have a value.");
		
		Option.Inner inner = new Option.Inner(option);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'option' must have a name.");
		
		_definedGlobalOptions.put(inner.name(), inner);
		_definedOptionGlobalAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedOptionGlobalAlternatives.put(alternative, inner.name());
	}
	
	public void add(Command command)
	{
		if(_definedArguments.size() > 0)
			throw
				new ConfigurationException(
					"Both bare command line arguments and commands cannot be used at the same time."
						+ " Use either one of them."
				);
		
		if(command == null)
			throw new ConfigurationException("'command' must have a value.");
		
		Command.Inner inner = new Command.Inner(command);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'command' must have a name.");
		
		_definedCommands.put(inner.name(), inner);
		_definedCommandAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedCommandAlternatives.put(alternative, inner.name());
	}
	
	public <T> void add(Class<T> argumentType, Argument argument)
	{
		if(_definedCommands.size() > 0)
			throw
				new ConfigurationException(
					"Both bare command line arguments and commands cannot be used at the same time."
						+ " Use either one of them."
				);
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
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		parse(className, args);
	}

	
	public void parse(Object callerObject, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		if(callerObject == null)
			throw new NullPointerException("'callerObject' must have a value.");
		parse(callerObject, null, args);
	}
	
	
	public void parse(Class<?> callerClass, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		if(callerClass == null)
			throw new NullPointerException("'callerClass' must have a value.");
		parse(null, callerClass, args);
	}

	
	public void parsePrintAndExitOnError(String[] args)
	{
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			parse(className, args);
		}
		catch(ConstraintException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(Throwable t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
			System.exit(-2);
		}
	}

	
	public void parsePrintAndExitOnError(Object callerObject, String[] args)
	{
		try {
			parse(callerObject, args);
		}
		catch(ConstraintException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(Throwable t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
			System.exit(-2);
		}
	}
	
	public void parsePrintAndExitOnError(Class<?> callerClass, String[] args)
	{
		try {
			parse(callerClass, args);
		}
		catch(ConstraintException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		catch(Throwable t) {
			System.out.println(t.getMessage());
			t.printStackTrace();
			System.exit(-2);
		}
	}
	

	private void parse(String className, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			parse(
				null, 
				Class.forName(className),
				args
			);
		}
		catch(ClassNotFoundException e) {
			String msg = 
				"'" + className + "' was not found. An attempt to find automatically a defining "
					+ "class for the annotated fields failed. Use other parse() method call.";
			throw new RuntimeException(msg, e);
		}
	}
	
	
	private void parse(Object callerObject, Class<?> callerClass, String[] args)
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		assert callerObject != null || callerClass != null;

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
						new IllegalCommandLineArgumentException(
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
			throw new IllegalCommandLineArgumentException("'" + arg + "' not defined.");
		}
		if(_mandatoryArguments && _cmdLineArguments.size() == 0)
			throw new IllegalCommandLineArgumentException("A mandatory command line argument is missing.");
		if(_definedCommands.size() > 0 && _cmdLineCommand == null)
			throw new CommandNotFoundException("No command found from the command line.");

		// Global options.
		Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineOptions);
		
		// Global arguments.
		Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineArguments);
		
		// Command, command options, command arguments and excutors.
		if(_cmdLineCommand != null) {
			Util.setAnnotatedValue(
				callerObject,
				callerClass,
				_cmdLineCommand.name(),
				_cmdLineCommand.id()
			);
			Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineCommand.cmdLineOptions());
			Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineCommand.cmdLineArguments());
			_cmdLineCommand.execute(_cmdLineOptions);
		}
	}
}
