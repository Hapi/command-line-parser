package com.hapiware.utils.cmdline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.hapiware.utils.cmdline.constraint.AnnotatedFieldSetException;
import com.hapiware.utils.cmdline.constraint.CommandNotFoundException;
import com.hapiware.utils.cmdline.constraint.ConfigurationException;
import com.hapiware.utils.cmdline.constraint.Constraint;
import com.hapiware.utils.cmdline.constraint.ConstraintException;
import com.hapiware.utils.cmdline.constraint.Enumeration;
import com.hapiware.utils.cmdline.constraint.IllegalCommandLineArgumentException;
import com.hapiware.utils.cmdline.element.Argument;
import com.hapiware.utils.cmdline.element.Command;
import com.hapiware.utils.cmdline.element.Description;
import com.hapiware.utils.cmdline.element.Option;
import com.hapiware.utils.cmdline.writer.Writer;
import com.hapiware.utils.cmdline.writer.Writer.HeadingLevel;


public class CommandLineParser
{
	private enum HelpType { OPTIONS, ARGUMENTS, COMMANDS, COMMAND_OPTIONS, COMMAND_ARGUMENTS };
	
	
	private final Description _description;
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
	private final boolean _useAnnotations;
	private final Class<?> _mainClass;
	private Set<HelpType> _definedArgumentTypes = new HashSet<HelpType>();
	
	
	public CommandLineParser(Class<?> mainClass, Description description)
	{
		if(mainClass == null)
			throw new NullPointerException("'mainClass' must have a value.");
		if(description == null)
			throw new NullPointerException("'description' must have a value.");
		
		_useAnnotations = true;
		_mainClass = mainClass;
		_description = description;
	}
	
	public CommandLineParser(Class<?> mainClass, boolean useAnnotations, Description description)
	{
		if(mainClass == null)
			throw new NullPointerException("'mainClass' must have a value.");
		if(description == null)
			throw new NullPointerException("'description' must have a value.");
		
		_useAnnotations = useAnnotations;
		_mainClass = mainClass;
		_description = description;
	}
	
	public void add(Option option)
	{
		if(option == null)
			throw new ConfigurationException("'option' must have a value.");
		
		Option.Inner inner = new Option.Inner(option);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'option' must have a name.");
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException("Option '" + inner.name() + "' must have a description.");
		
		_definedGlobalOptions.put(inner.name(), inner);
		_definedOptionGlobalAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedOptionGlobalAlternatives.put(alternative, inner.name());
		
		_definedArgumentTypes.add(HelpType.OPTIONS);
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
		
		Command.Inner inner = new Command.Inner(command, _useAnnotations);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'command' must have a name.");
		
		if(inner.shortDescription() == null || inner.shortDescription().length() == 0)
			throw
				new ConfigurationException(
					"Command '" + inner.name() + "' must have a short description."
				);
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException("Command '" + inner.name() + "' must have a description.");
		
		_definedCommands.put(inner.name(), inner);
		_definedCommandAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			_definedCommandAlternatives.put(alternative, inner.name());
		
		_definedArgumentTypes.add(HelpType.COMMANDS);
		if(inner.cmdLineOptions().size() > 0)
			_definedArgumentTypes.add(HelpType.COMMAND_OPTIONS);
		if(inner.cmdLineArguments().size() > 0)
			_definedArgumentTypes.add(HelpType.COMMAND_ARGUMENTS);
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
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException("Argument '" + inner.name() + "' must have a description.");
		
		if(_useAnnotations && inner.optional() && !inner.hasDefaultValueForOptional()) {
			String msg =
				"When annotations are used then optional arguments must have a default value "
					+ "('" + inner.name() + "'). "
					+ "Use Argument.optional(T) instead of Argument.optional().";
			throw new ConfigurationException(msg);
		}
		
		_definedArguments.put(inner.name(), inner);
		if(!inner.optional()) {
			_mandatoryArguments = true;
			if(_numOfOptionalArguments >= 2) {
				String msg =
					"If there are more than one optional argument they must be the last arguments "
						+ "('" + inner.name() + "'). "
						+ " A single optional argument can have any position.";
				throw new ConfigurationException(msg);
			}
		}
		else
			_numOfOptionalArguments++;
		
		_definedArgumentTypes.add(HelpType.ARGUMENTS);
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
				options.add(new Option.Inner(option));
		
		return options.toArray(new Option.Inner[0]);
	}
	
