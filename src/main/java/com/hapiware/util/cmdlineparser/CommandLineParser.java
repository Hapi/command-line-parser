package com.hapiware.util.cmdlineparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
 * System property {@code screenwidth.default}.
 * 
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class CommandLineParser
{
	private enum HelpType { OPTIONS, ARGUMENTS, COMMANDS, COMMAND_OPTIONS, COMMAND_ARGUMENTS };
	
	private static final String COMPLETE_HELP_COMMAND = "all";
	private static final String OPTS_HELP_COMMAND = "opts";
	private static final String CMDS_HELP_COMMAND = "cmds";
	private static final String CMD_HELP_COMMAND = "cmd=";
	private static final String ARGS_HELP_COMMAND = "args";
	private static final String SCREEN_WIDTH_PROPERTY = "screenwidth.default";
	private static final int DEFAULT_SCREEN_WIDTH;
	
	static {
		int screenWidth = 100;
		try {
			 screenWidth = Integer.parseInt(System.getProperty(SCREEN_WIDTH_PROPERTY));
		}
		catch(Throwable ignore) {
			// Does nothing.
		}
		finally {
			DEFAULT_SCREEN_WIDTH = screenWidth;
		}
	}
	
	
	private final Description _description;
	private Map<String, Option.Inner> _definedGlobalOptions = new LinkedHashMap<String, Option.Inner>();
	private Map<String, String> _definedGlobalOptionAlternatives = new HashMap<String, String>();
	private Map<String, Command.Inner> _definedCommands = new LinkedHashMap<String, Command.Inner>();
	private Map<String, String> _definedCommandAlternatives = new HashMap<String, String>();
	private Map<String, Argument.Inner<?>> _definedArguments =
		new LinkedHashMap<String, Argument.Inner<?>>();
	private boolean _mandatoryArguments;
	private int _numOfOptionalArguments;
	private List<Option.Inner> _cmdLineOptions = new ArrayList<Option.Inner>(); 
	private Command.Inner _cmdLineCommand;
	private List<Argument.Inner<?>> _cmdLineArguments = new ArrayList<Argument.Inner<?>>();
	private final Class<?> _mainClass;
	private final String _javaCommand;
	private Set<HelpType> _definedArgumentTypes = new HashSet<HelpType>();
	private List<String> _exampleArguments = new LinkedList<String>();
	private final Writer _writer;
	
	
	public CommandLineParser(Class<?> mainClass, Description description)
	{
		this(mainClass, DEFAULT_SCREEN_WIDTH, description);
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
		_writer = writer;
		_javaCommand = "java -jar " + _mainClass.getPackage().getImplementationTitle() + ".jar";
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
		if(_definedGlobalOptionAlternatives.containsKey(inner.name()))
			throw
				new ConfigurationException("Option name '" + inner.name() + "' must be unique.");
		
		_definedGlobalOptions.put(inner.name(), inner);
		_definedGlobalOptionAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			if(_definedGlobalOptionAlternatives.put(alternative, inner.name()) != null)
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
		
		Command.Inner inner = new Command.Inner(command);
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
		if(_definedCommandAlternatives.containsKey(inner.name()))
			throw
				new ConfigurationException("Command name '" + inner.name() + "' must be unique.");

		_definedCommands.put(inner.name(), inner);
		_definedCommandAlternatives.put(inner.name(), inner.name());
		for(String alternative : inner.alternatives())
			if(_definedCommandAlternatives.put(alternative, inner.name()) != null)
				throw
					new ConfigurationException(
						"Command alternative name '" + alternative + "' must be unique."
					);
		
		_definedArgumentTypes.add(HelpType.COMMANDS);
		if(inner.definedOptions().size() > 0)
			_definedArgumentTypes.add(HelpType.COMMAND_OPTIONS);
		if(inner.definedArguments().size() > 0)
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
		
		Argument.Inner<T> inner = new Argument.Inner<T>(argument, argumentType);
		if(inner.name() == null || inner.name().trim().length() == 0)
			throw new ConfigurationException("'argument' must have a name.");
		
		if(inner.description().size() == 0)
			throw
				new ConfigurationException("Argument '" + inner.name() + "' must have a description.");
		if(_definedArguments.containsKey(inner.name()))
			throw
				new ConfigurationException("Argument name '" + inner.name() + "' must be unique.");

		
		if(inner.optional() && !inner.hasDefaultValueForOptional()) {
			String msg =
				"When annotations are used then optional arguments must have a default value "
					+ "('" + inner.name() + "').";
			throw new ConfigurationException(msg);
		}
		
		for(Constraint<?> constraint : inner.constraints())
			if(!constraint.typeCheck(argumentType)) {
				String msg =
					"Using '" + constraint.getClass().getName() + "' with argument type '"
						+ argumentType + "' creates a type conflict ('" + inner.name() + "').";
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

	public void addExampleArguments(String exampleArguments)
	{
		if(exampleArguments == null || exampleArguments.trim().length() == 0)
			throw new ConfigurationException("'exampleArguments' must have a value.");
		
		_exampleArguments.add(exampleArguments);
	}
	
	public boolean optionExists(String name)
	{
		for(Option.Inner option : _cmdLineOptions)
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
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
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
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
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			System.exit(-2);
		}
	}

	
	public void parsePrintAndExitOnError(Object callerObject, String[] args)
	{
		try {
			parse(callerObject, args);
		}
		catch(ConstraintException e) {
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
			System.exit(-2);
		}
	}
	
	public void parsePrintAndExitOnError(Class<?> callerClass, String[] args)
	{
		try {
			parse(callerClass, args);
		}
		catch(ConstraintException e) {
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(AnnotatedFieldSetException e) {
			printErrorMessageWithoutHelp(e);
			System.exit(-1);
		}
		catch(CommandNotFoundException e) {
			printErrorWithCommandsHelp(e);
			System.exit(-1);
		}
		catch(IllegalCommandLineArgumentException e) {
			printErrorWithShortHelp(e);
			System.exit(-1);
		}
		catch(Throwable t) {
			printThrowable(t);
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
				System.exit(0);
			}
			
			String helpCommand = args[1];
			if(helpCommand.equals(COMPLETE_HELP_COMMAND)) {
				printCompleteHelp();
				System.exit(0);
			}
			if(_definedGlobalOptions.size() > 0 && helpCommand.equals(OPTS_HELP_COMMAND)) {
				printGlobalOptionsHelp();
				System.exit(0);
			}
			if(_definedCommands.size() > 0 && helpCommand.equals(CMDS_HELP_COMMAND)) {
				printCommandsHelp();
				System.exit(0);
			}
			if(_definedCommands.size() > 0 && helpCommand.startsWith(CMD_HELP_COMMAND)) {
				String[] afterSplit = helpCommand.split("=");
				if(afterSplit.length > 1)
					printCommandHelp(afterSplit[1]);
				else
					printCommandHelp("");
				System.exit(0);
			}
			if(_definedArguments.size() > 0 && helpCommand.equals(ARGS_HELP_COMMAND)) {
				printGlobalArgumentsHelp();
				System.exit(0);
			}

			_writer.header();
			_writer.level1Begin("Help error:");
			_writer.line(Level.L1, "'" + helpCommand + "' is not a valid help command.");
			_writer.line(Level.L1, "");
			_writer.level1End();
			printUsage();
			_writer.footer();
			System.exit(0);
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

		Set<Option.Inner> nonMultipleOptionCheckSet = new HashSet<Option.Inner>();
		_cmdLineCommand = null;
		while(cmdLineArgs.size() > 0) {
			String arg = cmdLineArgs.get(0);
			if(
				Util.checkOption(
					arg,
					cmdLineArgs,
					_definedGlobalOptions,
					_definedGlobalOptionAlternatives,
					nonMultipleOptionCheckSet,
					_cmdLineOptions
				)
			)
				continue;
			else {
				if(arg.startsWith("-")) {
					String msg = "'" + arg + "' is not a valid option.";
					throw new IllegalCommandLineArgumentException(msg);
				}
				
				if(_cmdLineCommand != null)
					throw
						new IllegalCommandLineArgumentException(
							"'" + arg + "' cannot be interpreted as a proper command line parameter."
						);
			}
					
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
			_cmdLineCommand.execute(Collections.unmodifiableList(_cmdLineOptions));
		}
	}

	
	private void showVersionAndExit()
	{
		_writer.header();
		_writer.level1Begin("Version: " + _mainClass.getPackage().getImplementationVersion());
		_writer.level1End();
		_writer.footer();
		System.exit(0);
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
		printUsage();
		printGlobalOptions();
		_writer.footer();
	}
	
	public void printGlobalArgumentsHelp()
	{
		_writer.header();
		printUsage();
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
		Command.Inner command = _definedCommands.get(_definedCommandAlternatives.get(commandName));
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
		command += _definedArgumentTypes.contains(HelpType.ARGUMENTS) ? " ARGS" : "";
		_writer.level1Begin("Usage:");
		_writer.codeBegin(Level.L1);
		_writer.codeLine(_javaCommand + helpCommand);
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
		Map<String, Option.Inner> options,
		boolean isCommand
	)
	{
		if(options.size() == 0)
			return;
		
		if(isCommand)
			_writer.level3Begin("CMD-OPTS:");
		else
			_writer.level1Begin("OPTS:");
		for(Entry<String, Option.Inner> optionEntry : options.entrySet()) {
			Option.Inner option = optionEntry.getValue();
			
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
						paragraph = 
							"Argument is optional. " 
								+ option.argument().defaultValueDescription() 
								+ paragraph;
					if(option.multiple())
						paragraph = "This option can occur several times. " + paragraph;
					
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
		Map<String, Argument.Inner<?>> arguments,
		boolean isCommand
	)
	{
		if(arguments.size() == 0)
			return;
		
		if(isCommand)
			_writer.level3Begin("CMD-ARGS:");
		else
			_writer.level1Begin("ARGS:");
		for(Entry<String, Argument.Inner<?>> argumentEntry : arguments.entrySet()) {
			Argument.Inner<?> argument = argumentEntry.getValue();
			
			// Adds argument name.
			if(isCommand)
				_writer.level4Begin(argument.name());
			else
				_writer.level2Begin(argument.name());

			// Adds description and handles optional arguments and possible default values.
			boolean isFirstParagraph = true;
			Level level = isCommand ? Level.L4 : Level.L2;
			for(String paragraph : argument.description()) {
				if(isFirstParagraph && argument.optional()) {
					paragraph = 
						"Argument is optional. " 
							+ argument.defaultValueDescription() 
							+ paragraph;
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

	
	private void printCommand(Command.Inner command)
	{
		// Adds command names.
		String commandNames = command.name();
		for(String alternative : command.alternatives())
			commandNames += ", " + alternative;
		commandNames +=
			_definedArgumentTypes.contains(HelpType.COMMAND_OPTIONS) ? " [CMD-OPTS]" : "";
		for(Entry<String, Argument.Inner<?>> argumentEntry : command.definedArguments().entrySet()) {
			Argument.Inner<?> argument = argumentEntry.getValue();
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
		for(Entry<String, Command.Inner> commandEntry : _definedCommands.entrySet())
			printCommand(commandEntry.getValue());
		_writer.level1End();
	}
	
	private void printShortCommands()
	{
		if(_definedCommands.size() == 0)
			return;
		
		_writer.level1Begin("Commands:");
		for(Entry<String, Command.Inner> commandEntry : _definedCommands.entrySet()) {
			Command.Inner command = commandEntry.getValue();
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
}
