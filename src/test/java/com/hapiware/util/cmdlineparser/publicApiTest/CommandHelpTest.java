package com.hapiware.util.cmdlineparser.publicApiTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.hapiware.util.cmdlineparser.AnnotatedFieldSetException;
import com.hapiware.util.cmdlineparser.Argument;
import com.hapiware.util.cmdlineparser.Command;
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.ExitException;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.OptionArgument;
import com.hapiware.util.cmdlineparser.TestUtil;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.constraint.Enumeration;
import com.hapiware.util.cmdlineparser.writer.ScreenWriter;
import com.hapiware.util.publisher.Publisher;


public class CommandHelpTest
	extends
		TestBase
{
	private CommandLineParser _parser;
	private final static ByteArrayOutputStream _os = new ByteArrayOutputStream();
	private final static ScreenWriter _writer;

	private interface SScreenWriter
	{
		public ScreenWriter createForTesting(PrintStream stream, int screenWidth);
	}

	static {
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		_writer = sWriter.createForTesting(new PrintStream(_os), 100);
	}
	
	@AfterMethod
	public void resetStream() throws IOException
	{
		_os.reset();
	}
	
	@BeforeTest
	public void initTest()
	{
		_parser =
			new CommandLineParser(
				CommandHelpTest.class,
				_writer,
				new Description()
					.d("Lists and sets the logging level of Java Loggers")
					.d("(java.util.logging.Logger) on the run.")
					.d("Optionally log4j is also supported but it requires")
					.d("java.util.logging.LoggingMXBean interface to be implemented for log4j.")
					.d("See http://www.hapiware.com/jmx-tools.")
			);
		_parser.add(new Option("v") {{
			alternatives("verbose");
			multiple();
			description("Prints more verbose output.");
		}});
		_parser.add(new Option("l") {{
			alternatives("log");
			description("Logs every step of the process.");
		}});
		
		_parser.add(new Command("j", "Shows running JVMs (jobs).") {{
			alternatives("jobs");
			d("Shows PIDs and names of running JVMs (jobs). PID starting with");
			d("an asterisk (*) means that JMX agent is not runnig on that JVM.");
			d("Start the target JVM with -Dcom.sun.management.jmxremote or");
			d("if you are running JVM 1.6 or later use startjmx service.");
		}});
		
		final Option optionT =
			new Option("t") {{
				alternatives("type");
				description("Type of the logger (i.e. Java logger or log4j logger).");
				set(String.class, new OptionArgument<String>() {{
					constraint(new Enumeration<String>() {{
						value("4", "stands for log4j logger.");
						valueIgnoreCase("j", "stands for Java logger.");
					}});
				}});
			}};
		
		// list command
		_parser.add(new Command("l", "Lists current logging levels.") {{
			alternatives("list");
			add(optionT);
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Process id of the running JVM.");
			}});
			add(Integer.class, new Argument<Integer>("PATTERN") {{
				d("Java regular expression for matching logger names.");
				d("A special value ").b("root").d(" lists only the root logger(s).");
			}});
			d("Lists current logging levels for all loggers matching PATTERN");
			d("in a JVM process identified by PID.");
			d("Logger type is identified by a prefix. ").b("(J)")
				.d(" for Java loggers and ").b("(4)").d(" for log4j loggers.");
		}});
		
		// parent command
		_parser.add(new Command("p", "Lists loggers and their parent loggers.") {{
			alternatives("parent");
			add(optionT);
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Process id of the running JVM.");
			}});
			add(Integer.class, new Argument<Integer>("PATTERN") {{
				d("Java regular expression for matching logger names.");
			}});
			d("Lists loggers and their direct parent loggers matching PATTERN");
			d("in a JVM process identified by PID.");
			d("Logger type is identified by a prefix. ").b("(J)")
				.d(" for Java loggers and ").b("(4)").d(" for log4j loggers.");
		}});
		
		// set command
		_parser.add(new Command("s", "Sets a new logging level.") {{
			alternatives("set");
			add(optionT);
			add(Integer.class, new Argument<Integer>("PID") {{
				description("Process id of the running JVM.");
			}});
			add(Integer.class, new Argument<Integer>("PATTERN") {{
				d("Java regular expression for matching logger names.");
				d("A special value ").b("root").d(" sets only the root logger(s).");
			}});
			add(String.class, new Argument<String>("LEVEL") {{
				d("Represents a new logging level for the logger.");
				d("See logger documentation for further help.");
				p();
				d("LEVEL accepts a special value ").b("null").d(" to set the logging level");
				d("to follow a parent logger's logging level.");
			}});
			d("Sets a new logging level LEVEL for all loggers matching PATTERN");
			d("in a JVM process identified by PID.");
		}});
		
		// Examples.
		_parser.addExampleArguments("jobs");
		_parser.addExampleArguments("--log l 50001 ^.+");
		_parser.addExampleArguments("list 50001 root");
		_parser.addExampleArguments("p 50001 ^com\\.hapiware\\..*Worker.*");
		_parser.addExampleArguments("set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO");
		_parser.addExampleArguments("set --type 4 50001 .*Test null");
		
		TestUtil.replaceExitHandler(_parser);
	}
	
	@Test
	public void showVersion()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--version" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			assertEquals(_os.toString(), "Version: 1.0.0-for-testing\n");
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void shortHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "\n"
				+ "Description:\n"
				+ "    Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run.\n"
				+ "    Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to\n"
				+ "    be implemented for log4j. See http://www.hapiware.com/jmx-tools.\n"
				+ "\n"
				+ "Commands:\n"
				+ "    j, jobs: Shows running JVMs (jobs).\n"
				+ "    l, list: Lists current logging levels.\n"
				+ "    p, parent: Lists loggers and their parent loggers.\n"
				+ "    s, set: Sets a new logging level.\n"
				+ "    \n"
				+ "Notice:\n"
				+ "    This is a short help. To get a complete help run:\n"
				+ "    java -jar cmd-parser.jar -? all\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void completeHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "\n"
				+ "Description:\n"
				+ "    Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run.\n"
				+ "    Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to\n"
				+ "    be implemented for log4j. See http://www.hapiware.com/jmx-tools.\n"
				+ "\n"
				+ "OPTS:\n"
				+ "    -v, --verbose\n"
				+ "        Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "    -l, --log\n"
				+ "        Logs every step of the process.\n"
				+ "\n"
				+ "CMD:\n"
				+ "    j, jobs\n"
				+ "        Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that\n"
				+ "        JMX agent is not runnig on that JVM. Start the target JVM with\n"
				+ "        -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.\n"
				+ "\n"
				+ "    l, list [CMD-OPTS] PID PATTERN\n"
				+ "        Lists current logging levels for all loggers matching PATTERN in a JVM process identified by\n"
				+ "        PID. Logger type is identified by a prefix. '(J)' for Java loggers and '(4)' for log4j\n"
				+ "        loggers.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names. A special value 'root' lists only\n"
				+ "                the root logger(s).\n"
				+ "\n"
				+ "    p, parent [CMD-OPTS] PID PATTERN\n"
				+ "        Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified\n"
				+ "        by PID. Logger type is identified by a prefix. '(J)' for Java loggers and '(4)' for log4j\n"
				+ "        loggers.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names.\n"
				+ "\n"
				+ "    s, set [CMD-OPTS] PID PATTERN LEVEL\n"
				+ "        Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified\n"
				+ "        by PID.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names. A special value 'root' sets only\n"
				+ "                the root logger(s).\n"
				+ "\n"
				+ "            LEVEL\n"
				+ "                Represents a new logging level for the logger. See logger documentation for further\n"
				+ "                help.\n"
				+ "\n"
				+ "                LEVEL accepts a special value 'null' to set the logging level to follow a parent\n"
				+ "                logger's logging level.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --help cmd=j\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar jobs\n"
				+ "    java -jar cmd-parser.jar --log l 50001 ^.+\n"
				+ "    java -jar cmd-parser.jar list 50001 root\n"
				+ "    java -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*\n"
				+ "    java -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO\n"
				+ "    java -jar cmd-parser.jar set --type 4 50001 .*Test null\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void optionsHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "opts" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "\n"
				+ "OPTS:\n"
				+ "    -v, --verbose\n"
				+ "        Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "    -l, --log\n"
				+ "        Logs every step of the process.\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void commandsHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Commands:\n"
				+ "    j, jobs: Shows running JVMs (jobs).\n"
				+ "    l, list: Lists current logging levels.\n"
				+ "    p, parent: Lists loggers and their parent loggers.\n"
				+ "    s, set: Sets a new logging level.\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void command_jobs_Help()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "cmd=jobs" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"CMD:\n"
				+ "    j, jobs\n"
				+ "        Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that\n"
				+ "        JMX agent is not runnig on that JVM. Start the target JVM with\n"
				+ "        -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void command_list_Help()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "cmd=list" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"CMD:\n"
				+ "    l, list [CMD-OPTS] PID PATTERN\n"
				+ "        Lists current logging levels for all loggers matching PATTERN in a JVM process identified by\n"
				+ "        PID. Logger type is identified by a prefix. '(J)' for Java loggers and '(4)' for log4j\n"
				+ "        loggers.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names. A special value 'root' lists only\n"
				+ "                the root logger(s).\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void command_parent_Help()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "cmd=parent" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"CMD:\n"
				+ "    p, parent [CMD-OPTS] PID PATTERN\n"
				+ "        Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified\n"
				+ "        by PID. Logger type is identified by a prefix. '(J)' for Java loggers and '(4)' for log4j\n"
				+ "        loggers.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names.\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void command_set_Help()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "cmd=set" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"CMD:\n"
				+ "    s, set [CMD-OPTS] PID PATTERN LEVEL\n"
				+ "        Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified\n"
				+ "        by PID.\n"
				+ "\n"
				+ "        CMD-OPTS:\n"
				+ "            -t, --type\n"
				+ "                Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "                Values:\n"
				+ "                    * '4', stands for log4j logger.\n"
				+ "                    * 'j', stands for Java logger.\n"
				+ "\n"
				+ "        CMD-ARGS:\n"
				+ "            PID\n"
				+ "                Process id of the running JVM.\n"
				+ "\n"
				+ "            PATTERN\n"
				+ "                Java regular expression for matching logger names. A special value 'root' sets only\n"
				+ "                the root logger(s).\n"
				+ "\n"
				+ "            LEVEL\n"
				+ "                Represents a new logging level for the logger. See logger documentation for further\n"
				+ "                help.\n"
				+ "\n"
				+ "                LEVEL accepts a special value 'null' to set the logging level to follow a parent\n"
				+ "                logger's logging level.\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void argumentsHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "args" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Help error:\n"
				+ "    'args' is not a valid help command.\n"
				+ "    \n"
				+ "Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
}