	public Argument.Inner<?> argument(String name)
	{
		for(Argument.Inner<?> argument : _cmdLineArguments)
			if(argument.name().equals(name))
				return argument.clone();

		return null;
	}
	
	public boolean commandExists(String name)
	{
		return _definedCommandAlternatives.containsKey(name);
	}
	
	public Command.Inner getCommand()
	{
		return new Command.Inner(_cmdLineCommand);
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

		// TODO: Handle all the help commands.
		
		if(args.length == 1 && args[0].equals("--version"))
			showVersionAndExit();
		
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

	
	private void showVersionAndExit()
	{
		System.out.println(
			"  Version: " + _mainClass.getPackage().getImplementationVersion()
		);
		System.exit(0);
	}

	
	public void printHelp(Writer writer)
	{
		final String javaCommand = 
			"java -jar " + _mainClass.getPackage().getImplementationTitle() + ".jar";
		
		writer.header();
		printUsage(writer, javaCommand);
		printDescription(writer);
		printGlobalOptions(writer);
		// TODO: Print Commands
		// TODO: Print Command options
		// TODO: Print Command arguments
		// TODO: Print Arguments
		// TODO: Print Examples (add method to add examples).
		writer.footer();
	}
	
	private String changeStrong(Writer writer, String inputText)
	{
		return
			inputText.replaceAll(
				Description.STRONG_BEGIN_TAG,
				writer.strongEnd()
			).replaceAll(Description.STRONG_END_TAG, writer.strongEnd());
	}
	
	private void printUsage(Writer writer, String javaCommand)
	{
		String helpCommand = " -? | --help ['full'";
		helpCommand += _definedArgumentTypes.contains(HelpType.OPTIONS) ? " | 'opts'" : "";
		helpCommand += _definedArgumentTypes.contains(HelpType.COMMANDS) ? " | 'cmds'" : "";
		helpCommand += _definedArgumentTypes.contains(HelpType.OPTIONS) ? " | OPT" : "";
		helpCommand += _definedArgumentTypes.contains(HelpType.COMMANDS) ? " | CMD" : "";
		helpCommand += "]";
		String command = "";
		command += _definedArgumentTypes.contains(HelpType.OPTIONS) ? " OPTS" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMANDS) ? " CMD" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMAND_OPTIONS) ? " CMD-OPTS" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMAND_ARGUMENTS) ? " CMD-ARGS" : "";
		command += _definedArgumentTypes.contains(HelpType.ARGUMENTS) ? " OPTS" : " ARGS";
		writer.h1("Usage:");
		writer.line(HeadingLevel.H1, javaCommand + helpCommand);
		writer.line(HeadingLevel.H1, javaCommand + " --version");
		writer.line(HeadingLevel.H1, javaCommand + command);
		writer.line(HeadingLevel.H1, "");
	}
	
	private void printDescription(Writer writer)
	{
		writer.h1("Description:");
		for(String paragraph : _description.toParagraphs())
			writer.paragraph(HeadingLevel.H1, changeStrong(writer, paragraph));
	}
	
	private void printGlobalOptions(Writer writer)
	{
		if(_definedGlobalOptions.size() == 0)
			return;
		
		writer.h1("OPTS:");
		for(Entry<String, Option.Inner> optionSet : _definedGlobalOptions.entrySet()) {
			Option.Inner option = optionSet.getValue();
			
			// TODO: Add alternatives also to header.
			writer.h2(option.name());
			
			for(String paragraph : option.description())
				writer.paragraph(HeadingLevel.H2, changeStrong(writer, paragraph));
			
			// TODO: Add restriction descriptions
			boolean hasEnumConstraint = false;
			boolean hasOtherConstraints = false;
			if(option.argument() != null)
				for(Constraint constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration)
						hasEnumConstraint = true;
					else
						hasOtherConstraints = true;
				}
			
			if(hasOtherConstraints) {
				writer.h3("Constraints:");
				writer.listBegin(HeadingLevel.H3);
				for(Constraint constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration)
						hasEnumConstraint = true;
					else
						for(String constraintDesc : constraint.description().toParagraphs())
							writer.listItem(changeStrong(writer, constraintDesc));
				}
				writer.listEnd();
			}
				
			if(hasEnumConstraint) {
				writer.h3("Values:");
				writer.listBegin(HeadingLevel.H3);
				for(Constraint constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration)
						for(String constraintDesc : constraint.description().toParagraphs())
							writer.listItem(changeStrong(writer, constraintDesc));
				}
				writer.listEnd();
			}
		}
	}
	
	
}
