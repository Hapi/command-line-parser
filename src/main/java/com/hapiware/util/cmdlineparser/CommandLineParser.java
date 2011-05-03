package com.hapiware.util.cmdlineparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.hapiware.util.cmdlineparser.annotation.Id;
import com.hapiware.util.cmdlineparser.constraint.Constraint;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;
import com.hapiware.util.cmdlineparser.constraint.Length;
import com.hapiware.util.cmdlineparser.constraint.MaxLength;
import com.hapiware.util.cmdlineparser.constraint.MaxValue;
import com.hapiware.util.cmdlineparser.constraint.MinLength;
import com.hapiware.util.cmdlineparser.constraint.MinValue;
import com.hapiware.util.cmdlineparser.writer.ConfluenceWriter;
import com.hapiware.util.cmdlineparser.writer.GitHubWriter;
import com.hapiware.util.cmdlineparser.writer.HtmlWriter;
import com.hapiware.util.cmdlineparser.writer.ScreenWriter;
import com.hapiware.util.cmdlineparser.writer.WikidotWriter;
import com.hapiware.util.cmdlineparser.writer.Writer;
import com.hapiware.util.cmdlineparser.writer.Writer.Level;
import com.hapiware.util.cmdlineparser.writer.XmlWriter;


/**
 * {@code CommandLineParser} is an utility class for parsing command line. In addition to just
 * parse command line arguments {@code CommandLineParser} also has
 * <a href="#cmdlineparser-built-in-command-line-options">a built-in help system</a> which
 * handles basic automatically command line arguments like {@code -?} or {@code --help}. Parsed
 * command line arguments can be directed to member fields by
 * <a href="#cmdlineparser-annotations">annotations</a>. Also, arguments can have constraints like
 * minValue, maxValue or value ranges etc. and for defined constraints informative help text
 * fragments are generated automatically (for more information see
 * <a href="#cmdlineparser-configuration-elements">Configuration elements</a>).
 * <p>
 * All the help texts can be generated to
 * <a href="#cmdlineparser-writer">HTML and XML as well as to various wiki formats</a> just by
 * defining a system property (see <a href="#cmdlineparser-system-properties">System properties</a>).
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-requirements">Requirements</a></h3>
 * <h4>{@code MANIFEST.MF} file</h4>
 * {@code MANIFEST.MF} file <u>must have the following items</u>:
 * 	<ul>
 * 		<li>
 * 			{@code Implementation-Title:}, which is used for creating usage help texts. This
 * 			must contain the final name for the .jar file without the .jar extension. For
 * 			example, if the jar file is {@code utility.jar} then {@code Implementation-Title:}
 * 			must be {@code utility}.
 *		</li>
 * 		<li>
 * 			{@code Implementation-Version:}, which is used for (built-in) {@code --version}
 * 			option.
 * 		</li>
 * 	</ul>
 * 
 * If either of the items is missing a {@link ConfigurationException} is thrown with a descriptive
 * error message.
 * 
 * Also, {@code MANIFEST.MF} file must have {@code Main-Class:} field defined.
 * 
 * <h4><a name="cmdlineparser-packaging">Packaging</a></h4>
 * All the classes in the {@code com.hapiware.util.cmdlineparser} and it's sub-packages must be
 * included to the final jar file. If you are a Maven user see chapter
 * <a href="#cmdlineparser-for-maven-users">For Maven users</a>.
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-command-line-structure">Command line structure</a></h3>
 * {@code CommandLineParser} can be configured either to use arguments (like {@code ls} utility)
 * or to handle several commands (like {@code git}).
 * 
 * <h4><a name="cmdlineparser-using-arguments">Using arguments</a></h4>
 * If {@code CommandLineParser} is configured to use arguments the general form is:
 * <pre>
 * 	java -jar utilname.jar OPTS ARGS
 * </pre>
 * 
 * where:
 * 	<ul>
 * 		<li>
 * 			{@code OPTS} are options for the utility like {@code -v}, {@code --type}, etc.
 * 		</li>
 * 		<li>
 * 			{@code ARGS} are mandatory and/or optional arguments for the utility like {@code 12},
 * 			{@code ^.+Writer$}, etc.
 * 		</li>
 * 	</ul>
 * 
 * Notice that {@code CommandLineParser} is able to handle the situation where some of the options
 * are given after the arguments. Thus the general form for the arguments confguration is:
 *
 * <pre>
 * 	java -jar utilname.jar OPTS ARGS OPTS
 * </pre>
 * 
 * The order options and arguments are defined (using various {@code add()} methods) is irrelevant.
 * 
 * Here is an example of argument definition:
 * <pre>
 * private static CommandLineParser _clp;
 * 
 * {@code @Id}("ALGORITHM") private static String _algorithm;
 * {@code @Id}("ITER") private static long _iter;
 * {@code @Id}("INPUT") private static String _input;
 * {@code @Id}("DIGEST") private static String _digest;
 * 
 * static {
 *     _clp =
 *         new CommandLineParser(
 *             Hash.class,
 *             new Description()
 *                 .b("hash").d(" is an utility to create and check hashed strings.")
 *         );
 *     _clp.add(String.class, new Argument<String>("ALGORITHM") {{
 *         description("An algorithm to be used for hashing. Case is irrelevant.");
 *         constraint(new Enumeration<String>() {{
 *             valueIgnoreCase("sha", " SHA algorithm. Same as SHA-1.");
 *             valueIgnoreCase("sha-1", "SHA-1 algorithm. Same as SHA.");
 *             valueIgnoreCase("md5", "MD5 algorithm.");
 *         }});
 *     }});
 *     _clp.add(Long.class, new Argument<Long>("ITER") {{
 *         description("Number of iterations.");
 *         minValue(1l);
 *     }});
 *     _clp.add(String.class, new Argument<String>("INPUT") {{
 *         description("A string to be salt-hashed.");
 *     }});
 *     _clp.add(String.class, new Argument<String>("DIGEST") {{
 *         description("A digest to be tested if defined.");
 *         optional("", false);
 *     }});
 *     _clp.addExampleArguments("sha 1000 password");
 *     _clp.addExampleArguments("sha 1000 password D16soUrQ0lPaJMO2yy7aN7RZnI6Nx1Zj");
 * }
 * </pre>
 * 
 * <h4><a name="cmdlineparser-using-commands">Using commands</a></h4>
 * If {@code CommandLineParser} is configured to use commands the general form is:
 * <pre>
 * 	java -jar utilname.jar OPTS CMD CMD-OPTS CMD-ARGS
 * </pre>
 * 
 * where:
 * 	<ul>
 * 		<li>
 * 			{@code OPTS} are global options for the utility like {@code -v}, {@code --log}, etc.
 * 			Notice the difference between {@code OPTS} and {@code CMD-OPTS}.
 * 		</li>
 * 		<li>
 * 			{@code CMD} is a command name like {@code set}, {@code commit}, {@code list}, etc.
 * 		</li>
 * 		<li>
 * 			{@code CMD-OPTS} are options for the command like {@code -n}, {@code --type}, etc.
 * 			Notice that each defined command can have its own specific command options. Notice
 * 			also the difference between {@code CMD-OPTS} and {@code OPTS}.
 * 		</li>
 * 		<li>
 * 			{@code CMD-ARGS} are mandatory and/or optional arguments for the command like
 * 			{@code 12},	{@code ^.+Writer$}, etc. {@code CMD-ARGS} are command specific.
 * 		</li>
 * 	</ul>
 * 
 * {@code CommandLineParser} is able to handle the situation where some of the global options and
 * command options are given after the command arguments. Thus the general form for the command is:
 *
 * <pre>
 * 	java -jar utilname.jar OPTS CMD CMD-OPTS CMD-ARGS CMD-OPTS OPTS
 * </pre>
 * 
 * Notice here that the order between latter {@code CMD-OPTS} and {@code OPTS} is significant.
 * 
 * Here is an example of how an utility with multiple commands can be defined:
 * <pre>
 * private static CommandLineParser _clp;
 * static {
 *     _clp =
 *         new CommandLineParser(
 *             CommandHelpTest.class,
 *             _writer,
 *             new Description()
 *                 .d("Lists and sets the logging level of Java Loggers")
 *                 .d("(java.util.logging.Logger) on the run.")
 *                 .d("Optionally log4j is also supported but it requires")
 *                 .d("java.util.logging.LoggingMXBean interface to be implemented for log4j.")
 *                 .d("See http://www.hapiware.com/jmx-tools.")
 *         );
 *     _clp.add(new Option("v") {{
 *         alternatives("verbose");
 *         multiple();
 *         description("Prints more verbose output.");
 *     }});
 *     _clp.add(new Option("l") {{
 *         alternatives("log");
 *         description("Logs every step of the process.");
 *     }});
 *     
 *     _clp.add(
 *         new Command(
 *             "j",
 *             "Shows running JVMs (jobs).",
 *             new CommandExecutor() {
 *                 public void execute(
 *                     Data command,
 *                     List<Option.Data> globalOptions)
 *                 {
 *                     // Show jobs...
 *                 }
 *             }
 *         ) {{
 *             alternatives("jobs");
 *             d("Shows PIDs and names of running JVMs (jobs). PID starting with");
 *             d("an asterisk (*) means that JMX agent is not runnig on that JVM.");
 *             d("Start the target JVM with -Dcom.sun.management.jmxremote or");
 *             d("if you are running JVM 1.6 or later use startjmx service.");
 *     }});
 *     
 *     final Option optionT =
 *         new Option("t") {{
 *             alternatives("type");
 *             description("Type of the logger (i.e. Java logger or log4j logger).");
 *             set(String.class, new OptionArgument<String>() {{
 *                 constraint(new Enumeration<String>() {{
 *                     value("4", "stands for log4j logger.");
 *                     valueIgnoreCase("j", "stands for Java logger.");
 *                 }});
 *             }});
 *         }};
 *     
 *     _clp.add(
 *         new Command(
 *             "l",
 *             "Lists current logging levels.",
 *             new CommandExecutor() {
 *                 public void execute(
 *                     Data command,
 *                     List<Option.Data> globalOptions)
 *                 {
 *                     // List logging levels...
 *                 }
 *             }
 *         ) {{
 *             alternatives("list");
 *             add(optionT);
 *             add(Integer.class, new Argument<Integer>("PID") {{
 *                 description("Process id of the running JVM.");
 *             }});
 *             add(Integer.class, new Argument<Integer>("PATTERN") {{
 *                 d("Java regular expression for matching logger names.");
 *                 d("A special value ").b("root").d(" lists only the root logger(s).");
 *             }});
 *             d("Lists current logging levels for all loggers matching PATTERN");
 *             d("in a JVM process identified by PID.");
 *             d("Logger type is identified by a prefix. ").b("(J)")
 *                 .d(" for Java loggers and ").b("(4)").d(" for log4j loggers.");
 *     }});
 *     
 *     _clp.add(
 *         new Command(
 *             "s",
 *             "Sets a new logging level.",
 *             new CommandExecutor() {
 *                 public void execute(
 *                     Data command,
 *                     List<Option.Data> globalOptions)
 *                 {
 *                     // Set new logging level...
 *                 }
 *             }
 *         ) {{
 *             alternatives("set");
 *             add(optionT);
 *             add(Integer.class, new Argument<Integer>("PID") {{
 *                 description("Process id of the running JVM.");
 *             }});
 *             add(Integer.class, new Argument<Integer>("PATTERN") {{
 *                 d("Java regular expression for matching logger names.");
 *                 d("A special value ").b("root").d(" sets only the root logger(s).");
 *             }});
 *             add(String.class, new Argument<String>("LEVEL") {{
 *                 d("Represents a new logging level for the logger.");
 *                 d("See logger documentation for further help.");
 *                 p();
 *                 d("LEVEL accepts a special value ").b("null").d(" to set the logging level");
 *                 d("to follow a parent logger's logging level.");
 *             }});
 *             d("Sets a new logging level LEVEL for all loggers matching PATTERN");
 *             d("in a JVM process identified by PID.");
 *     }});
 *     
 *     _clp.addExampleArguments("jobs");
 *     _clp.addExampleArguments("--log l 50001 ^.+");
 *     _clp.addExampleArguments("list 50001 root");
 *     _clp.addExampleArguments("set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO");
 *     _clp.addExampleArguments("set --type 4 50001 .*Test null");
 * }
 * </pre>
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-parsing-command-line">Parsing command line</a></h3>
 * Parsing is done one of the {@code parse} commands:
 * 	<ul>
 * 		<li>{@link #parse(String[])}</li>
 * 		<li>{@link #parse(Class, String[])}</li>
 * 		<li>{@link #parse(Object, String[])}</li>
 * 		<li>{@link #parsec(String[])}</li>
 * 		<li>{@link #parsec(Class, String[])}</li>
 * 		<li>{@link #parsec(Object, String[])}</li>
 * 		<li>{@link #parsech(String[])}</li>
 * 		<li>{@link #parsech(Class, String[])}</li>
 * 		<li>{@link #parsech(Object, String[])}</li>
 * 		<li>{@link #parseInternalOptions(String[])}</li>
 * 	</ul>
 * Parse methods try to parse given arguments according to the configuration and throw
 * an exception if the parsing fails. Otherwise the execution of the code continues normally.
 * The difference between {@code parse()}, {@code parsec()} and {@code parsech()} methods is how
 * they handle exceptions and help messages. {@code parse()} methods throw an exception if
 * something goes wrong and it is the programmer's responsibility to show error messages and help
 * texts. To make the usage of {@code CommandLineParser} much easier {@code parsec()} and
 * {@code parsech()} methods were introduced. Both methods catch all the exceptions and writes
 * error messages using the current {@link Writer}. They also will call {@link System#exit(int)}
 * automatically as a part of the exception handling.The difference between {@code parsec()} and
 * {@code parsech()} methods is that {@code parsech()} writes help texts in addition to error
 * messages whereas {@code parsec()} does not.
 * <p>
 * The next example uses {@link #parsec(String[])} method and is a continuation to an example
 * introduced in <a href="#cmdlineparser-using-arguments">Using arguments</a>:
 * <pre>
 * public static void main(String[] args)
 * {
 *     _clp.parsec(args);
 *     if(_digest.length() == 0)
 *         createDigest(_algorithm, _iter, _input);
 *     else
 *         verifyDigest(_algorithm, _iter, _input, _digest);
 * }
 * </pre>
 * If the command line cannot be parsed properly {@link #parsec(String[])} writes the error message
 * and then exits using {@link System#exit(int)}. Otherwise the code continues normally.
 * <p>
 * {@link #parseInternalOptions(String[])} method differs from the other parse commands in that
 * it only reacts to internal options by printing a proper message (e.g. help or version number)
 * and exits using {@link System#exit(int)}. If no internal options are found among given arguments
 * then the code continues normally after {@link #parseInternalOptions(String[])} call. This is
 * used in those scenarios where there are something else to do, like reading a configuration file,
 * before calling the {@code parse()}. For example:
 * <pre>
 * public static void main(String[] args)
 * {
 *     _clp.parseInternalOptions(args);
 *     readConfigurationFile();
 *     generateConfigurationFiles();
 *     _clp.parsec(args);
 * }
 * </pre>
 * Now, the program can react to internal options without first handling configuration files.
 * 
 * 
 * <h3><a name="cmdlineparser-coding-style">Coding style</a></h3>
 * Argument and command definitions can be defined with two different ways; using double-brace
 * syntax or by chaining commands. They can be mixed if wanted.
 * 
 * The examples above uses mainly the double-brace syntax and here is a copied code fragment: 
 * <pre>
 *     _clp.add(String.class, new Argument<String>("ALGORITHM") {{
 *         description("An algorithm to be used for hashing. Case is irrelevant.");
 *         constraint(new Enumeration<String>() {{
 *             valueIgnoreCase("sha", " SHA algorithm. Same as SHA-1.");
 *             valueIgnoreCase("sha-1", "SHA-1 algorithm. Same as SHA.");
 *             valueIgnoreCase("md5", "MD5 algorithm.");
 *         }});
 *     }});
 * </pre>
 * 
 * Notice that {@code description(String)} method belongs to {@code Argument} class whereas
 * {@code valueIgnoreCase(T, String)} belongs to {@code Enumeration} class. The same example
 * could have been written like this by using method chaining:
 * <pre>
 *     _clp.add(
 *         String.class,
 *         new Argument<String>("ALGORITHM")
 *             .description("An algorithm to be used for hashing. Case is irrelevant.")
 *             .constraint(
 *                 new Enumeration<String>()
 *                     .valueIgnoreCase("sha", " SHA algorithm. Same as SHA-1.")
 *                     .valueIgnoreCase("sha-1", "SHA-1 algorithm. Same as SHA.")
 *                     .valueIgnoreCase("md5", "MD5 algorithm.")
 *             )
 *     );
 * </pre>
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-built-in-command-line-options">Built-in command line options</a></h3>
 * {@code CommandLineParser} has two built-in options:
 * 	<ul>
 * 		<li>{@code -?}, {@code --help} for showing help.</li>
 * 		<li>{@code --version} for asking the version number of the utility</li>
 * 	</ul>
 * 
 *  Help option has several subcommands	depending is {@code CommandLineParser} defined to use
 *  arguments or commands. For argument definition the help commands are:
 * 	<ul>
 * 		<li>{@code --help all} for full help.</li>
 * 		<li>{@code --help usage} for showing just the usage synopsis.</li>
 * 		<li>{@code --help examples} for showing examples.</li>
 * 		<li>{@code --help opts} for showing just the options.</li>
 * 		<li>{@code --help args} for showing just the argumets</li>
 * 	</ul>
 *  
 * For command definition the help commands are:
 * 	<ul>
 * 		<li>{@code --help all} for full help.</li>
 * 		<li>{@code --help usage} for showing just the usage synopsis.</li>
 * 		<li>{@code --help examples} for showing examples.</li>
 * 		<li>{@code --help opts} for showing just the options.</li>
 * 		<li>{@code --help cmds} lists all the commands and their short descriptions.</li>
 * 		<li>{@code --help cmd=CMD} shows a full help for the single command {@code CMD}.</li>
 * 	</ul>
 * 
 * Here is an example help command call:
 * <pre>
 * 	java -jar utility.jar --help all
 * </pre>
 * 
 * Help commands are available automatically depending on only how the {@code CommandLineParser}
 * has been configured (i.e. no extra programming is needed).
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-configuration-elements">Configuration elements</a></h3>
 * {@code CommandLineParser} is configured using the following elements:
 * 	<ul>
 * 		<li>
 * 			<b>Argument</b> is a bare argument for the utility or the command. Arguments are type
 * 			checked in compile and run time. An argument can be optional. For more information see
 * 			{@link #add(Class, Argument)}, {@link Command#add(Class, Argument)}, {@link Argument}
 * 			and a chapter <a href="#cmdlineparser-using-arguments">Using arguments</a>.
 * 		</li>
 * 		<li>
 * 			<b>Option</b> is an optional command line argument which is identified either by
 * 			preceding minus (-) or minus-minus (--). For more information see {@link #add(Option)},
 * 			{@link Command#add(Option)} and {@link Option}.
 * 		</li>
 * 		<li>
 * 			<b>Option argument</b> is an argument for the option when needed. Option arguments are
 * 			also type checked in compile and run time. For more information see
 * 			{@link Option#set(Class, OptionArgument)} and {@link OptionArgument}.
 * 		</li>
 * 		<li>
 * 			<b>Command</b> elements are used for defining multiple tasks for the same command line
 * 			utility. For more information see {@link #add(Command)}, {@link Command} and a chapter
 * 			<a href="#cmdlineparser-using-commands">Using commands</a>.
 * 		</li>
 * 		<li>
 * 			<b>Description</b> element is used to create help texts. The idea to use a separate
 * 			class for creating help texts instead of using {@code String} is flexibility. With
 * 			description elements a programmer does not need to care about screen width or other
 * 			formatting aspects. For more information see {@link Description}.
 * 		</li>
 * 		<li>
 * 			<b>Constraint</b> elements can be used to set constraints for arguments (or option
 * 			arguments). Constraints	also will create help text fragments automatically. There are
 * 			several built-in constraints but own constraints can be created by implementing
 * 			{@link Constraint} interface. Built in constraints are:
 * 			<ul>
 * 				<li>{@link Length}</li>
 * 				<li>{@link MinLength}</li>
 * 				<li>{@link MaxLength}</li>
 * 				<li>{@link MinValue}</li>
 * 				<li>{@link MaxValue}</li>
 * 				<li>{@link Enumeration}</li>
 * 			</ul>
 * 			Constraints are set by using {@link Argument#constraint(Constraint)} method but there
 * 			are also helper methods for all built-in constraints except for {@link Enumeration}.
 * 			The helper methods are: 
 * 			<ul>
 * 				<li>{@link Argument#length(int)} (and {@link OptionArgument#length(int)})</li>
 * 				<li>{@link Argument#minLength(int)} (and {@link OptionArgument#minLength(int)})</li>
 * 				<li>{@link Argument#maxLength(int)} (and {@link OptionArgument#maxLength(int)})</li>
 * 				<li>{@link Argument#minValue(Object)} (and {@link OptionArgument#minValue(Object)})</li>
 * 				<li>{@link Argument#maxValue(Object)} (and {@link OptionArgument#maxValue(Object)})</li>
 * 			</ul>
 * 		</li>
 * 	</ul>
 * 
 * <h4><a name="cmdlineparser-annotations">Annotations</a></h4>
 * Annotation {@link Id} can be used to mark a member field to have a value automatically from
 * parsed command line arguments. Values for annotated fields are set for matched {@link Id#value()}
 * by comparing it to the id (element's name by default) of defined configuration elements. In
 * general the name of the configuration element is used for {@link Id#value()} to annotate a
 * member field. There may arise a need to use same names for different elements. For example,
 * a global option and a command option may have the same short name. This should not be
 * a common problem but there is a way to get around this by defining a different id for either
 * of the conflicting element (thus keeping the same name). Now, {@link Id#value()} can be the
 * just defined id. For more information see:
 * 	<ul>
 * 		<li>{@link Argument#Argument(String)} and {@link Argument#id(String)}</li>
 * 		<li>
 * 			{@link Option#Option(String)}, {@link Option#alternatives(String...)} and
 * 			{@link Option#id(String)}
 * 		</li>
 * 		<li>
 * 			{@link Command#Command(String, String)}, {@link Command#alternatives(String...)} and
 * 			{@link Command#id(String)}
 * 		</li>
 * 	</ul> 
 * 
 * If the option does not have an argument then the annotated member field must be {@code boolean}.
 * If {@link Option#multiple()} has been set then the annotated field must be an array of
 * defined argument types. If there are no defined arguments the field must be a {@code boolean}
 * array.
 * 
 * <h4><a name="cmdlineparser-command-executors">Command executors</a></h4>
 * There is two ways to trigger some action depending on what command has been called from the
 * command line; manual and by using command executors.
 * <p>
 * Manually a programmer can either use a combination of {@link Id#value()} and
 * {@link Command#id(String)} or instead of using annotations use {@link CommandLineParser#getCommand()}.
 * The other way is to implement {@link CommandExecutor} interface and give the instance of the
 * implemented class as parameter to {@link Command#Command(String, String, CommandExecutor)}.
 * 
 * <h4><a name="cmdlineparser-writer">Writer</a></h4>
 * {@link Writer} is an interface to format the output of the help texts and error messages.
 * If no writer is defined for {@code CommandLineParser} {@link ScreenWriter} is used as a default
 * {@link Writer}.
 * <p>
 * The main reason for the existence of {@link Writer} interface is the ability to reformat already
 * defined help texts for the command line tool's home page, for example.
 * <p>
 * It is also possible to use the current writer for writing the normal output of the command
 * line utility. The current writer can be fetched by calling {@link CommandLineParser#getWriter()}.
 * <p>
 * There are several built-in {@link Writer} implementations:
 * 	<ul>
 * 		<li>{@link ScreenWriter} (the default writer if nothing else is defined)</li>
 * 		<li>{@link WikidotWriter}</li>
 * 		<li>{@link ConfluenceWriter}</li>
 * 		<li>{@link GitHubWriter}</li>
 * 		<li>{@link HtmlWriter}</li>
 * 		<li>
 * 			{@link XmlWriter} is mainly for demonstrating the internal structure of the
 * 			built-in help system.
 * 		</li>
 * 	</ul>
 * 
 * See also <a href="#cmdlineparser-system-properties">System properties</a>.
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-configuration-principles">Configuration principles</a></h3>
 * The most important parameters for configuration elements are name and description which are
 * required. Also, for commands a short description is required. Options and commands can have
 * multiple alternative names. The use of {@link CommandLineParser#addExampleArguments(String)}
 * is not forced but highly recommended.
 * 
 * <h4><a name="cmdlineparser-configuration-naming-conventions">Naming conventions</a></h4>
 * Names must have a certain pattern (see {@link Util#checkName(String)}) and for options
 * there are some additional considerations. If the option name (or alternative name) is just
 * a single character then it is interpreted as a short option and is identified from the command
 * line arguments by a single minus (-) character. For example {@code -v}. On the other hand if
 * the option name is two or more characters long then it is interpreted as a long option and is
 * identified by two minus (--) characters. For example {@code --type}.
 * 
 * <h5><a name="cmdlineparser-configuration-option-definition">Defining options and fetching them</a></h5>
 * Notice the difference in naming options and fetching them using various {@code getOption()}
 * methods. The difference is that for definition the option name is given without the preceding
 * minus characters. The option is interpreted as a short or long option as defined above. However,
 * <u>when fetching and checking options the preceding minus charactes must be used</u> for short
 * and long options accordingly (e.g. "-a", "--verbose").
 * 
 * 
 * <h3><a name="cmdlineparser-system-properties">System properties</a></h3>
 * System property {@code writerclass} overrides the hard coded {@link Writer}. {@code writerclass}
 * must have a full class name of a class implementing {@link Writer} interface. The implementation
 * must have a default constructor. {@code writerclass} also recognizes a special format to use
 * built-in writer implementations. Internal writers are recognized by their class name prefixes
 * (case does matter) and are:
 * 	<ul>
 * 		<li>Screen (default, if nothing is hard coded)</li>
 * 		<li>Wikidot</li>
 * 		<li>Confluence</li>
 * 		<li>GitHub</li>
 * 		<li>Html</li>
 * 		<li>Xml</li>
 * 	</ul>
 * 
 * For example, to use GitHubWriter as a writer for the output:
 * <pre>
 * 	java -Dwriterclass=GitHub -jar myutil.jar
 * </pre>
 * 
 * See also {@link ScreenWriter} for it's system property.
 * 
 * 
 * 
 * <h3><a name="cmdlineparser-for-maven-users">For Maven users</a></h3>
 * Here is an example {@code pom.xml} showing all the necessary elements to package
 * {@code CommandLineParser} correctly:
 * <xmp>
 * <?xml version="1.0" encoding="UTF-8"?>
 * <project
 *     xmlns="http://maven.apache.org/POM/4.0.0"
 *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd"
 * >
 *     <modelVersion>4.0.0</modelVersion>
 *     <groupId>com.hapiware.util</groupId>
 *     <artifactId>hash</artifactId>
 *     <version>1.0.0</version>
 *     <build>
 *         <finalName>hash</finalName>
 *         <plugins>
 *             <plugin>
 *                 <groupId>org.apache.maven.plugins</groupId>
 *                 <artifactId>maven-compiler-plugin</artifactId>
 *                 <configuration>
 *                     <source>1.5</source>
 *                     <target>1.5</target>
 *                 </configuration>
 *             </plugin>
 *             <plugin>
 *                 <groupId>org.apache.maven.plugins</groupId>
 *                 <artifactId>maven-shade-plugin</artifactId>
 *                 <executions>
 *                     <execution>
 *                         <phase>package</phase>
 *                         <goals>
 *                             <goal>shade</goal>
 *                         </goals>
 *                         <configuration>
 *                             <artifactSet>
 *                                 <includes>
 *                                     <include>com.hapiware.util:command-line-parser</include>
 *                                 </includes>
 *                             </artifactSet>
 *                         </configuration>
 *                     </execution>
 *                 </executions>
 *             </plugin>
 *             <plugin>
 *                 <groupId>org.apache.maven.plugins</groupId>
 *                 <artifactId>maven-jar-plugin</artifactId>
 *                 <configuration>
 *                     <archive>
 *                         <manifest>
 *                             <mainClass>com.hapiware.util.Hash</mainClass>
 *                         </manifest>
 *                         <manifestEntries>
 *                             <Implementation-Title>${build.finalName}</Implementation-Title> 
 *                             <Implementation-Version>${project.version}</Implementation-Version>
 *                             <Implementation-Vendor>http://www.hapiware.com</Implementation-Vendor>
 *                         </manifestEntries>
 *                     </archive>
 *                 </configuration>
 *             </plugin>
 *         </plugins>
 *     </build>
 *     <dependencies>
 *         <dependency>
 *             <groupId>com.hapiware.util</groupId>
 *             <artifactId>command-line-parser</artifactId>
 *             <version>[1.0.0,)</version>
 *         </dependency>
 *     </dependencies>
 * </project>
 * </xmp>
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
	
		
	/**
	 * Creates a command line parser with {@link ScreenWriter} as a default {@link Writer}.
	 * 
	 * @param mainClass
	 * 		The class which is defined to {@code MANIFEST.MF} as {@code Main-Class}. Usually
	 * 		the class which is used to configure {@code CommandLineParser}.
	 * 
	 * @param description
	 * 		A description of the command line utility.
	 * 
	 * @throws ConfigurationException
	 * 		If any of the parameters is {@code null} or {@code MANIFEST.MF} is missing items.
	 * 		The exception has a descriptive error message.
	 */
	public CommandLineParser(Class<?> mainClass, Description description)
	{
		this(mainClass, new ScreenWriter(), description);
	}
	
	
	/**
	 * Creates a command line parser with {@link ScreenWriter} as a default {@link Writer}
	 * 
	 * @param mainClass
	 * 		The class which is defined to {@code MANIFEST.MF} as {@code Main-Class}. Usually
	 * 		the class which is used to configure {@code CommandLineParser}.
	 * 
	 * @param screenWidth
	 * 		A width of the "screen" in number of characters. More precisely the width of
	 * 		the print area. See also {@link ScreenWriter} documentation.
	 * 
	 * @param description
	 * 		A description of the command line utility.
	 * 
	 * @throws ConfigurationException
	 * 		If any of the parameters is {@code null} or {@code MANIFEST.MF} is missing items.
	 * 		The exception has a descriptive error message.
	 */
	public CommandLineParser(Class<?> mainClass, int screenWidth, Description description)
	{
		this(mainClass, new ScreenWriter(screenWidth), description);
	}
	
	
	/**
	 * Creates a command line parser with a user defined {@link Writer}.
	 * 
	 * @param mainClass
	 * 		The class which is defined to {@code MANIFEST.MF} as {@code Main-Class}. Usually
	 * 		the class which is used to configure {@code CommandLineParser}.
	 * 
	 * @param writer
	 * 		A writer to be used.
	 * 
	 * @param description
	 * 		A description of the command line utility.
	 * 
	 * @throws ConfigurationException
	 * 		If any of the parameters is {@code null} or {@code MANIFEST.MF} is missing items.
	 * 		The exception has a descriptive error message.
	 */
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
	
	
	/**
	 * Adds a new option for {@code CommandLineParser}.
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

	
	/**
	 * Adds a new command for {@code CommandLineParser}. Using {@code add(Command)} also means
	 * that {@code CommandLineParser} is configured to use commands and (global) arguments
	 * cannot be used.
	 * 
	 * @param command
	 * 		A command object to be added.
	 * 
	 * @throws ConfigurationException
	 * 		<ul>
	 * 			<li>
	 * 				an argument is already configured (i.e. {@code CommandLineParser} is
	 * 				already configured to use arguments instead of commands).
	 * 			</li>
	 * 			<li>{@code command} is {@code null}.</li>
	 * 			<li>{@code command} does not have a name or it is not unique.</li>
	 * 			<li>any of the alternative names for the {@code command} is not unique.</li>
	 * 			<li>{@code description} description is missing.</li>
	 * 		</ul>
	 */
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
	
	
	/**
	 * Adds a new argument for {@code CommandLineParser}. Using {@code add(Class, Argument)} also
	 * means that {@code CommandLineParser} is configured to use (global) arguments and commands
	 * cannot be used.
	 * <p>
	 * Type for the argument is defined twice. Generics are used for compile time type checking
	 * and thus making it easier for programmer to keep type safety. {@code CommandLineParser}
	 * also checks type in run time and thus the type must be set as a class also.
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
	 * 			<li>
	 * 				a command is already configured (i.e. {@code CommandLineParser} is
	 * 				already configured to use commands instead of arguments).
	 * 			</li>
	 * 			<li>{@code argument} is {@code null}.</li>
	 * 			<li>{@code argument} does not have a name or it is not unique.</li>
	 * 			<li>{@code argument} description is missing.</li>
	 * 			<li>there is a constraint type mismatch.</li>
	 * 			<li>optional arguments are misplaced</li>
	 * 		</ul>
	 */
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

	
	/**
	 * Adds a line of example command line arguments. Example arguments are used by the help
	 * system. The use of example arguments is not required but highly recommended.
	 * 
	 * @param exampleArguments
	 * 		Just the command line arguments for the command line utility.
	 */
	public void addExampleArguments(String exampleArguments)
	{
		if(exampleArguments == null)
			throw new ConfigurationException("'exampleArguments' must have a value.");
		
		_exampleArguments.add(exampleArguments);
	}
	
	
	/**
	 * Checks if the option exists among the command line arguments.
	 * 
	 * @param name
	 * 		A name (or alternative name) of the option with preceding
	 * 		minus characters (- or --). For example: "-a", "--verbose".
	 * 
	 * @return
	 * 		{@code true} if the option exists.
	 * 
	 * @throws IllegalArgumentException
	 * 		When {@code name} does not have preceding minus character(s).
	 */
	public boolean optionExists(String name)
	{
		Util.checkOptionName(name);
		
		for(Option.Internal option : _cmdLineGlobalOptions)
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
				return true;
		
		return false;
	}
	
	
	/**
	 * Returns the option if it exists on the command line.
	 * 
	 * @param name
	 * 		A name (or alternative name) of the option with preceding
	 * 		minus characters (- or --). For example: "-a", "--verbose".
	 * 
	 * @return
	 * 		The option object if exists on the command line. {@code null} if the option does not
	 * 		exist or does not have an argument.
	 * 
	 * @throws IllegalArgumentException
	 * 		When {@code name} does not have preceding minus character(s).
	 */
	public Option.Data getOption(String name)
	{
		Util.checkOptionName(name);
		try {
			return getOptions(name)[0];
		}
		catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	
	/**
	 * Returns the value of the option if it exists on the command line.
	 * 
	 * @param <T>
	 * 		A type of the option argument.
	 * 
	 * @param name
	 * 		A name (or alternative name) of the option with preceding
	 * 		minus characters (- or --). For example: "-a", "--verbose".
	 * 		
	 * @return
	 * 		The option value if the option exists on the command line. {@code null} if the option
	 * 		does not exist or does not have an argument.
	 * 
	 * @throws IllegalArgumentException
	 * 		When {@code name} does not have preceding minus character(s).
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOptionValue(String name)
	{
		Option.Data option = getOption(name);
		if(option != null && option.getArgument() != null)
			return (T)option.getArgument().getValue();
		else
			return null;
	}
	
	
	/**
	 * Returns an array of options if exists on the command line. The first option is the
	 * left-most option on the command line.
	 * 
	 * @param name
	 * 		A name (or alternative name) of the option with preceding
	 * 		minus characters (- or --). For example: "-a", "--verbose".
	 * 
	 * @return
	 * 		An array of option objects.
	 * 
	 * @throws IllegalArgumentException
	 * 		When {@code name} does not have preceding minus character(s).
	 */
	public Option.Data[] getOptions(String name)
	{
		Util.checkOptionName(name);
		
		List<Option.Data> options = new ArrayList<Option.Data>();
		for(Option.Internal option : _cmdLineGlobalOptions)
			if(option.name().equals(_definedGlobalOptionAlternatives.get(name)))
				options.add(new Option.Data(option));
		
		return options.toArray(new Option.Data[0]);
	}
	
	
	/**
	 * Returns all the options found from the command line.
	 * 
	 * @return
	 * 		An array of option objects.
	 */
	public Option.Data[] getAllOptions()
	{
		List<Option.Data> options = new ArrayList<Option.Data>();
		for(Option.Internal option : _cmdLineGlobalOptions)
			options.add(new Option.Data(option));
		
		return options.toArray(new Option.Data[0]);
	}
	
	
	/**
	 * Returns an argument from the command line if exists. Notice that only optional arguments
	 * can be missing.
	 * 
	 * @param name
	 * 		A name of the argument.
	 * 
	 * @return
	 * 		An argument object if exist on the command line.
	 */
	public Argument.Data<?> getArgument(String name)
	{
		for(Argument.Internal<?> argument : _cmdLineArguments)
			if(argument.name().equals(name))
				return argument.createDataObject();

		return null;
	}
	
	
	/**
	 * Returns the value of the argument if exists on the command line.
	 * 
	 * @param <T>
	 * 		A type of the argument.
	 * 
	 * @param name
	 * 		A name of the argument.
	 * 		
	 * @return
	 * 		The argument value if the argument exists on the command line. {@code null} otherwise.
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
	 * Returns all the arguments found from the command line.
	 * 
	 * @return
	 * 		An array of argument objects.
	 */
	public Argument.Data<?>[] getAllArguments()
	{
		List<Argument.Data<?>> arguments = new ArrayList<Argument.Data<?>>();
		for(Argument.Internal<?> argument : _cmdLineArguments)
			arguments.add(argument.createDataObject());
		
		return arguments.toArray(new Argument.Data[0]);
	}
	
	
	/**
	 * Checks if the command exists among the command line arguments.
	 * 
	 * @param name
	 * 		A name (or alternative name) of the command.
	 * 
	 * @return
	 * 		{@code true} if the command exists.
	 */
	public boolean commandExists(String name)
	{
		return _definedCommandAlternatives.containsKey(name);
	}
	
	
	/**
	 * Returns the command found from the command line.
	 * 
	 * @return
	 * 		The found command object. 
	 */
	public Command.Data getCommand()
	{
		return new Command.Data(_cmdLineCommand);
	}
	
	
	/**
	 * Returns the current {@link Writer} implementation.
	 * 
	 * @return
	 * 		A writer currently in use.
	 */
	public Writer getWriter()
	{
		return _writer;
	}
	
	
	/**
	 * Parses given command line arguments. If something goes wrong a proper exeption is thrown.
	 * Otherwise the method returns and allows the normal program execution. See also
	 * <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @throws ConstraintException
	 * 		When a constraint violation is detected.
	 * 
	 * @throws AnnotatedFieldSetException
	 * 		When the parsed value cannot be set to an annotated field.
	 * 
	 * @throws CommandNotFoundException
	 * 		When an undefined command is detected from the command line.
	 * 
	 * @throws IllegalCommandLineArgumentException
	 * 		When a given command line argument cannot be interpreted as an argument, command
	 * 		argument, option argument or command option argument.
	 * 
	 * @see #parsec(String[])
	 * @see #parsech(String[])
	 */
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

	
	/**
	 * Parses given command line arguments. If something goes wrong a proper exeption is thrown.
	 * Otherwise the method returns and allows the normal program execution.
	 * <p>
	 * Notice that this method requires {@code callerObject} as an argument. {@code callerObject}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct object but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message and indicates that either this or {@link #parse(Class, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerObject
	 * 		The caller object (or more precisly the object which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @throws ConstraintException
	 * 		When a constraint violation is detected.
	 * 
	 * @throws AnnotatedFieldSetException
	 * 		When the parsed value cannot be set to an annotated field.
	 * 
	 * @throws CommandNotFoundException
	 * 		When an undefined command is detected from the command line.
	 * 
	 * @throws IllegalCommandLineArgumentException
	 * 		When a given command line argument cannot be interpreted as an argument, command
	 * 		argument, option argument or command option argument.
	 * 
	 * @see #parsec(Object, String[])
	 * @see #parsech(Object, String[])
	 */
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

	
	/**
	 * Parses given command line arguments. If something goes wrong a proper exeption is thrown.
	 * Otherwise the method returns and allows the normal program execution.
	 * <p>
	 * Notice that this method requires {@code callerClass} as an argument. {@code callerClass}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct class but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message indicating that either this or {@link #parse(Object, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerClass
	 * 		The caller class (or more precisly the class which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @throws ConstraintException
	 * 		When a constraint violation is detected.
	 * 
	 * @throws AnnotatedFieldSetException
	 * 		When the parsed value cannot be set to an annotated field.
	 * 
	 * @throws CommandNotFoundException
	 * 		When an undefined command is detected from the command line.
	 * 
	 * @throws IllegalCommandLineArgumentException
	 * 		When a given command line argument cannot be interpreted as an argument, command
	 * 		argument, option argument or command option argument.
	 * 
	 * @see #parsec(Class, String[])
	 * @see #parsech(Class, String[])
	 */
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
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * and a (short) help message shown. Otherwise the method returns and allows the normal program
	 * execution. See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(String[])
	 * @see #parsec(String[])
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


	/**
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * and a (short) help message shown. Otherwise the method returns and allows the normal program
	 * execution.
	 * <p>
	 * Notice that this method requires {@code callerObject} as an argument. {@code callerObject}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct object but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message and indicates that either this or {@link #parse(Class, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerObject
	 * 		The caller object (or more precisly the object which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(Object, String[])
	 * @see #parsec(Object, String[])
	 */
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
	

	/**
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * and a (short) help message shown. Otherwise the method returns and allows the normal program
	 * execution.
	 * <p>
	 * Notice that this method requires {@code callerClass} as an argument. {@code callerClass}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct class but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message indicating that either this or {@link #parse(Object, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerClass
	 * 		The caller class (or more precisly the class which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(Class, String[])
	 * @see #parsec(Class, String[])
	 */
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
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * but a help message <u>is not shown</u>. Otherwise the method returns and allows the normal
	 * program execution.
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(String[])
	 * @see #parsech(String[])
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


	/**
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * but a help message <u>is not shown</u>. Otherwise the method returns and allows the normal
	 * program execution.
	 * <p>
	 * Notice that this method requires {@code callerObject} as an argument. {@code callerObject}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct object but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message and indicates that either this or {@link #parse(Class, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerObject
	 * 		The caller object (or more precisly the object which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(Object, String[])
	 * @see #parsech(Object, String[])
	 */
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
	

	/**
	 * Parses given command line arguments. If something goes wrong an error message is printed
	 * but a help message <u>is not shown</u>. Otherwise the method returns and allows the normal
	 * program execution.
	 * <p>
	 * Notice that this method requires {@code callerClass} as an argument. {@code callerClass}
	 * is required for setting parsed values to annotated fields. {@link #parse(String[])} tries
	 * to quess a correct class but if it fails it throws a {@link NullPointerException} with
	 * a descritive error message indicating that either this or {@link #parse(Object, String[])}
	 * method should be used.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param callerClass
	 * 		The caller class (or more precisly the class which contains the annotated member
	 * 		fields).
	 * 
	 * @param args
	 * 		Command line arguments.
	 * 
	 * @see #parse(Class, String[])
	 * @see #parsech(Class, String[])
	 */
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
	
	
	/**
	 * Parses given command line arguments for the internal options like --version and --help.
	 * If an internal option is recognised then the corresponding message is printed and
	 * {@link System#exit(int)} is called thus ending the execution of the program. On the other
	 * hand if no internal options are found then the execution continues normally.
	 * <p>
	 * This method is useful if there is a need to do something before calling one of
	 * the {@code parse()} methods. For example, if a configuration file is read before calling
	 * {@code parse()} a programmer is still able to show version number and help texts without
	 * reading the configuration file first.
	 * <p>
	 * See also <a href="#cmdlineparser-parsing-command-line">Parsing command line</a>.
	 * 
	 * @param args
	 * 		Command line arguments.
	 */
	public void parseInternalOptions(String args[])
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

		parseInternalOptions(args);
		
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
			_cmdLineCommand.execute(_cmdLineGlobalOptions);
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

	
	/**
	 * Prints a complete help using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
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
	
	
	/**
	 * Prints a short help using a selected writer. Short help contains usage, description,
	 * a possible command list and global arguments.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
	public void printShortHelp()
	{
		_writer.header();
		printShortHelpWithoutHeaders();
		_writer.footer();
	}
	

	/**
	 * Prints just the usage using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
	public void printUsageHelp()
	{
		_writer.header();
		printUsage();
		_writer.footer();
	}

	/**
	 * Prints just all the examples using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
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
	
	
	/**
	 * Prints just all the global options using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
	public void printGlobalOptionsHelp()
	{
		_writer.header();
		printGlobalOptions();
		_writer.footer();
	}
	
	
	/**
	 * Prints just the global arguments using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
	public void printGlobalArgumentsHelp()
	{
		_writer.header();
		printGlobalArguments();
		_writer.footer();
	}
	
	
	/**
	 * Prints a given <b>unexpected</b> {@link Throwable} using a selected writer. Compare this
	 * method to {@link #printErrorMessageWithoutHelp(Throwable)}. This method can be used for
	 * printing errors if the built-in print mechanisms are not enough.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 * 
	 * @param t
	 * 		A throwble to be printed.
	 * 
	 * @see #printErrorMessageWithoutHelp(Throwable)
	 * @see #parse(String[])
	 * @see #parse(Class, String[])
	 * @see #parse(Object, String[])
	 * @see #parsec(String[])
	 * @see #parsec(Class, String[])
	 * @see #parsec(Object, String[])
	 * @see #parsech(String[])
	 * @see #parsech(Class, String[])
	 * @see #parsech(Object, String[])
	 */
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

	/**
	 * Prints a given {@link Throwable} with a short help using a selected writer. This method can
	 * be used for printing errors if the built-in print mechanisms are not enough.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 * 
	 * @param cause
	 * 		A throwble to be printed.
	 * 
	 * @see #printShortHelp()
	 * @see #parse(String[])
	 * @see #parse(Class, String[])
	 * @see #parse(Object, String[])
	 * @see #parsec(String[])
	 * @see #parsec(Class, String[])
	 * @see #parsec(Object, String[])
	 * @see #parsech(String[])
	 * @see #parsech(Class, String[])
	 * @see #parsech(Object, String[])
	 */
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
	
	/**
	 * Prints a given {@link Throwable} with a command help using a selected writer. This method can
	 * be used for printing errors if the built-in print mechanisms are not enough.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 * 
	 * @param cause
	 * 		A throwble to be printed.
	 * 
	 * @see #parse(String[])
	 * @see #parse(Class, String[])
	 * @see #parse(Object, String[])
	 * @see #parsec(String[])
	 * @see #parsec(Class, String[])
	 * @see #parsec(Object, String[])
	 * @see #parsech(String[])
	 * @see #parsech(Class, String[])
	 * @see #parsech(Object, String[])
	 */
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
	
	
	/**
	 * Prints a given <b>known</b> {@link Throwable} using a selected writer. Compare this
	 * method to {@link #printThrowable(Throwable)}. This method can be used for printing errors
	 * if the built-in print mechanisms are not enough.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 * 
	 * @param cause
	 * 		A throwble to be printed.
	 * 
	 * @see #printThrowable(Throwable)
	 * @see #parse(String[])
	 * @see #parse(Class, String[])
	 * @see #parse(Object, String[])
	 * @see #parsec(String[])
	 * @see #parsec(Class, String[])
	 * @see #parsec(Object, String[])
	 * @see #parsech(String[])
	 * @see #parsech(Class, String[])
	 * @see #parsech(Object, String[])
	 */
	public void printErrorMessageWithoutHelp(Throwable cause)
	{
		_writer.header();
		_writer.level1Begin("Error:");
		//_writer.paragraph(Level.L1, cause.getClass().getName());
		_writer.paragraph(Level.L1, cause.getMessage());
		_writer.level1End();
		_writer.footer();
	}
	
	/**
	 * Prints a list of commands and their short descriptions using a selected writer.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 */
	public void printCommandsHelp()
	{
		_writer.header();
		printShortCommands();
		_writer.footer();
	}
	
	/**
	 * Prints help for the selected command using a selected writer. If {@code commandName} does
	 * not exist an error message is printed instead.
	 * <p>
	 * For more information about writers see <a href="#cmdlineparser-writer">Writer</a>.
	 * 
	 * @param commandName
	 * 		A command which help is wanted.
	 */
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
	
	/**
	 * Creates a writer based on the system property. First the property value is tried for
	 * class creation. If it does not succeed then the shorter form is attempted. For the short
	 * form the class name is created by combining the {@link Writer}'s package name, the property
	 * value and a word {@code Writer}.
	 * 
 	 * @return
 	 * 		The writer based on the system property. {@code null} if the writer cannot be created.
	 */
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
