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
import com.hapiware.util.cmdlineparser.CommandLineParser;
import com.hapiware.util.cmdlineparser.CommandNotFoundException;
import com.hapiware.util.cmdlineparser.Description;
import com.hapiware.util.cmdlineparser.ExitException;
import com.hapiware.util.cmdlineparser.IllegalCommandLineArgumentException;
import com.hapiware.util.cmdlineparser.Option;
import com.hapiware.util.cmdlineparser.TestUtil;
import com.hapiware.util.cmdlineparser.constraint.ConstraintException;
import com.hapiware.util.cmdlineparser.writer.ScreenWriter;
import com.hapiware.util.publisher.Publisher;

public class ArgumentHelpTest
	extends
		TestBase
{
	private final static int DEFAULT_PORT = 80;
	private final static int DEFAULT_NUMBER_OF_THREADS = 20;
	private final static int MAX_NUMBER_OF_THREADS = 100;
	
	
	private CommandLineParser _parser;
	private final static ByteArrayOutputStream _os = new ByteArrayOutputStream();
	private final static ScreenWriter _writer;

	private interface SScreenWriter
	{
		public ScreenWriter createForTesting(PrintStream stream);
	}

	static {
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		_writer = sWriter.createForTesting(new PrintStream(_os));
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
				ArgumentHelpTest.class,
				_writer,
				new Description()
					.d("A small and customisable web server for publishing a directory tree")
					.d("over (local) net. Web root directory will be the directory")
					.d("where ").b("fileweb").d(" was started.")
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
		_parser.add(Integer.class, new Argument<Integer>("PORT") {{
			optional(DEFAULT_PORT);
			minValue(1);
			description("A port number.");
		}});
		_parser.add(Integer.class, new Argument<Integer>("NUM_OF_THREADS") {{
			optional(DEFAULT_NUMBER_OF_THREADS);
			minValue(1);
			maxValue(MAX_NUMBER_OF_THREADS);
			d("Maximum number of threads in the thread pool.");
		}});
		_parser.addExampleArguments("");
		_parser.addExampleArguments("50001");
		_parser.addExampleArguments("50001 35");
		
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
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    A small and customisable web server for publishing a directory tree over (local) net. Web root\n"
				+ "    directory will be the directory where 'fileweb' was started.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [PORT]\n"
				+ "        A port number. Argument is optional. Default value is '80'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "\n"
				+ "    [NUM_OF_THREADS]\n"
				+ "        Maximum number of threads in the thread pool. Argument is optional. Default value is '20'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "            * Maximum value is '100'.\n"
				+ "\n"
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
	public void usageHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "usage" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void examplesHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parse(
				new String[] { "--help", "examples" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar\n"
				+ "    java -jar cmd-parser.jar 50001\n"
				+ "    java -jar cmd-parser.jar 50001 35\n"
				+ "\n";
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
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    A small and customisable web server for publishing a directory tree over (local) net. Web root\n"
				+ "    directory will be the directory where 'fileweb' was started.\n"
				+ "\n"
				+ "OPTS:\n"
				+ "    -v, --verbose\n"
				+ "        Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "    -l, --log\n"
				+ "        Logs every step of the process.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [PORT]\n"
				+ "        A port number. Argument is optional. Default value is '80'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "\n"
				+ "    [NUM_OF_THREADS]\n"
				+ "        Maximum number of threads in the thread pool. Argument is optional. Default value is '20'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "            * Maximum value is '100'.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar\n"
				+ "    java -jar cmd-parser.jar 50001\n"
				+ "    java -jar cmd-parser.jar 50001 35\n"
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
				"OPTS:\n"
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
				"ARGS:\n"
				+ "    [PORT]\n"
				+ "        A port number. Argument is optional. Default value is '80'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "\n"
				+ "    [NUM_OF_THREADS]\n"
				+ "        Maximum number of threads in the thread pool. Argument is optional. Default value is '20'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "            * Maximum value is '100'.\n"
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
				"Help error:\n"
				+ "    'cmds' is not a valid help command.\n"
				+ "    \n"
				+ "Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	
	@Test
	public void twoMandatoryAndOneOptionalArgumentHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		CommandLineParser p =
			new CommandLineParser(
				ArgumentHelpTest.class,
				sWriter.createForTesting(new PrintStream(os)),
				new Description()
					.d("Testing two mandatory arguments")
					.d("and single optional argument.")
			);
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			description("Description for TYPE.");
		}});
		p.add(Integer.class, new Argument<Integer>("ACTION") {{
			optional(1);
			description("Description for ACTION.");
		}});
		p.add(Integer.class, new Argument<Integer>("LEVEL") {{
			description("Description for LEVEL.");
		}});
		TestUtil.replaceExitHandler(p);
		
		try {
			p.parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar ARGS\n"
				+ "\n"
				+ "Description:\n"
				+ "    Testing two mandatory arguments and single optional argument.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    TYPE\n"
				+ "        Description for TYPE.\n"
				+ "\n"
				+ "    [ACTION]\n"
				+ "        Description for ACTION. Argument is optional. Default value is '1'.\n"
				+ "\n"
				+ "    LEVEL\n"
				+ "        Description for LEVEL.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void optionalArgumentEmptyStringHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		CommandLineParser p =
			new CommandLineParser(
				ArgumentHelpTest.class,
				sWriter.createForTesting(new PrintStream(os)),
				new Description()
					.d("Testing two mandatory arguments")
					.d("and single optional argument.")
			);
		p.add(String.class, new Argument<String>("TYPE") {{
			optional("");
			description("Description for TYPE.");
		}});
		TestUtil.replaceExitHandler(p);
		
		try {
			p.parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    Testing two mandatory arguments and single optional argument.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [TYPE]\n"
				+ "        Description for TYPE. Argument is optional. Default value is an empty string.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void optionalArgumentEmptyStringDefaultValueNotShownHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		CommandLineParser p =
			new CommandLineParser(
				ArgumentHelpTest.class,
				sWriter.createForTesting(new PrintStream(os)),
				new Description()
					.d("Testing two mandatory arguments")
					.d("and single optional argument.")
			);
		p.add(String.class, new Argument<String>("TYPE") {{
			optional("", false);
			description("Description for TYPE.");
		}});
		TestUtil.replaceExitHandler(p);
		
		try {
			p.parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    Testing two mandatory arguments and single optional argument.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [TYPE]\n"
				+ "        Description for TYPE. Argument is optional.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	@Test
	public void optionalArgumentIntegerDefaultValueNotShownHelp()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		SScreenWriter sWriter = Publisher.publish(SScreenWriter.class, ScreenWriter.class);
		CommandLineParser p =
			new CommandLineParser(
				ArgumentHelpTest.class,
				sWriter.createForTesting(new PrintStream(os)),
				new Description()
					.d("Testing two mandatory arguments")
					.d("and single optional argument.")
			);
		p.add(Integer.class, new Argument<Integer>("TYPE") {{
			optional(1, false);
			description("Description for TYPE.");
		}});
		TestUtil.replaceExitHandler(p);
		
		try {
			p.parse(
				new String[] { "--help", "all" }
			);
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    Testing two mandatory arguments and single optional argument.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [TYPE]\n"
				+ "        Description for TYPE. Argument is optional.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "\n";
			assertEquals(os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}

	
	@Test
	public void parseInternalOptionsHelpAll()
	{
		try {
			_parser.parseInternalOptions(new String[] { "--help", "all" });
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n"
				+ "Description:\n"
				+ "    A small and customisable web server for publishing a directory tree over (local) net. Web root\n"
				+ "    directory will be the directory where 'fileweb' was started.\n"
				+ "\n"
				+ "OPTS:\n"
				+ "    -v, --verbose\n"
				+ "        Prints more verbose output. This option can occur several times.\n"
				+ "\n"
				+ "    -l, --log\n"
				+ "        Logs every step of the process.\n"
				+ "\n"
				+ "ARGS:\n"
				+ "    [PORT]\n"
				+ "        A port number. Argument is optional. Default value is '80'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "\n"
				+ "    [NUM_OF_THREADS]\n"
				+ "        Maximum number of threads in the thread pool. Argument is optional. Default value is '20'.\n"
				+ "\n"
				+ "        Constraints:\n"
				+ "            * Minimum value is '1'.\n"
				+ "            * Maximum value is '100'.\n"
				+ "\n"
				+ "Examples:\n"
				+ "    java -jar cmd-parser.jar -? all\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar\n"
				+ "    java -jar cmd-parser.jar 50001\n"
				+ "    java -jar cmd-parser.jar 50001 35\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	
	@Test
	public void parseInternalOptionsCommandsHelp()
	{
		try {
			_parser.parseInternalOptions(new String[] { "--help", "cmds" });
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Help error:\n"
				+ "    'cmds' is not a valid help command.\n"
				+ "    \n"
				+ "Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}
	
	
	@Test
	public void parseInternalOptionsUsage()
	{
		try {
			_parser.parseInternalOptions(new String[] { "--help", "usage" });
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			String hereDoc =
				"Usage:\n"
				+ "    java -jar cmd-parser.jar -? | --help ['all' | 'opts' | 'args']\n"
				+ "    java -jar cmd-parser.jar -? | --help ['usage' | 'examples']\n"
				+ "    java -jar cmd-parser.jar --version\n"
				+ "    java -jar cmd-parser.jar [OPTS] [ARGS]\n"
				+ "\n";
			assertEquals(_os.toString(), hereDoc);
			return;
		}
		fail("Should throw ExitException.");
	}

	
	@Test
	public void parseInternalOptionsShowVersion()
		throws
			ConstraintException,
			AnnotatedFieldSetException,
			CommandNotFoundException,
			IllegalCommandLineArgumentException
	{
		try {
			_parser.parseInternalOptions(new String[] { "--version" });
		}
		catch(ExitException e) {
			assertEquals(e.exitStatus, 0);
			assertEquals(_os.toString(), "Version: 1.0.0-for-testing\n");
			return;
		}
		fail("Should throw ExitException.");
	}
}
