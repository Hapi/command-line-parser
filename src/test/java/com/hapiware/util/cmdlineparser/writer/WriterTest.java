package com.hapiware.util.cmdlineparser.writer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
import com.hapiware.util.cmdlineparser.publicApiTest.TestBase;
import com.hapiware.util.publisher.Publisher;


public class WriterTest
	extends
		TestBase
{
	private interface SScreenWriter
	{
		public ScreenWriter createForTesting(PrintStream stream);
	}
	private interface SWikidotWriter
	{
		public WikidotWriter createForTesting(PrintStream stream);
	}
	private interface SConfluenceWriter
	{
		public ConfluenceWriter createForTesting(PrintStream stream);
	}
	private interface SGitHubWriter
	{
		public GitHubWriter createForTesting(PrintStream stream);
	}

	
	private CommandLineParser createParser(Writer writer)
	{
		try {
			replacePackage(WriterTest.class);
		}
		catch(Throwable e) {
			e.printStackTrace();
		}
		CommandLineParser parser =
			new CommandLineParser(
				WriterTest.class,
				writer,
				new Description()
					.d("Lists and sets the logging level of Java Loggers")
					.d("(java.util.logging.Logger) on the run.")
					.d("Optionally log4j is also supported but it requires")
					.d("java.util.logging.LoggingMXBean interface to be implemented for log4j.")
					.d("See http://www.hapiware.com/jmx-tools.")
			);
		parser.add(new Option("v") {{
			alternatives("verbose");
			multiple();
			description("Prints more verbose output.");
		}});
		parser.add(new Option("l") {{
			alternatives("log");
			description("Logs every step of the process.");
		}});
		
		parser.add(new Command("j", "Shows running JVMs (jobs).") {{
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
		parser.add(new Command("l", "Lists current logging levels.") {{
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
		parser.add(new Command("p", "Lists loggers and their parent loggers.") {{
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
		parser.add(new Command("s", "Sets a new logging level.") {{
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
		parser.addExampleArguments("jobs");
		parser.addExampleArguments("--log l 50001 ^.+");
		parser.addExampleArguments("list 50001 root");
		parser.addExampleArguments("p 50001 ^com\\.hapiware\\..*Worker.*");
		parser.addExampleArguments("set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO");
		parser.addExampleArguments("set --type 4 50001 .*Test null");
		
		TestUtil.replaceExitHandler(parser);
		return parser;
	}
	
	
	@Test
	public void screenWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SScreenWriter.class, ScreenWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
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
				+ "            -t, --type <value>\n"
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
				+ "            -t, --type <value>\n"
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
				+ "            -t, --type <value>\n"
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
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void screenWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SScreenWriter.class, ScreenWriter.class).createForTesting(ps)
			).parse(
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
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void wikidotWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SWikidotWriter.class, WikidotWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"+ Usage:\n"
				+ "[[code]]\n"
				+ "java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "java -jar cmd-parser.jar --version\n"
				+ "java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "[[/code]]\n"
				+ "\n"
				+ "+ Description:\n"
				+ "Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run. Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to be implemented for log4j. See http://www.hapiware.com/jmx-tools.\n"
				+ "\n"
				+ "+ OPTS:\n"
				+ "++ -v, --verbose\n"
				+ "Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "++ -l, --log\n"
				+ "Logs every step of the process.\n"
				+ "\n"
				+ "+ CMD:\n"
				+ "++ j, jobs\n"
				+ "Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that JMX agent is not runnig on that JVM. Start the target JVM with -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.\n"
				+ "\n"
				+ "++ l, list [CMD-OPTS] PID PATTERN\n"
				+ "Lists current logging levels for all loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. **(J)** for Java loggers and **(4)** for log4j loggers.\n"
				+ "\n"
				+ "+++ CMD-OPTS:\n"
				+ "++++ -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "+++++ Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "+++ CMD-ARGS:\n"
				+ "++++ PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "++++ PATTERN\n"
				+ "Java regular expression for matching logger names. A special value **root** lists only the root logger(s).\n"
				+ "\n"
				+ "++ p, parent [CMD-OPTS] PID PATTERN\n"
				+ "Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. **(J)** for Java loggers and **(4)** for log4j loggers.\n"
				+ "\n"
				+ "+++ CMD-OPTS:\n"
				+ "++++ -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "+++++ Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "+++ CMD-ARGS:\n"
				+ "++++ PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "++++ PATTERN\n"
				+ "Java regular expression for matching logger names.\n"
				+ "\n"
				+ "++ s, set [CMD-OPTS] PID PATTERN LEVEL\n"
				+ "Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified by PID.\n"
				+ "\n"
				+ "+++ CMD-OPTS:\n"
				+ "++++ -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "+++++ Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "+++ CMD-ARGS:\n"
				+ "++++ PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "++++ PATTERN\n"
				+ "Java regular expression for matching logger names. A special value **root** sets only the root logger(s).\n"
				+ "\n"
				+ "++++ LEVEL\n"
				+ "Represents a new logging level for the logger. See logger documentation for further help.\n"
				+ "\n"
				+ "LEVEL accepts a special value **null** to set the logging level to follow a parent logger's logging level.\n"
				+ "\n"
				+ "+ Examples:\n"
				+ "[[code]]\n"
				+ "java -jar cmd-parser.jar -? all\n"
				+ "java -jar cmd-parser.jar --help cmd=j\n"
				+ "java -jar cmd-parser.jar --version\n"
				+ "java -jar cmd-parser.jar jobs\n"
				+ "java -jar cmd-parser.jar --log l 50001 ^.+\n"
				+ "java -jar cmd-parser.jar list 50001 root\n"
				+ "java -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*\n"
				+ "java -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO\n"
				+ "java -jar cmd-parser.jar set --type 4 50001 .*Test null\n"
				+ "[[/code]]\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void wikidotWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SWikidotWriter.class, WikidotWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"+ Commands:\n"
				+ "j, jobs: Shows running JVMs (jobs). _\n"
				+ "l, list: Lists current logging levels. _\n"
				+ "p, parent: Lists loggers and their parent loggers. _\n"
				+ "s, set: Sets a new logging level. _\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}

	@Test
	public void confluenceWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SConfluenceWriter.class, ConfluenceWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"h1. Usage:\n"
				+ "{code}\n"
				+ "java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "java -jar cmd-parser.jar --version\n"
				+ "java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "{code}\n"
				+ "\n"
				+ "h1. Description:\n"
				+ "Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run. Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to be implemented for log4j. See http://www.hapiware.com/jmx-tools.\n"
				+ "\n"
				+ "h1. OPTS:\n"
				+ "h2. -v, --verbose\n"
				+ "Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "h2. -l, --log\n"
				+ "Logs every step of the process.\n"
				+ "\n"
				+ "h1. CMD:\n"
				+ "h2. j, jobs\n"
				+ "Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that JMX agent is not runnig on that JVM. Start the target JVM with -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.\n"
				+ "\n"
				+ "h2. l, list [CMD-OPTS] PID PATTERN\n"
				+ "Lists current logging levels for all loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. *(J)* for Java loggers and *(4)* for log4j loggers.\n"
				+ "\n"
				+ "h3. CMD-OPTS:\n"
				+ "h4. -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "h5. Values:\n"
				+ "* *4*, stands for log4j logger.\n"
				+ "* *j*, stands for Java logger.\n"
				+ "\n"
				+ "h3. CMD-ARGS:\n"
				+ "h4. PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "h4. PATTERN\n"
				+ "Java regular expression for matching logger names. A special value *root* lists only the root logger(s).\n"
				+ "\n"
				+ "h2. p, parent [CMD-OPTS] PID PATTERN\n"
				+ "Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. *(J)* for Java loggers and *(4)* for log4j loggers.\n"
				+ "\n"
				+ "h3. CMD-OPTS:\n"
				+ "h4. -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "h5. Values:\n"
				+ "* *4*, stands for log4j logger.\n"
				+ "* *j*, stands for Java logger.\n"
				+ "\n"
				+ "h3. CMD-ARGS:\n"
				+ "h4. PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "h4. PATTERN\n"
				+ "Java regular expression for matching logger names.\n"
				+ "\n"
				+ "h2. s, set [CMD-OPTS] PID PATTERN LEVEL\n"
				+ "Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified by PID.\n"
				+ "\n"
				+ "h3. CMD-OPTS:\n"
				+ "h4. -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "h5. Values:\n"
				+ "* *4*, stands for log4j logger.\n"
				+ "* *j*, stands for Java logger.\n"
				+ "\n"
				+ "h3. CMD-ARGS:\n"
				+ "h4. PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "h4. PATTERN\n"
				+ "Java regular expression for matching logger names. A special value *root* sets only the root logger(s).\n"
				+ "\n"
				+ "h4. LEVEL\n"
				+ "Represents a new logging level for the logger. See logger documentation for further help.\n"
				+ "\n"
				+ "LEVEL accepts a special value *null* to set the logging level to follow a parent logger's logging level.\n"
				+ "\n"
				+ "h1. Examples:\n"
				+ "{code}\n"
				+ "java -jar cmd-parser.jar -? all\n"
				+ "java -jar cmd-parser.jar --help cmd=j\n"
				+ "java -jar cmd-parser.jar --version\n"
				+ "java -jar cmd-parser.jar jobs\n"
				+ "java -jar cmd-parser.jar --log l 50001 ^.+\n"
				+ "java -jar cmd-parser.jar list 50001 root\n"
				+ "java -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*\n"
				+ "java -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO\n"
				+ "java -jar cmd-parser.jar set --type 4 50001 .*Test null\n"
				+ "{code}\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void confluenceWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SConfluenceWriter.class, ConfluenceWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"h1. Commands:\n"
				+ "j, jobs: Shows running JVMs (jobs). \\\\\n"
				+ "l, list: Lists current logging levels. \\\\\n"
				+ "p, parent: Lists loggers and their parent loggers. \\\\\n"
				+ "s, set: Sets a new logging level. \\\\\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void gitHubWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SGitHubWriter.class, GitHubWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"# Usage:\n"
				+ "\tjava -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]\n"
				+ "\tjava -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "\tjava -jar cmd-parser.jar --version\n"
				+ "\tjava -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS\n"
				+ "\n"
				+ "# Description:\n"
				+ "Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run. Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to be implemented for log4j. See http://www.hapiware.com/jmx-tools.\n"
				+ "\n"
				+ "# OPTS:\n"
				+ "## -v, --verbose\n"
				+ "Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "## -l, --log\n"
				+ "Logs every step of the process.\n"
				+ "\n"
				+ "# CMD:\n"
				+ "## j, jobs\n"
				+ "Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that JMX agent is not runnig on that JVM. Start the target JVM with -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.\n"
				+ "\n"
				+ "## l, list [CMD-OPTS] PID PATTERN\n"
				+ "Lists current logging levels for all loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. **(J)** for Java loggers and **(4)** for log4j loggers.\n"
				+ "\n"
				+ "### CMD-OPTS:\n"
				+ "#### -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "##### Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "### CMD-ARGS:\n"
				+ "#### PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "#### PATTERN\n"
				+ "Java regular expression for matching logger names. A special value **root** lists only the root logger(s).\n"
				+ "\n"
				+ "## p, parent [CMD-OPTS] PID PATTERN\n"
				+ "Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. **(J)** for Java loggers and **(4)** for log4j loggers.\n"
				+ "\n"
				+ "### CMD-OPTS:\n"
				+ "#### -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "##### Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "### CMD-ARGS:\n"
				+ "#### PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "#### PATTERN\n"
				+ "Java regular expression for matching logger names.\n"
				+ "\n"
				+ "## s, set [CMD-OPTS] PID PATTERN LEVEL\n"
				+ "Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified by PID.\n"
				+ "\n"
				+ "### CMD-OPTS:\n"
				+ "#### -t, --type <value>\n"
				+ "Type of the logger (i.e. Java logger or log4j logger).\n"
				+ "\n"
				+ "##### Values:\n"
				+ "* **4**, stands for log4j logger.\n"
				+ "* **j**, stands for Java logger.\n"
				+ "\n"
				+ "### CMD-ARGS:\n"
				+ "#### PID\n"
				+ "Process id of the running JVM.\n"
				+ "\n"
				+ "#### PATTERN\n"
				+ "Java regular expression for matching logger names. A special value **root** sets only the root logger(s).\n"
				+ "\n"
				+ "#### LEVEL\n"
				+ "Represents a new logging level for the logger. See logger documentation for further help.\n"
				+ "\n"
				+ "LEVEL accepts a special value **null** to set the logging level to follow a parent logger's logging level.\n"
				+ "\n"
				+ "# Examples:\n"
				+ "\tjava -jar cmd-parser.jar -? all\n"
				+ "\tjava -jar cmd-parser.jar --help cmd=j\n"
				+ "\tjava -jar cmd-parser.jar --version\n"
				+ "\tjava -jar cmd-parser.jar jobs\n"
				+ "\tjava -jar cmd-parser.jar --log l 50001 ^.+\n"
				+ "\tjava -jar cmd-parser.jar list 50001 root\n"
				+ "\tjava -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*\n"
				+ "\tjava -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO\n"
				+ "\tjava -jar cmd-parser.jar set --type 4 50001 .*Test null\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void gitHubWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(
				Publisher.publish(SGitHubWriter.class, GitHubWriter.class).createForTesting(ps)
			).parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"# Commands:\n"
				+ "j, jobs: Shows running JVMs (jobs).\n"
				+ "l, list: Lists current logging levels.\n"
				+ "p, parent: Lists loggers and their parent loggers.\n"
				+ "s, set: Sets a new logging level.\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void xmlWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(new XmlWriter(ps)).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
				+ "<cmdline-out>\n"
				+ "    <level-1>\n"
				+ "        <heading>Usage:</heading>\n"
				+ "        <code>\n"
				+ "            <line>java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]</line>\n"
				+ "            <line>java -jar cmd-parser.jar -? | --help ['usage' | 'examples']</line>\n"
				+ "            <line>java -jar cmd-parser.jar --version</line>\n"
				+ "            <line>java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS</line>\n"
				+ "        </code>\n"
				+ "    </level-1>\n"
				+ "    <level-1>\n"
				+ "        <heading>Description:</heading>\n"
				+ "        <paragraph>Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run. Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to be implemented for log4j. See http://www.hapiware.com/jmx-tools.</paragraph>\n"
				+ "    </level-1>\n"
				+ "    <level-1>\n"
				+ "        <heading>OPTS:</heading>\n"
				+ "        <level-2>\n"
				+ "            <heading>-v, --verbose</heading>\n"
				+ "            <paragraph>Prints more verbose output. This option can occur several times.</paragraph>\n"
				+ "        </level-2>\n"
				+ "        <level-2>\n"
				+ "            <heading>-l, --log</heading>\n"
				+ "            <paragraph>Logs every step of the process.</paragraph>\n"
				+ "        </level-2>\n"
				+ "    </level-1>\n"
				+ "    <level-1>\n"
				+ "        <heading>CMD:</heading>\n"
				+ "        <level-2>\n"
				+ "            <heading>j, jobs</heading>\n"
				+ "            <paragraph>Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that JMX agent is not runnig on that JVM. Start the target JVM with -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.</paragraph>\n"
				+ "        </level-2>\n"
				+ "        <level-2>\n"
				+ "            <heading>l, list [CMD-OPTS] PID PATTERN</heading>\n"
				+ "            <paragraph>Lists current logging levels for all loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. <strong>(J)</strong> for Java loggers and <strong>(4)</strong> for log4j loggers.</paragraph>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-OPTS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>-t, --type <value></heading>\n"
				+ "                    <paragraph>Type of the logger (i.e. Java logger or log4j logger).</paragraph>\n"
				+ "                    <level-5>\n"
				+ "                        <heading>Values:</heading>\n"
				+ "                        <list>\n"
				+ "                            <item><strong>4</strong>, stands for log4j logger.</item>\n"
				+ "                            <item><strong>j</strong>, stands for Java logger.</item>\n"
				+ "                        </list>\n"
				+ "                    </level-5>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-ARGS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PID</heading>\n"
				+ "                    <paragraph>Process id of the running JVM.</paragraph>\n"
				+ "                </level-4>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PATTERN</heading>\n"
				+ "                    <paragraph>Java regular expression for matching logger names. A special value <strong>root</strong> lists only the root logger(s).</paragraph>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "        </level-2>\n"
				+ "        <level-2>\n"
				+ "            <heading>p, parent [CMD-OPTS] PID PATTERN</heading>\n"
				+ "            <paragraph>Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. <strong>(J)</strong> for Java loggers and <strong>(4)</strong> for log4j loggers.</paragraph>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-OPTS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>-t, --type <value></heading>\n"
				+ "                    <paragraph>Type of the logger (i.e. Java logger or log4j logger).</paragraph>\n"
				+ "                    <level-5>\n"
				+ "                        <heading>Values:</heading>\n"
				+ "                        <list>\n"
				+ "                            <item><strong>4</strong>, stands for log4j logger.</item>\n"
				+ "                            <item><strong>j</strong>, stands for Java logger.</item>\n"
				+ "                        </list>\n"
				+ "                    </level-5>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-ARGS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PID</heading>\n"
				+ "                    <paragraph>Process id of the running JVM.</paragraph>\n"
				+ "                </level-4>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PATTERN</heading>\n"
				+ "                    <paragraph>Java regular expression for matching logger names.</paragraph>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "        </level-2>\n"
				+ "        <level-2>\n"
				+ "            <heading>s, set [CMD-OPTS] PID PATTERN LEVEL</heading>\n"
				+ "            <paragraph>Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified by PID.</paragraph>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-OPTS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>-t, --type <value></heading>\n"
				+ "                    <paragraph>Type of the logger (i.e. Java logger or log4j logger).</paragraph>\n"
				+ "                    <level-5>\n"
				+ "                        <heading>Values:</heading>\n"
				+ "                        <list>\n"
				+ "                            <item><strong>4</strong>, stands for log4j logger.</item>\n"
				+ "                            <item><strong>j</strong>, stands for Java logger.</item>\n"
				+ "                        </list>\n"
				+ "                    </level-5>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "            <level-3>\n"
				+ "                <heading>CMD-ARGS:</heading>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PID</heading>\n"
				+ "                    <paragraph>Process id of the running JVM.</paragraph>\n"
				+ "                </level-4>\n"
				+ "                <level-4>\n"
				+ "                    <heading>PATTERN</heading>\n"
				+ "                    <paragraph>Java regular expression for matching logger names. A special value <strong>root</strong> sets only the root logger(s).</paragraph>\n"
				+ "                </level-4>\n"
				+ "                <level-4>\n"
				+ "                    <heading>LEVEL</heading>\n"
				+ "                    <paragraph>Represents a new logging level for the logger. See logger documentation for further help.</paragraph>\n"
				+ "                    <paragraph>LEVEL accepts a special value <strong>null</strong> to set the logging level to follow a parent logger's logging level.</paragraph>\n"
				+ "                </level-4>\n"
				+ "            </level-3>\n"
				+ "        </level-2>\n"
				+ "    </level-1>\n"
				+ "    <level-1>\n"
				+ "        <heading>Examples:</heading>\n"
				+ "        <code>\n"
				+ "            <line>java -jar cmd-parser.jar -? all</line>\n"
				+ "            <line>java -jar cmd-parser.jar --help cmd=j</line>\n"
				+ "            <line>java -jar cmd-parser.jar --version</line>\n"
				+ "            <line>java -jar cmd-parser.jar jobs</line>\n"
				+ "            <line>java -jar cmd-parser.jar --log l 50001 ^.+</line>\n"
				+ "            <line>java -jar cmd-parser.jar list 50001 root</line>\n"
				+ "            <line>java -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*</line>\n"
				+ "            <line>java -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO</line>\n"
				+ "            <line>java -jar cmd-parser.jar set --type 4 50001 .*Test null</line>\n"
				+ "        </code>\n"
				+ "    </level-1>\n"
				+ "</cmdline-out>\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void xmlWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(new XmlWriter(ps)).parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
				+ "<cmdline-out>\n"
				+ "    <level-1>\n"
				+ "        <heading>Commands:</heading>\n"
				+ "        <line>j, jobs: Shows running JVMs (jobs).</line>\n"
				+ "        <line>l, list: Lists current logging levels.</line>\n"
				+ "        <line>p, parent: Lists loggers and their parent loggers.</line>\n"
				+ "        <line>s, set: Sets a new logging level.</line>\n"
				+ "    </level-1>\n"
				+ "</cmdline-out>\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void htmlWriterTest()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(new HtmlWriter(ps)).parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
				+ "<html>\n"
				+ "<head />\n"
				+ "<body>\n"
				+ "    <h1>Usage:</h1>\n"
				+ "    <div class=\"code\">\n"
				+ "        <p>java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'cmds' | cmd=CMD]</p>\n"
				+ "        <p>java -jar cmd-parser.jar -? | --help ['usage' | 'examples']</p>\n"
				+ "        <p>java -jar cmd-parser.jar --version</p>\n"
				+ "        <p>java -jar cmd-parser.jar [OPTS] CMD [CMD-OPTS] CMD-ARGS</p>\n"
				+ "    </div>\n"
				+ "\n"
				+ "    <h1>Description:</h1>\n"
				+ "    <p>Lists and sets the logging level of Java Loggers (java.util.logging.Logger) on the run. Optionally log4j is also supported but it requires java.util.logging.LoggingMXBean interface to be implemented for log4j. See http://www.hapiware.com/jmx-tools.</p>\n"
				+ "\n"
				+ "    <h1>OPTS:</h1>\n"
				+ "    <h2>-v, --verbose</h2>\n"
				+ "    <p>Prints more verbose output. This option can occur several times.</p>\n"
				+ "    <h2>-l, --log</h2>\n"
				+ "    <p>Logs every step of the process.</p>\n"
				+ "\n"
				+ "    <h1>CMD:</h1>\n"
				+ "    <h2>j, jobs</h2>\n"
				+ "    <p>Shows PIDs and names of running JVMs (jobs). PID starting with an asterisk (*) means that JMX agent is not runnig on that JVM. Start the target JVM with -Dcom.sun.management.jmxremote or if you are running JVM 1.6 or later use startjmx service.</p>\n"
				+ "    <h2>l, list [CMD-OPTS] PID PATTERN</h2>\n"
				+ "    <p>Lists current logging levels for all loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. <b>(J)</b> for Java loggers and <b>(4)</b> for log4j loggers.</p>\n"
				+ "    <h3>CMD-OPTS:</h3>\n"
				+ "    <h4>-t, --type <value></h4>\n"
				+ "    <p>Type of the logger (i.e. Java logger or log4j logger).</p>\n"
				+ "    <h5>Values:</h5>\n"
				+ "    <ul>\n"
				+ "        <li><b>4</b>, stands for log4j logger.</li>\n"
				+ "        <li><b>j</b>, stands for Java logger.</li>\n"
				+ "    </ul>\n"
				+ "    <h3>CMD-ARGS:</h3>\n"
				+ "    <h4>PID</h4>\n"
				+ "    <p>Process id of the running JVM.</p>\n"
				+ "    <h4>PATTERN</h4>\n"
				+ "    <p>Java regular expression for matching logger names. A special value <b>root</b> lists only the root logger(s).</p>\n"
				+ "    <h2>p, parent [CMD-OPTS] PID PATTERN</h2>\n"
				+ "    <p>Lists loggers and their direct parent loggers matching PATTERN in a JVM process identified by PID. Logger type is identified by a prefix. <b>(J)</b> for Java loggers and <b>(4)</b> for log4j loggers.</p>\n"
				+ "    <h3>CMD-OPTS:</h3>\n"
				+ "    <h4>-t, --type <value></h4>\n"
				+ "    <p>Type of the logger (i.e. Java logger or log4j logger).</p>\n"
				+ "    <h5>Values:</h5>\n"
				+ "    <ul>\n"
				+ "        <li><b>4</b>, stands for log4j logger.</li>\n"
				+ "        <li><b>j</b>, stands for Java logger.</li>\n"
				+ "    </ul>\n"
				+ "    <h3>CMD-ARGS:</h3>\n"
				+ "    <h4>PID</h4>\n"
				+ "    <p>Process id of the running JVM.</p>\n"
				+ "    <h4>PATTERN</h4>\n"
				+ "    <p>Java regular expression for matching logger names.</p>\n"
				+ "    <h2>s, set [CMD-OPTS] PID PATTERN LEVEL</h2>\n"
				+ "    <p>Sets a new logging level LEVEL for all loggers matching PATTERN in a JVM process identified by PID.</p>\n"
				+ "    <h3>CMD-OPTS:</h3>\n"
				+ "    <h4>-t, --type <value></h4>\n"
				+ "    <p>Type of the logger (i.e. Java logger or log4j logger).</p>\n"
				+ "    <h5>Values:</h5>\n"
				+ "    <ul>\n"
				+ "        <li><b>4</b>, stands for log4j logger.</li>\n"
				+ "        <li><b>j</b>, stands for Java logger.</li>\n"
				+ "    </ul>\n"
				+ "    <h3>CMD-ARGS:</h3>\n"
				+ "    <h4>PID</h4>\n"
				+ "    <p>Process id of the running JVM.</p>\n"
				+ "    <h4>PATTERN</h4>\n"
				+ "    <p>Java regular expression for matching logger names. A special value <b>root</b> sets only the root logger(s).</p>\n"
				+ "    <h4>LEVEL</h4>\n"
				+ "    <p>Represents a new logging level for the logger. See logger documentation for further help.</p>\n"
				+ "    <p>LEVEL accepts a special value <b>null</b> to set the logging level to follow a parent logger's logging level.</p>\n"
				+ "\n"
				+ "    <h1>Examples:</h1>\n"
				+ "    <div class=\"code\">\n"
				+ "        <p>java -jar cmd-parser.jar -? all</p>\n"
				+ "        <p>java -jar cmd-parser.jar --help cmd=j</p>\n"
				+ "        <p>java -jar cmd-parser.jar --version</p>\n"
				+ "        <p>java -jar cmd-parser.jar jobs</p>\n"
				+ "        <p>java -jar cmd-parser.jar --log l 50001 ^.+</p>\n"
				+ "        <p>java -jar cmd-parser.jar list 50001 root</p>\n"
				+ "        <p>java -jar cmd-parser.jar p 50001 ^com\\.hapiware\\..*Worker.*</p>\n"
				+ "        <p>java -jar cmd-parser.jar set -tJ 50001 ^com\\.hapiware\\..*Worker.* INFO</p>\n"
				+ "        <p>java -jar cmd-parser.jar set --type 4 50001 .*Test null</p>\n"
				+ "    </div>\n"
				+ "\n"
				+ "</body>\n"
				+ "</html>\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void htmlWriterTest2()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		try {
			createParser(new HtmlWriter(ps)).parse(
				new String[] { "--help", "cmds" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
				+ "<html>\n"
				+ "<head />\n"
				+ "<body>\n"
				+ "    <h1>Commands:</h1>\n"
				+ "    <p>j, jobs: Shows running JVMs (jobs).</p>\n"
				+ "    <p>l, list: Lists current logging levels.</p>\n"
				+ "    <p>p, parent: Lists loggers and their parent loggers.</p>\n"
				+ "    <p>s, set: Sets a new logging level.</p>\n"
				+ "\n"
				+ "</body>\n"
				+ "</html>\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
}
