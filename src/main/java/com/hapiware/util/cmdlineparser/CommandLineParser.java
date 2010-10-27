package com.hapiware.util.cmdlineparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.hapiware.util.cmdlineparser.constraint.Constraint;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;
import com.hapiware.util.cmdlineparser.writer.ScreenWriter;
import com.hapiware.util.cmdlineparser.writer.Writer;
import com.hapiware.util.cmdlineparser.writer.Writer.Level;


/**
 * System property {@code writerclass} overrides the hard coded {@link Writer}. {@code writerclass}
 * must have a full class name implementing {@link Writer} interface and have the default constructor. 
 * {@code writerclass} also recognizes a special format to use internal writer implementations.
 * Internal writers are recognized by their class name prefixes (i.e. Screen, Html, Xml, Wikidot,
 * Confluence, GitHub). The naming convention is that if the package name is
 * {@code com.hapiware.util.cmdlineparser.writer} and the implementation class name ends with word
 * {@code Writer} then the part before word {@code Writer} can be used as a parameter for
 * {@code writer.class}.
 * 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public final class CommandLineParser
{
	private enum HelpType { OPTIONS, ARGUMENTS, COMMANDS, COMMAND_OPTIONS, COMMAND_ARGUMENTS };
	
	private static final String COMPLETE_HELP_COMMAND = "all";
	private static final String USAGE_HELP_COMMAND = "usage";
	private static final String EXAMPLES_HELP_COMMAND = "examples";
	private static final String OPTS_HELP_COMMAND = "opts";
	private static final String CMDS_HELP_COMMAND = "cmds";
	private static final String CMD_HELP_COMMAND = "cmd=";
	private static final String ARGS_HELP_COMMAND = "args";
	private static final String WRITER_CLASS_PROPERTY = "writerclass";

	
	private final Description _description;
	private Map<String, Option.Internal> _definedGlobalOptions = new LinkedHashMap<String, Option.Internal>();
	private Map<String, String> _definedGlobalOptionAlternatives = new HashMap<String, String>();
	private Map<String, Command.Internal> _definedCommands = new LinkedHashMap<String, Command.Internal>();
	private Map<String, String> _definedCommandAlternatives = new HashMap<String, String>();
	private Map<String, Argument.Internal<?>> _definedArguments =
		new LinkedHashMap<String, Argument.Internal<?>>();
	private boolean _mandatoryArguments;
	private int _numOfOptionalArguments;
	private boolean _previousWasOptional;
	private List<Option.Internal> _cmdLineGlobalOptions = new ArrayList<Option.Internal>(); 
	private Command.Internal _cmdLineCommand;
	private List<Argument.Internal<?>> _cmdLineArguments = new ArrayList<Argument.Internal<?>>();
	private final Class<?> _mainClass;
	private final String _javaCommand;
	private Set<HelpType> _definedArgumentTypes = new HashSet<HelpType>();
	private List<String> _exampleArguments = new LinkedList<String>();
	private final Writer _writer;
	
	// This is overridden in tests by using reflection.
	private final ExitHandler _exitHandler =
		new ExitHandler()
		{
			public void exit(int status)
			{
				System.exit(status);
			}
		};
	
	
	public CommandLineParser(Class<?> mainClass, Description description)
	{
		this(mainClass, new ScreenWriter(), description);
	}
	
	public CommandLineParser(Class<?> mainClass, int screenWidth, Description description)
	{
		this(mainClass, new ScreenWriter(screenWidth), description);
	}
	
	public CommandLineParser(
		Class<?> mainClass,
		Writer writer,
		Description description
	)
	{
		if(mainClass == null)
			throw new ConfigurationException("'mainClass' must have a value.");
		if(writer == null)
			throw new ConfigurationException("'writer' must have a value.");
		if(description == null)
			throw new ConfigurationException("'description' must have a value.");
		if(mainClass.getPackage().getImplementationTitle() == null)
			throw new ConfigurationException("Implementation-Title: is missing from MANIFEST.MF.");
		if(mainClass.getPackage().getImplementationVersion() == null)
			throw new ConfigurationException("Implementation-Version: is missing from MANIFEST.MF.");
		
		_mainClass = mainClass;
		Writer writerFromSystemProperty = createSystemPropertyWriter();
		_writer = writerFromSystemProperty != null ? writerFromSystemProperty : writer;
		_javaCommand = "java -jar " + _mainClass.getPackage().getImplementationTitle() + ".jar";
		_description = description;
	}
	
	public void add(Option option)
	{
		if(option == null)
			throw new ConfigurationException("'option' must have a value.");
		
		Option.Internal internal = new Option.Internal(option);
		if(internal.name() == null || internal.name().trim().length() == 0)
			throw new ConfigurationException("'option' must have a name.");
		
		if(internal.description().size() == 0)
			throw
				new ConfigurationException("Option '" + internal.name() + "' must have a description.");
		if(_definedGlobalOptionAlternatives.containsKey(internal.name()))
			throw
				new ConfigurationException("Option name '" + internal.name() + "' must be unique.");
		
		_definedGlobalOptions.put(internal.name(), internal);
		_definedGlobalOptionAlternatives.put(internal.name(), internal.name());
		for(String alternative : internal.alternatives())
			if(_definedGlobalOptionAlternatives.put(alternative, internal.name()) != null)
				throw
					new ConfigurationException(
						"Option alternative name '" + alternative + "' must be unique."
					);
		
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
		
		Command.Internal internal = new Command.Internal(command);
		if(internal.name() == null || internal.name().trim().length() == 0)
			throw new ConfigurationException("'command' must have a name.");
		
		if(internal.shortDescription() == null || internal.shortDescription().length() == 0)
			throw
				new ConfigurationException(
					"Command '" + internal.name() + "' must have a short description."
				);
		
		if(internal.description().size() == 0)
			throw
				new ConfigurationException("Command '" + internal.name() + "' must have a description.");
		if(_definedCommandAlternatives.containsKey(internal.name()))
			throw
				new ConfigurationException("Command name '" + internal.name() + "' must be unique.");

		_definedCommands.put(internal.name(), internal);
		_definedCommandAlternatives.put(internal.name(), internal.name());
		for(String alternative : internal.alternatives())
			if(_definedCommandAlternatives.put(alternative, internal.name()) != null)
				throw
					new ConfigurationException(
						"Command alternative name '" + alternative + "' must be unique."
					);
		
		_definedArgumentTypes.add(HelpType.COMMANDS);
		if(internal.definedOptions().size() > 0)
			_definedArgumentTypes.add(HelpType.COMMAND_OPTIONS);
		if(internal.definedArguments().size() > 0)
			_definedArgumentTypes.add(HelpType.COMMAND_ARGUMENTS);
	}
	
	public <T> void add(Class<T> argumentType, Argument<T> argument)
	{
		if(_definedCommands.size() > 0)
			throw
				new ConfigurationException(
					"Both bare command line arguments and commands cannot be used at the same time."
						+ " Use either one of them."
				);
		if(argument == null)
			throw new ConfigurationException("'argument' must have a value.");
		
		Argument.Internal<T> internal = new Argument.Internal<T>(argument, argumentType);
		if(internal.name() == null || internal.name().trim().length() == 0)
			throw new ConfigurationException("'argument' must have a name.");
		
		if(internal.description().size() == 0)
			throw
				new ConfigurationException("Argument '" + internal.name() + "' must have a description.");
		if(_definedArguments.containsKey(internal.name()))
			throw
				new ConfigurationException("Argument name '" + internal.name() + "' must be unique.");

		
		for(Constraint<?> constraint : internal.constraints())
			if(!constraint.typeCheck(argumentType)) {
				String msg =
					"Using '" + constraint.getClass().getName() + "' with argument type '"
						+ argumentType + "' creates a type conflict ('" + internal.name() + "').";
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
					+ "The first conflicting argument is '" + internal.name() + "'. "
					+ "A single optional argument can have any position.";
			throw new ConfigurationException(msg);
		}
		_previousWasOptional = internal.optional();
		
		_definedArgumentTypes.add(HelpType.ARGUMENTS);
	}

	public void addExampleArguments(String exampleArguments)
	{
		if(exampleArguments == null)
			throw new ConfigurationException("'exampleArguments' must have a value.");
		
		_exampleArguments.add(exampleArguments);
	}
	
	public boolean optionExists(String name)
	{
		for(Option.Internal option : _cmdLineGlobalOptions)
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
				return true;
		
		return false;
	}
	
	public Option.Data getOption(String name)
	{
		try {
			return getOptions(name)[0];
		}
		catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOptionValue(String name)
	{
		Option.Data option = getOption(name);
		if(option != null && option.getArgument() != null)
			return (T)option.getArgument().getValue();
		else
			return null;
	}
	
	public Option.Data[] getOptions(String name)
	{
		List<Option.Data> options = new ArrayList<Option.Data>();
		for(Option.Internal option : _cmdLineGlobalOptions)
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
				options.add(new Option.Data(option));
		
		return options.toArray(new Option.Data[0]);
	}
	
	public Option.Data[] getAllOptions()
	{
		List<Option.Data> options = new ArrayList<Option.Data>();
		for(Option.Internal option : _cmdLineGlobalOptions)
			options.add(new Option.Data(option));
		
		return options.toArray(new Option.Data[0]);
	}
	
	public Argument.Data<?> getArgument(String name)
	{
		for(Argument.Internal<?> argument : _cmdLineArguments)
			if(argument.name().equals(name))
				return argument.createDataObject();

		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getArgumentValue(String name)
	{
		Argument.Data<?> argument = getArgument(name);
		if(argument != null)
			return (T)argument.getValue();
		else
			return null;
	}
	
	public Argument.Data<?>[] getAllArguments()
	{
		List<Argument.Data<?>> arguments = new ArrayList<Argument.Data<?>>();
		for(Argument.Internal<?> argument : _cmdLineArguments)
			arguments.add(argument.createDataObject());
		
		return arguments.toArray(new Argument.Data[0]);
	}
	
	public boolean commandExists(String name)
	{
		return _definedCommandAlternatives.containsKey(name);
	}
	
	public Command.Data getCommand()
	{
		return new Command.Data(_cmdLineCommand);
	}
	
	public Writer getWriter()
	{
		return _writer;
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

	
	/**
	 * Parses command line arguments, catches exceptions, prints error message and shows help.
	 * 
	 * TODO: Add more information...
	 * @param args
	 */
	public void parsech(String[] args)
	{
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			parse(className, args);
		}
		catch(ConstraintException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
		}
	}

	
	public void parsech(Object callerObject, String[] args)
	{
		try {
			parse(callerObject, args);
		}
		catch(ConstraintException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
		}
	}
	
	public void parsech(Class<?> callerClass, String[] args)
	{
		try {
			parse(callerClass, args);
		}
		catch(ConstraintException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
		}
	}
	

	/**
	 * Parses command line arguments, catches exceptions and prints error message. Does not show help.
	 * 
	 * TODO: Add more information...
	 * @param args
	 */
	public void parsec(String[] args)
	{
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			parse(className, args);
		}
		catch(ConstraintException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
		}
	}

	
	public void parsec(Object callerObject, String[] args)
	{
		try {
			parse(callerObject, args);
		}
		catch(ConstraintException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
		}
	}
	
	public void parsec(Class<?> callerClass, String[] args)
	{
		try {
			parse(callerClass, args);
		}
		catch(ConstraintException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorMessageWithoutHelp(e);
			_exitHandler.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			_exitHandler.exit(-2);
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
	
	
	private void checkInternalCommand(String args[])
	{
		if(args.length == 1 && args[0].equals("--version"))
			showVersionAndExit();

		if(
			(
				args.length == 1 
				|| args.length == 2
			)
			&& (
				args[0].equals("-?")
				|| args[0].equalsIgnoreCase("--help")
			)
		) {
			if(args.length == 1) {
				printShortHelp();
				_exitHandler.exit(0);
			}
			
			String helpCommand = args[1];
			if(helpCommand.equals(COMPLETE_HELP_COMMAND)) {
				printCompleteHelp();
				_exitHandler.exit(0);
			}
			if(helpCommand.equals(USAGE_HELP_COMMAND)) {
				printUsageHelp();
				_exitHandler.exit(0);
			}
			if(helpCommand.equals(EXAMPLES_HELP_COMMAND)) {
				printExamplesHelp();
				_exitHandler.exit(0);
			}
			if(_definedGlobalOptions.size() > 0 && helpCommand.equals(OPTS_HELP_COMMAND)) {
				printGlobalOptionsHelp();
				_exitHandler.exit(0);
			}
			if(_definedCommands.size() > 0 && helpCommand.equals(CMDS_HELP_COMMAND)) {
				printCommandsHelp();
				_exitHandler.exit(0);
			}
			if(_definedCommands.size() > 0 && helpCommand.startsWith(CMD_HELP_COMMAND)) {
				String[] afterSplit = helpCommand.split("=");
				if(afterSplit.length > 1)
					printCommandHelp(afterSplit[1]);
				else
					printCommandHelp("");
				_exitHandler.exit(0);
			}
			if(_definedArguments.size() > 0 && helpCommand.equals(ARGS_HELP_COMMAND)) {
				printGlobalArgumentsHelp();
				_exitHandler.exit(0);
			}

			_writer.header();
			_writer.level1Begin("Help error:");
			_writer.line(Level.L1, "'" + helpCommand + "' is not a valid help command.");
			_writer.line(Level.L1, "");
			_writer.level1End();
			printUsage();
			_writer.footer();
			_exitHandler.exit(0);
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

		checkInternalCommand(args);
		
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

		Set<Option.Internal> nonMultipleOptionCheckSet = new HashSet<Option.Internal>();
		_cmdLineCommand = null;
		boolean argumentsChecked = false;
		while(cmdLineArgs.size() > 0) {
			String arg = cmdLineArgs.get(0);
			if(
				Util.checkOption(
					arg,
					cmdLineArgs,
					_definedGlobalOptions,
					_definedGlobalOptionAlternatives,
					nonMultipleOptionCheckSet,
					_cmdLineGlobalOptions
				)
			)
				continue;
			else {
				if(arg.startsWith("-") && !Pattern.matches(Util.NEGATIVE_NUMBER_PATTERN, arg)) {
					String msg = "'" + arg + "' is not a valid option.";
					throw new IllegalCommandLineArgumentException(msg);
				}
				
				if(_cmdLineCommand != null) {
					String msg =
						"Command line argument '" + arg + "' "
							+ "for command '" + _cmdLineCommand.name() + "' " 
							+ "cannot be interpreted as a proper command line argument. "
							+ "All the arguments must be sequentially positioned. "
							+ "Check that there are no options between arguments.";
					throw new IllegalCommandLineArgumentException(msg);
				}
			}
					
			if(_definedCommands.size() > 0) {
				Command.Internal command = _definedCommands.get(_definedCommandAlternatives.get(arg));
				if(command == null)
					throw
						new CommandNotFoundException(
							"A command was expected but '" + arg + "' cannot be interpreted "
								+ "as a command."
						);
				_cmdLineCommand = new Command.Internal(command);
				if(_cmdLineCommand.parse(cmdLineArgs))
					continue;
			}
			else {
				if(argumentsChecked) {
					String msg =
						"Command line argument '" + arg + "' "
							+ "cannot be interpreted as a proper command line argument. "
							+ "All the arguments must be sequentially positioned. "
							+ "Check that there are no options between arguments.";
					throw new IllegalCommandLineArgumentException(msg);
				}
				if(Util.checkArguments(null, cmdLineArgs, _definedArguments, _cmdLineArguments)) {
					argumentsChecked = true;
					continue;
				}
			}
			
			// If this point is reached then it means that
			// the command line argument is undefined.
			throw new IllegalCommandLineArgumentException("'" + arg + "' not defined.");
		}

		// There are no command line arguments and all the arguments are optional.
		if(_cmdLineArguments.size() == 0 && _definedArguments.size() > 0 && !_mandatoryArguments) {
			Set<Entry<String, Argument.Internal<?>>> entrySet = _definedArguments.entrySet();
			for(Iterator<?> it = entrySet.iterator(); it.hasNext();) {
				@SuppressWarnings("unchecked")
				Argument.Internal<?> argument =
					((Entry<String, Argument.Internal<?>>)it.next()).getValue();
				argument.setDefaultValue();
				_cmdLineArguments.add(argument);
			}
		}
		
		if(_mandatoryArguments && _cmdLineArguments.size() == 0)
			throw
				new IllegalCommandLineArgumentException(
					"A mandatory command line argument is missing."
				);
		if(_definedCommands.size() > 0 && _cmdLineCommand == null)
			throw new CommandNotFoundException("No command found from the command line.");

		// Global options.
		Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineGlobalOptions);
		
		// Global arguments.
		Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineArguments);
		
		// Command, command options, command arguments and executors.
		if(_cmdLineCommand != null) {
			Util.setAnnotatedValue(
				callerObject,
				callerClass,
				_cmdLineCommand.name(),
				_cmdLineCommand.id()
			);
			Util.setAnnotatedOptions(callerObject, callerClass, _cmdLineCommand.cmdLineOptions());
			Util.setAnnotatedArguments(callerObject, callerClass, _cmdLineCommand.cmdLineArguments());
			List<Option.Data> optionData = new ArrayList<Option.Data>();
			for(Option.Internal internal : _cmdLineGlobalOptions)
				optionData.add(new Option.Data(internal));
			_cmdLineCommand.execute(Collections.unmodifiableList(optionData));
		}
	}

	
	private void showVersionAndExit()
	{
		_writer.header();
		_writer.level1Begin("Version: " + _mainClass.getPackage().getImplementationVersion());
		_writer.level1End();
		_writer.footer();
		_exitHandler.exit(0);
	}

	
	public void printCompleteHelp()
	{
		_writer.header();
		printUsage();
		printDescription();
		printGlobalOptions();
		printCommands();
		printGlobalArguments();
		printExamples();
		_writer.footer();
	}
	
	public void printShortHelp()
	{
		_writer.header();
		printShortHelpWithoutHeaders();
		_writer.footer();
	}
	
	public void printUsageHelp()
	{
		_writer.header();
		printUsage();
		_writer.footer();
	}
	
	public void printExamplesHelp()
	{
		_writer.header();
		printExamples();
		_writer.footer();
	}
	
	private void printShortHelpWithoutHeaders()
	{
		printUsage();
		printDescription();
		printShortCommands();
		printGlobalArguments();
		_writer.line(Level.L1, "");
		_writer.level1Begin("Notice:");
		_writer.line(
			Level.L1,
			"This is a short help. To get a complete help run:"
		);
		_writer.line(Level.L1, _javaCommand + " -? " + COMPLETE_HELP_COMMAND);
		_writer.level1End();
	}
	
	public void printGlobalOptionsHelp()
	{
		_writer.header();
		printGlobalOptions();
		_writer.footer();
	}
	
	public void printGlobalArgumentsHelp()
	{
		_writer.header();
		printGlobalArguments();
		_writer.footer();
	}
	
	public void printThrowable(Throwable t)
	{
		_writer.header();
		_writer.level1Begin(t.getClass().getName());
		if(t.getCause() != null)
			_writer.paragraph(Level.L1, t.getCause().getClass().getName());
		_writer.paragraph(Level.L1, t.getMessage());
		for(StackTraceElement stackTraceElement : t.getStackTrace())
			_writer.paragraph(Level.L1, stackTraceElement.toString());
		_writer.level1End();
		_writer.footer();
	}

	public void printErrorWithShortHelp(Throwable cause)
	{
		_writer.header();
		_writer.level1Begin("Error:");
		//_writer.paragraph(Level.L1, cause.getClass().getName());
		_writer.paragraph(Level.L1, cause.getMessage());
		_writer.level1End();
		printShortHelpWithoutHeaders();
		_writer.footer();
	}
	
	public void printErrorWithCommandsHelp(Throwable cause)
	{
		_writer.header();
		_writer.level1Begin("Error:");
		//_writer.paragraph(Level.L1, cause.getClass().getName());
		_writer.paragraph(Level.L1, cause.getMessage());
		_writer.level1End();
		printShortCommands();
		_writer.footer();
	}
	
	public void printErrorMessageWithoutHelp(Throwable cause)
	{
		_writer.header();
		_writer.level1Begin("Error:");
		//_writer.paragraph(Level.L1, cause.getClass().getName());
		_writer.paragraph(Level.L1, cause.getMessage());
		_writer.level1End();
		_writer.footer();
	}
	
	public void printCommandsHelp()
	{
		_writer.header();
		printShortCommands();
		_writer.footer();
	}
	
	public void printCommandHelp(String commandName)
	{
		Command.Internal command = _definedCommands.get(_definedCommandAlternatives.get(commandName));
		_writer.header();
		if(command != null) {
			_writer.level1Begin("CMD:");
			printCommand(command);
			_writer.level1End();
		}
		else {
			_writer.level1Begin("Help error:");
			_writer.line(Level.L1, "'" + commandName + "' is not a valid command.");
			_writer.line(Level.L1, "");
			_writer.level1End();
			printShortCommands();
		}
		_writer.footer();
	}
	
	private String replaceStrong(String inputText)
	{
		return
			inputText.replaceAll(
				Description.STRONG_BEGIN_TAG,
				_writer.strongBegin()
			).replaceAll(Description.STRONG_END_TAG, _writer.strongEnd());
	}
	
	private void printUsage()
	{
		String usageExamplesHelpCommand =
			" -? | --help ['" + USAGE_HELP_COMMAND + "' | '" + EXAMPLES_HELP_COMMAND + "']";
		String helpCommand = " -? | --help ['" + COMPLETE_HELP_COMMAND + "'"; 
		helpCommand +=
			_definedArgumentTypes.contains(HelpType.OPTIONS) ? " | '" + OPTS_HELP_COMMAND + "'" : "";
		helpCommand +=
			_definedArgumentTypes.contains(HelpType.ARGUMENTS) ? " | '" + ARGS_HELP_COMMAND + "'" : "";
		helpCommand +=
			_definedArgumentTypes.contains(HelpType.COMMANDS) ? " | '" + CMDS_HELP_COMMAND + "'" : "";
		helpCommand +=
			_definedArgumentTypes.contains(HelpType.COMMANDS) ? " | " + CMD_HELP_COMMAND + "CMD" : "";
		helpCommand += "]";
		String command = "";
		command += _definedArgumentTypes.contains(HelpType.OPTIONS) ? " [OPTS]" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMANDS) ? " CMD" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMAND_OPTIONS) ? " [CMD-OPTS]" : "";
		command += _definedArgumentTypes.contains(HelpType.COMMAND_ARGUMENTS) ? " CMD-ARGS" : "";
		command +=
			_definedArgumentTypes.contains(HelpType.ARGUMENTS) ? 
				(_definedArguments.size() > 0 && !_mandatoryArguments ? " [ARGS]" : " ARGS") 
				: "";
		_writer.level1Begin("Usage:");
		_writer.codeBegin(Level.L1);
		_writer.codeLine(_javaCommand + helpCommand);
		_writer.codeLine(_javaCommand + usageExamplesHelpCommand);
		_writer.codeLine(_javaCommand + " --version");
		_writer.codeLine(_javaCommand + command);
		_writer.codeEnd();
		_writer.level1End();
	}
	
	private void printDescription()
	{
		_writer.level1Begin("Description:");
		for(String paragraph : _description.toParagraphs())
			_writer.paragraph(Level.L1, replaceStrong(paragraph));
		_writer.level1End();
	}

	private void printOptions(
		Map<String, Option.Internal> options,
		boolean isCommand
	)
	{
		if(options.size() == 0)
			return;
		
		if(isCommand)
			_writer.level3Begin("CMD-OPTS:");
		else
			_writer.level1Begin("OPTS:");
		for(Entry<String, Option.Internal> optionEntry : options.entrySet()) {
			Option.Internal option = optionEntry.getValue();
			
			// Adds option names.
			String optionNames = option.name();
			for(String alternative : option.alternatives())
				optionNames += ", " + alternative;
			if(isCommand)
				_writer.level4Begin(optionNames);
			else
				_writer.level2Begin(optionNames);

			// Adds description and handles optional arguments and possible default values.
			boolean isFirstParagraph = true;
			Level level = isCommand ? Level.L4 : Level.L2;
			for(String paragraph : option.description()) {
				if(isFirstParagraph) {
					if(option.argument() != null && option.argument().optional())
						paragraph += 
							" Argument is optional. " 
								+ option.argument().defaultValueDescription(); 
					if(option.multiple())
						paragraph += " This option can occur several times.";
					
					isFirstParagraph = false;
				}
				_writer.paragraph(level, replaceStrong(paragraph));
			}
			
			boolean hasEnumConstraint = false;
			boolean hasOtherConstraints = false;
			if(option.argument() != null)
				for(Constraint<?> constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration<?>)
						hasEnumConstraint = true;
					else
						hasOtherConstraints = true;
				}
			
			level = isCommand ? Level.L5 : Level.L3;
			if(hasOtherConstraints) {
				if(isCommand)
					_writer.level5Begin("Constraints:");
				else
					_writer.level3Begin("Constraints:");
				_writer.listBegin(level);
				for(Constraint<?> constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration<?>)
						hasEnumConstraint = true;
					else
						for(String constraintDesc : constraint.description().toParagraphs())
							_writer.listItem(replaceStrong(constraintDesc));
				}
				_writer.listEnd();
				if(isCommand)
					_writer.level5End();
				else
					_writer.level3End();
			}
				
			if(hasEnumConstraint) {
				if(isCommand)
					_writer.level5Begin("Values:");
				else
					_writer.level3Begin("Values:");
				_writer.listBegin(level);
				for(Constraint<?> constraint : option.argument().constraints()) {
					if(constraint instanceof Enumeration<?>)
						for(String constraintDesc : constraint.description().toParagraphs())
							_writer.listItem(replaceStrong(constraintDesc));
				}
				_writer.listEnd();
				if(isCommand)
					_writer.level5End();
				else
					_writer.level3End();
			}
			
			if(isCommand)
				_writer.level4End();
			else
				_writer.level2End();
		}
		if(isCommand)
			_writer.level3End();
		else
			_writer.level1End();
	}

	
	private void printArguments(
		Map<String, Argument.Internal<?>> arguments,
		boolean isCommand
	)
	{
		if(arguments.size() == 0)
			return;
		
		if(isCommand)
			_writer.level3Begin("CMD-ARGS:");
		else
			_writer.level1Begin("ARGS:");
		for(Entry<String, Argument.Internal<?>> argumentEntry : arguments.entrySet()) {
			Argument.Internal<?> argument = argumentEntry.getValue();
			
			// Adds argument name.
			String argumentName =
				argument.optional() ? "[" + argument.name() + "]" : argument.name();
			if(isCommand)
				_writer.level4Begin(argumentName);
			else
				_writer.level2Begin(argumentName);

			// Adds description and handles optional arguments and possible default values.
			boolean isFirstParagraph = true;
			Level level = isCommand ? Level.L4 : Level.L2;
			for(String paragraph : argument.description()) {
				if(isFirstParagraph && argument.optional()) {
					paragraph += 
						" Argument is optional. " 
							+ argument.defaultValueDescription(); 
					isFirstParagraph = false;
				}
				_writer.paragraph(level, replaceStrong(paragraph));
			}
			
			boolean hasEnumConstraint = false;
			boolean hasOtherConstraints = false;
			for(Constraint<?> constraint : argument.constraints()) {
				if(constraint instanceof Enumeration<?>)
					hasEnumConstraint = true;
				else
					hasOtherConstraints = true;
			}
			
			level = isCommand ? Level.L5 : Level.L3;
			if(hasOtherConstraints) {
				if(isCommand)
					_writer.level5Begin("Constraints:");
				else
					_writer.level3Begin("Constraints:");
				_writer.listBegin(level);
				for(Constraint<?> constraint : argument.constraints()) {
					if(constraint instanceof Enumeration<?>)
						hasEnumConstraint = true;
					else
						for(String constraintDesc : constraint.description().toParagraphs())
							_writer.listItem(replaceStrong(constraintDesc));
				}
				_writer.listEnd();
				if(isCommand)
					_writer.level5End();
				else
					_writer.level3End();
			}
				
			if(hasEnumConstraint) {
				if(isCommand)
					_writer.level5Begin("Values:");
				else
					_writer.level3Begin("Values:");
				_writer.listBegin(level);
				for(Constraint<?> constraint : argument.constraints()) {
					if(constraint instanceof Enumeration<?>)
						for(String constraintDesc : constraint.description().toParagraphs())
							_writer.listItem(replaceStrong(constraintDesc));
				}
				_writer.listEnd();
				if(isCommand)
					_writer.level5End();
				else
					_writer.level3End();
			}
			
			if(isCommand)
				_writer.level4End();
			else
				_writer.level2End();
		}
		
		if(isCommand)
			_writer.level3End();
		else
			_writer.level1End();
	}

	
	private void printGlobalOptions()
	{
		printOptions(_definedGlobalOptions, false);
	}

	
	private void printCommand(Command.Internal command)
	{
		// Adds command names.
		String commandNames = command.name();
		for(String alternative : command.alternatives())
			commandNames += ", " + alternative;
		commandNames +=
			command.definedOptions().size() > 0 ? " [CMD-OPTS]" : "";
		for(Entry<String, Argument.Internal<?>> argumentEntry : command.definedArguments().entrySet()) {
			Argument.Internal<?> argument = argumentEntry.getValue();
			if(argument.optional())
				commandNames += " [" + argument.name() + "]";
			else
				commandNames += " " + argument.name();
		}
		_writer.level2Begin(commandNames);
		
		for(String paragraph : command.description())
			_writer.paragraph(Level.L2, replaceStrong(paragraph));
		
		printOptions(command.definedOptions(), true);
		
		printArguments(command.definedArguments(), true);
		_writer.level2End();
	}
	
	private void printCommands()
	{
		if(_definedCommands.size() == 0)
			return;
		
		_writer.level1Begin("CMD:");
		for(Entry<String, Command.Internal> commandEntry : _definedCommands.entrySet())
			printCommand(commandEntry.getValue());
		_writer.level1End();
	}
	
	private void printShortCommands()
	{
		if(_definedCommands.size() == 0)
			return;
		
		_writer.level1Begin("Commands:");
		for(Entry<String, Command.Internal> commandEntry : _definedCommands.entrySet()) {
			Command.Internal command = commandEntry.getValue();
			String shortDescription = command.name();
			for(String alternative : command.alternatives())
				shortDescription += ", " + alternative;
			_writer.line(Level.L1, shortDescription + ": " + command.shortDescription());
		}
		_writer.level1End();
	}

	private void printGlobalArguments()
	{
		printArguments(_definedArguments, false);
	}
	
	private void printExamples()
	{
		_writer.level1Begin("Examples:");
		_writer.codeBegin(Level.L1);
		_writer.codeLine(_javaCommand + " -? " + COMPLETE_HELP_COMMAND);
		if(_definedCommands.size() > 0)
			_writer.codeLine(
				_javaCommand + " --help cmd=" + _definedCommands.keySet().iterator().next()
			);
		_writer.codeLine(_javaCommand + " --version");
		
		for(String example : _exampleArguments)
			_writer.codeLine(_javaCommand + " " + example);
		_writer.codeEnd();
		_writer.level1End();
	}
	
	private static Writer createSystemPropertyWriter()
	{
		String propertyClassName = "";
		try {
			propertyClassName = System.getProperty(WRITER_CLASS_PROPERTY);
			if(propertyClassName == null)
				return null;
		}
		catch(Throwable ignore) {
			return null;
		}
		
		Class<?> writerClass = null;
		try {
			writerClass = Class.forName(propertyClassName);
		}
		catch(ClassNotFoundException e) {
			try {
				String className =
					Writer.class.getPackage().getName() + "." + propertyClassName + "Writer";
				writerClass = Class.forName(className);
			}
			catch(ClassNotFoundException e2) {
				return null;
			}
		}
		
		Writer writer = null;
		try {
			Constructor<?> constructor = writerClass.getDeclaredConstructor((Class<?>[])null);
			writer = (Writer)constructor.newInstance((Object[])null);
		}
		catch(SecurityException e) {
			// Does nothing.
		}
		catch(NoSuchMethodException e) {
			// Does nothing.
		}
		catch(IllegalArgumentException e) {
			// Does nothing.
		}
		catch(InstantiationException e) {
			// Does nothing.
		}
		catch(IllegalAccessException e) {
			// Does nothing.
		}
		catch(InvocationTargetException e) {
			// Does nothing.
		}

		return writer;
	}
}
